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

public class BeanForumThread implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private ForumThread currentThread;
	private Long currentThreadId;
	private Integer offsetPage;
	private Integer numCommentsPerPage;
	private Integer numCommentsTotal;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanPaginationBar}")
	private BeanPaginationBar beanPaginationBar;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanPaginationBar();
		numCommentsPerPage = BeanSettingsForumPage.getNumCommentsPerPageStatic();
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
	
	private void initBeanPaginationBar() {
		// Buscamos el BeanPaginationBar en la sesion.
		beanPaginationBar = null;
		beanPaginationBar = (BeanPaginationBar)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanPaginationBar");
		
		// si no existe lo creamos e inicializamos:
		if (beanPaginationBar == null) { 
			beanPaginationBar = new BeanPaginationBar();
			beanPaginationBar.init();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanPaginationBar", beanPaginationBar);
		}
	}
	
	@PreDestroy
	public void end(){
		clearStatus();
	}
	
	// // // //
	// METODOS
	// // // //
	
	public static void incrementCountVisitsStatic(ForumThread thread){
		// Este metodo se usa por ejemplo desde el "FilterForumThread"
		try{
			Factories.getService().getServiceThread()
					.incrementCountVisits( thread );
		}catch( Exception e ){
			//TODO Log
		}
	}
	
	public List<Comment> getListCommentsByThread(){
		List<Comment> result = new ArrayList<Comment>();
		try{
			result=Factories.getService().getServiceComment()
					.getCommentsByThread(
						currentThread.getId(), offsetPage, numCommentsPerPage);
			numCommentsTotal = Factories.getService().getServiceComment()
					.getNumCommentsByThread(currentThread.getId());
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
	public List<Comment> getListCommentsByThreadReverseOrder(){
		// este metodo solo se usa desde 'forum_thread_comment.xhtml'
		List<Comment> result = new ArrayList<Comment>();
		try{
			result = Factories.getService().getServiceComment()
					.getCommentsByThreadReverseOrder(
							currentThread.getId(), 0, numCommentsPerPage);
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
	/**
	 * Obtiene la url necesaria para que los componentes 'h:outpuLink'
	 * que llaman a este metodo enlacen a la pagina 'forum_thread.xhtml'.
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForumThread'.
	 * @param threadId el 'hilo' del foro que se va a consultar
	 * en la siguiente vista (forum_thread.xhtml).
	 * @param offset pagina del 'hilo' que se va a consultar.
	 * @return url de la pagina de 'forum_thread.xhtml'.
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
	
	public ForumThread getThreadById(Long threadId){
		// Este metodo se usa por ejemplo en el snippet 'profile_list_publications'
		return getThreadByIdStatic(threadId);
	}
	
	public static ForumThread getThreadByIdStatic(Long threadId){
		// Este metodo se usa por ejemplo en el filtro 'FilterForumThread'
		ForumThread result = null;
		try{
			result = Factories.getService().getServiceThread()
					.getThreadById(threadId);
		}catch( Exception e ){
			//TODO Log
		}
		return result;
	}
	
	private String getLinkToThread(){
		String url = BeanUtils.getUrlUserPages() + "forum_thread.xhtml";
		return url;
	}
	
	/**
	 * Obtiene la url necesaria para que los componentes 'h:outpuLink'
	 * que llaman a este metodo enlacen a la pagina 'forum_thread_comment.xhtml'.
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForumThreadComment'.
	 * @param threadId el 'hilo' del foro al cual pertenece el post que se va a
	 * crear o editar en la siguiente vista (forum_thread_comment.xhtml).
	 * @param action accion que se va a realizar en la pagina
	 * 'forum_thread_comment.xhtml'.<br/>
	 * Si action=1 la vista mostrara lo necesario para crear un comentario<br/>
	 * Si action=2 la vista mostrara lo necesario para editar un comentario<br/>
	 * Si action=3 la vista mostrara lo necesario para citar un comentario<br/>
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
	
	public String getOutputLinkToCreateCorrection(
			Long threadId, Long commentId){
		return getOutputLinkToCRUDCorrection(threadId, 1, commentId);
	}
	
	public String getOutputLinkToEditCorrection(
			Long threadId, Long correctionId){
		return getOutputLinkToCRUDCorrection(threadId, 2, correctionId);
	}
	
	/**
	 * Obtiene la url necesaria para que los componentes 'h:outpuLink'
	 * que llaman a este metodo enlacen a la pagina
	 * 'forum_thread_correction.xhtml'.
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForumThreadCorrection'.
	 * @param threadId el 'hilo' del foro al cual pertenece el post que se va a
	 * crear o editar en la siguiente vista (forum_thread_correction.xhtml).
	 * @param action accion que se va a realizar en la pagina
	 * 'forum_thread_correction.xhtml'.<br/>
	 * Si action=1 la vista mostrara lo necesario para crear correccion<br/>
	 * Si action=2 la vista mostrara lo necesario para editar correccion<br/>
	 * @param postId id del post que se va a crear/actualizar
	 * @return url de la pagina de 'forum_thread_correction.xhtml'
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
	
	public String getCommandLinkToVoteThread(ForumThread thread,String anchor){
		voteThread(thread);
		// Si han votado la noticia pudo ser desde "forum_thread.xhtml"
		// o desde "forum.xhtml". Asi que se redirige a la pagina actual,
		// sea una u otra:
		return BeanUtilsView.renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	private void voteThread(ForumThread thread){
		Long loggedUserId = beanLogin.getLoggedUser().getId();
		try{
			// No solo edita en bdd la informacion del ForumThread, sino que
			// tambien actualiza el ForumThread almacenado en este bean:
			currentThread = Factories.getService().getServiceThread().voteThread(
					loggedUserId, thread);
		}catch( Exception e ){
			//TODO Log
		}
	}
	
	public boolean threadAlreadyVotedByUser(ForumThread thread){
		boolean result = false;
		Long loggedUserId = beanLogin.getLoggedUser().getId();
		try{
			result = Factories.getService().getServiceThread()
					.threadAlreadyVotedByUser(loggedUserId, thread.getId());
		}catch( Exception e ){
			//TODO Log
		}
		return result;
	}
	
	public static ForumPost getPostByIdStatic(Long postId){
		// Este metodo es estatico porque se usa por ejemplo en el filtro
		// 'FilterForumThreadComment' y 'FilterForumThreadCorrection'
		// sin necesidad de una instancia
		ForumPost result = null;
		try{
			result = Factories.getService().getServiceForumPost()
					.getForumPostById(postId);
		}catch( Exception e ){
			//TODO Log
		}
		return result;
	}
	
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
