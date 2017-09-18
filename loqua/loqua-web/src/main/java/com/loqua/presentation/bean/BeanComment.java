package com.loqua.presentation.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.ForumPost;
import com.loqua.model.ForumThread;
import com.loqua.presentation.bean.applicationBean.BeanSettingsForumPage;
import com.loqua.presentation.bean.applicationBean.BeanUtils;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de comentarios de los hilos del foro.
 * @author Gonzalo
 */
public class BeanComment implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Parametro 'comment' recibido en la URL, que indica el identificador
	 * del comentario que se desea editar o citar. <br/>
	 * Se inicializa en la vista 'forum_thread_comment.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private String commViewParam;
	
	/** Es el comentario que va a crear/editar/citar. <br/>
	 * Se inicializa en la vista 'forum_thread_comment.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo
	 * {@link #commViewParam}. */
	private Comment commentToCRUD;
	
	/** Numero maximo de comentarios mostrados en cada hilo del foro. */
	private static Integer numCommentsPerPage
		= BeanSettingsForumPage.getNumCommentsPerPageStatic();
	
	/** Parametro 'action' recibido en la URL, que indica la accion
	 * que se realiza desde la vista que invoca a este Bean:
	 * <ul><li>Si el valor es 1: crear comentario
	 * </li><li>Si el valor es 2: editar comentario
	 * </li><li>Si el valor es 3: citar comentario
	 * </li></ul>
	 * Se inicializa en la vista 'forum_thread_comment.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private int actionType;
	
	/** Texto introducido en el componente Summernote de la vista
	 * que invoca a este Bean, que indica el texto plano del comentario. */
	private String plainTextComment;
	
	/** Texto manejado por el componente Summernote de la vista
	 * que invoca a este Bean, que indica el codigo HTML del texto
	 * introducido del comentario. */
	private String textCodeComment;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el bean inyectado {@link BeanLogin} */
	@PostConstruct
	public void init() {
		initBeanLogin();
	}
	
	/** Inicializa los atributos {@link #textCodeComment}
	 * y {@link #plainTextComment} para que el componente summernote
	 * de las vistas muestre el texto que se edita.<br/>
	 * Va destinado a ser invocado desde la la seccion '<f:metadata>'
	 * de la vista forum_thread_comment.xhtml. */
	public void onLoad() {
		if( actionType==2 ){
			// Si se desea editar un comentario se inicializan estos valores
			// para que el componente summernote muestre el texto que se edita
			textCodeComment=commentToCRUD.getTextHtml();
			plainTextComment=commentToCRUD.getText();
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
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Obtiene el comentario citado por un comentario dado.
	 * En esta version de la aplicacion no se permite que un comentario cite
	 * a mas de un comentario. Por eso se devuelve un unico elemento, y no
	 * una lista de ellos.
	 * @param comment
	 * (Comment) comentario que cita al comentario obtenido
	 * @return
	 * (CommentQuoteTo) Comentario citado
	 */
	public CommentQuoteTo getCommentQuotedByComment(Comment comment){
		CommentQuoteTo result = null;
		List<CommentQuoteTo> listQuotedComments = new ArrayList<CommentQuoteTo>();
		try{
			listQuotedComments = Factories.getService().getServiceComment()
					.getCommentsQuotedByComment( comment.getId() );
			if(listQuotedComments!=null && ! listQuotedComments.isEmpty()){
				result = listQuotedComments.get(0);
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at 'getCommentQuotedByComment()'");
		}
		return result;
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina concreta,
	 * dentro del hilo del foro, en la que se muestra el comentario dado.
	 * @param forumPostId identificador del comentario al que se enlaza
	 * @return enlace al comentario dado, que puede ser empleado
	 * desde los componentes OutputLink de las vistas
	 */
	public String getOutputLinkToPost(Long forumPostId){
		ForumPost post = BeanForumThread.getPostByIdStatic(forumPostId);
		String urlParameters = getLinkParametersToForumPost(post);
		if(urlParameters==null) return null;
		return getLinkToThread() + "?" + urlParameters;
	}
	
	/**
	 * Halla la URL de la pagina 'forum_thread.xhtml' del tipo del usuario
	 * logueado (sea administrador o usuario registrado, accede a dicha pagina
	 * con su rol correspondiente).
	 * @return la URL hallada
	 */
	private String getLinkToThread(){
		String url = BeanUtils.getUrlUserPages() + "forum_thread.xhtml";
		return url;
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina concreta,
	 * dentro del hilo del foro, en la que se muestra el comentario dado.
	 * @param post comentario al que se enlaza
	 * @return enlace al comentario dado, que puede ser empleado
	 * desde los componentes CommandLink de las vistas
	 */
	public static String getCommandLinkToPostStatic(ForumPost post){
		// Este metodo es estatico para poder usarlo desde BeanCorrection
		// sin necesidad de una instancia
		String urlParameters = getLinkParametersToForumPost(post);
		if( urlParameters==null ) return null;
		String url = "forum_thread.xhtml?faces-redirect=true" + urlParameters;
		return url;
	}
	
	/**
	 * Halla la 'query string' necesaria para construir la URL que enlaza
	 * a la pagina concreta, dentro del hilo del foro, en la que se muestra
	 * el comentario dado
	 * @param post comentario al que se enlaza
	 * @return la 'query string' del enlace al comentario dado. Es una cadena
	 * de texto que define los parametros y el ancla de la URL
	 */
	private static String getLinkParametersToForumPost(ForumPost post){
		if(post==null) return null; // el post se habia eliminado
		ForumThread thread = post.getForumThread();
		if(thread==null) return null; // la noticia se habia eliminado
		int positionInThread = 1;
		positionInThread = post.getAsCommnent().getPositionIndex();
		int page = (int) 
				Math.ceil(positionInThread/numCommentsPerPage.floatValue());
		return "thread="+thread.getId()
				+ "&page="+page
				+ "#referenceTo_comment"+positionInThread;
	}
	
	/**
	 * Halla el texto de un comentario dado
	 * @param forumPostId identificador del comentario que se consulta
	 * @return texto del comentario
	 */
	public String getCommnentTextByPost(Long forumPostId){
		ForumPost post = BeanForumThread.getPostByIdStatic(forumPostId);
		if(post==null) return null; // el post se habia eliminado
		return post.getAsCommnent().getText();
	}
	
	/**
	 * Halla el enlace necesario para recargar la vista actual, tras usar
	 * el CommandLink que ejecuta el voto de un comentario.
	 * @param comment Comentario votado,
	 * al que se enlaza tras recargar la vista
	 * @param page numero de pagina, dentro del hilo del foro, en la que
	 * se muestra el comentario dado
	 * @return enlace al comentario dado, empleado
	 * desde los componentes CommandLink de las vistas
	 */
	public String getCommandLinkToVoteComment(Comment comment, Integer page){
		voteComment(comment);
		String anchor = "#referenceTo_comment" + comment.getPositionIndex();
		return BeanUtilsView.renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	/**
	 * Efectua el voto de un comentario dado
	 * @param comment comentario que se vota
	 */
	private void voteComment(Comment comment){
		Long loggedUserId = beanLogin.getLoggedUser().getId();
		try{
			Factories.getService().getServiceComment().voteComment(
					loggedUserId, comment);
		}catch( Exception e ){
			log.error("Unexpected Exception at 'voteComment()'");
		}
	}
	
	/**
	 * Comprueba si un comentario ya ha sido votado por el usuario logueado
	 * @param comment comentario que se comprueba
	 * @return
	 * 'true' si el comentario ya ha sido votado <br/>
	 * 'false' si el comentario aun no ha sido votado
	 */
	public boolean commentAlreadyVotedByUser(Comment comment){
		boolean result = false;
		Long loggedUserId = beanLogin.getLoggedUser().getId();
		try{
			result = Factories.getService().getServiceComment()
					.commentAlreadyVotedByUser(loggedUserId, comment.getId());
		}catch( Exception e ){
			log.error("Unexpected Exception at 'commentAlreadyVotedByUser()'");
		}
		return result;
	}
	
	/**
	 * Halla el enlace al que se redirige la navegacion, tras usar
	 * el CommandLink que ejecuta el envio o edicion de un comentario.
	 * El enlace conducira a la pagina donde se muestra
	 * el comentario en el hilo.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @param thread hilo del foro en el que se crea el comentario
	 * @return enlace al comentario guardado, o a pagina de error si se produce
	 * alguna excepcion
	 */
	public String getCommandLinkToSendComment(BeanActionResult beanActionResult,
			ForumThread thread){
		String result = null;
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try{
			if( actionType==1 ){
				// crear comentario:
				result = createComment(thread);
			}else if( actionType==2 ){
				// editar comentario:
				result = updateTextComment(thread);
			}else if( actionType==3 ){
				// citar comentario:
				result = quoteComment(thread);
			}
		}catch( Exception e ){
			// Dos opciones: o bien mostrar mensaje, o bien redirigir a error.
			// Se elije la segunda opcion y se deja comentada la primera:
			// plainTextComment = textCodeComment = ""; // limpiar variables
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
	 * Crea un comentario en el hilo indicado y halla el enlace
	 * a la pagina dentro del hilo donde se mostrara el comentario.
	 * @param thread hilo del foro en el que se crea el comentario
	 * @return enlace al comentario guardado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	private String createComment(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		compilePlainTextComment();
		Comment comm = Factories.getService().getServiceComment().sendComment(
				thread, plainTextComment, textCodeComment,
				beanLogin.getLoggedUser());
		return getCommandLinkToPostStatic(comm);
	}
	
	/**
	 * Edita un comentario en el hilo indicado y halla el enlace
	 * a la pagina donde se muestra el comentario en el hilo
	 * @param thread hilo del foro en el que se edita el comentario
	 * @return enlace al comentario editado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	private String updateTextComment(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		compilePlainTextComment();
		Factories.getService().getServiceComment().updateTextComment(
				commentToCRUD, plainTextComment, textCodeComment);
		return getCommandLinkToPostStatic(commentToCRUD);
	}
	
	/**
	 * Crea un comentario en el hilo indicado, citanto a otro
	 * comentario dado, y halla el enlace
	 * a la pagina donde se mostrara el comentario en el hilo
	 * @param thread hilo del foro en el que se crea el comentario
	 * @return enlace al comentario guardado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	private String quoteComment(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		compilePlainTextComment();
		Comment comm = Factories.getService().getServiceComment().quoteComment(
				commentToCRUD, plainTextComment, textCodeComment,
				beanLogin.getLoggedUser());
		return getCommandLinkToPostStatic(comm);
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
		FilterThreadComment (ej: al ir a Crear un comentario agregando
		en la url a mano el parametro 'comment' con un valor no numerico) */
		this.commViewParam = commViewParam;
		try{
			setCommentToCRUDById( Long.parseLong(commViewParam) );
		}catch( NumberFormatException nfe ){
			redirectToErrorNotFound();
		}
	}
	/**
	 * Inicializa el atributo {@link #commentToCRUD} segun el identificador
	 * indicado.
	 * @param currentCommentId atributo 'id' del comentario que se consulta
	 */
	public void setCommentToCRUDById(Long currentCommentId) {
		if( currentCommentId==null ) return;
		commentToCRUD = 
				(Comment) BeanForumThread.getPostByIdStatic(currentCommentId);
	}
	
	// // // // // // // // // // // // // // // // //
	// METODOS PARA EL MANEJO DEL TEXTO DE SUMMERNOTE
	// // // // // // // // // // // // // // // // //
	
	/**
	 * Corrige el valor del texto plano del comentario que se guardara
	 * en la base de datos.
	 */
	private void compilePlainTextComment() {
		/* En la vista de crear/editar/citar comentarios,
		el usuario esablece el texto del comentario mediante un summernote.
		Pero summernote une las palabras cuando hay saltos de linea intermedios
		(ej: traduce "<p>word1</p><p>word2</p>" por "word1word2"). 
		En este caso se solvento agregando mediante javascript un "\n"
		para indicar el salto al final de cada parrafo (ej: "word1\nword2").
		Al hacer eso, el String automaticamente agrega un caracter de escape
		(ej: "word1\\nword2").
		Asi que, para que quede correctamente guardado en la bdd,
		ahora se necesita sustituir "\\n" por "\n". */
		String regExpNewParagraph = "(\\\\n)";
		plainTextComment = plainTextComment.replaceAll(regExpNewParagraph,"\n");
	}
	
	/**
	 * Invoca a {@link #verifyTextCodeSummernote} para validar el texto HTML
	 * introducido, e inicializa el atributo {@link #textCodeComment}..
	 * @return el valor actualizado del atributo {@link #textCodeComment}
	 */
	public String getTextCodeComment() {
		textCodeComment = verifyTextCodeSummernote(textCodeComment);
		return textCodeComment;
	}
	/**
	 * Inicializa el atributo {@link #textCodeComment} segun el texto recibido.
	 * @param textCode codigo HTML del texto introducido por el usuario en
	 * el componente Summernote de la vista
	 */
	public void setTextCodeComment(String textCode) {
		this.textCodeComment = verifyTextCodeSummernote(textCode);
	}
	/**
	 * Corrige el valor del texto HTML del comentario introducido
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
	
	public Comment getCommentToCRUD() {
		return commentToCRUD;
	}
	public void setCommentToCRUD(Comment comment) {
		this.commentToCRUD = comment;
	}
	
	public String getPlainTextComment() {
		return plainTextComment;
	}
	public void setPlainTextComment(String plainText) {
		this.plainTextComment = plainText;
	}
}
