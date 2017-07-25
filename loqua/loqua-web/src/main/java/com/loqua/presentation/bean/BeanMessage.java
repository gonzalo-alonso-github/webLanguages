package com.loqua.presentation.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;

public class BeanMessage implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	private Integer numUnreadMessages;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		loadNumUnreadMessages();
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
	
	private void loadNumUnreadMessages(){
		try{
			numUnreadMessages = Factories.getService().getServiceMessage()
					.getNumUnreadMessagesReceivedByUser(
							beanLogin.getLoggedUser().getId());
			if(numUnreadMessages == null) numUnreadMessages=0;
			else if(numUnreadMessages > 99) numUnreadMessages=99;
		}catch( Exception e ){
			// TODO Log
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public int getNumUnreadMessages() {
		return numUnreadMessages;
	}
	public void setNumUnreadMessages(int numUnreadMessages) {
		this.numUnreadMessages = numUnreadMessages;
	}
}
