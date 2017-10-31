package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.ChangePassword;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsActionLimits;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;
import com.loqua.presentation.validator.ValidatorEmailExists;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas a la restauracion (no edicion) de contrase&ntilde;a del usuario,
 * incluyendo la verificacion de dicha accion mediante el envio de un email de
 * confirmacion.
 * @author Gonzalo
 */
public class BeanUserRestorePassword implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Usuario que restaura su contrase&ntilde;a, identificado cuando el
	 * atributo {@link User#urlConfirm} coincide con el
	 * {@link #urlConfirm} de este Bean. */
	private User userFound;
	
	/** Direccion de email introducida por el usuario no logueado que trata de
	 * restaurar su contrase&ntilde;a. */
	private String email;
	
	/** Parametro 'confirm' recibido en la URL. Es una cadena aleatoria
	 * (de 26 caracteres) que permite identificar al usuario que accede
	 * a la URL de confirmacion de la restauracion de su contrase&ntilde;a.
	 * <br>
	 * Se utiliza en la vista 'password_restore_confirm.xhtml',
	 * donde se inicializa mediante el &lt;f:viewParam&gt; que invoca
	 * al metodo set del atributo. */
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
		userFound = null;
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
	// METODOS PARA ENVIAR EL EMAIL DE RESTAURACION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Genera un nuevo objeto {@link ChangePassword} invocando al metodo
	 * {@link #sendEmailForPasswordRestore}.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido
	 * ('errorUnexpectedAnonymous').
	 * @see #sendEmailForPasswordRestore
	 */
	public String generateEmailForPasswordRestore(
			BeanActionResult beanActionResult) {
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		userFound = ValidatorEmailExists.getUser();
		try {
			String resultSendEmail = sendEmailForPasswordRestore();
			if( ! resultSendEmail.equals("noError") ){
				Map<String, Integer> mapActionsLimits = 
						beanSettingsActionLimits.getMapActionLimitsProperties();
				beanActionResult.setMsgActionResult(resultSendEmail);
				beanActionResult.setMsgActionResultExact(
						beanActionResult.getMsgActionResult().replaceFirst(
						"\\?1",mapActionsLimits.get(resultSendEmail).toString()));				
			}else{
				beanActionResult.setSuccess(true);
			}
		} catch (Exception e) {
			action = "errorUnexpectedAnonymous";
			log.error("Unexpected Exception at "
					+ "'generateEmailForPasswordRestore()'");
		}
		userFound = null;
		beanActionResult.setFinish(true);
		return action;
	}
	
	/**
	 * Envia al email del usuario un correo que muestra un enlace para que 
	 * confirme su cambio de contrase&ntilde;a.
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @throws EntityAlreadyFoundException
	 */
	private String sendEmailForPasswordRestore() 
			throws EntityAlreadyFoundException {
		List<String> content = new ArrayList<String>();
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
		content.add(0, bundle.getString("mailContentPasswordRestore01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentPasswordRestore02")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/password_restore_confirm.xhtml?confirm=");
		content.add(1, "\n\n" + bundle.getString("mailContentPasswordRestore03")
				+ ":" + "\n\t");
		
		// Enviar el correo:
		String result = Factories.getService()
				.getServiceUserAccessDataChange()
				.sendEmailForPasswordRestore(userFound, content, subject,
						beanSettingsActionLimits.getMapActionLimitsProperties());
		return result;
	}
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA CONFIRMAR LA RESTAURACION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // //	
	
	/**
	 * Cambia el estado del objeto {@link ChangePassword} (cuyo atributo
	 * {@link ChangePassword#urlConfirm} coincide con el parametro
	 * 'confirm' de la URL de la vista) estableciendo a 'true' el valor de
	 * su propiedad {@link ChangePassword#confirmed}.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina que indica que la URL es desconocida
	 * ('errorUnknownUrl').
	 */
	public String confirmRestorePassword(BeanActionResult beanActionResult) {
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try {
			ChangePassword passRestore = getPasswordChangeByUrlConfirm();
			if( passRestore!=null ){
				if( passRestore.getConfirmed()==true ){
					beanActionResult.setMsgActionResult(
							"errorAlreadyPerformed");
				}else{
					setPasswordRestoreToConfirmed(passRestore,beanActionResult);
					User userToUpdate = passRestore.getUser();
					userToUpdate.setPassword(
							passRestore.getPasswordGenerated());
					userToUpdate.setLoginFails(0);
					updateUser(userToUpdate, beanActionResult);
					beanActionResult.setSuccess(true);
					beanActionResult.setMsgActionResult(
							"descriptionPasswordRestoreConfirm");
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (Exception e) {
			action = "errorUnexpectedAnonymous";
			log.error("Unexpected Exception at 'confirmRestorePassword()'");
		}
		}
		beanActionResult.setFinish(true);
		return action;
	}

	/**
	 * Halla el objeto {@link ChangePassword} segun el atributo
	 * {@link ChangePassword#urlConfirm}
	 * @return el objeto ChangePassword obtenido, o valor 'null' si no se
	 * encuentra
	 */
	private ChangePassword getPasswordChangeByUrlConfirm() {
		ChangePassword passwordRestore = null;
		passwordRestore = Factories.getService()
				.getServiceUserAccessDataChange().getPasswordChangeByUrlConfirm(
						urlConfirm, ChangePassword.RESTORE);
		return passwordRestore;
	}
	
	/**
	 * Cambia el estado del objeto {@link ChangePassword} indicado
	 * estableciendo a 'true' el valor de
	 * su propiedad {@link ChangePassword#confirmed}
	 * @param passwordRestore objeto {@link ChangePassword} que se actualiza
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	private void setPasswordRestoreToConfirmed(
			ChangePassword passwordRestore, BeanActionResult beanActionResult){
		passwordRestore.setConfirmed(true);
		try{
			Factories.getService().getServiceUserAccessDataChange()
				.updatePasswordChange(passwordRestore);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			log.error("EntityNotFoundException at "
					+ "'setPasswordRestoreToConfirmed()'");
		}
	}
	
	/**
	 * Actualiza en el sistema los datos del usuario indicado.
	 * @param user usuario que se actualiza
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	private void updateUser(User user, BeanActionResult beanActionResult){
		try{
			Factories.getService().getServiceUser().updateAllDataByUser(user);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			log.error("EntityNotFoundException at 'updateUser()'");
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
	    this.email = email;
	}
	public String getUrlConfirm() {
		return urlConfirm;
	}
	public void setUrlConfirm(String urlConfirm) {
	    this.urlConfirm = urlConfirm;
	}
}
