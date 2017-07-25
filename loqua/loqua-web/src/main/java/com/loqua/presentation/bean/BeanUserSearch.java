package com.loqua.presentation.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.bean.requestBean.BeanActionResult;

public class BeanUserSearch implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final String REGULAR_EXPRESSION_EMAIL = 
			"[0-9a-zA-Z]+@[0-9a-zA-Z]+\\.[0-9a-zA-Z]+";
	
	private String stringToSearch;
	private User user;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}") 
	private BeanLogin beanLogin;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanUserData}") 
	private BeanUserData beanUserData;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanUser();
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
	
	private void initBeanUser() {
		// Buscamos el beanUserData en la sesion.
		beanUserData = null;
		beanUserData = (BeanUserData)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanUserData");
		// si no existe lo creamos e inicializamos:
		if (beanUserData == null) { 
			beanUserData = new BeanUserData();
			beanUserData.init();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanUserData", beanUserData);
		}
	}

	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	public String searchUser(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String result = "";
		User loggedUser = beanLogin.getLoggedUser();
		if( loggedUser==null ){
			beanActionResult.setMsgActionResult("errorLoginRequired");
			beanActionResult.setFinish(true);
			return result;
		}
		try{
			if( introducedWellFormedEmail(stringToSearch) ){
				user = Factories.getService().getServiceUser()
						.getUserNotRemovedByEmail(stringToSearch);
			}else{
				user = Factories.getService().getServiceUser()
						.getUserNotRemovedByNick(stringToSearch);
			}
			if( user != null && 
					(user.getPrivacityData().getAppearingInSearcher()
					|| loggedUser.getRole().equals(User.ADMINISTRATOR) ) ){
				beanActionResult.setSuccess(false);
				result = "profile_user.xhtml?faces-redirect=true"
						+ "user="+user.getId();
			}else{
				beanActionResult.setMsgActionResult(
						"descriptionSearchUserNotFound");
			}
		}catch( Exception e ){
			// TODO Log
			result = "errorUnexpected";
		}
		beanActionResult.setFinish(true);
		return result;
	}
	
	private boolean introducedWellFormedEmail(String stringToSearch) {
		boolean result = true;
		// Comprobar formato de email:
		if( ! stringToSearch.matches(REGULAR_EXPRESSION_EMAIL) ){
			return false;
		}
		return result;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //

	public BeanLogin getBeanLogin() {
		return beanLogin;
	}
	public void setBeanLogin( BeanLogin bLogin ) {
		beanLogin = bLogin;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getStringToSearch() {
		return stringToSearch;
	}
	public void setStringToSearch(String stringToSearch) {
		this.stringToSearch = stringToSearch;
	}
}
