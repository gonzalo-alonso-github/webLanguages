package com.loqua.presentation.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo del buscador de usuarios de las vistas.
 * @author Gonzalo
 */
public class BeanUserSearch implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	 /** Formato al que debe ajustarse el email introducido
	  * para buscar un usuario */
	private static final String REGULAR_EXPRESSION_EMAIL = 
			"[0-9a-zA-Z]+@[0-9a-zA-Z]+\\.[0-9a-zA-Z]+";
	
	/** Cadena de texto introducida en el buscador de usuarios de la pagina,
	 * que puede ser un email de un usuario o un pseud&oacute;nimo */
	private String stringToSearch;
	
	/** Usuario cuyo email o pseud&oacute;nimo se consulta
	 *  en el buscador de usuarios de la pagina */
	private User user;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}") 
	private BeanLogin beanLogin;
	
	/** Inyeccion de dependencia del {@link BeanUserData} */
	@ManagedProperty(value="#{beanUserData}") 
	private BeanUserData beanUserData;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa los beans inyectados:
	 * {@link BeanLogin} y {@link BeanUserData}
	 */
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanUser();
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
	
	/** Inicializa el objeto {@link BeanUserData} inyectado */
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

	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Inicializa el atributo {@link #user} de este Bean buscando
	 * el usuario, segun su atributo 'email' o 'nick' indicado por
	 * {@link #stringToSearch}, que no este en estado eliminado. <br>
	 * Depues de eso halla el enlace necesario para redirigir la navegacion
	 * a la pagina del perfil del usuario encontrado.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return Si el usuario ha sido encontrado, devuelve la direccion URL
	 * que conduce a su pagina de perfil. <br>
	 * De lo contrario devuelve el valor 'null'
	 */
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
			result = "errorUnexpected";
			log.error("Unexpected Exception at 'searchUser()'");
		}
		beanActionResult.setFinish(true);
		return result;
	}
	
	/**
	 * Comprueba si el texto indicado cumple el formato de email indicado
	 * por la propiedad {@link #REGULAR_EXPRESSION_EMAIL} de esta clase.
	 * @param email el email cuyo formato se comprueba
	 * @return
	 * 'true' si el email dado cumple con el formato determinado <br>
	 * 'false' si el email dado no cumple con el formato determinado
	 */
	private boolean introducedWellFormedEmail(String email) {
		boolean result = true;
		// Comprobar formato de email:
		if( ! email.matches(REGULAR_EXPRESSION_EMAIL) ){
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
