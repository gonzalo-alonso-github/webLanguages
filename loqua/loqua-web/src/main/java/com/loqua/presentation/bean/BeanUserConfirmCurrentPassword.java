package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.User;

// Este bean se usa en la vista de /snippets/profile/form_CurrentPassword.xhtml,
// y tambien en /snippets/profile/modalwindows.xhtml, que incluye a dicho snippet,
// y tambien en /pages/registeredUser/profile_edit.xhtml, que incuye a este
public class BeanUserConfirmCurrentPassword implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String password;
	private boolean currentPasswordConfirmed;
	private String msgActionResult;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		currentPasswordConfirmed = false;
	}
	
	@PreDestroy
	public void end(){}
	
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
	
	// // // //
	// METODOS
	// // // //
	
	public String confirmCurrentPassword(){
		String result = null;
		User userToEdit = null;
		try {
			userToEdit = Factories.getService().getServiceUser()
				.getUserToLogin(beanLogin.getLoggedUser().getEmail(), password);
		} catch (Exception e) {
			// TODO Log
			result = "errorUnexpected";
		}
		if( userToEdit==null ){
			setMsgActionResult("errorConfirmCurrentPassword");
		}else{
			msgActionResult=null;
			currentPasswordConfirmed = true;
		}
		return result;
	}
	
	public void clearStatus(){
		msgActionResult=null;
		currentPasswordConfirmed = false;
		password = "";
	}
	
	// // // // // // // // // // // // // //
	// GETTERS & SETTERS CON LOGICA DE NEGOCIO
	// // // // // // // // // // // // // //
	
	public String getMsgActionResult() {
		String message = msgActionResult;
		msgActionResult = null;
		return message;
	}
	public void setMsgActionResult(String message) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    ResourceBundle bundle = facesContext.getApplication()
	    		.getResourceBundle(facesContext, "msgs");
	    this.msgActionResult = bundle.getString(message);
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean getCurrentPasswordConfirmed() {
		return currentPasswordConfirmed;
	}
	public void setCurrentPasswordConfirmed(boolean currentPasswordConfirmed) {
	    this.currentPasswordConfirmed = currentPasswordConfirmed;
	}
}
