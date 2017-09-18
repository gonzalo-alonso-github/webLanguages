package com.loqua.presentation.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de mensajes.
 * @author Gonzalo
 */
public class BeanMessage implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Numero de mensajes recibidos por el usuario
	 * que aun no han sido leidos */
	private Integer numUnreadMessages;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el bean inyectado {@link BeanLogin} */
	@PostConstruct
	public void init() {
		initBeanLogin();
		loadNumUnreadMessages();
	}
	
	/** Inicializa el objeto {@link BeanLogin} inyectado */
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

	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/** Inicializa el atributo {@link #numUnreadMessages} */
	private void loadNumUnreadMessages(){
		try{
			numUnreadMessages = Factories.getService().getServiceMessage()
					.getNumUnreadMessagesReceivedByUser(
							beanLogin.getLoggedUser().getId());
			if(numUnreadMessages == null) numUnreadMessages=0;
			else if(numUnreadMessages > 99) numUnreadMessages=99;
		}catch( Exception e ){
			log.error("Unexpected Exception at 'loadNumUnreadMessages()'");
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
