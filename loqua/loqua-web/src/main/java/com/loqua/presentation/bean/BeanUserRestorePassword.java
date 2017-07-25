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
import com.loqua.model.ChangePassword;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsActionLimits;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.validator.ValidatorEmailExists;

public class BeanUserRestorePassword implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private User userFound;
	private String email;
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
		userFound = null;
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
	
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA ENVIAR EL EMAIL DE RESTAURACION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // //
	
	public String generateEmailForPasswordRestore(
			BeanActionResult beanActionResult) {
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		userFound = ValidatorEmailExists.getUser();
		try {
			String resultSendEmail = sendEmailForPasswordRestore();
			if( ! resultSendEmail.equals("noError") ){
				Map<String, Integer> mapActionsLimits = 
						beanSettingsActionLimits.getMapActionLimitsProperties();
				beanActionResult.setMsgActionResult(resultSendEmail);
				beanActionResult.setMsgActionResultExact(
						beanActionResult.getMsgActionResult().replaceFirst(
						"\\?1",mapActionsLimits.get(resultSendEmail).toString()));				
			}else{
				beanActionResult.setSuccess(true);
			}
		} catch (Exception e) {
			action = "errorUnexpectedAnonymous";
			// TODO log
		}
		userFound = null;
		beanActionResult.setFinish(true);
		return action;
	}
	
	private String sendEmailForPasswordRestore() 
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
		content.add(0, bundle.getString("mailContentPasswordRestore01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentPasswordRestore02")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/password_restore_confirm.xhtml?confirm=");
		content.add(1, "\n\n" + bundle.getString("mailContentPasswordRestore03")
				+ ":" + "\n\t");
		
		// Enviar el correo:
		String result = Factories.getService()
				.getServiceUserAccessDataChange()
				.sendEmailForPasswordRestore(userFound, content, subject,
						beanSettingsActionLimits.getMapActionLimitsProperties());
		return result;
	}
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA CONFIRMAR LA RESTAURACION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // //	
	
	public String confirmRestorePassword(BeanActionResult beanActionResult) {
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try {
			ChangePassword passRestore = getPasswordChangeByUrlConfirm();
			if( passRestore!=null ){
				if( passRestore.getConfirmed()==true ){
					beanActionResult.setMsgActionResult(
							"errorAlreadyPerformed");
				}else{
					setPasswordRestoreToConfirmed(passRestore,beanActionResult);
					User userToUpdate = passRestore.getUser();
					userToUpdate.setPassword(
							passRestore.getPasswordGenerated());
					userToUpdate.setLoginFails(0);
					updateUser(userToUpdate, beanActionResult);
					beanActionResult.setSuccess(true);
					beanActionResult.setMsgActionResult(
							"descriptionPasswordRestoreConfirm");
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (Exception e) {
			action = "errorUnexpectedAnonymous";
			// TODO log
		}
		}
		beanActionResult.setFinish(true);
		return action;
	}

	private ChangePassword getPasswordChangeByUrlConfirm() {
		ChangePassword passwordRestore = null;
		passwordRestore = Factories.getService().getServiceUserAccessDataChange()
				.getPasswordChangeByUrlConfirm(urlConfirm, ChangePassword.RESTORE);
		return passwordRestore;
	}
	
	private void setPasswordRestoreToConfirmed(
			ChangePassword passwordRestore, BeanActionResult beanActionResult){
		passwordRestore.setConfirmed(true);
		try{
			Factories.getService().getServiceUserAccessDataChange()
				.updatePasswordChange(passwordRestore);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			// TODO log
		}
	}
	
	private void updateUser(User user, BeanActionResult beanActionResult){
		try{
			Factories.getService().getServiceUser()
				.updateAllDataByUser(user, false);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			// TODO log
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
	    this.email = email;
	}
	public String getUrlConfirm() {
		return urlConfirm;
	}
	public void setUrlConfirm(String urlConfirm) {
	    this.urlConfirm = urlConfirm;
	}
}
