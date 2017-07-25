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

public class BeanPublication implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	private Publication publicationToCRUD;
	List<Publication> visiblePubsByUser;
	private Integer numPublicationsPerPage;
	private Integer numPublicationsByUser;
	private boolean includePubsByContacts;
	private int numProcessedPubs;

	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanSettingsProfilePage}")
	private BeanSettingsProfilePage beanSettingsProfilePage;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
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

	/**
	 * Inicializa el BeanSettingsProfile perteneciente a esta clase.</br>
	 * Si el BeanSettingsProfile ya fue inicializado,
	 * simplemente se obtiene del contexto de aplicacion.</br>
	 * Si el BeanSettingsProfile no existe en el contexto de aplicacion,
	 * se crea y se guarda en sesion bajo la clave 'beanSettingsProfile'.
	 */
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
	
	@PreDestroy
	public void end(){
		clearStatus();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * 
	 * @param user el autor de la publicacion que se introduce, necesario para
	 * generar el nuevo objeto Publication y para para recargar
	 * la lista de publicaciones de este bean, una vez editada
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
			// TODO Log
			beanActionResult.setMsgActionResult("errorPublicationCreate");
		}
		beanActionResult.setFinish(true);
		resetStatus(user.getId(), includePubsByContacts);
	}
	
	/**
	 * 
	 * @param user el perfil de usuario donde esta la lista de publicaciones,
	 * util para recargar la lista de publicaciones de este bean, una vez editada.
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
			// TODO Log
			// beanActionResult.setMsgActionResult("errorPublicationUpdate");
		}
		beanActionResult.setFinish(true);
		resetStatus(user.getId(), includePubsByContacts);
	}
	
	/**
	 * 
	 * @param user el perfil de usuario donde esta la lista de publicaciones,
	 * util para recargar la lista de publicaciones de este bean, una vez editada.
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
			// TODO Log
			// actionDeletePub.setMsgActionResult("errorPublicationDelete");
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
	 * &nbsp;&nbsp;&nbsp;&nbsp;lista de publicaciones del usuario y de sus contactos
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
	
	public List<Publication> getNextPublicationsByUser(Long userId,
			boolean alsoByContacts, BeanActionResult beanActionResult){
		// Este metodo es parecido al getPublicationsByUser pero sin la condicion
		// if( publicationsByUser==null || publicationsByUser.isEmpty() )
		if( alsoByContacts==false ){
			loadListPublicationsByUser(userId);
		}else{
			loadListPublicationsByUserAndContacts(userId);
		}
		return visiblePubsByUser;
	}
	
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
				// comprobando, por cada una, si deberia ser mostrada al visiante
				// en cuyo caso se agrega a la lista "visiblePubsByUser":
				addPubIfVisible( nextBundlePubs );
			}
		}catch( Exception e ){
			// TODO Log
		}
	}
	
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
				// comprobando, por cada una, si deberia ser mostrada al visiante
				// en cuyo caso se agrega a la lista "visiblePubsByUser":
				addPubIfVisible( nextBundlePubs );
			}
		}catch( Exception e ){
			// TODO Log
		}
	}
	
	private void addPubIfVisible(List<Publication> publicationsByUser) {
		for( Publication pub : publicationsByUser ){
			if( BeanUserView.shouldBeShownByPrivacityStatic(
					beanLogin.getLoggedUser(),pub.getUser(),pub.getPrivacity())){
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
	 * la parte previa al parametro señalado con el caracter '?1'.
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
	 * la parte posterior al parametro señalado con el caracter '?1'.
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
	 * el modo en que debe mostrarse el valor eventValue de la publicacion.
	 * Las publicaciones pueden estar asociadas con 'eventos' predefinidos,
	 * en cuyo caso el parametro Publication.eventValue puede hacer referencia a
	 * un objeto Comment, Correction, o User.
	 * @param pub Publicacion de la cual se obtiene el valor
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
	
	public String getThreadTitleByPost(Publication pub){
		String result = "";
		long eventValue = pub.getEventValue();
		ForumPost post = Factories.getService()
				.getServiceForumPost().getForumPostById(eventValue);
		if( post!=null ) {
			result = post.getForumThread().getTitle();
		}else{ 
			//TODO Log
		}
		return result;
	}
	
	public String getUserNick(Publication pub){
		String result = "";
		long eventValue = pub.getEventValue();
		try{
			User user = Factories.getService()
				.getServiceUser().getUserById(eventValue);
			result = user.getNick();
		} catch (EntityNotFoundException e) {
			// TODO Log
		}
		return result;
	}
	
	public boolean exsistOlderPublicationsByUser(){
		// publications.size() = numero de publicaciones ya cargadas
		// return visiblePubsByUser.size() < numPublicationsByUser;
		return numProcessedPubs < numPublicationsByUser;
	}
	
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
