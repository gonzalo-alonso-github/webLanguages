package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.Publication;
import com.loqua.presentation.bean.applicationBean.BeanSettingsProfilePage;
import com.loqua.presentation.logging.LoquaLogger;

public class BeanPublicationAsNotification implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private List<Publication> lastNotifications;
	private Integer limitNotifications;

	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanSettingsProfilePage}")
	private BeanSettingsProfilePage beanSettingsProfilePage;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanSettingsProfile();
		limitNotifications =
				beanSettingsProfilePage.getNumNotifications();
		loadLastNotifications();
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
	
	/**
	 * Inicializa el BeanSettingsProfile perteneciente a esta clase.</br>
	 * Si el BeanSettingsProfile ya fue inicializado,
	 * simplemente se obtiene del contexto de aplicacion.</br>
	 * Si el BeanSettingsProfile no existe en el contexto de aplicacion,
	 * se crea y se guarda en sesion bajo la clave 'beanSettingsProfile'.
	 */
	private void initBeanSettingsProfile() {
		// Buscamos el BeanSettings en la sesion.
		beanSettingsProfilePage = null;
		beanSettingsProfilePage = (BeanSettingsProfilePage)
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("beanSettingsProfilePage");
		// si no existe lo creamos e inicializamos:
		if (beanSettingsProfilePage == null) { 
			beanSettingsProfilePage = new BeanSettingsProfilePage();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put(
						"beanSettingsProfilePage", beanSettingsProfilePage);
		}
	}

	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	private void loadLastNotifications(){
		try{
			lastNotifications = Factories.getService().getServicePublication()
				.getLastNotificationsByUser(
						beanLogin.getLoggedUser().getId(),limitNotifications);
		}catch( Exception e ){
			log.error("Exception at 'loadLastNotifications()'");
		}
	}
	
	public void setNotificationsToRead(){
		try{
			Factories.getService().getServicePublication()
					.setNotificationsToRead(beanLogin.getLoggedUser().getId());
		}catch( Exception e ){
			log.error("Exception at 'setNotificationsToRead()'");
		}
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
	
	public List<Publication> getLastNotifications() {
		List<Publication> result = lastNotifications==null ? 
				new ArrayList<Publication>() : lastNotifications;
		return result;
	}
	public void setLastNotifications(List<Publication> notifications) {
		this.lastNotifications = notifications;
	}
	public int getNumUnreadNotifications(){
		int result = 0;
		try{
			result = Factories.getService().getServicePublication()
					.getNumUnreadNotificationsByUser(
							beanLogin.getLoggedUser().getId());
		}catch( Exception e ){
			log.error("Exception at 'getNumUnreadNotifications()'");
		}
		return result;
	}
}
