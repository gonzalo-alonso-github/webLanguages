package com.loqua.presentation.bean;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
//import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.ForumThread;
import com.loqua.model.User;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de correcciones de comentarios de los hilos del foro.
 * @author Gonzalo
 */
public class BeanCorrection implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Parametro 'correction' recibido en la URL, que indica el identificador
	 * de la correccion que se desea editar. <br/>
	 * Se inicializa en la vista 'forum_thread_correction.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private String corrViewParam;
	
	/** Parametro 'comment' recibido en la URL, que indica el identificador
	 * del comentario que se desea corregir. <br/>
	 * Se inicializa en la vista 'forum_thread_correction.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private String commViewParam;
	
	/** Es la correccion que va a crear/editar. <br/>
	 * Se inicializa en la vista 'forum_thread_correction.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo
	 * {@link #corrViewParam}. */
	private Correction correctionToCRUD;
	
	/** Es el comentario asociado a la correccion que se va a crear/editar.
	 * <br/> Se inicializa en la vista 'forum_thread_correction.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo
	 * {@link #commViewParam}. */
	private Comment commentToCorrect;
	
	/** Es la correccion que va a eliminar.
	 * Se inicializa desde la vista "forum_thread_correction.xhtml",
	 * donde los botones para desaconsejar correcciones tienen un actionLitener
	 * donde inicializan este valor */
	private Correction correctionOfList;
	
	/** Parametro 'action' recibido en la URL, que indica la accion
	 * que se realiza desde la vista que invoca a este Bean:
	 * <ul><li>Si el valor es 1: crear correccion
	 * </li><li>Si el valor es 2: editar correccion
	 * </li></ul>
	 * Se inicializa en la vista 'forum_thread_correction.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private int actionType;
	
	/** Texto introducido en el componente Summernote de la vista
	 * que invoca a este Bean, que indica el texto plano de la correccion */
	private String plainTextCorrection;
	
	/** Texto manejado por el componente Summernote de la vista
	 * que invoca a este Bean, que indica el codigo HTML del texto
	 * introducido de la correccion */
	private String textCodeCorrection;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el bean inyectado {@link BeanLogin} */
	@PostConstruct
	public void init() {
		correctionOfList = new Correction();
		initBeanLogin();
	}
	
	/** Inicializa los atributos {@link #textCodeCorrection}
	 * y {@link #plainTextCorrection} para que el componente summernote
	 * de las vistas muestre el texto que se edita.<br/>
	 * Va destinado a ser invocado desde la la seccion '<f:metadata>'
	 * de la vista forum_thread_correction.xhtml. */
	public void onLoad() {
		if( actionType==2 ){
			// Si se desea editar un comentario se inicializan estos valores
			// para que el componente summernote muestre el texto que se edita
			setCommentToCorrectById( correctionToCRUD.getComment().getId() );
			textCodeCorrection=correctionToCRUD.getTextHtml();
			plainTextCorrection=correctionToCRUD.getText();
		}
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
	
	/** Destructor del bean. */
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Halla la correccion aprobada de un comentario dado
	 * @param comment identificador del comentario al que pertenece
	 * la correccion aprobada que se consulta
	 * @return la unica Correction, perteneciente al Comment dado,
	 * cuyo atributo 'approved' es 'true', o null si no existe
	 */
	public Correction getApprovedCorrectionByComment(Comment comment){
		Correction result = null;
		try{
			result = Factories.getService().getServiceCorrection()
					.getApprovedCorrectionByComment( comment.getId() );
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'getApprovedCorrectionByComment()'");
		}
		return result;
	}
	
	/**
	 * Hallas la correcciones no aprobadas de un comentario dado
	 * @param comment identificador del comentario al que pertenecen las
	 * correcciones que se consultan
	 * @return lista de Correction, pertenecientes al Comment dado,
	 * cuyo atributo 'approved' es 'false', o null si no existe
	 */
	public List<Correction> getNotApprovedCorrectionsByComment(Comment comment){
		List<Correction> result = new ArrayList<Correction>();
		try{
			result = Factories.getService().getServiceCorrection()
					.getNotApprovedCorrectionsByComment( comment.getId() );
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'getNotApprovedCorrectionsByComment()'");
		}
		return result;
	}
	
	/**
	 * Halla la descripcion que se muestra al pie de una correccion dada,
	 * que indica el numero de usuarios que la han recomendado
	 * @param correction correccion que se consulta
	 * @return la descripcion que indica el numero de recomendaciones de la
	 * correccion
	 */
	public String getMessageForCorrectionAgrees(Correction correction){
		String result = "";
		int numAgrees = getNumCorrectionAgrees(correction);
		boolean userAlreadyAgree = getUserAgreeCorrection(
				beanLogin.getLoggedUser(), correction);
		if( numAgrees==0 ){
			result = BeanSettingsSession.getTranslationStatic(
					"descriptionNoAgreesForCorrecion");
		}else if( userAlreadyAgree ){
			if( numAgrees==1 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionYouAlreadyAgreeForCorrecion");
			}else if( numAgrees==2 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionYouAndOneOtherAgreeForCorrecion");
			}else if( numAgrees>2 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionYouAndOhersAgreeForCorrecion");
				result=result.replaceFirst("\\?1",String.valueOf(numAgrees-1));
			}
		}else /*if( ! userAlreadyAgree )*/{
			if( numAgrees==1 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionOneAgreeForCorrecion");
			}else if( numAgrees>1 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionManyAgreesForCorrecion");
				result=result.replaceFirst("\\?1",String.valueOf(numAgrees));
			}
		}
		return result;
	}
	
	/**
	 * Comprueba si el usuario dado ha dado su recomendacion a una correccion
	 * @param user User que se consulta
	 * @param correction Correction que se consulta 
	 * @return
	 * true: si el usuario ha dado su recomendacion a la correccion
	 * false: si el usuario aun no ha dado su recomendacion a la correccion
	 */
	public boolean getUserAgreeCorrection(User user, Correction correction){
		boolean result = false;
		try{
			result = Factories.getService().getServiceCorrection()
					.getUserAgreeCorrection(user.getId(), correction.getId());
		}catch( Exception e ){
			log.error("Unexpected Exception at 'getUserAgreeCorrection()'");
		}
		return result;
	}
	
	/**
	 * Halla el numero de recomedaciones que ha recibido la correccion dada
	 * @param correction Correction que se consulta
	 * @return cantidad de CorrectionAgree que pertenecen a la Correction dada
	 */
	private Integer getNumCorrectionAgrees(Correction correction){
		Integer result = null;
		try{
			result = Factories.getService().getServiceCorrection()
					.getNumCorrectionAgrees( correction.getId() );
			if( result==null || result<0 ){
				result=0;
			}else if( result>=99 ){
				result=99;
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at 'getNumCorrectionAgrees()'");
		}
		return result;
	}
	
	/**
	 * Halla la descripcion que se muestra al pie de una correccion dada,
	 * que indica el numero de usuarios que la han desaconsejado
	 * @param correction correccion que se consulta
	 * @return la descripcion que indica el numero de desaprobaciones de la
	 * correccion
	 */
	public String getMessageForCorrectionDisagrees(Correction correction){
		String result = "";
		int numDisagrees = getNumCorrectionDisagrees(correction);
		boolean userAlreadyDisagree = getUserDisagreeCorrection(
				beanLogin.getLoggedUser(), correction);
		if( numDisagrees==0 ){
			result = BeanSettingsSession.getTranslationStatic(
					"descriptionNoDisagreesForCorrecion");
		}else if( userAlreadyDisagree ){
			if( numDisagrees==1 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionYouAlreadyDisagreeForCorrecion");
			}else if( numDisagrees==2 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionYouAndOneOtherDisagreeForCorrecion");
			}else if( numDisagrees>2 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionYouAndOhersDisagreeForCorrecion");
				result=result.replaceFirst("\\?1",String.valueOf(numDisagrees-1));
			}
		}else /*if( ! userAlreadyAgree )*/{
			if( numDisagrees==1 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionOneDisagreeForCorrecion");
			}else if( numDisagrees>1 ){
				result = BeanSettingsSession.getTranslationStatic(
						"descriptionManyDisagreesForCorrecion");
				result=result.replaceFirst("\\?1",String.valueOf(numDisagrees));
			}
		}
		return result;
	}
	
	/**
	 * Comprueba si el usuario dado ha dado su desaprobacion a una correccion
	 * @param user User que se consulta
	 * @param correction Correction que se consulta 
	 * @return
	 * true: si el usuario ha dado su desaprobacion a la correccion
	 * false: si el usuario aun no ha dado su desaprobacion a la correccion
	 */
	public boolean getUserDisagreeCorrection(User user, Correction correction){
		boolean result = false;
		try{
			result = Factories.getService().getServiceCorrection()
					.getUserDisagreeCorrection(user.getId(), correction.getId());
		}catch( Exception e ){
			log.error("Unexpected Exception at 'getUserDisagreeCorrection()'");
		}
		return result;
	}
	
	/**
	 * Halla el numero de desaprobaciones que ha recibido la correccion dada
	 * @param correction Correction que se consulta
	 * @return cantidad de CorrectionDisagree que pertenecen
	 * a la Correction dada
	 */
	private Integer getNumCorrectionDisagrees(Correction correction){
		Integer result = null;
		try{
			result = Factories.getService().getServiceCorrection()
					.getNumCorrectionDisagrees( correction.getId() );
			if( result==null || result<0 ){
				result=0;
			}else if( result>=99 ){
				result=99;
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at 'getNumCorrectionDisagrees()'");
		}
		return result;
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina concreta,
	 * dentro del hilo del foro, en la que se muestra la correccion que se
	 * aprueba.
	 * @param corr Correccion aprobada,
	 * a la que se enlaza tras recargar la vista
	 * @param page numero de pagina, dentro del hilo del foro, en la que
	 * se muestra el comentario dado
	 * @return enlace a la correccion dada, empleado
	 * desde los componentes CommandLink de las vistas
	 */
	public String getCommandLinkToAcceptCorrection(Correction corr){
		String result = null;
		try{
			Factories.getService().getServiceCorrection().acceptCorrection(corr);
			result = BeanComment.getCommandLinkToPostStatic(corr);
		}catch( Exception e ){
			return "errorUnexpected";
		}
		if( result==null ) result = "errorUnexpected";
		return result;
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina concreta,
	 * dentro del hilo del foro, en la que se muestra la correccion que se
	 * elimina.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return enlace a la correccion dada, empleado
	 * desde los componentes CommandLink de las vistas
	 */
	public String getCommandLinkToDeleteSuggestedCorr(
			BeanActionResult beanActionResult){
		String result = null;
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try{
			Factories.getService().getServiceCorrection().deleteCorrection(
					correctionOfList);
			result = BeanComment.getCommandLinkToPostStatic(correctionOfList);
		}catch( Exception e ){
			// Dos opciones: o bien mostrar mensaje, o bien redirigir a error.
			// Se elije la segunda opcion y se deja comentada la primera:
			// beanActionResult.setMsgActionResult("errorRejectCorrection");
			// return null;
			return "errorUnexpected";
		}
		if( result!=null ) beanActionResult.setSuccess(true);
		else result = "errorUnexpected";
		beanActionResult.setFinish(true);
		return result;
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina concreta,
	 * dentro del hilo del foro, en la que se muestra la correccion que se
	 * recomienda.
	 * @param corr correccion que se recomienda
	 * @param anchor 'ancla', en la URL, que identifica a la correccion que
	 * se recomienda
	 * @return enlace a la correccion dada, empleado
	 * desde los componentes CommandLink de las vistas
	 */
	public String getCommandLinkToRecommendCorrection(
			Correction corr, String anchor){
		recommendCorrection(corr);
		return BeanUtilsView.renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	/**
	 * Recomienda una correccion por parte del usuario logueado
	 * @param corr correccion que se recomienda
	 */
	private void recommendCorrection(Correction corr){
		User user = beanLogin.getLoggedUser();
		try {
			Factories.getService().getServiceCorrection()
				.recommendCorrection(user.getId(), corr);
		} catch (Exception e) {
			log.error("Unexpected Exception at 'recommendCorrection()'");
		}
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina concreta,
	 * dentro del hilo del foro, en la que se muestra la correccion que se
	 * desaconseja.
	 * @param corr correccion que se recomienda
	 * @param anchor 'ancla', en la URL, que identifica a la correccion que
	 * se desaconseja
	 * @return enlace a la correccion dada, empleado
	 * desde los componentes CommandLink de las vistas
	 */
	public String getCommandLinkToDissuadeCorrection(
			Correction corr, String anchor){
		dissuadeCorrection(corr);
		return BeanUtilsView.renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	/**
	 * Desaconseja una correccion por parte del usuario logueado
	 * @param corr correccion que se recomienda
	 */
	private void dissuadeCorrection(Correction corr){
		User user = beanLogin.getLoggedUser();
		try {
			Factories.getService().getServiceCorrection()
				.dissuadeCorrection(user.getId(), corr.getId());
		} catch (Exception e) {
			log.error("Unexpected Exception at 'dissuadeCorrection()'");
		}
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina concreta,
	 * dentro del hilo del foro, en la que se muestra la correccion que se
	 * aprueba.
	 * @param corr Correccion aprobada,
	 * a la que se enlaza tras recargar la vista
	 * @param page numero de pagina, dentro del hilo del foro, en la que
	 * se muestra el comentario dado
	 * @return enlace a la correccion dada, empleado
	 * desde los componentes CommandLink de las vistas
	 */
	
	/**
	 * Halla el enlace al que se redirige la navegacion, tras usar
	 * el CommandLink que ejecuta el envio o edicion de una correccion.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @param thread hilo del foro al que pertenece la correccion que se
	 * procesa
	 * @return enlace a la correccion guardada, o a pagina de error
	 * si se produce alguna excepcion
	 */
	public String getCommandLinkToSendCorrection(
			BeanActionResult beanActionResult, ForumThread thread){
		String result = null;
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try{
			if( actionType==1 ){
				// crear correccion:
				result = createCorrection(thread);
			}else if( actionType==2 ){
				// editar correccion:
				result = updateTextCorrection(thread);
			}
		}catch( Exception e ){
			// Dos opciones: o bien mostrar mensaje, o bien redirigir a error.
			// Se elije la segunda opcion y se deja comentada la primera:
			// plainTextCorrection=textCodeCorrection=""; // limpiar variables
			// beanActionResult.setMsgActionResult("errorPublishPost");
			// return null;
			return "errorUnexpected";
		}
		if( result!=null ) beanActionResult.setSuccess(true);
		else result = "errorUnexpected";
		beanActionResult.setFinish(true);
		return result;
	}
	
	/**
	 * Crea una correccion en el hilo indicado y halla el enlace
	 * a la pagina dentro del hilo donde se mostrara la correccion.
	 * @param thread hilo del foro en el que se crea la correccion
	 * @return enlace a la correccion guardada
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	private String createCorrection(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		/* aqui no es necesario llamar a compilePlainTextCorrection();
		porque para crear una Correction no se usa summernote */
		plainTextCorrection = "";
		String sentence = "";
		int iterator = 0;
		// procesar desde la primera hasta la penultima frase:
		for( ;iterator<sentencesToCorrect.toArray().length-1; iterator++ ){
			sentence = sentencesToCorrect.get(iterator);
			if( sentence.trim().length()!=0 ){
				plainTextCorrection += escapeHtml4(sentence);
				if(sentence.endsWith(".")==false){plainTextCorrection += ".";}
				plainTextCorrection += " ";
			}
		}
		// procesar la ultima frase:
		sentence = sentencesToCorrect.get(iterator);
		if( sentence.trim().length()!=0 ){
			plainTextCorrection += escapeHtml4(sentence);
			if( sentence.endsWith(".")==false ){ plainTextCorrection += "."; }
		}
		// con el set hacemos que textCodeCorrection = mismo texto, en html:
		setTextCodeCorrection(plainTextCorrection);
		Factories.getService().getServiceCorrection().sendCorrection(
				commentToCorrect, plainTextCorrection, textCodeCorrection,
				beanLogin.getLoggedUser());
		return BeanComment.getCommandLinkToPostStatic(commentToCorrect);
	}
	
	/**
	 * Edita una correccion en el hilo indicado y halla el enlace
	 * a la pagina dentro del hilo donde se mostrara la correccion.
	 * @param thread hilo del foro en el que se edita la correccion
	 * @return enlace a la correccion guardada
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	private String updateTextCorrection(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		compilePlainTextCorrection();
		Factories.getService().getServiceCorrection().updateTextCorrection(
				correctionToCRUD, plainTextCorrection, textCodeCorrection);
		return BeanComment.getCommandLinkToPostStatic(commentToCorrect);
	}
	
	/**
	 * Redirige la navegacion a la pagina que indica
	 * que la URL solicitada no existe
	 */
	private void redirectToErrorNotFound() {
		ExternalContext ec = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = "errorPageNotFound.xhtml";
		try {
			ec.redirect(url);
		} catch (IOException e) {}
	}
	
	// // // // // // // // // // // // // // //
	// GETTERS & SETTERS CON LOGICA DE NEGOCIO
	// // // // // // // // // // // // // // //
	
	/** Metodo 'get' del atributo {@link corrViewParam}.
	 * @return el atributo {@link corrViewParam}
	 */
	public String getCorrViewParam() {
		return corrViewParam;
	}
	/**
	 * Comprueba si es correto el valor recibido por parametro en el metodo,
	 * que es el parametro 'correction' recibido en la URL.
	 * Si es correcto, inicializa el atributo {@link corrViewParam};
	 * de lo contrario redirige a la pagina que indica
	 * que la URL es desconocida.
	 * @param corrViewParam cadena de texto que indica el atributo 'id'
	 * de la correcion que se consulta
	 */
	public void setCorrViewParam(String corrViewParam) {
		/* En este caso el parametro viene dado en la url
		y puede ser manipulado, por tanto conviene usar este try-catch.
		Ademas el NumberFormatException no siempre se comprueba en el filtro
		FilterThreadCorrection (ej: al ir a Corregir un comentario agregando
		en la url a mano el parametro 'correction' con un valor no numerico) */
		this.corrViewParam = corrViewParam;
		try{
			setCorrectionToCRUDById( Long.parseLong(corrViewParam) );
		}catch( NumberFormatException nfe ){
			redirectToErrorNotFound();
		}
	}
	/**
	 * Inicializa el atributo {@link #correctionToCRUD} segun el identificador
	 * indicado.
	 * @param correctionToCRUDId atributo 'id' de la correccion
	 * que se consulta
	 */
	public void setCorrectionToCRUDById(Long correctionToCRUDId) {
		this.correctionToCRUD = (Correction) BeanForumThread.getPostByIdStatic(
				correctionToCRUDId);
	}

	/** Metodo 'get' del atributo {@link commViewParam}.
	 * @return el atributo {@link commViewParam}
	 */
	public String getCommViewParam() {
		return commViewParam;
	}
	/**
	 * Comprueba si es correto el valor recibido por parametro en el metodo,
	 * que es el parametro 'comment' recibido en la URL.
	 * Si es correcto, inicializa el atributo {@link commViewParam};
	 * de lo contrario redirige a la pagina que indica
	 * que la URL es desconocida.
	 * @param commViewParam cadena de texto que indica el atributo 'id'
	 * del comentario que se consulta
	 */
	public void setCommViewParam(String commViewParam) {
		/* En este caso el parametro viene dado en la url
		y puede ser manipulado, por tanto conviene usar este try-catch.
		Ademas el NumberFormatException no siempre se comprueba en el filtro
		FilterThreadCorrection (ej: al ir a Corregir un comentario agregando
		en la url a mano el parametro 'comment' con un valor no numerico) */
		this.commViewParam = commViewParam;
		try{
			setCommentToCorrectById( Long.parseLong(commViewParam) );
		}catch( NumberFormatException nfe ){
			redirectToErrorNotFound();
		}
	}
	/**
	 * Inicializa el atributo {@link #commentToCorrect} segun el identificador
	 * indicado.
	 * @param commentToCorrectId atributo 'id' del comentario que se corrige
	 */
	public void setCommentToCorrectById(Long commentToCorrectId) {
		setCommentToCorrect( (Comment) BeanForumThread.getPostByIdStatic(
				commentToCorrectId) );
		//loadSentencesToCorrect();
	}
	
	// // // // // // // // // // // // // // // // //
	// METODOS PARA EL MANEJO DEL TEXTO DE SUMMERNOTE
	// // // // // // // // // // // // // // // // //
	
	/**
	 * Corrige el valor del texto plano de la correccion que se guardara
	 * en la base de datos.
	 */
	private void compilePlainTextCorrection() {
		/* En la vista de editar correcciones,
		el usuario esablece el texto de la correccion mediante un summernote.
		Pero summernote une las palabras cuando hay saltos de linea intermedios
		(ej: traduce "<p>word1</p><p>word2</p>" por "word1word2"). 
		En este caso se solvento agregando mediante javascript un "\n"
		para indicar el salto al final de cada parrafo (ej: "word1\nword2").
		Al hacer eso, el String automaticamente agrega un caracter de escape
		(ej: "word1\\nword2").
		Asi que, para que quede correctamente guardado en la bdd,
		ahora se necesita sustituir "\\n" por "\n". */
		String regExpNewParagraph = "(\\\\n)";
		plainTextCorrection =
				plainTextCorrection.replaceAll(regExpNewParagraph, "\n");
	}
	
	/**
	 * Invoca a {@link #verifyTextCodeSummernote} para validar el texto HTML
	 * introducido, e inicializa el atributo {@link #textCodeCorrection}.
	 * @return el valor actualizado del atributo {@link #textCodeCorrection}
	 */
	public String getTextCodeCorrection() {
		textCodeCorrection = verifyTextCodeSummernote(textCodeCorrection);
		return textCodeCorrection;
	}
	/**
	 * Inicializa el atributo {@link #textCodeCorrection}
	 * segun el texto recibido.
	 * @param textCode codigo HTML del texto introducido por el usuario en
	 * el componente Summernote de la vista
	 */
	public void setTextCodeCorrection(String textCode) {
		this.textCodeCorrection = verifyTextCodeSummernote(textCode);
	}
	/**
	 * Corrige el valor del texto HTML de la correccion editada
	 * por el usuario en el componente Summernote de la vista.
	 * @param textCode codigo HTML del texto introducido por el usuario en
	 * el componente Summernote de la vista
	 */
	private String verifyTextCodeSummernote(String textCode){
		if( textCode!=null ){
			String summernotePreffix = "<p>";
			String summernoteSuffix = "</p>";
			if( ! textCode.startsWith(summernotePreffix)
					&& ! textCode.endsWith(summernoteSuffix)){
				textCode = summernotePreffix + textCode + summernoteSuffix;
			}
		}
		return textCode;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public int getActionType(){
		return actionType;
	}
	public void setActionType(int actionType){
		this.actionType = actionType;
	}
	
	public Correction getCorrectionOfList() {
		return correctionOfList;
	}
	public void setCorrectionOfList(Correction correction) {
		this.correctionOfList = correction;
	}
	
	public Correction getCorrectionToCRUD() {
		return correctionToCRUD;
	}
	public void setCorrectionToCRUD(Correction correctionToCRUD) {
		this.correctionToCRUD = correctionToCRUD;
	}
	
	public Comment getCommentToCorrect() {
		return commentToCorrect;
	}
	public void setCommentToCorrect(Comment commentToCorrect) {
		this.commentToCorrect = commentToCorrect;
		loadSentencesToCorrect();
	}
	
	public String getPlainTextCorrection() {
		return plainTextCorrection;
	}
	public void setPlainTextCorrection(String plainText) {
		this.plainTextCorrection = plainText;
	}
	
	
	// // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA REALIZAR LA CORRECCION DE CADA FRASE DEL COMENTARIO
	// // // // // // // // // // // // // // // // // // // // // // //
	
	/** Lista de frases en las que se divide el comentario original
	 * que se va a corregir */
	private List<String> sentencesUnmodifiable = new ArrayList<String>();
	/** Lista de frases del comentario, una vez corregidas por el usuario */
	private List<String> sentencesToCorrect = new ArrayList<String>();
	
	/** Carga la lista {@link #sentencesUnmodifiable} */
	public void loadSentencesToCorrect(){
		if( commentToCorrect==null || commentToCorrect.getText()==null)
			return;
		// El caracter separador entre frases es el punto:
		String regExpSinglePoint = "([\\., ]*\\. )";
		// El salto de linea tambien separa las frases:
		String regExpNewParagraph = "(\\n)";
		String regExp = regExpSinglePoint + "|" + regExpNewParagraph;
		String sentences[] = commentToCorrect.getText().split(regExp);
		/*sentencesUnmodifiable = new ArrayList<String>(Arrays.asList(sentences));
		sentencesToCorrect = new ArrayList<String>(Arrays.asList(sentences));*/
		sentencesUnmodifiable = new ArrayList<String>();
		sentencesToCorrect = new ArrayList<String>();
		for( int i=0; i<sentences.length; i++ ){
			if( ! sentences[i].trim().isEmpty() ){
				sentencesUnmodifiable.add(sentences[i]);
				sentencesToCorrect.add(sentences[i]);
			}
		}
	}
	
	/** Indica la frase, del comentario original, que el usuario
	 * ha seleccionado para corregirla */
	private int selectedSentenceToCorrect;
	
	/** Restaura una frase corregida por el usuario; es decir, le
	 * sobreescribe el texto que mostraba en el texto original */
	public void resetSentence(){
		String reset = sentencesUnmodifiable.get(selectedSentenceToCorrect);
		sentencesToCorrect.set(selectedSentenceToCorrect, reset);
		
		/* Si este metodo se llamase desde el action y no desde actionListener,
		deberia devolver la url de destino con la queryString adecuada
		para que pase el filtro FilterForumCorrection.
		De lo contrario pareceria que al pinchar el commandLink no pasa nada.
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest)
			facesContext.getExternalContext().getRequest();
		String uri = req.getRequestURI().toString();
		String samePage = uri.substring( uri.lastIndexOf("/")+1, uri.length() );
		String sameQueryString = req.getQueryString();
		String url = samePage + "?faces-redirect=true" + sameQueryString;
		return url;
		*/
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public List<String> getListSentencesToCorrect(){
		return sentencesToCorrect;
	}
	public void setListSentencesToCorrect(List<String> sentences){
		sentencesToCorrect = sentences;
	}
	
	public int getSelectedSentenceToCorrect(){
		return selectedSentenceToCorrect;
	}
	public void setSelectedSentenceToCorrect(int sentence){
		selectedSentenceToCorrect = sentence;
	}
}
