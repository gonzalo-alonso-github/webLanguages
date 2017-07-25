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

public class BeanComment implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String commViewParam;
	/**
	 * Es el comentario que va a crear/editar/citar.
	 * Indirectamente, se usa desde la vista "forum_thread_comment.xhtml",
	 * que inicializa este valor en sus &lt;f:viewParam&gt;
	 */
	private Comment commentToCRUD;
	
	private static Integer numCommentsPerPage
		= BeanSettingsForumPage.getNumCommentsPerPageStatic();
	private int actionType;
	private String plainTextComment;
	private String textCodeComment;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
	}
	
	public void onLoad() {
		// Este metodo se usa, por ejemplo, desde "forum_thread_comment.xhtml"
		// en la seccion "<f:metadata>"
		if( actionType==2 ){
			// Si se desea editar un comentario, inicializamos estos valores
			// para que el componente summernote muestre el texto que se edita
			textCodeComment=commentToCRUD.getTextHtml();
			plainTextComment=commentToCRUD.getText();
		}
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
	
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Accede a la base de datos para obtener el comentario citado por un
	 * comentario dado, consultando los objetos CommentQuoteTo cuya propiedad
	 * 'actorComment' referencia al comentario dado.
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
			//TODO Log
		}
		return result;
	}
	
	public String getOutputLinkToPost(Long forumPostId){
		ForumPost post = BeanForumThread.getPostByIdStatic(forumPostId);
		String urlParameters = getLinkParametersToForumPost(post);
		if(urlParameters==null) return null;
		return getLinkToThread() + "?" + urlParameters;
	}
	
	private String getLinkToThread(){
		String url = BeanUtils.getUrlUserPages() + "forum_thread.xhtml";
		return url;
	}
	
	public static String getCommandLinkToPostStatic(ForumPost post){
		// Este metodo es estatico para poder usarlo desde BeanCorrection
		// sin necesidad de una instancia
		String urlParameters = getLinkParametersToForumPost(post);
		if( urlParameters==null ) return null;
		String url = "forum_thread.xhtml?faces-redirect=true" + urlParameters;
		return url;
	}
	
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
	
	public String getCommnentTextByPost(Long forumPostId){
		ForumPost post = BeanForumThread.getPostByIdStatic(forumPostId);
		if(post==null) return null; // el post se habia eliminado
		return post.getAsCommnent().getText();
	}
	
	public String getCommandLinkToVoteComment(Comment comment, Integer page){
		voteComment(comment);
		String anchor = "#referenceTo_comment" + comment.getPositionIndex();
		return BeanUtilsView.renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	private void voteComment(Comment comment){
		Long loggedUserId = beanLogin.getLoggedUser().getId();
		try{
			Factories.getService().getServiceComment().voteComment(
					loggedUserId, comment);
		}catch( Exception e ){
			//TODO Log
		}
	}
	
	public boolean commentAlreadyVotedByUser(Comment comment){
		boolean result = false;
		Long loggedUserId = beanLogin.getLoggedUser().getId();
		try{
			result = Factories.getService().getServiceComment()
					.commentAlreadyVotedByUser(loggedUserId, comment.getId());
		}catch( Exception e ){
			//TODO Log
		}
		return result;
	}
	
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
	
	private String createComment(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		Comment comm = Factories.getService().getServiceComment().sendComment(
				thread, plainTextComment, textCodeComment,
				beanLogin.getLoggedUser());
		return getCommandLinkToPostStatic(comm);
	}
	
	private String updateTextComment(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		Factories.getService().getServiceComment().updateTextComment(
				commentToCRUD, plainTextComment, textCodeComment);
		return getCommandLinkToPostStatic(commentToCRUD);
	}
	
	private String quoteComment(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		Comment comm = Factories.getService().getServiceComment().quoteComment(
				commentToCRUD, plainTextComment, textCodeComment,
				beanLogin.getLoggedUser());
		return getCommandLinkToPostStatic(comm);
	}
	
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
	
	public String getCommViewParam() {
		return commViewParam;
	}
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
	public void setCommentToCRUDById(Long currentCommentId) {
		if( currentCommentId==null ) return;
		commentToCRUD = 
				(Comment) BeanForumThread.getPostByIdStatic(currentCommentId);
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
	
	public String getTextCodeComment() {
		return textCodeComment;
	}
	public void setTextCodeComment(String textCode) {
		this.textCodeComment = textCode;
	}
}
