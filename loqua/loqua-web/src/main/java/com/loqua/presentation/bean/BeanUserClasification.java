package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.User;

public class BeanUserClasification implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	Map<Integer, User> smallClasificationByLoggedUser;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		initSmallClasificationByLoggedUser();
	}
	
	private void initBeanLogin() {
		// Buscamos el BeanLogin en la sesion.
		beanLogin = null;
		beanLogin = (BeanLogin)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanLogin");
		// si no existe lo creamos e inicializamos:
		if( beanLogin == null ){
			beanLogin = new BeanLogin();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanLogin", beanLogin);
		}
	}

	private void initSmallClasificationByLoggedUser(){
		if( smallClasificationByLoggedUser==null ){
			smallClasificationByLoggedUser = getSmallClasificationFromDB(
					beanLogin.getLoggedUser() );
		}
	}
	
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	public Map<Integer, User> getSmallClasificationByUser(User user){
		if( beanLogin.getLoggedUser().getId()==user.getId() ){
			return getSmallClasificationByLoggedUser();
		}else{
			return getSmallClasificationFromDB(user);
		}
	}

	private Map<Integer, User> getSmallClasificationFromDB(User user) {
		Map<Integer, User> mapClasification = new HashMap<Integer, User>();
		mapClasification = Factories.getService().getServiceUser()
				.getSmallClasificationByUser(user);
		return mapClasification;
	}
	
	public boolean isTheVeryLastUser( int position ){
		int numRegisteredUsers = Factories.getService().getServiceUser()
				.getNumRegisteredUsersAndAdminFromMemory();
		if(position==numRegisteredUsers) return true;
		return false;
	}
	
	public void reloadSmallLoggedUserClasification(){
		smallClasificationByLoggedUser=null;
		smallClasificationByLoggedUser = getSmallClasificationFromDB(
				beanLogin.getLoggedUser() );
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public Map<Integer, User> getSmallClasificationByLoggedUser(){
		initSmallClasificationByLoggedUser();
		return smallClasificationByLoggedUser;
	}
	public void setSmallClasificationByLoggedUser(Map<Integer, User> clasif){
		smallClasificationByLoggedUser = clasif;
	}
}
