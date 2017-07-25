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
import com.loqua.model.Correction;
import com.loqua.model.ForumThread;
import com.loqua.model.User;
import com.loqua.presentation.bean.requestBean.BeanActionResult;

public class BeanCorrection implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String corrViewParam;
	private String commViewParam;
	/**
	 * Es la correccion que va a crear/editar.
	 * Indirectamente, se usa desde la vista "forum_thread_correction.xhtml",
	 * que inicializa este valor en sus &lt;f:viewParam&gt;
	 */
	private Correction correctionToCRUD;
	/**
	 * Es el comentario asociado a la correccion que se va a crear/editar.
	 * Indirectamente, se usa desde la vista "forum_thread_correction.xhtml",
	 * que inicializa este valor en sus &lt;f:viewParam&gt;
	 */
	private Comment commentToCorrect;
	
	/**
	 * Es la correccion que va a desaconsejar.
	 * Se usa desde la vista "forum_thread_list_comments.xhtml",
	 * donde los botones para desaconsejar correcciones tienen un actionLitener
	 * donde inicializan este valor
	 */
	private Correction correctionOfList;
	private int actionType;
	private String plainTextCorrection;
	private String textCodeCorrection;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		correctionOfList = new Correction();
		initBeanLogin();
	}
	
	public void onLoad() {
		// Este metodo se usa, por ejemplo, desde "forum_thread_correction.xhtml"
		// en la seccion "<f:metadata>"
		if( actionType==2 ){
			// Si se desea editar una correccion, inicializamos estos valores
			// para que el componente summernote muestre el texto que se edita
			setCommentToCorrectById( correctionToCRUD.getComment().getId() );
			textCodeCorrection=correctionToCRUD.getTextHtml();
			plainTextCorrection=correctionToCRUD.getText();
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
	
	public Correction getApprovedCorrectionByComment(Comment comment){
		Correction result = null;
		try{
			result = Factories.getService().getServiceCorrection()
					.getApprovedCorrectionByComment( comment.getId() );
		}catch( Exception e ){
			//TODO
		}
		return result;
	}
	
	public List<Correction> getNotApprovedCorrectionsByComment(Comment comment){
		List<Correction> result = new ArrayList<Correction>();
		try{
			result = Factories.getService().getServiceCorrection()
					.getNotApprovedCorrectionsByComment( comment.getId() );
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
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
	
	public boolean getUserAgreeCorrection(User user, Correction correction){
		boolean result = false;
		try{
			result = Factories.getService().getServiceCorrection()
					.getUserAgreeCorrection(user.getId(), correction.getId());
		}catch( Exception e ){
			//TODO Log
		}
		return result;
	}
	
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
			//TODO Log
		}
		return result;
	}
	
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
	
	public boolean getUserDisagreeCorrection(User user, Correction correction){
		boolean result = false;
		try{
			result = Factories.getService().getServiceCorrection()
					.getUserDisagreeCorrection(user.getId(), correction.getId());
		}catch( Exception e ){
			//TODO Log
		}
		return result;
	}
	
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
			//TODO Log
		}
		return result;
	}
	
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
	
	public String getCommandLinkToRecommendCorrection(
			Correction corr, String anchor){
		recommendCorrection(corr);
		return BeanUtilsView.renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	private void recommendCorrection(Correction corr){
		User user = beanLogin.getLoggedUser();
		try {
			Factories.getService().getServiceCorrection()
				.recommendCorrection(user.getId(), corr);
		} catch (Exception e) {
			// TODO Log
		}
	}
	
	public String getCommandLinkToDissuadeCorrection(
			Correction corr, String anchor){
		dissuadeCorrection(corr);
		return BeanUtilsView.renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	private void dissuadeCorrection(Correction corr){
		User user = beanLogin.getLoggedUser();
		try {
			Factories.getService().getServiceCorrection()
				.dissuadeCorrection(user.getId(), corr.getId());
		} catch (Exception e) {
			// TODO Log
		}
	}
	
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
	
	private String createCorrection(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		Factories.getService().getServiceCorrection().sendCorrection(
				commentToCorrect, plainTextCorrection, textCodeCorrection,
				beanLogin.getLoggedUser());
		return BeanComment.getCommandLinkToPostStatic(commentToCorrect);
	}
	
	private String updateTextCorrection(ForumThread thread)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		Factories.getService().getServiceCorrection().updateTextCorrection(
				correctionToCRUD, plainTextCorrection, textCodeCorrection);
		return BeanComment.getCommandLinkToPostStatic(commentToCorrect);
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
	
	public String getCorrViewParam() {
		return corrViewParam;
	}
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
	public void setCorrectionToCRUDById(Long correctionToCRUDId) {
		this.correctionToCRUD = (Correction) BeanForumThread.getPostByIdStatic(
				correctionToCRUDId);
	}

	public String getCommViewParam() {
		return commViewParam;
	}
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
	public void setCommentToCorrectById(Long commentToCorrectId) {
		this.commentToCorrect = (Comment) BeanForumThread.getPostByIdStatic(
				commentToCorrectId);
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
	}
	
	public String getPlainTextCorrection() {
		return plainTextCorrection;
	}
	public void setPlainTextCorrection(String plainText) {
		this.plainTextCorrection = plainText;
	}
	
	public String getTextCodeCorrection() {
		return textCodeCorrection;
	}
	public void setTextCodeCorrection(String textCode) {
		this.textCodeCorrection = textCode;
	}
}
