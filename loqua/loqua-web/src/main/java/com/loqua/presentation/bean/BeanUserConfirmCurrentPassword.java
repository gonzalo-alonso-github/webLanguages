package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean utilizado para verificar la identidad del usuario antes de permitirle
 * realizar alguna accion que comprometa datos importantes de su cuenta
 * (por ejemplo: cambiar su email, cambiar su contrase&ntilde;a, o eliminar
 * su cuenta de usuario).
 * @author Gonzalo
 */
//Este bean se usa en la vista de /snippets/profile/form_CurrentPassword.xhtml,
//y tambien en /snippets/profile/modalwindows.xhtml, que incluye a dicho snippet,
//y tambien en /pages/registeredUser/profile_edit.xhtml, que incuye a este
public class BeanUserConfirmCurrentPassword implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Contrase&ntilde;a que debe introducir el usuario, utilizada
	 * para verificar su identidad antes de realizar la accion */
	private String password;
	
	/** Indica si la contrase&ntilde;a introducida por el usuario ha sido
	 * verificada */
	private boolean currentPasswordConfirmed;
	
	/** Mensaje que describe el resultado de la verificacion de la
	 * contrase&ntilde;a introducida por el usuario */
	private String msgActionResult;
	
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
		currentPasswordConfirmed = false;
	}
	
	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
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
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Comprueba que el usuario logueado ha introducido correctamente
	 * su contrase&ntilde;a. Si el usuario falla al introducir su
	 * contrase&ntilde;a, inicializa el atributo {@ #msgActionResult}
	 * que mostrara en la vista la advertencia del fallo.
	 * @return
	 * Tanto si el usuario introduce correctamente su contrase&ntilde;a,
	 * como si falla al hacerlo, se devuelve valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 */
	public String confirmCurrentPassword(){
		String result = null;
		User userToEdit = null;
		try {
			userToEdit = Factories.getService().getServiceUser()
				.getUserToLogin(beanLogin.getLoggedUser().getEmail(), password);
		} catch (Exception e) {
			result = "errorUnexpected";
			log.error("Unexpected Exception at 'confirmCurrentPassword()'");
		}
		if( userToEdit==null ){
			setMsgActionResult("errorConfirmCurrentPassword");
		}else{
			msgActionResult=null;
			currentPasswordConfirmed = true;
		}
		return result;
	}

	/**
	 * Borra el estado del Bean sobreescribiendo las propiedades del mismo
	 * con sus valores por defecto.
	 */
	public void clearStatus(){
		msgActionResult=null;
		currentPasswordConfirmed = false;
		password = "";
	}
	
	// // // // // // // // // // // // // //
	// GETTERS & SETTERS CON LOGICA DE NEGOCIO
	// // // // // // // // // // // // // //
	
	/**
	 * Metodo 'get' del atributo {@link #msgActionResult}. No solo obtiene
	 * el valor del atributo, sino que ademas lo inicializa a su valor
	 * por defecto, con el fin de evitar que el mensaje de error
	 * se siga mostrando durante mas peticiones Ajax.
	 * @return el atributo {@link #error}
	 */
	public String getMsgActionResult() {
		String message = msgActionResult;
		msgActionResult = null;
		return message;
	}
	/**
	 * Metodo 'set' del atributo {@link #msgActionResult}. No solo sobrescribe
	 * el valor del atributo, sino que lo traduce al lenguaje indicado
	 * por el Locale de {@link BeanSettingsSession#locale}.
	 * @param message nombre de la propiedad, en el fichero
	 * 'bundle.properties' de internacionalizacion, cuyo valor sobrescribe al
	 * atributo {@link #msgActionResult}
	 */
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
