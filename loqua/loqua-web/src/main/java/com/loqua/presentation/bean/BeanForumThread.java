package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.Comment;
import com.loqua.model.ForumPost;
import com.loqua.model.ForumThread;
import com.loqua.presentation.bean.applicationBean.BeanSettingsForumPage;
import com.loqua.presentation.bean.applicationBean.BeanUtils;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de la pagina de cada hilo del foro.
 * @author Gonzalo
 */
public class BeanForumThread implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Parametro 'thread' recibido en la URL, que indica el identificador
	 * del hilo del foro que se desea consultar. <br>
	 * Se inicializa en las vistas 'forum_thread.xhtml',
	 * 'forum_thread_comment.xhtml' o 'forum_thread_correction.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private Long currentThreadId;
	
	/** Es el hilo del foro que va a consultar. <br>
	 * Se inicializa en las vistas 'forum_thread.xhtml',
	 * 'forum_thread_comment.xhtml' o 'forum_thread_correction.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo
	 * {@link #currentThreadId}. */
	private ForumThread currentThread;
	
	/** Parametro 'page' recibido en la URL, que indica el numero de la pagina
	 * que se desea consultar dentro del hilo del foro. <br>
	 * Se inicializa en la vista 'forum_thread.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private Integer offsetPage;
	
	/** Numero maximo de comentarios en cada pagina del hilo. */
	private Integer numCommentsPerPage;
	
	/** Numero total de comentarios del hilo que se visita. */
	private Integer numCommentsTotal;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el bean inyectado: {@link BeanLogin}
	 */
	@PostConstruct
	public void init() {
		initBeanLogin();
		numCommentsPerPage = BeanSettingsForumPage.getNumCommentsPerPageStatic();
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
	public void end(){
		clearStatus();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Incremeta el numero de visitas recibidas por un hilo del foro.
	 * @param thread hilo del foro que se actualiza
	 */
	public static void incrementCountVisitsStatic(ForumThread thread){
		// Este metodo se usa por ejemplo desde el "FilterForumThread"
		try{
			Factories.getService().getServiceThread()
					.incrementCountVisits( thread );
		}catch( Exception e ){
			new BeanForumThread().log.error("Unexpected Exception at "
					+ "'incrementCountVisitsStatic()'");
		}
	}
	
	/**
	 * Halla los comentarios que pertenecen a un hilo determinado del foro,
	 * ordenados ascendentemente por su fecha. Inicializa el atributo
	 * {@link #numCommentsTotal} con el numero de ellos.
	 * @return la lista de comentrios obtenida
	 */
	public List<Comment> getListCommentsByThread(){
		List<Comment> result = new ArrayList<Comment>();
		try{
			result=Factories.getService().getServiceComment()
					.getCommentsByThread(
						currentThread.getId(), offsetPage, numCommentsPerPage);
			numCommentsTotal = Factories.getService().getServiceComment()
					.getNumCommentsByThread(currentThread.getId());
		}catch( Exception e ){
			log.error("Unexpected Exception at 'getListCommentsByThread()'");
		}
		return result;
	}
	
	/**
	 * Halla los comentarios que pertenecen a un hilo determinado del foro,
	 * ordenados descendentemente por su fecha.
	 * @return la lista de comentrios obtenida
	 */
	public List<Comment> getListCommentsByThreadReverseOrder(){
		// este metodo solo se usa desde 'forum_thread_comment.xhtml'
		List<Comment> result = new ArrayList<Comment>();
		try{
			result = Factories.getService().getServiceComment()
					.getCommentsByThreadReverseOrder(
							currentThread.getId(), 0, numCommentsPerPage);
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'getListCommentsByThreadReverseOrder()'");
		}
		return result;
	}
	
	/**
	 * Obtiene la URL necesaria para que los componentes OutpuLink de la vista
	 * que llaman a este metodo enlacen a la pagina 'forum_thread.xhtml',
	 * indicando en la 'query string' de la URL la noticia ('thread') dada.<br>
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForumThread'.
	 * @param threadId el 'hilo' del foro que se va a consultar
	 * en la siguiente vista (forum_thread.xhtml).
	 * @param offset pagina del 'hilo' que se va a consultar.
	 * @return la URL de la pagina de 'forum_thread.xhtml'.
	 */
	public String getOutputLinkToThread(Long threadId, Integer offset){
		// Como este bean es ambito view,
		// la unica forma de conocer cual es la noticia seleccionada
		// al pinchar en un enlace es devolver un parametro en la url.
		ForumThread thread = getThreadById(threadId);
		if(thread==null) return "errorUnexpected";
		String url = getLinkToThread() + "?"
				+ "thread="+threadId
				+ "&page="+offset;
		return url;
	}
	
	/**
	 * Consulta hilos del foro segun su atributo 'id'
	 * @param threadId atributo 'id' del ForumThread que se consulta
	 * @return objeto ForumThread cuyo atributo 'id' coincide
	 * con el parametro dado, o null si no existe
	 */
	public ForumThread getThreadById(Long threadId){
		// Este metodo se usa en el snippet 'profile_list_publications'
		return getThreadByIdStatic(threadId);
	}
	
	/**
	 * Version estatica del metodo {@link #getThreadById}
	 * @param threadId atributo 'id' del ForumThread que se consulta
	 * @return objeto ForumThread cuyo atributo 'id' coincide
	 * con el parametro dado, o null si no existe
	 */
	public static ForumThread getThreadByIdStatic(Long threadId){
		// Este metodo se usa por ejemplo en el filtro 'FilterForumThread'
		ForumThread result = null;
		try{
			result = Factories.getService().getServiceThread()
					.getThreadById(threadId);
		}catch( Exception e ){
			new BeanForumThread().log.error("Unexpected Exception at "
					+ "'getThreadByIdStatic()'");
		}
		return result;
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
	 * Obtiene la url necesaria para que los componentes OutpuLink
	 * que llaman a este metodo enlacen a la pagina 'forum_thread_comment.xhtml'.
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForumThreadComment'.
	 * @param threadId el 'hilo' del foro al cual pertenece el post que se va a
	 * crear o editar en la siguiente vista (forum_thread_comment.xhtml).
	 * @param action accion que se va a realizar en la pagina
	 * 'forum_thread_comment.xhtml'.<br>
	 * Si action=1 la vista mostrara lo necesario para crear un comentario<br>
	 * Si action=2 la vista mostrara lo necesario para editar un comentario<br>
	 * Si action=3 la vista mostrara lo necesario para citar un comentario<br>
	 * @param commentId si se va a editar/citar un comentario, este parametro
	 * sera el id de dicho comentario. Si se va a crear un comentario entonces
	 * sera null.
	 * @return url de la pagina de 'forum_thread_comment.xhtml'
	 * con los parametros necesarios para realizar la accion solicitada
	 */
	public String getOutputLinkToCRUDComment(
			Long threadId, int action, Long commentId){
		// Como este bean es ambito view,
		// la unica forma de conocer cual es la noticia seleccionada
		// al pinchar en un enlace es devolver un parametro en la url.
		String page = BeanUtils.getUrlUserPages()+"forum_thread_comment.xhtml";
		String url = page + "?" + "action=" + action + "&thread="+threadId;
		if( action!=1 || commentId!=null ){ url += "&comment="+commentId; }
		return url;
	}
	
	 /**
	  * Halla el enlace al que se redirige la navegacion, tras usar
	  * el OutputLink que ejecuta el envio de un comentario.
	  * @param threadId  el 'hilo' del foro al cual pertenece el post
	  * que se va a crear
	  * @param commentId el id del comentario que se corrige
	  * @return la URL de la pagina de 'forum_thread_correction.xhtml'
	  * con los parametros necesarios para crear una correccion
	  */
	public String getOutputLinkToCreateCorrection(
			Long threadId, Long commentId){
		return getOutputLinkToCRUDCorrection(threadId, 1, commentId);
	}
	
	/**
	 * Halla el enlace al que se redirige la navegacion, tras usar
	  * el OutputLink que ejecuta la edicion de una correcion.
	 * @param threadId  el 'hilo' del foro al cual pertenece el post
	 * que se va a editar
	 * @param correctionId el id de la correccion que se edita
	 * @return la URL de la pagina de 'forum_thread_correction.xhtml'
	  * con los parametros necesarios para editar una correccion
	 */
	public String getOutputLinkToEditCorrection(
			Long threadId, Long correctionId){
		return getOutputLinkToCRUDCorrection(threadId, 2, correctionId);
	}
	
	/**
	 * Obtiene la url necesaria para que los componentes OutpuLink
	 * que llaman a este metodo enlacen a la pagina
	 * 'forum_thread_correction.xhtml'.
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForumThreadCorrection'.
	 * @param threadId el 'hilo' del foro al cual pertenece el post que se va a
	 * crear o editar en la siguiente vista (forum_thread_correction.xhtml).
	 * @param action accion que se va a realizar en la pagina
	 * 'forum_thread_correction.xhtml'.<br>
	 * Si action=1 la vista mostrara lo necesario para crear correccion<br>
	 * Si action=2 la vista mostrara lo necesario para editar correccion<br>
	 * @param postId id del post que se va a crear/actualizar
	 * @return la URL de la pagina de 'forum_thread_correction.xhtml'
	 * con los parametros necesarios para realizar la accion solicitada
	 */
	private String getOutputLinkToCRUDCorrection(
			Long threadId, int action, Long postId){
		// Como este bean es ambito view,
		// la unica forma de conocer cual es la noticia seleccionada
		// al pinchar en un enlace es devolver un parametro en la url.
		String page = BeanUtils.getUrlUserPages()+"forum_thread_correction.xhtml";
		String url = page + "?" + "action=" + action + "&thread="+threadId;
		if( action==1 ){ url += "&comment="+postId; }
		else if( action==2 ){ url += "&correction="+postId; }
		return url;
	}
	
	/**
	 * Halla el enlace necesario para recargar la vista actual, tras usar
	 * el CommandLink que ejecuta el voto de un hilo del foro. El enlace
	 * incluira un 'ancla' para situar el scroll del navegador en el punto
	 * exacto donde se muetra la noticia votada
	 * @param thread hilo del foro que se puntua
	 * @param anchor el 'ancla' de la URL que identifica al hilo del foro
	 * que puntua
	 * @return la URL de la pagina actual
	 */
	public String getCommandLinkToVoteThread(ForumThread thread,String anchor){
		voteThread(thread);
		// Si han votado la noticia pudo ser desde "forum_thread.xhtml"
		// o desde "forum.xhtml". Asi que se redirige a la pagina actual,
		// sea una u otra:
		return BeanUtilsView.renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	/**
	 * Efectua el voto del hilo indicado del foro
	 * @param thread hilo del foro que se puntua
	 */
	private void voteThread(ForumThread thread){
		Long loggedUserId = beanLogin.getLoggedUser().getId();
		try{
			// No solo edita la informacion del ForumThread, sino que
			// tambien actualiza el ForumThread almacenado en este bean:
			currentThread = Factories.getService().getServiceThread().voteThread(
					loggedUserId, thread);
		}catch( Exception e ){
			log.error("Unexpected Exception at 'voteThread()'");
		}
	}
	
	/**
	 * Comprueba si el usuario dado ha puntuado el hilo indicado del foro
	 * @param thread hilo del foro que se consulta
	 * @return
	 * 'true' si el usuario ya ha puntuado el hilo del foro <br>
 	 * 'false' si el usuario aun no ha puntuado el hilo del foro
	 */
	public boolean threadAlreadyVotedByUser(ForumThread thread){
		boolean result = false;
		if( beanLogin==null || beanLogin.getLoggedUser()==null ) return false;
		Long loggedUserId = beanLogin.getLoggedUser().getId();
		try{
			result = Factories.getService().getServiceThread()
					.threadAlreadyVotedByUser(loggedUserId, thread.getId());
		}catch( Exception e ){
			log.error("Unexpected Exception at 'threadAlreadyVotedByUser()'");
		}
		return result;
	}
	
	/**
	 * Obtiene la participacion (el 'post') del foro segun
	 * el identificador dado
	 * @param postId atributo 'id' del ForumPost que se consulta
	 * @return la participacion del foro obtenida
	 */
	public static ForumPost getPostByIdStatic(Long postId){
		// Este metodo es estatico porque se usa por ejemplo en el filtro
		// 'FilterForumThreadComment' y 'FilterForumThreadCorrection'
		// sin necesidad de una instancia
		ForumPost result = null;
		try{
			result = Factories.getService().getServiceForumPost()
					.getForumPostById(postId);
		}catch( Exception e ){
			new BeanForumThread().log.error("Unexpected Exception at "
					+ "'getPostByIdStatic()'");
		}
		return result;
	}
	
	/**
	 * Borra el estado del Bean sobreescribiendo las propiedades del mismo
	 * con sus valores por defecto.
	 */
	private void clearStatus() {
		offsetPage = null;
		numCommentsTotal = 0;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public ForumThread getCurrentThread() {
		return currentThread;
	}
	public void setCurrentThread(ForumThread currentThread) {
		this.currentThread = currentThread;
	}
	
	public Long getCurrentThreadId() {
		return currentThreadId;
	}
	public void setCurrentThreadId(Long currentThreadId) {
		if( currentThreadId==null ) return;
		this.currentThreadId = currentThreadId;
		currentThread=getThreadById(currentThreadId);
	}
	
	public Integer getOffsetPage() {
		return offsetPage;
	}
	public void setOffsetPage(Integer offsetPage) {
		this.offsetPage = offsetPage;
	}
	
	public Integer getNumCommentsTotal() {
		if( numCommentsTotal==null ) numCommentsTotal=0;
		return numCommentsTotal;
	}
	public void setNumCommentsTotal(int numCommentsTotal) {
		this.numCommentsTotal = numCommentsTotal;
	}
}
