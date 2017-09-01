package com.loqua.presentation.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.mindrot.jbcrypt.BCrypt;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsActionLimits;
import com.loqua.presentation.bean.applicationBean.BeanUserImages;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

// Al ser un bean de scope 'request', los campos se resetean ante cada peticion
public class BeanUserRegister implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private User userToRegister;
	private String radioBtnGender = null;
	private String urlConfirm;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanSettingsSession}")
	private BeanSettingsSession beanSettingsSession;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanSettingsActionLimits}")
	private BeanSettingsActionLimits beanSettingsActionLimits;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanUserImages}")
	private BeanUserImages beanUserImages;

	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		userToRegister = new User();
		//initBeanLogin();
		initBeanSettings();
		initBeanSettingsActionLimits();
		initBeanUserImages();
	}
	/*
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
	*/
	private void initBeanSettings() {
		// Buscamos el BeanSettingsSession en la sesion.
		beanSettingsSession = null;
		beanSettingsSession = (BeanSettingsSession)FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("beanSettingsSession");
		// si no existe lo creamos e inicializamos:
		if (beanSettingsSession == null) { 
			beanSettingsSession = new BeanSettingsSession();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanSettingsSession", beanSettingsSession);
		}
	}
	
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
	
	private void initBeanUserImages() {
		// Buscamos el BeanUserImages en la sesion.
		beanUserImages = null;
		beanUserImages = (BeanUserImages)FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("beanUserImages");
		// si no existe lo creamos e inicializamos:
		if (beanUserImages == null) { 
			beanUserImages = new BeanUserImages();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("beanUserImages", beanUserImages);
		}
	}

	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // //
	// METODOS PARA ENVIAR EL EMAIL DE REGISTRO
	// // // // // // // // // // // // // // //
	
	public String generateEmailForRegister(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		// la urlConfirm tiene 26 caracteres (6*5=130)
		String urlConfirm = new BigInteger(130, new SecureRandom()).toString(32);
		userToRegister.setUrlConfirm(urlConfirm);
		try {
			String resultSendEmail = 
					sendEmailForRegister(userToRegister);
			if( ! resultSendEmail.equals("noError") ){
				beanActionResult.setMsgActionResult(resultSendEmail);
			}else{
				generateUser();
				beanActionResult.setSuccess(true);
			}
		} catch (EntityAlreadyFoundException e) {
			action = "errorRegister";
			beanActionResult.setMsgActionResult("errorAlreadyFoundEmail");
			log.error("EntityAlreadyFoundException at "
					+ "'generateEmailForRegister()'");
		} catch (Exception e) {
			action = "errorUnexpectedAnonymous";
			log.error("Unexpected Exception at 'generateEmailForRegister()'");
		}
		beanActionResult.setFinish(true);
		return action;
	}
	
	private String sendEmailForRegister(User userToRegister){
		List<String> content = new ArrayList<String>();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication().getResourceBundle(
				facesContext, "msgs");
		
		// Asunto del correo:
		String subject = bundle.getString("mailSubjectRegister");
		
		// Contenido del correo:
		HttpServletRequest req = (HttpServletRequest)
				facesContext.getExternalContext().getRequest();
		String uri = req.getRequestURL().toString();
		String url = uri.substring(0, uri.length()-req.getRequestURI().length())
				+ req.getContextPath() + "/";
		// Aqui bastaria que 'content' sea un simple String y no List<String>,
		// pero para facilitar la uniformidad del codigo, queria que este metodo
		// se parezca al de BeanRestorePassword.sendEmailForPasswordRestore
		content.add(0, bundle.getString("mailContentRegister01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentRegister02")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/register_confirm.xhtml?confirm=");
		
		// Enviar el correo:
		String result = Factories.getService().getServiceUser()
				.sendEmailForRegister(userToRegister, content, subject,
						beanSettingsActionLimits.getMapActionLimitsProperties());
		return result;
	}
	
	private void generateUser() throws EntityAlreadyFoundException, Exception {
		userToRegister.setRole(User.USER);
		userToRegister.setActive(false);
		userToRegister.setLocked(false);
		userToRegister.setRemoved(false);
		userToRegister.setLocale( beanSettingsSession.getLocaleLanguage() );
		userToRegister.setDateRegistered(new Date());
		//beanUserImages.setDefaultImageToUser(userToRegister);
		
		String hashedPassword = hashPassword(userToRegister.getPassword());
		userToRegister.setPassword(hashedPassword);
		
		Factories.getService().getServiceUser().createUser(userToRegister);
	}
	
	private String hashPassword(String password){
		String salt = BCrypt.gensalt(12);
		String hashedPassword = BCrypt.hashpw(password, salt);
		return hashedPassword;
	}

	// // // // // // // // // // // // //
	// METODOS PARA CONFIRMAR EL REGISTRO
	// // // // // // // // // // // // //
	
	public String confirmRegistration(BeanActionResult beanActionResult) {
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		String action = null;
		if( urlConfirm==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}else{
			try {
				User userToActivate = getUserByUrlConfirm(beanActionResult);
				if( userToActivate!=null ){
					if( userToActivate.getActive()==true ){
						beanActionResult.setMsgActionResult(
								"errorAlreadyPerformed");
					}else{
						userToActivate.setActive(true);
						userToActivate.setUrlConfirm(null);
						updateUser(userToActivate, beanActionResult);
						beanActionResult.setMsgActionResult(
								"descriptionRegisterConfirm");
						beanActionResult.setSuccess(true);
					}
				}
			} catch (Exception e) {
				action = "errorUnexpectedAnonymous";
				log.error("Unexpected Exception at 'confirmRegistration()'");
			}
		}
		beanActionResult.setFinish(true);
		return action;
	}

	private User getUserByUrlConfirm(BeanActionResult beanActionResult) {
		User userToActivate = null;
		userToActivate = Factories.getService().getServiceUser()
				.getUserByUrlConfirm(urlConfirm);
		if( userToActivate==null ){
			beanActionResult.setMsgActionResult("errorUnknownUrl");
		}
		return userToActivate;
	}
	
	private void updateUser(User user, BeanActionResult beanActionResult)
			throws Exception{
		try{
			Factories.getService().getServiceUser()
				.updateAllDataByUser(user, true);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			log.error("EntityNotFoundException at 'updateUser()'");
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public User getUserToRegister() {
		return userToRegister;
	}
	public void setUserToRegister(User userToRegister) {
		this.userToRegister = userToRegister;
	}
	public String getRadioBtnGender() {
		return radioBtnGender;
	}
	public void setRadioBtnGender(String radioBtnGender) {
		this.radioBtnGender = radioBtnGender;
		userToRegister.getUserInfoPrivacity().setGender( 
				Boolean.valueOf(radioBtnGender) );
	}
	public String getUrlConfirm() {
		return urlConfirm;
	}
	public void setUrlConfirm(String urlConfirm) {
	    this.urlConfirm = urlConfirm;
	}
}
