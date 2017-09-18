package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.ForumPost;
import com.loqua.model.Publication;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsProfilePage;
import com.loqua.presentation.bean.applicationBean.BeanUtils;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de publicaciones de las paginas personales de usuarios
 * (paginas de inicio y de perfil).
 * @author Gonzalo
 */
public class BeanPublication implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Es la publicacion que se desea crear/editar/eliminar */
	private Publication publicationToCRUD;
	
	/** Lista de publicaciones que, segun su nivel de privacidad, son visibles
	 * para el usuario que accede a la vista */
	private List<Publication> visiblePubsByUser;
	
	/** Numero maximo de publicaciones consultadas en la pagina personal,
	 * cada vez que el usuario pincha en el boton de 'Ver mas'. */
	private Integer numPublicationsPerPage;
	
	/** Numero total de publicaciones realizadas por el usuario cuya
	 * pagina personal se consulta */
	private Integer numPublicationsByUser;
	
	/** Indica si la lista de publicaciones, mostradas en la pagina personal
	 * de un usuario, deberia incluir aquellas realizadas
	 * por los contactos del mismo. <br/>
	 * Deberia ser asi cuando el usuario este visitando su propia pagina
	 * de inicio. <br/> No deberia ser asi cuando el usuario este visitando
	 * su propia pagina de perfil (no confundir con la pagina de inicio),
	 * o la pagina de perfil de otro usuario.*/
	private boolean includePubsByContacts;
	
	/** Numero de publicaciones que se estan mostrando en la pagina personal
	 * consultada. Cuenta tambien las publicaciones que se han agregado
	 * cada vez que el usuario pincha en el boton de 'Ver mas'. */
	private int numProcessedPubs;

	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	/** Inyeccion de dependencia del {@link BeanSettingsProfilePage} */
	@ManagedProperty(value="#{beanSettingsProfilePage}")
	private BeanSettingsProfilePage beanSettingsProfilePage;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa los beans inyectados:
	 * {@link BeanLogin} y {@link BeanSettingsProfilePage}
	 */
	@PostConstruct
	public void init() {
		includePubsByContacts=false;
		numProcessedPubs=0;
		publicationToCRUD = new Publication();
		initBeanLogin();
		initBeanSettingsProfile();
		numPublicationsPerPage =
				beanSettingsProfilePage.getNumPublicationsPerPage();
	}
	
	/** Inicializa el objeto {@link BeanLogin} inyectado */
	private void initBeanLogin() {
		// Buscamos el BeanLogin en la sesion.
		beanLogin = null;
		beanLogin = (BeanLogin)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanLogin");
		// si no existe lo creamos e inicializamos:
		if (beanLogin == null) { 
			beanLogin = new BeanLogin();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanLogin", beanLogin);
		}
	}

	/** Inicializa el objeto {@link BeanSettingsProfilePage} inyectado */
	private void initBeanSettingsProfile() {
		// Buscamos el BeanSettings en la sesion.
		beanSettingsProfilePage = null;
		beanSettingsProfilePage = (BeanSettingsProfilePage)
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("beanSettingsProfilePage");
		// si no existe lo creamos e inicializamos:
		if (beanSettingsProfilePage == null) { 
			beanSettingsProfilePage = new BeanSettingsProfilePage();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put(
						"beanSettingsProfilePage", beanSettingsProfilePage);
		}
	}
	
	/** Destructor del bean. */
	@PreDestroy
	public void end(){
		clearStatus();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Crea una nueva publicacion.
	 * @param user el autor de la publicacion que se introduce, necesario para
	 * generar el nuevo objeto Publication y para para recargar
	 * la lista {@link #visiblePubsByUser} de este bean, una vez editada.
	 * @param includePubsByContacts define si la lista de publicaciones del bean
	 * incluye las de los contactos del usuario dado. Util para recargar
	 * la lista de publicaciones una vez editada.
	 * @param beanActionResult bean que se encarga de imprimir en la vista el
	 * resultado de la accion, exitoso o no.
	 */
	public void generatePublication(User user,BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		// las propiedades 'content' y 'privacity' ya se han establecido
		// desde la vista de profile_me.xhtml
		publicationToCRUD.setDatePubThis(new Date());
		publicationToCRUD.setSelfGeneratedThis(false);
		publicationToCRUD.setReadPubThis(false);
		publicationToCRUD.setUserThis(user);
		try {
			Factories.getService().getServicePublication()
				.createPublication(publicationToCRUD);
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			beanActionResult.setMsgActionResult("errorPublicationCreate");
			log.error("Unexpected Exception at 'generatePublication()'");
		}
		beanActionResult.setFinish(true);
		resetStatus(user.getId(), includePubsByContacts);
	}
	
	/**
	 * Actualiza una publicacion existente.
	 * @param user el perfil de usuario donde esta la lista de publicaciones,
	 * util para recargar la lista {@link #visiblePubsByUser} de este bean,
	 * una vez editada.
	 * @param includePubsByContacts define si la lista de publicaciones del bean
	 * incluye las de los contactos del usuario dado. Util para recargar
	 * la lista de publicaciones una vez editada.
	 * @param beanActionResult bean que se encarga de imprimir en la vista el
	 * resultado de la accion, exitoso o no.
	 */
	public void updatePublication(User user, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try {
			Factories.getService().getServicePublication()
				.updatePublication(publicationToCRUD);
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			// beanActionResult.setMsgActionResult("errorPublicationUpdate");
			log.error("Unexpected Exception at 'updatePublication()'");
		}
		beanActionResult.setFinish(true);
		resetStatus(user.getId(), includePubsByContacts);
	}
	
	/**
	 * Elimina una publicacion existente.
	 * @param user el perfil de usuario donde esta la lista de publicaciones,
	 * util para recargar la lista {@link #visiblePubsByUser} de este bean,
	 * una vez editada.
	 * @param includePubsByContacts define si la lista de publicaciones del bean
	 * incluye las de los contactos del usuario dado. Util para recargar
	 * la lista de publicaciones una vez editada.
	 * @param beanActionResult bean que se encarga de imprimir en la vista el
	 * resultado de la accion, exitoso o no.
	 */
	public void deletePublication(User user, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try {
			Factories.getService().getServicePublication()
				.deletePublication(publicationToCRUD);
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			// actionDeletePub.setMsgActionResult("errorPublicationDelete");
			log.error("Unexpected Exception at 'deletePublication()'");
		}
		beanActionResult.setFinish(true);
		resetStatus(user.getId(), includePubsByContacts);
	}
	
	/**
	 * Obtiene las publicaciones del usuario dado y, si se especifica,
	 * tambien las de sus contactos
	 * @param userId usuario de quien se consultan las publicaciones
	 * @param alsoByContacts define si la lista de publicaciones obtenida
	 * debe incluir las de los contactos del usuario dado.
	 * @param beanActionResult bean que se encarga de imprimir en la vista el
	 * resultado de la accion, exitoso o no.
	 * @return Si alsoByContacts==false:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;lista de publicaciones del usuario dado<br/>
	 * Si alsoByContacts==true:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;lista de publicaciones del usuario
	 * y de sus contactos
	 */
	public List<Publication> getPublicationsByUser(Long userId,
			boolean alsoByContacts, BeanActionResult beanActionResult){
		// Sucede un 'bug' con las ventanas 'modal' de bootstrap: al cerrarlas
		// se renderiza de nuevo la pagina. Eso genera que, por ejemplo,
		// cuando se abre la ventana de confirmacion para eliminar una publicacion,
		// al darle 'cancelar' se recarga la lista de publicaciones,
		// llamando de nuevo a este metodo.
		// Asi que pongo este 'if' para que, al hacer eso,
		// este metodo no cargue las publicaciones mas antiguas.
		if( visiblePubsByUser==null || visiblePubsByUser.isEmpty() ){
			if( alsoByContacts==false ){
				loadListPublicationsByUser(userId);
			}else{
				loadListPublicationsByUserAndContacts(userId);
			}
		}
		return visiblePubsByUser;
	}
	
	/**
	 * Agrega a la lista de publicaciones mostrada las siguientes
	 * 'n' publicaciones que aun no se han consultado (donde 'n' es
	 * {@link #numPublicationsByUser}).
	 * @param userId usuario de quien se consultan las publicaciones
	 * @param alsoByContacts define si la lista de publicaciones obtenida
	 * debe incluir las de los contactos del usuario dado.
	 * @param beanActionResult bean que se encarga de imprimir en la vista el
	 * resultado de la accion, exitoso o no.
	 * @return Si alsoByContacts==false:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;lista de publicaciones del usuario dado<br/>
	 * Si alsoByContacts==true:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;lista de publicaciones del usuario
	 * y de sus contactos
	 */
	public List<Publication> getNextPublicationsByUser(Long userId,
			boolean alsoByContacts, BeanActionResult beanActionResult){
		if( alsoByContacts==false ){
			loadListPublicationsByUser(userId);
		}else{
			loadListPublicationsByUserAndContacts(userId);
		}
		return visiblePubsByUser;
	}
	
	/**
	 * Iniciaiza las propiedades {@link #visiblePubsByUser},
	 * {@link #numPublicationsByUser} y {@link #numProcessedPubs}, consultando
	 * la lista de publicaciones del usaurio dado.
	 * @param userId usuario de quien se consultan las publicaciones
	 */
	private void loadListPublicationsByUser(Long userId){
		if( visiblePubsByUser==null ){
			visiblePubsByUser = new ArrayList<Publication>();
		}
		try{
			numPublicationsByUser=Factories.getService().getServicePublication()
					.getNumPublicationsByUser(userId);
			List<Publication> nextBundlePubs = new ArrayList<Publication>();
			int initialSize = visiblePubsByUser.size();
			// Mientras la lista "visiblePubsByUser" no se haya incrementado
			// en [numPublicationsPerPage] elementos,
			// y aun no hayamos procesado todas las publicaciones del usuario
			while( visiblePubsByUser.size()-initialSize < numPublicationsPerPage
					&& numProcessedPubs<numPublicationsByUser ){
				// procesa las siguientes [numPublicationsPerPage] publicaciones
				nextBundlePubs = Factories.getService()
						.getServicePublication().getPublicationsByUser(userId,
								numProcessedPubs, numPublicationsPerPage);
				numProcessedPubs += nextBundlePubs.size();
				// comprobando, por cada una, si deberia ser mostrada
				// al visiante, en cuyo caso se agrega a la lista
				// "visiblePubsByUser":
				addPubIfVisible( nextBundlePubs );
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at 'loadListPublicationsByUser()'");
		}
	}
	
	/**
	 * Iniciaiza las propiedades {@link #visiblePubsByUser},
	 * {@link #numPublicationsByUser} y {@link #numProcessedPubs}, consultando
	 * la lista de publicaciones del usaurio dado y de sus contactos.
	 * @param userId usuario de quien se consultan las publicaciones
	 */
	private void loadListPublicationsByUserAndContacts(Long userId){
		if( visiblePubsByUser==null ){
			visiblePubsByUser = new ArrayList<Publication>();
		}
		try{
			numPublicationsByUser=Factories.getService().getServicePublication()
					.getNumPublicationsByUserAndContacts(userId);
			List<Publication> nextBundlePubs = new ArrayList<Publication>();
			int initialSize = visiblePubsByUser.size();
			// Mientras la lista "visiblePubsByUser" no se haya incrementado
			// en [numPublicationsPerPage] elementos,
			// y aun no hayamos procesado todas las publicaciones del usuario
			while( visiblePubsByUser.size()-initialSize < numPublicationsPerPage
					&& numProcessedPubs<numPublicationsByUser ){
				// procesa las siguientes [numPublicationsPerPage] publicaciones
				nextBundlePubs = Factories.getService().getServicePublication()
						.getPublicationsByUserAndContacts(userId,
								numProcessedPubs, numPublicationsPerPage);
				numProcessedPubs += nextBundlePubs.size();
				// comprobando, por cada una, si deberia ser mostrada
				// al visiante, en cuyo caso se agrega a la lista
				// "visiblePubsByUser":
				addPubIfVisible( nextBundlePubs );
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at"
					+ "'loadListPublicationsByUserAndContacts()'");
		}
	}
	
	/**
	 * Agrega a la lista {@link #visiblePubsByUser} aquellas publicaciones de
	 * la lista indicada que, por su nivel de privacidad, son visibles
	 * para el usuario que las consulta.
	 * @param publicationsByUser lista de publicaciones que se comprueba
	 */
	private void addPubIfVisible(List<Publication> publicationsByUser) {
		for( Publication pub : publicationsByUser ){
			if( BeanUserView.shouldBeShownByPrivacityStatic(
					beanLogin.getLoggedUser(),pub.getUser(),
					pub.getPrivacity())){
				visiblePubsByUser.add(pub);
			}
		}
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA OBTENER EL TEXTO DE UNA PUBLICACION
	// GENERADA POR CUALQUIER USUARIO
	// // // // // // // // // // // // // // // // // //
	
	/**
	 * Recorta el texto de la Publicacion
	 * (texto predefinido en los ficheros 'events_X.properties') para obtener
	 * la parte previa al parametro se&ntilde;alado con el caracter '?1'.
	 * Por ejemplo, si el texto de la Publicacion es:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"ha alcanzado los ?1 comentarios"<br/>
	 * entonces este metodo devolvera:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"ha alcanzado los"<br/>
	 * @param publication Publicacion de la cual se desea obtener el texto
	 * @return Primera parte del texto de una publicacion
	 */
	public String getUserPublicationPart1(Publication publication){
		String publicationText = getUserPublication(publication);
		return BeanUtils.getTextPart1(publicationText);
	}
	
	/**
	 * Recorta el texto de la Publicacion
	 * (texto predefinido en los ficheros 'events_X.properties') para obtener
	 * la parte posterior al parametro se&ntilde;alado con el caracter '?1'.
	 * Por ejemplo, si el texto de la Publicacion es:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"ha alcanzado los ?1 comentarios"<br/>
	 * entonces este metodo devolvera:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"comentarios"<br/>
	 * @param publication Publicacion de la cual se desea obtener el texto
	 * @return Segunda parte del texto de una publicacion
	 */
	public String getUserPublicationPart2(Publication publication){
		String publicationText = getUserPublication(publication);
		return BeanUtils.getTextPart2(publicationText);
	}
	
	/** Traduce el texto de una publicacion
	 * al idioma en que se visualiza la pagina,
	 * a partir de los datos de los ficheros 'events_X.properties'.
	 * @param publication Publicacion de la cual se desea obtener el texto
	 * @return texto de la publicacion dada, en el idioma configurado por el usuario
	 */
	public String getUserPublication(Publication publication){
		String result = "";
		if( publication.getEvent()==null ){
			result = publication.getContent();
		}else{
			result = publication.getEvent().getContent();
			if( beanLogin.getLoggedUser().equals(publication.getUser()) ){
				result = result.replaceFirst("eventUser", "eventYou");
			}
			result = BeanSettingsSession.getTranslationEventsStatic(result);
		}
		return result;
	}
	
	/**
	 * Dada una publicacion, y a raiz del objeto Event asociado a esta, obtiene
	 * el modo en que debe mostrarse el valor eventValue de la publicacion.<br/>
	 * Las publicaciones pueden estar asociadas con 'eventos' predefinidos,
	 * en cuyo caso el parametro {@link Publication#eventValue}
	 * puede hacer referencia a un objeto Comment, Correction, o User.
	 * @param pub publicacion de la cual se obtiene el valor
	 * @return (int) un valor entre 1 y 4
	 * <ul><li>si se devuelve 1, el valor de la publicacion no debe mostrar
	 * ningun enlace</li>
	 * <li>si se devuelve 2, el valor de la publicacion
	 * sera un enlace a un comentario</li>
	 * <li>si se devuelve 3, el valor de la publicacion
	 * sera un enlace a una correccion</li>
	 * <li>si se devuelve 4, el valor de la publicacion
	 * sera un enlace a un perfil de usuario</li> 
	 */
	public int getPublicationValueType(Publication pub){
		if( pub.getEvent()==null){ return 1; }
		long eventType = pub.getEvent().getType();
		if( eventType<=100 ){ return 1; }
		if( eventType>=101 && eventType<=200 ){
			return 2;
		}
		if( eventType>=201 && eventType<=300 ){
			return 3;
		}
		if( eventType>=301 && eventType<=400 ){
			return 4;
		}
		if( eventType>=401 && eventType<=500 ){
			return 5;
		}
		return 1;
	}
	
	/**
	 * Halla el titulo del hilo al que pertenece el post al que hace referencia
	 * la publicacion dada.
	 * @param pub publicacion que se consulta
	 * @return titulo del hilo
	 */
	public String getThreadTitleByPost(Publication pub){
		String result = "";
		long eventValue = pub.getEventValue();
		ForumPost post = Factories.getService()
				.getServiceForumPost().getForumPostById(eventValue);
		if( post!=null ) {
			result = post.getForumThread().getTitle();
		}else{ 
			log.info("Unexpected outcome at 'getThreadTitleByPost()'");
		}
		return result;
	}
	
	/**
	 * Halla el pseudonimo del usuario al que hace referencia
	 * la publicacion dada.
	 * @param pub publicacion que se consulta
	 * @return pseudonimo del usuario
	 */
	public String getUserNick(Publication pub){
		String result = "";
		long eventValue = pub.getEventValue();
		try{
			User user = Factories.getService()
				.getServiceUser().getUserById(eventValue);
			result = user.getNick();
		} catch (EntityNotFoundException e) {
			log.error("EntityNotFoundException at 'getUserNick()'");
		}
		return result;
	}
	
	/**
	 * Comprueba si en la pagina personal del usuario aun no se han consultado
	 * todas las publicaciones visibles (y por tanto, deberia seguir
	 * mostrandose el boton de 'Ver mas').
	 * @return
	 * 'true' si hay publicaciones sin consultar <br/>
	 * 'false' si ya se estan mostrando todas las publicaciones del usuario
	 */
	public boolean exsistOlderPublicationsByUser(){
		// publications.size() = numero de publicaciones ya cargadas
		// return visiblePubsByUser.size() < numPublicationsByUser;
		return numProcessedPubs < numPublicationsByUser;
	}
	
	/**
	 * Sobreescribe las propiedades {@link #visiblePubsByUser},
	 * {@link #numPublicationsByUser} y {@link #numProcessedPubs}
	 * inicializandolas sin tener en cuenta su informacion actual.
	 * @param userID usuario de quien se consultan las publicaciones
	 * @param includePubsByContacts indica si la lista de publicaciones del bean
	 * incluye las de los contactos del usuario dado. Util para recargar
	 * la lista de publicaciones una vez editada.
	 */
	private void resetStatus(Long userID, boolean includePubsByContacts) {
		publicationToCRUD = new Publication();
		visiblePubsByUser = null;
		numProcessedPubs = 0;
		if( includePubsByContacts==false ){
			loadListPublicationsByUser(userID);
		}else{
			loadListPublicationsByUserAndContacts(userID);
		}
	}

	/**
	 * Borra el estado del Bean sobreescribiendo las propiedades del mismo
	 * con sus valores por defecto.
	 */
	private void clearStatus() {
		// Actualmente este metodo no es necesario, porque este beanPublication
		// es ambito view, sus propiedades se inicializan a cada cambio de vista.
		// Pero puede que en versiones posteriores este bean sea ambito session,
		// en cuyo caso este metodo si podria resultar necesario.
		publicationToCRUD = new Publication();
		visiblePubsByUser = new ArrayList<Publication>();
		numPublicationsByUser = 0;
		includePubsByContacts=false;
		numProcessedPubs=0;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public Publication getPublicationToCRUD() {
		return publicationToCRUD;
	}
	public void setPublicationToCRUD(Publication publicationToCRUD) {
		this.publicationToCRUD = publicationToCRUD;
	}
	
	public boolean getIncludePubsByContacts(){
		return includePubsByContacts;
	}
	public void setIncludePubsByContacts(boolean includePubsByContacts){
		this.includePubsByContacts = includePubsByContacts;
	}
}
