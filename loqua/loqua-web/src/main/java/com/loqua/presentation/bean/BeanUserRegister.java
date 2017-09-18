package com.loqua.presentation.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
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

/**
 * Bean, de ambito de peticion, encargado de realizar
 * todas las operaciones relativas al manejo de la pagina
 * de registro de usuarios. No solo incluye el dar de alta al nuevo usuario,
 * sino tambien el proceso previo de verificar la accion mediante el envio de
 * un email de confirmacion. <br/>
 * Al ser un bean de ambito de peticion ('request'),
 * todo el estado de esta clase se resetea ante cada peticion.
 * @author Gonzalo
 */
public class BeanUserRegister implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Datos introducidos por el usuario que se registra. <br/> Para completar
	 * la informacion que se da de alta, sera necesario inicializar tambien
	 * otros atributos del User asignandoles valores por defecto; de ello
	 * se encarga el metodo {@link #generateUser}. */
	private User userToRegister;
	
	/** Dato del sexo del usuario, introducido al registrarse. <br/> */
	private String radioBtnGender = null;
	
	/** Parametro 'confirm' recibido en la URL. Es una cadena aleatoria
	 * (de 26 caracteres) que permite identificar
	 * al usuario que accede a la URL de confirmacion de su registro
	 * en la aplicacion. <br/>
	 * Se utiliza en la vista 'register_confirm.xhtml', donde se inicializa
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private String urlConfirm;
	
	/** Inyeccion de dependencia del {@link BeanSettingsSession} */
	@ManagedProperty(value="#{beanSettingsSession}")
	private BeanSettingsSession beanSettingsSession;
	
	/** Inyeccion de dependencia del {@link BeanSettingsActionLimits} */
	@ManagedProperty(value="#{beanSettingsActionLimits}")
	private BeanSettingsActionLimits beanSettingsActionLimits;
	
	/** Inyeccion de dependencia del {@link BeanUserImages} */
	@ManagedProperty(value="#{beanUserImages}")
	private BeanUserImages beanUserImages;

	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa los beans inyectados:
	 * {@link BeanSettingsSession}, {@link BeanSettingsActionLimits}
	 * y {@link BeanUserImages} */
	@PostConstruct
	public void init() {
		userToRegister = new User();
		initBeanSettings();
		initBeanSettingsActionLimits();
		initBeanUserImages();
	}
	
	/** Inicializa el objeto {@link BeanSettingsSession} inyectado */
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
	
	/** Inicializa el objeto {@link BeanUserImages} inyectado */
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

	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // //
	// METODOS PARA ENVIAR EL EMAIL DE REGISTRO
	// // // // // // // // // // // // // // //
	
	/**
	 * Indica, mediante el objeto {@link #beanActionResult}, el resultado
	 * de la accion de invocar al metodo {@link #sendEmailForRegister}.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorRegister' o
	 * 'errorUnexpected').
	 * @see #sendEmailForRegister
	 */
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
	
	/**
	 * Envia al email del usuario un correo que muestra un enlace para que 
	 * confirme su registro en la aplicacion y se active su cuenta.
	 * @param userToRegister usuario que confirma el registro
	 * y cuya cuenta sera activada
	 * @return
	 * Si la accion se produce sin ningun error, retorna la cadena 'noError'.
	 * <br/>  Si se alcanza el limite de registros de usuarios permitidos
	 * en cierto lapso de tiempo, se devuelve la cadena 'limitTooRegistrations'
	 */
	private String sendEmailForRegister(User userToRegister){
		String content = "";
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
		// pero para facilitar la uniformidad del codigo, este metodo
		// se asemeja al de BeanRestorePassword.sendEmailForPasswordRestore
		content = bundle.getString("mailContentRegister01")
				+ "\n\n" + bundle.getString("mailContentAdviceRemove")
				+ "\n\n" + bundle.getString("mailContentRegister02")
				+ ":" +"\n\t" + url
				+ "pages/confirmationPages/register_confirm.xhtml?confirm=";
		
		// Enviar el correo:
		String result = Factories.getService().getServiceUser()
				.sendEmailForRegister(userToRegister, content, subject,
						beanSettingsActionLimits.getMapActionLimitsProperties());
		return result;
	}
	
	/**
	 * Inicializa los datos por defecto que el usuario que se da de alta
	 * no introduce en la vista. <br/>
	 * Son datos relativos al estado del usuario
	 * que normalmente no se le permiten editar directamente, salvo el atributo
	 * {@link User#locale}, que el usuario puede modificar al cambiar el idioma
	 * de la pagina. Dicho {@link User#locale}, a la hora de registrar a un
	 * usuario, se obtiene a partir de la informacion del navegador,
	 * inicializandose en {@link BeanSettingsSession#loadLocale}.
	 * @throws EntityAlreadyFoundException
	 * @throws Exception
	 */
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
	
	/**
	 * Cifra con hash la contrase&ntilde;a recibida.
	 * @param password contrase&ntilde;a que se cifra
	 * @return la contrase&ntilde;a, una vez cifrada
	 */
	private String hashPassword(String password){
		String salt = BCrypt.gensalt(12);
		String hashedPassword = BCrypt.hashpw(password, salt);
		return hashedPassword;
	}

	// // // // // // // // // // // // //
	// METODOS PARA CONFIRMAR EL REGISTRO
	// // // // // // // // // // // // //
	
	/**
	 * Cambia el estado del objeto {@link User} (cuyo atributo
	 * {@link User#urlConfirm} coincide con el parametro
	 * 'confirm' de la URL de la vista) estableciendo a 'true' el valor de
	 * su propiedad {@link User#active}.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br/>
	 * Si el usuario ya habia confirmado su regisro, devuelve la regla
	 * de navegacion que redirige a la pagina que indica que la accion ya fue
	 * realizada ('errorAlreadyPerformed'). <br/>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina que indica que la URL es desconocida
	 * ('errorUnexpectedAnonymous').
	 */
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
	 * @throws Exception
	 */
	private void updateUser(User user, BeanActionResult beanActionResult)
			throws Exception{
		try{
			Factories.getService().getServiceUser()
				.updateAllDataByUser(user);
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
