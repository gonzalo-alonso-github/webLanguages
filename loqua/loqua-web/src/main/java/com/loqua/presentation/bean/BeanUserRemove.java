package com.loqua.presentation.bean;

import java.io.Serializable;
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
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas a la eliminacion de la cuenta de usuario, incluyendo
 * la verificacion de dicha accion mediante el envio de un email de
 * confirmacion.
 * @author Gonzalo
 */
public class BeanUserRemove implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Parametro 'confirm' recibido en la URL. Es una cadena aleatoria
	 * (de 26 caracteres) que permite identificar
	 * al usuario que accede a la URL de confirmacion de la eliminacion
	 * de su cuenta. <br/>
	 * Se utiliza en la vista 'removeAccount_confirm.xhtml', donde se inicializa
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private String urlConfirm;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	/** Inyeccion de dependencia del {@link BeanSettingsActionLimits} */
	@ManagedProperty(value="#{beanSettingsActionLimits}")
	private BeanSettingsActionLimits beanSettingsActionLimits;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa los beans inyectados:
	 * {@link BeanLogin} y {@link BeanSettingsActionLimits}
	 */
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanSettingsActionLimits();
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
	
	/** Inicializa el objeto {@link BeanSettingsActionLimits} inyectado */
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

	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO ELIMINE SU CUENTA DE USUARIO
	// // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Indica, mediante el objeto {@link #beanActionResult}, el resultado
	 * de la accion de invocar al metodo {@link #sendEmailForRemoveUser}.
	 * @param beanUserView objeto {@link BeanUserView} cuyo atributo
	 * {@link BeanUserView#user} indica el usuario cuya cuenta sera eliminada.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @see #sendEmailForRemoveUser
	 */
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
			log.error("Unexpected Exception at 'generateEmailForRemoveUser()'");
		}
		beanActionResult.setFinish(true);
		return result;
	}
	
	/**
	 * Envia al email del usuario un correo que muestra un enlace para que 
	 * confirme la eliminacion de su cuenta.
	 * @param userToRemove usuario cuya cuenta sera eliminada
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @throws EntityNotFoundException
	 */
	private void sendEmailForRemoveUser(User userToRemove) 
			throws EntityNotFoundException {
		String content = "";
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
		content = bundle.getString("mailContentRemoveAccount01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentRemoveAccount02")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/"
				+ "removeAccount_confirm.xhtml?confirm=";
		
		// Enviar el correo:
		Factories.getService().getServiceUser()
				.sendEmailForRemoveUser(userToRemove, content, subject);
	}
	
	// METODOS PARA CONFIRMAR LA ELIMINACION DE LA CUENTA DE USUARIO
	
	/**
	 * Cambia el estado del objeto {@link User} (cuyo atributo
	 * {@link User#urlConfirm} coincide con el parametro
	 * 'confirm' de la URL de la vista) estableciendo a 'true' el valor de
	 * su propiedad {@link User#removed}.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina que indica que la URL es desconocida
	 * ('errorUnknownUrl').
	 */
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
			log.error("Unexpected Exception at 'confirmRemoveAccount()'");
		}
		}
		beanActionResult.setFinish(true);
		return result;
	}
	
	/**
	 * Halla el objeto {@link User} segun el atributo {@link User#urlConfirm}.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return el objeto User obtenido, o valor 'null' si no se encuentra
	 */
	private User getUserByUrlConfirm(BeanActionResult beanActionResult) {
		User userToActivate = null;
		userToActivate = Factories.getService().getServiceUser()
				.getUserByUrlConfirm(urlConfirm);
		if( userToActivate==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}
		return userToActivate;
	}
	
	/**
	 * Actualiza en el sistema los datos del usuario indicado.
	 * @param user usuario que se actualiza
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	private void updateUser(User user, BeanActionResult beanActionResult)
			throws Exception{
		try{
			user.removeUserData();
			deleteUserAccount(user);
			Factories.getService().getServiceUser().updateDataByUser(user);
		} catch (Exception e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			log.error("Unexpected Exception at 'updateUser()'");
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
