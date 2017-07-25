package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsActionLimits;
import com.loqua.presentation.bean.requestBean.BeanActionResult;

public class BeanUserRemove implements Serializable{
	
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
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO ELIMINE SU CUENTA DE USUARIO
	// // // // // // // // // // // // // // // // // // // //
	
	public String generateEmailForRemoveUser(
			BeanUserView beanUserView, BeanActionResult beanActionResult) {
		String result = null;
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		User userToRemove = beanUserView.getUser();
		try {
			sendEmailForRemoveUser(userToRemove);
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			result = "errorUnexpected";
			// TODO log
		}
		beanActionResult.setFinish(true);
		return result;
	}
	
	private void sendEmailForRemoveUser(User userToRemove) 
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
		content.add(0, bundle.getString("mailContentRemoveAccount01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentRemoveAccount02")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/removeAccount_confirm.xhtml?confirm=");
		
		// Enviar el correo:
		Factories.getService().getServiceUser()
				.sendEmailForRemoveUser(userToRemove, content, subject);
	}
	
	// METODOS PARA CONFIRMAR LA ELIMINACION DE LA CUENTA DE USUARIO
	
	public String confirmRemoveAccount(BeanActionResult beanActionResult){
		String result = null;
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try{
			User userToRemove = getUserByUrlConfirm(beanActionResult);
			if( userToRemove!=null ){
				if( userToRemove.getRemoved()==true ){
					beanActionResult.setMsgActionResult(
							"errorAlreadyPerformed");
				}else{
					updateUser( userToRemove, beanActionResult );
					if( beanLogin.getLoggedUser()!=null 
							&& beanLogin.getLoggedUser().equals(userToRemove) ){
						beanLogin.close();
					}
					beanActionResult.setMsgActionResult(
							"descriptionRemoveAccountConfirm");
					beanActionResult.setSuccess(true);
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (Exception e) {
			result = "errorUnexpected";
			// TODO log
		}
		}
		beanActionResult.setFinish(true);
		return result;
	}
	
	private User getUserByUrlConfirm(BeanActionResult beanActionResult) {
		User userToActivate = null;
		userToActivate = Factories.getService().getServiceUser()
				.getUserByUrlConfirm(urlConfirm);
		if( userToActivate==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}
		return userToActivate;
	}
	
	private void updateUser(User user, BeanActionResult beanActionResult)
			throws Exception{
		try{
			user.removeUserData();
			deleteUserAccount(user);
			Factories.getService().getServiceUser().updateDataByUser(user);
		} catch (Exception e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			// TODO log
		}
	}
	
	private void deleteUserAccount(User user) throws EntityNotFoundException {
		Factories.getService().getServiceUser().deleteUserAccount(user);
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
