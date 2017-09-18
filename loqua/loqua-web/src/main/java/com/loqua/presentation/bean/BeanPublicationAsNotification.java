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

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo publicaciones que se muestran como notificaciones
 * (estas se muestran en el menu horizontal superior de la pagina,
 * o bien en la pagina 'notifications' para pantallas peque&ntilde;as).
 * @author Gonzalo
 */
public class BeanPublicationAsNotification implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Lista de notificaciones recibidas por un usuario,
	 * ordenadas por fecha descendientemente. El numero de ellas esta
	 * limitado segun el valor del atributo {@link limitNotifications}. */
	private List<Publication> lastNotifications;
	
	/** Numero maximo de notificaciones que se mostraran en la vista */
	private Integer limitNotifications;

	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	/** Inyeccion de dependencia del {@link BeanSettingsProfilePage} */
	@ManagedProperty(value="#{beanSettingsProfilePage}")
	private BeanSettingsProfilePage beanSettingsProfilePage;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa los beans inyectados:
	 * {@link BeanLogin} y {@link BeanSettingsProfilePage}
	 */
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanSettingsProfile();
		limitNotifications =
				beanSettingsProfilePage.getNumNotifications();
		loadLastNotifications();
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
	
	/** Inicializa el objeto {@link BeanSettingsProfilePage} inyectado */
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

	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Inicializa el atributo {@link #lastNotifications}, consultando la lista
	 * de notificaciones del usuario
	 */
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
