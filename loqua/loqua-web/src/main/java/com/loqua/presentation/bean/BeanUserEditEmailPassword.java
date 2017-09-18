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
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsActionLimits;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas a la edicion de email o de contrase&ntilde;a del usuario,
 * incluyendo la verificacion de dichas acciones mediante el envio de un email
 * de confirmacion.
 * @author Gonzalo
 */
public class BeanUserEditEmailPassword implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Parametro 'confirm' recibido en la URL. Es una cadena aleatoria
	 * (de 26 caracteres) que permite identificar
	 * al usuario que accede a la URL de confirmacion de su cambio
	 * de email o contrase&ntilde;a. <br/>
	 * Se utiliza en las vistas .xhtml ubicadas en la ruta
	 * 'pages/confirmationPages/', y en ellas se inicializa
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
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU EMAIL
	// // // // // // // // // // // // // // // //
	
	// METODOS PARA ENVIAR EL EMAIL DE CONFIMACION AL EMAIL ORIGINAL
	
	/**
	 * Genera un nuevo objeto {@link ChangeEmail} invocando al metodo
	 * {@link #sendEmailForEditEmail}.
	 * @param beanUserView objeto {@link BeanUserView} cuyo atributo
	 * {@link BeanUserView#user} almacena, entre otros datos, el nuevo email
	 * introducido por el usuario en la vista
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @see #sendEmailForEditEmail
	 */
	public String generateEmailForEditEmail(
			BeanUserView beanUserView, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		// Obtener los datos originales del usuario desde el beanLogin
		// (porque el email original sera guardado en la tabla ChangeEmail)
		User updatedUser = beanLogin.getLoggedUser();
		// Ahora 'updatedUser.getEmail()' almacena el email previo al cambio,
		// mientras que 'beanUserView.getUser().getEmail()' almacena
		// el nuevo email.
		// Ambos valores seran utilizados en la logica de negocio,
		// para guardar el nuevo ChangeEmail en la tabla
		try {
			String resultSendEmail = sendEmailForEditEmail(
					updatedUser, beanUserView.getUser().getEmail());
			if( ! resultSendEmail.equals("noError") ){
				Map<String, Integer> mapActionsLimits = 
						beanSettingsActionLimits.getMapActionLimitsProperties();
				beanActionResult.setMsgActionResult(resultSendEmail);
				beanActionResult.setMsgActionResultExact(
						beanActionResult.getMsgActionResult().replaceFirst("\\?1",
						mapActionsLimits.get(resultSendEmail).toString()) );
			}else{
				beanActionResult.setSuccess(true);
			}
		} catch (Exception e) {
			action = "errorUnexpected";
			log.error("Unexpected Exception at 'generateEmailForEditEmail()'");
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	/**
	 * Envia al email original del usuario un correo que muestra un enlace
	 * para que el usuario confirme su cambio de email.
	 * @param updatedUser usuario que actualiza su email
	 * @param newEmail la nueva direccion de email que desea establecer
	 * el usuario
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @throws EntityAlreadyFoundException
	 */
	private String sendEmailForEditEmail(User updatedUser, String newEmail)
			throws EntityAlreadyFoundException {
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
		content = bundle.getString("mailContentEditEmail01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentEditEmail02")
				+ ":" +"\n\t" + newEmail
				+ "\n\n" + bundle.getString("mailContentEditEmail03")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/"
				+ "emailChange_first_confirm.xhtml?confirm=";
		// Enviar el correo:
		String result = Factories.getService().getServiceUserAccessDataChange()
				.sendEmailForEditEmail(updatedUser, newEmail, content, subject,
						beanSettingsActionLimits.getMapActionLimitsProperties());
		return result;
	}
	
	// METODOS PARA CONFIRMAR EL EMAIL ORIGINAL EDITADO
	
	/**
	 * Cambia el estado del objeto {@link ChangeEmail} (cuyo atributo
	 * {@link ChangeEmail#urlConfirm} coincide con el parametro
	 * 'confirm' de la URL de la vista) estableciendo a 'true' el valor de
	 * su propiedad {@link ChangeEmail#confirmedPreviousEmail}. <br/>
	 * Despues de eso invoca al metodo {@link #sendEmailForEditEmailSecondStep}.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @see #sendEmailForEditEmailSecondStep
	 */
	public String confirmEmailChangeFirstStep(
			BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try{
			ChangeEmail emailChange = getEmailChangeByUrlConfirm();
			if( emailChange!=null ){
				if( emailChange.getConfirmedPreviousEmail()==true ){
					beanActionResult.setMsgActionResult(
							"errorAlreadyPerformed");
				}else{
					setPreviousEmailToConfirmed(emailChange, beanActionResult);
					sendEmailForEditEmailSecondStep(emailChange);
					beanActionResult.setSuccess(true);
					beanActionResult.setMsgActionResult(
							"descriptionEditEmalFirstConfirm");
					beanActionResult.setMsgActionResultExact(
							beanActionResult.getMsgActionResult()
							.replaceFirst("\\?1",emailChange.getNewEmail()) );
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (Exception e) {
			action = "errorUnexpected";
			log.error("Unexpected Exception at "
					+ "'confirmEmailChangeFirstStep()'");
		}
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	/**
	 * Envia al email original del usuario un correo que muestra un enlace
	 * para que el usuario confirme su cambio de email por segunda vez.
	 * @param emailChange el objeto {@link ChangeEmail} que se confirma por
	 * segunda vez
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @throws EntityNotFoundException
	 */
	private void sendEmailForEditEmailSecondStep(ChangeEmail emailChange)
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
		content= bundle.getString("mailContentEditEmailSecondStep01")
			+ "\n\n" + bundle.getString("mailContentAdviceRemove")
			+ "\n\n" + bundle.getString("mailContentEditEmailSecondStep02")
					.replaceFirst("\\?1", emailChange.getPreviousEmail())
			+ "\n\n" + bundle.getString("mailContentEditEmailSecondStep03")
			+ ":" +"\n\t" + url
			+ "pages/confirmationPages/"
			+ "emailChange_second_confirm.xhtml?confirm=";
		
		// Enviar el correo:
		Factories.getService().getServiceUserAccessDataChange()
				.sendEmailForEditEmailSecondStep(emailChange, content, subject);
	}
	
	// METODOS PARA CONFIRMAR EL EMAIL NUEVO EDITADO
	
	/**
	 * Cambia el estado del objeto {@link ChangeEmail} (cuyo atributo
	 * {@link ChangeEmail#urlConfirm} coincide con el parametro
	 * 'confirm' de la URL de la vista) estableciendo a 'true' el valor de
	 * su propiedad {@link ChangeEmail#confirmedNewEmail}. <br/>
	 * Despues de eso actualiza finalmente el email del usuario.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 */
	public String confirmEmailChangeSecondStep(
			BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try{
			ChangeEmail emailChange = getEmailChangeByUrlConfirm();
			if( emailChange!=null ){
				if( emailChange.getConfirmedNewEmail()==true ){
					beanActionResult
						.setMsgActionResult("errorAlreadyPerformed");
				}else{
					setNewEmailToConfirmed(emailChange, beanActionResult);
					User updatedUser = emailChange.getUser();
					updatedUser.setEmail( emailChange.getNewEmail() );
					updateUser( updatedUser, beanActionResult );
					beanLogin.setLoggedUser( updatedUser );
					beanActionResult.setMsgActionResult(
							"descriptionEditEmalSecondConfirm");
					beanActionResult.setMsgActionResultExact(
							beanActionResult.getMsgActionResult()
							.replaceFirst("\\?1",emailChange.getNewEmail()) );
					beanActionResult.setSuccess(true);
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (Exception e) {
			action = "errorUnexpected";
			log.error("Unexpected Exception at "
					+ "'confirmEmailChangeSecondStep()'");
		}
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	/**
	 * Halla el objeto {@link ChangeEmail} segun el atributo
	 * {@link ChangeEmail#urlConfirm}
	 * @return el objeto ChangeEmail obtenido, o valor 'null' si no se
	 * encuentra
	 */
	private ChangeEmail getEmailChangeByUrlConfirm() {
		ChangeEmail objectChangeEmail = null;
		objectChangeEmail = 
				Factories.getService().getServiceUserAccessDataChange()
				.getEmailChangeByUrlConfirm(urlConfirm);
		return objectChangeEmail;
	}
	
	/**
	 * Cambia el estado del objeto {@link ChangeEmail} indicado
	 * estableciendo a 'true' el valor de
	 * su propiedad {@link ChangeEmail#confirmedPreviousEmail}
	 * @param emailChange objeto {@link ChangeEmail} que se actualiza
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	private void setPreviousEmailToConfirmed(
			ChangeEmail emailChange, BeanActionResult beanActionResult){
		emailChange.setConfirmedPreviousEmail(true);
		try{
			Factories.getService().getServiceUserAccessDataChange()
				.updateEmailChange(emailChange);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			log.error("EntityNotFoundException at "
					+ "'setPreviousEmailToConfirmed()'");
		}
	}
	
	/**
	 * Cambia el estado del objeto {@link ChangeEmail} indicado
	 * estableciendo a 'true' el valor de
	 * su propiedad {@link ChangeEmail#confirmedNewEmail}
	 * @param emailChange objeto {@link ChangeEmail} que se actualiza
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	private void setNewEmailToConfirmed(
			ChangeEmail emailChange, BeanActionResult beanActionResult){
		emailChange.setConfirmedNewEmail(true);
		try{
			Factories.getService().getServiceUserAccessDataChange()
				.updateEmailChange(emailChange);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			log.error("EntityNotFoundException at "
					+ "'setNewEmailToConfirmed()'");
		}
	}
	
	/**
	 * Actualiza en el sistema los datos del usuario indicado.
	 * @param user usuario que se actualiza
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	private void updateUser(User user, BeanActionResult beanActionResult) {
		try{
			Factories.getService().getServiceUser().updateAllDataByUser(user);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			log.error("EntityNotFoundException at 'updateUser()'");
		}
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU CONTRASENA
	// // // // // // // // // // // // // // // // // //
	
	/**
	 * Genera un nuevo objeto {@link ChangePassword} invocando al metodo
	 * {@link #sendEmailForEditPassword}.
	 * @param beanUserView objeto {@link BeanUserView} cuyo atributo
	 * {@link BeanUserView#user} almacena, entre otros datos, la
	 * nueva contrase&ntilde;a introducida por el usuario en la vista.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @see #sendEmailForEditPassword
	 */
	public String generateEmailForEditPassword(
			BeanUserView beanUserView, BeanActionResult beanActionResult) {
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		User updatedUser = beanLogin.getLoggedUser();
		try {
			String resultSendEmail = sendEmailForEditPassword(
					updatedUser, beanUserView.getUser().getPassword());
			if( ! resultSendEmail.equals("noError") ){
				Map<String, Integer> mapActionsLimits = 
					beanSettingsActionLimits.getMapActionLimitsProperties();
				beanActionResult.setMsgActionResult(resultSendEmail);
				beanActionResult.setMsgActionResultExact(
						beanActionResult.getMsgActionResult().replaceFirst("\\?1",
					mapActionsLimits.get(resultSendEmail).toString()) );
			}else{
				beanActionResult.setSuccess(true);
			}
		} catch (Exception e) {
			action = "errorUnexpected";
			log.error("Unexpected Exception at "
					+ "'generateEmailForEditPassword()'");
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	/**
	 * Envia al email del usuario un correo que muestra un enlace para que 
	 * confirme su cambio de contrase&ntilde;a.
	 * @param updatedUser usuario que actualiza su contrase&ntilde;a
	 * @param newEmail la nueva contrase&ntilde;a que desea establecer
	 * el usuario
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @throws EntityAlreadyFoundException
	 */
	private String sendEmailForEditPassword(User updatedUser, String newPass) 
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
		String url=uri.substring(0, uri.length()-req.getRequestURI().length())
				+ req.getContextPath() + "/";
		content.add(0, bundle.getString("mailContentEditPassword01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentEditPassword02")
				+ ":" +"\n\t" + url + "pages/confirmationPages/"
				+ "passwordChange_confirm.xhtml?confirm=");
		content.add(1, "\n\n" + bundle.getString("mailContentEditPassword03")
				+ ":" + "\n\t");
		// Enviar el correo:
		String result = Factories.getService()
				.getServiceUserAccessDataChange()
				.sendEmailForEditPassword(updatedUser,newPass,content,subject,
					beanSettingsActionLimits.getMapActionLimitsProperties());
		return result;
	}
	
	// METODOS PARA CONFIRMAR LA CONTRASENA NUEVA EDITADA
	
	/**
	 * Cambia el estado del objeto {@link ChangePassword} (cuyo atributo
	 * {@link ChangePassword#urlConfirm} coincide con el parametro
	 * 'confirm' de la URL de la vista) estableciendo a 'true' el valor de
	 * su propiedad {@link ChangePassword#confirmed}.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 * @see #sendEmailForEditEmailSecondStep
	 */
	public String confirmPasswordChange(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
		try{
			ChangePassword passwordChange = getPasswordChangeByUrlConfirm();
			if( passwordChange!=null ){
				if( passwordChange.getConfirmed()==true ){
					beanActionResult.setMsgActionResult(
							"errorAlreadyPerformed");
				}else{
					setNewPasswordToConfirmed(passwordChange);
					User updatedUser = passwordChange.getUser();
					updatedUser.setPassword(
							passwordChange.getPasswordGenerated());
					updateUser( updatedUser, beanActionResult );
					beanLogin.setLoggedUser( updatedUser );
					beanActionResult.setMsgActionResult(
							"descriptionEditPasswordConfirm");
					beanActionResult.setSuccess(true);
				}
			}else{
				beanActionResult.setMsgActionResult("errorUnknownUrl");
			}
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			log.error("EntityNotFoundException at 'confirmPasswordChange()'");
		} catch (Exception e) {
			action = "errorUnexpected";
			log.error("Unexpected Exception at 'confirmPasswordChange()'");
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
						urlConfirm, ChangePassword.EDIT);
		return passwordRestore;
	}
	
	/**
	 * Cambia el estado del objeto {@link ChangePassword} indicado
	 * estableciendo a 'true' el valor de
	 * su propiedad {@link ChangePassword#confirmed}
	 * @param passwordChange objeto {@link ChangePassword} que se actualiza
	 * @throws EntityNotFoundException
	 */
	private void setNewPasswordToConfirmed(ChangePassword passwordChange)
			throws EntityNotFoundException {
		passwordChange.setConfirmed(true);
		Factories.getService().getServiceUserAccessDataChange()
			.updatePasswordChange(passwordChange);
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
