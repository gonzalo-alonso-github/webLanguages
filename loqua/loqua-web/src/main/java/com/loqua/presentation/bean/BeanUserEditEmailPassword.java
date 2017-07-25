package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsActionLimits;
import com.loqua.presentation.bean.requestBean.BeanActionResult;

public class BeanUserEditEmailPassword implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String urlConfirm;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanSettingsActionLimits}")
	private BeanSettingsActionLimits beanSettingsActionLimits;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanSettingsActionLimits();
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
	
	private void initBeanSettingsActionLimits() {
		// Buscamos el BeanSettingsActionLimits en la sesion.
		beanSettingsActionLimits = null;
		beanSettingsActionLimits = (BeanSettingsActionLimits)
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("beanSettingsActionLimits");
		// si no existe lo creamos e inicializamos:
		if (beanSettingsActionLimits == null) { 
			beanSettingsActionLimits = new BeanSettingsActionLimits();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap()
					.put("beanSettingsActionLimits", beanSettingsActionLimits);
		}
	}

	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU EMAIL
	// // // // // // // // // // // // // // // //
	
	// METODOS PARA ENVIAR EL EMAIL DE CONFIMACION AL EMAIL ORIGINAL
	
	public String generateEmailForEditEmail(
			BeanUserView beanUserView, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		// Obtener los datos originales del usuario desde el beanLogin
		// (porque el email original sera guardado en la tabla ChangeEmail)
		User updatedUser = beanLogin.getLoggedUser();
		// Ahora 'updatedUser.getEmail()' almacena el email previo al cambio,
		// mientras que 'beanUserView.getUser().getEmail()' almacena el nuevo email.
		// Ambos valores seran utilizados en la logica de negocio,
		// para guardar el nuevo ChangeEmail en la tabla
		try {
			String resultSendEmail = sendEmailForEditEmail(
					updatedUser, beanUserView.getUser().getEmail());
			if( ! resultSendEmail.equals("noError") ){
				Map<String, Integer> mapActionsLimits = 
						beanSettingsActionLimits.getMapActionLimitsProperties();
				beanActionResult.setMsgActionResult(resultSendEmail);
				beanActionResult.setMsgActionResultExact(
						beanActionResult.getMsgActionResult().replaceFirst("\\?1",
						mapActionsLimits.get(resultSendEmail).toString()) );
			}else{
				beanActionResult.setSuccess(true);
			}
		} catch (Exception e) {
			action = "errorUnexpected";
			// TODO log
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	private String sendEmailForEditEmail(User updatedUser, String newEmail)
			throws EntityAlreadyFoundException {
		List<String> content = new ArrayList<String>();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication().getResourceBundle(
				facesContext, "msgs");
		// Asunto del correo:
		String subject = bundle.getString("mailSubjectGeneric");
		
		// Contenido del correo:
		HttpServletRequest req = (HttpServletRequest)
				facesContext.getExternalContext().getRequest();
		String uri = req.getRequestURL().toString();
		String url = uri.substring(0, uri.length()-req.getRequestURI().length())
				+ req.getContextPath() + "/";
		// Aqui bastaria que 'content' sea un simple String y no List<String>,
		// pero para facilitar la uniformidad del codigo, queria que este metodo
		// se parezca al de BeanRestorePassword.sendEmailForPasswordRestore
		content.add(0, bundle.getString("mailContentEditEmail01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentEditEmail02")
				+ ":" +"\n\t" + newEmail
				+ "\n\n" + bundle.getString("mailContentEditEmail03")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/emailChange_first_confirm.xhtml?confirm=");
		// Enviar el correo:
		String result = Factories.getService().getServiceUserAccessDataChange()
				.sendEmailForEditEmail(updatedUser, newEmail, content, subject,
						beanSettingsActionLimits.getMapActionLimitsProperties());
		return result;
	}
	
	// METODOS PARA CONFIRMAR EL EMAIL ORIGINAL EDITADO
	
	public String confirmEmailChangeFirstStep(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try{
			ChangeEmail emailChange = getEmailChangeByUrlConfirm();
			if( emailChange!=null ){
				if( emailChange.getConfirmedPreviousEmail()==true ){
					beanActionResult.setMsgActionResult(
							"errorAlreadyPerformed");
				}else{
					setPreviousEmailToConfirmed(emailChange, beanActionResult);
					sendEmailForEditEmailSecondStep(emailChange);
					beanActionResult.setSuccess(true);
					beanActionResult.setMsgActionResult(
							"descriptionEditEmalFirstConfirm");
					beanActionResult.setMsgActionResultExact(
							beanActionResult.getMsgActionResult()
							.replaceFirst("\\?1",emailChange.getNewEmail()) );
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (Exception e) {
			action = "errorUnexpected";
			// TODO log
		}
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	private void sendEmailForEditEmailSecondStep(ChangeEmail emailChange)
			throws EntityNotFoundException {
		List<String> content = new ArrayList<String>();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication().getResourceBundle(
				facesContext, "msgs");
		
		// Asunto del correo:
		String subject = bundle.getString("mailSubjectGeneric");
		
		// Contenido del correo:
		HttpServletRequest req = (HttpServletRequest)
				facesContext.getExternalContext().getRequest();
		String uri = req.getRequestURL().toString();
		String url = uri.substring(0, uri.length()-req.getRequestURI().length())
				+ req.getContextPath() + "/";
		// Aqui bastaria que 'content' sea un simple String y no List<String>,
		// pero para facilitar la uniformidad del codigo, queria que este metodo
		// se parezca al de BeanRestorePassword.sendEmailForPasswordRestore
		content.add(0, bundle.getString("mailContentEditEmailSecondStep01")
			+ "\n\n" + bundle.getString("mailContentAdviceRemove")
			+ "\n\n" + bundle.getString("mailContentEditEmailSecondStep02")
					.replaceFirst("\\?1", emailChange.getPreviousEmail())
			+ "\n\n" + bundle.getString("mailContentEditEmailSecondStep03")
			+ ":" +"\n\t" + url
			+ "pages/confirmationPages/emailChange_second_confirm.xhtml?confirm=");
		
		// Enviar el correo:
		Factories.getService().getServiceUserAccessDataChange()
				.sendEmailForEditEmailSecondStep(emailChange, content, subject);
	}
	
	// METODOS PARA CONFIRMAR EL EMAIL NUEVO EDITADO
	
	public String confirmEmailChangeSecondStep(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try{
			ChangeEmail emailChange = getEmailChangeByUrlConfirm();
			if( emailChange!=null ){
				if( emailChange.getConfirmedNewEmail()==true ){
					beanActionResult
						.setMsgActionResult("errorAlreadyPerformed");
				}else{
					setNewEmailToConfirmed(emailChange, beanActionResult);
					User updatedUser = emailChange.getUser();
					updatedUser.setEmail( emailChange.getNewEmail() );
					updateUser( updatedUser, beanActionResult );
					beanLogin.setLoggedUser( updatedUser );
					beanActionResult.setMsgActionResult(
							"descriptionEditEmalSecondConfirm");
					beanActionResult.setMsgActionResultExact(
							beanActionResult.getMsgActionResult()
							.replaceFirst("\\?1",emailChange.getNewEmail()) );
					beanActionResult.setSuccess(true);
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (Exception e) {
			action = "errorUnexpected";
			// TODO log
		}
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	private ChangeEmail getEmailChangeByUrlConfirm() {
		ChangeEmail objectChangeEmail = null;
		objectChangeEmail = Factories.getService().getServiceUserAccessDataChange()
				.getEmailChangeByUrlConfirm(urlConfirm);
		return objectChangeEmail;
	}
	
	private void setPreviousEmailToConfirmed(
			ChangeEmail emailChange, BeanActionResult beanActionResult){
		emailChange.setConfirmedPreviousEmail(true);
		try{
			Factories.getService().getServiceUserAccessDataChange()
				.updateEmailChange(emailChange);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			// TODO log
		}
	}
	
	private void setNewEmailToConfirmed(
			ChangeEmail emailChange, BeanActionResult beanActionResult){
		emailChange.setConfirmedNewEmail(true);
		try{
			Factories.getService().getServiceUserAccessDataChange()
				.updateEmailChange(emailChange);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			// TODO log
		}
	}
	
	private void updateUser(User user, BeanActionResult beanActionResult) {
		try{
			Factories.getService().getServiceUser()
				.updateAllDataByUser(user, false);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			// TODO log
		}
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU CONTRASENA
	// // // // // // // // // // // // // // // // // //
	
	public String generateEmailForEditPassword(
			BeanUserView beanUserView, BeanActionResult beanActionResult) {
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		User updatedUser = beanLogin.getLoggedUser();
		try {
			String resultSendEmail = sendEmailForEditPassword(
					updatedUser, beanUserView.getUser().getPassword());
			if( ! resultSendEmail.equals("noError") ){
				Map<String, Integer> mapActionsLimits = 
					beanSettingsActionLimits.getMapActionLimitsProperties();
				beanActionResult.setMsgActionResult(resultSendEmail);
				beanActionResult.setMsgActionResultExact(
						beanActionResult.getMsgActionResult().replaceFirst("\\?1",
					mapActionsLimits.get(resultSendEmail).toString()) );
			}else{
				beanActionResult.setSuccess(true);
			}
		} catch (Exception e) {
			action = "errorUnexpected";
			// TODO log
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	private String sendEmailForEditPassword(User updatedUser, String newPass) 
			throws EntityAlreadyFoundException {
		List<String> content = new ArrayList<String>();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication().getResourceBundle(
				facesContext, "msgs");
		
		// Asunto del correo:
		String subject = bundle.getString("mailSubjectGeneric");
		
		// Contenido del correo:
		HttpServletRequest req = (HttpServletRequest)
				facesContext.getExternalContext().getRequest();
		String uri = req.getRequestURL().toString();
		String url = uri.substring(0, uri.length()-req.getRequestURI().length())
				+ req.getContextPath() + "/";
		content.add(0, bundle.getString("mailContentEditPassword01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentEditPassword02")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/passwordChange_confirm.xhtml?confirm=");
		content.add(1, "\n\n" + bundle.getString("mailContentEditPassword03")
				+ ":" + "\n\t");
		// Enviar el correo:
		String result = Factories.getService()
				.getServiceUserAccessDataChange()
				.sendEmailForEditPassword(updatedUser, newPass, content, subject,
						beanSettingsActionLimits.getMapActionLimitsProperties());
		return result;
	}
	
	// METODOS PARA CONFIRMAR LA CONTRASENA NUEVA EDITADA
	
	public String confirmPasswordChange(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try{
			ChangePassword passwordChange = getPasswordChangeByUrlConfirm();
			if( passwordChange!=null ){
				if( passwordChange.getConfirmed()==true ){
					beanActionResult.setMsgActionResult(
							"errorAlreadyPerformed");
				}else{
					setNewPasswordToConfirmed(passwordChange);
					User updatedUser = passwordChange.getUser();
					updatedUser.setPassword(passwordChange.getPasswordGenerated());
					updateUser( updatedUser, beanActionResult );
					beanLogin.setLoggedUser( updatedUser );
					beanActionResult.setMsgActionResult(
							"descriptionEditPasswordConfirm");
					beanActionResult.setSuccess(true);
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			// TODO log
		} catch (Exception e) {
			action = "errorUnexpected";
			// TODO log
		}
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	private ChangePassword getPasswordChangeByUrlConfirm() {
		ChangePassword passwordRestore = null;
		passwordRestore = Factories.getService().getServiceUserAccessDataChange()
				.getPasswordChangeByUrlConfirm(urlConfirm, ChangePassword.EDIT);
		return passwordRestore;
	}
	
	private void setNewPasswordToConfirmed(ChangePassword passwordRestore)
			throws EntityNotFoundException {
		passwordRestore.setConfirmed(true);
		Factories.getService().getServiceUserAccessDataChange()
			.updatePasswordChange(passwordRestore);
	}

	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public String getUrlConfirm() {
		return urlConfirm;
	}
	public void setUrlConfirm(String urlConfirm) {
	    this.urlConfirm = urlConfirm;
	}
}
