package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.Country;
import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.model.UserNativeLanguage;
import com.loqua.model.UserPracticingLanguage;
import com.loqua.presentation.bean.applicationBean.BeanUserImages;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de la pagina de edicion de perfil del usuario.
 * @author Gonzalo
 */
public class BeanUserEditProfile implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Lista de identificadores de los lenguajes utilizados por el usuario
	 * a nivel nativo */
	private List<Long> listNativeLanguagesIDs;
	
	/** Lista de identificadores de los lenguajes utilizados por el usuario
	 * a nivel de practicante */
	private List<Long> listPracticingLanguagesIDs;
	
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
	
	// // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SUS LISTAS DE LENGUAGES
	// // // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Comprueba si los lenguajes a nivel nativo y de practicante seleccionados
	 * por el usuario en la vista de edicion de perfil son adecuados (se impide
	 * que un idioma aparezca a la vez en ambas categorias), tras lo cual se
	 * efectua la actualizacion de ambas listas.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * Si la accion se realiza con exito, devuelve un valor 'null'. <br>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 */
	public String editProfileLanguages(BeanActionResult beanActionResult){
		// Los formularios de las vistas solo llaman a los setters
		// tras pinchar el boton 'submit'. Es decir, en este instante
		// tanto listPracticingLanguagesIDs como listNativeLanguagesIDs
		// ya almacenan los Language.id marcados en los inputs 'select'
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try{
			List<Long> practicedLanguagesIDsFromBeanUser = 
					getListPracticingLanguagesIDs();
			List<Long> nativeLanguagesIDsFromBeanUser =
					getListNativeLanguagesIDs();
			boolean changesInPracticedLanguages=!practicedLanguagesIDsFromBeanUser
					.equals(listPracticingLanguagesIDs);
			boolean changesInNativeLanguages=!nativeLanguagesIDsFromBeanUser
					.equals(listNativeLanguagesIDs);
			if(changesInPracticedLanguages || changesInNativeLanguages){
				if( ! verifyLanguagesSelected() ){
					beanActionResult.setMsgActionResult(
							"errorPracticingLanguagesInListNativeLanguages");
					beanActionResult.setFinish(true);
					return null;
				}else{ beanActionResult.clearMsgResult(); }
				if( changesInPracticedLanguages ){
					saveUserPracticedLanguages(practicedLanguagesIDsFromBeanUser);
				}
				if( changesInNativeLanguages ){
					saveUserNativeLanguages(nativeLanguagesIDsFromBeanUser);
				}
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at 'editProfileLanguages()'");
			return "errorUnexpected";
		}
		beanActionResult.setFinish(true);
		beanActionResult.setSuccess(true);
		return null;
	}
	
	/**
	 * Comprueba que la lista de lenguajes maternos
	 * ({@link #listNativeLanguagesIDs}) no contiene ningun elemento de
	 * la lista de lenguajes pacticados ({@link #listPracticingLanguagesIDs})
	 * @return
	 * 'true' si la lista de lenguajes maternos no contiene ningun elemento
	 * de la lista de lenguajes pacticados <br>
	 * 'false' si la lista de lenguajes maternos ya contiene algun elemento
	 * de la lista de lenguajes pacticados
	 */
	private boolean verifyLanguagesSelected(){
		for( Long practicedLanguageID : listPracticingLanguagesIDs ){
			if( listNativeLanguagesIDs.contains(practicedLanguageID) ){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Actualiza las asociaciones (objetos {@link UserPracticingLanguage})
	 * entre el usuario logueado y los lenguajes indicados.
	 * @param practicedLanguagesIDs lista de lenguajes a nivel de practicante
	 * seleccionados por el usuario
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	private void saveUserPracticedLanguages(
			List<Long> practicedLanguagesIDs) throws
			EntityAlreadyFoundException, EntityNotFoundException {
		User user = beanLogin.getLoggedUser();
		// Actualizar los UserPracticedLanguages pertinentes en bdd:
		Factories.getService().getServiceLanguage().updatePracticedLanguages(
				user, practicedLanguagesIDs,listPracticingLanguagesIDs);
		// Actualizar el mapPracticingLanguages de beanUserData
		updatePracticingLanguagesInSession();
	}
	
	/**
	 * Actualiza las asociaciones (objetos {@link UserNativeLanguage})
	 * entre el usuario logueado y los lenguajes indicados.
	 * @param nativeLanguagesIDs lista de lenguajes a nivel nativo
	 * seleccionados por el usuario
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	private void saveUserNativeLanguages(
			List<Long> nativeLanguagesIDs) throws
			EntityAlreadyFoundException, EntityNotFoundException {
		User user = beanLogin.getLoggedUser();
		// Actualizar los UserNativeLanguages pertinentes en bdd:
		Factories.getService().getServiceLanguage().updateNativeLanguages(
				user, nativeLanguagesIDs,listNativeLanguagesIDs);
		// Actualizar el mapNativeLanguages de beanUserData
		updateNativeLanguagesInSession();
	}
	
	/**
	 * Actualiza el Map&lt;Long,Languages&gt; mapPracticingLanguages
	 * de {@link BeanUserData}, sobreescribiendolo con los valores
	 * de la lista {@link #listPracticingLanguagesIDs}
	 */
	private void updatePracticingLanguagesInSession() {
		Map<Long,Language> mapPracticedLanguages=new HashMap<Long,Language>();
		mapPracticedLanguages = Factories.getService().getServiceLanguage()
				.getMapLanguagesByIdsFromDB(listPracticingLanguagesIDs);
				//FromCache
		beanUserData.setMapPracticingLanguages(mapPracticedLanguages);
	}
	
	/**
	 * Actualiza el Map&lt;Long,Languages&gt; mapNativeLanguages
	 * de {@link BeanUserData}, sobreescribiendolo con los valores
	 * de la lista {@link #listNativeLanguagesIDs}
	 */
	private void updateNativeLanguagesInSession() {
		Map<Long,Language> mapNativeLanguages=new HashMap<Long,Language>();
		mapNativeLanguages = Factories.getService().getServiceLanguage()
				.getMapLanguagesByIdsFromDB(listNativeLanguagesIDs);
				//FromCache
		beanUserData.setMapNativeLanguages(mapNativeLanguages);
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU PRIVACIDAD
	// // // // // // // // // // // // // // // // // //
	
	/**
	 * Actualiza los niveles de privacidad establecidos en la pagina visitada
	 * de edicion de perfil.
	 * @param user usuario cuyos datos se actualizan
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return devuelve el valor 'null' para evitar que el CommandLink 
	 * que invoca a este metodo redirija la navegacion a otra pagina
	 */
	public String editProfilePrivacity(
			User user, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		// Obtener los datos originales del usuario desde el beanLogin:
		User updatedUser = beanLogin.getLoggedUser();
		// Actualizar los datos originales con los datos editados en la vista:
		updatedUser.getPrivacityData().setPublications(
				user.getPrivacityData().getPublications());
		updatedUser.getPrivacityData().setContactsList(
				user.getPrivacityData().getContactsList());
		updatedUser.getPrivacityData().setAppearingInSearcher(
				user.getPrivacityData()
				.getAppearingInSearcher());
		updatedUser.getPrivacityData().setReceivingCorrectionRequests(
				user.getPrivacityData()
				.getReceivingCorrectionRequests());
		updatedUser.getPrivacityData().setReceivingMessages(
				user.getPrivacityData().getReceivingMessages());
		// Actualizar base de datos:
		updateUser( updatedUser, beanActionResult );
		// Actualizar el beanLogin:
		beanLogin.setLoggedUser( updatedUser );
		// limpiar los atributos usados  para renderizar la pagina:
		beanActionResult.setFinish(true);
		beanActionResult.setSuccess(true);
		return null;
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
			log.error("Unexpected Exception at 'updateUser()'");
		}
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU IMAGEN
	// // // // // // // // // // // // // // // // // //
	
	/**
	 * Actualiza la imagen del perfil del usuario con sesion iniciada. <br>
	 * Este metodo permite que un usuario dado pueda cambiar
	 * su propia imagen en su pagina del perfil.
	 * @param beanUserView objeto {@link BeanUserView} cuyo atributo
	 * {@link BeanUserView#user} contiene la nueva imagen introducida
	 * por el usuario en la vista
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return devuelve el valor 'null' para evitar que el CommandLink 
	 * que invoca a este metodo redirija la navegacion a otra pagina
	 */
	public String editLoggedUserAvatar(
			BeanUserView beanUserView, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		Part imagePartFromBeanUser = beanUserData.getImageProfile();
		if( imagePartFromBeanUser != null ){
			// 1: Actualizar la imagen subiendola a la cuenta de Azure:
			byte[] newImage = BeanUserImages.updateUserImage(
					beanUserView.getUser().getId(), imagePartFromBeanUser);
			// 2: Actualizar la imagen del usuario manejado en la vista:
			beanUserView.getUser().getUserInfoPrivacity().setImage(newImage);
			// 3: El nivel de privacidad de la imagen fue editado en la vista,
			// almacenado en beanUserView.getUser().getPrivacityData().getImage()
			// Actualizamos ese dato en la bdd:
			updateUser( beanUserView.getUser(), beanActionResult );
			// 4: Actualizar el beanLogin y la sesion:
			beanLogin.setLoggedUser( beanUserView.getUser() );
			Map<String, Object> session = FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap();
			session.put("LOGGED_USER", beanUserView.getUser());
			// limpiar los atributos usados para renderizar la pagina:
			beanActionResult.setFinish(true);
			beanActionResult.setSuccess(true);
		}
		return null;
	}
	
	/**
	 * Actualiza la imagen del perfil del usuario cuyo perfil se visita. <br>
	 * Este metodo permite que un usuario dado (previsiblemente un
	 * administrador) pueda cambiar la imagen del perfil ajeno que consulta.
	 * @param beanUserView objeto {@link BeanUserView} cuyo atributo
	 * {@link BeanUserView#user} indica la nueva imagen introducida
	 * por el usuario en la vista
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return devuelve el valor 'null' para evitar que el CommandLink 
	 * que invoca a este metodo redirija la navegacion a otra pagina
	 */
	public String editUserAvatar(
			BeanUserView beanUserView, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		Part imagePartFromBeanUser = beanUserData.getImageProfile();
		if( imagePartFromBeanUser != null ){
			// 1: Actualizar la imagen subiendola a la cuenta de Azure:
			byte[] newImage = BeanUserImages.updateUserImage(
					beanUserView.getUser().getId(), imagePartFromBeanUser);
			// 2: Actualizar la imagen del usuario manejado en la vista:
			beanUserView.getUser().getUserInfoPrivacity().setImage(newImage);
			// 3: El nivel de privacidad de la imagen fue editado en la vista,
			// almacenado en beanUserView.getUser().getPrivacityData().getImage()
			// Actualizamos ese dato en la bdd:
			updateUser( beanUserView.getUser(), beanActionResult );
			// limpiar los atributos usados para renderizar la pagina:
			beanActionResult.setFinish(true);
			beanActionResult.setSuccess(true);
		}
		return null;
	}
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU GENERO
	// // // // // // // // // // // // // // // //
	
	/**
	 * Actualiza el genero del usuario que ha iniciado la sesion.
	 * @param userView objeto {@link User} que almacena, entre otros datos,
	 * el genero actualizado por el usuario en la vista.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return devuelve el valor 'null' para evitar que el CommandLink 
	 * que invoca a este metodo redirija la navegacion a otra pagina
	 */
	public String editProfileGender(
			User userView, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		boolean newGender = userView.getUserInfoPrivacity().getGender();
		// Obtener los datos originales del usuario desde el beanLogin:
		User updatedUser = beanLogin.getLoggedUser();
		// Actualizar los datos originales con los datos editados en la vista:
		// en primer lugar actualizamos el genero:
		updatedUser.getUserInfoPrivacity().setGender(newGender);
		// en segundo lugar actualizamos el nivel de privacidad del genero:
		updatedUser.getPrivacityData().setGender(
				userView.getPrivacityData().getGender());
		// Actualizar base de datos:
		updateUser( updatedUser, beanActionResult );
		// Actualizar el beanLogin:
		beanLogin.setLoggedUser( updatedUser );
		beanLogin.getLoggedUser().getUserInfoPrivacity().setGender(newGender);
		Map<String, Object> session = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		session.put("LOGGED_USER", updatedUser);
		// limpiar los atributos usados  para renderizar la pagina:
		beanActionResult.setFinish(true);
		beanActionResult.setSuccess(true);
		return null;
	}
	
	// // // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU PAIS DE ORIGEN Y DE UBICACION
	// // // // // // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Actualiza los paises de origen y de ubicacion del usuario
	 * que ha iniciado la sesion.
	 * @param userView objeto {@link User} que almacena, entre otros datos,
	 * los paises de origen y de ubicacion actualizados por el usuario
	 * en la vista.
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return devuelve el valor 'null' para evitar que el CommandLink 
	 * que invoca a este metodo redirija la navegacion a otra pagina
	 */
	public String editProfileCountries(
			User userView, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		Country origin = userView.getUserInfoPrivacity()
				.getCountryOrigin();
		Country location = userView.getUserInfoPrivacity()
				.getCountryLocation();
		// Obtener los datos originales del usuario desde el beanLogin:
		User updatedUser = beanLogin.getLoggedUser();
		// Actualizar los datos originales con los datos editados en la vista:
		// en primer lugar actualizamos el pais de origen y de ubicacion:
		updatedUser.getUserInfoPrivacity().setCountryOrigin(origin);
		updatedUser.getUserInfoPrivacity().setCountryLocation(location);
		// en segundo lugar actualizamos el nivel de privacidad de ambos datos:
		updatedUser.getPrivacityData().setCountryOrigin(
				userView.getPrivacityData().getCountryOrigin());
		updatedUser.getPrivacityData().setCountryLocation(
				userView.getPrivacityData().getCountryLocation());
		// Actualizar base de datos:
		updateUser( updatedUser, beanActionResult );
		// Actualizar el beanLogin:
		beanLogin.setLoggedUser( updatedUser );
		beanLogin.getLoggedUser().getUserInfoPrivacity().setCountryOrigin(origin);
		beanLogin.getLoggedUser().getUserInfoPrivacity().setCountryLocation(location);
		Map<String, Object> session = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		session.put("LOGGED_USER", updatedUser);
		// limpiar los atributos usados  para renderizar la pagina:
		beanActionResult.setFinish(true);
		beanActionResult.setSuccess(true);
		return null;
	}
	
	// // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL ADMINISTRADOR CAMBIE EL ESTADO DE UN USUARIO
	// // // // // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Cambia el estado de activacion de un usuario dado
	 * @param user usuario que se actualiza
	 * @return Si la actualizacion se produce con existo, devuelve la regla
	 * de navegacion que redirige a la pagina de inicio
	 * ('successChangeUserStatus'). <br>
	 * Si se produce alguna excepcion, devuelve la regla de navegacion
	 * que redirige a la pagina de error desconocido ('errorUnexpected').
	 */
	public String changeUserStatus(User user){
		boolean newStatus = !user.getActive();
		user.setActive(newStatus);
		try {
			Factories.getService().getServiceUser().updateAllDataByUser(user);
			// Si se desactiva un usuario se agrega a la lista de desactivados:
			if( newStatus==false ){ putUserDeactivatedInAppContext(user); }
			// Si se activa un usuario se elimina de la lista de desactivados:
			if( newStatus==true ){deleteUserActivatedFromAppContext(user);}
		} catch (Exception e) {
			log.error("Unexpected Exception at 'changeUserStatus()'");
			return "errorUnexpectedAdmin";
		}
		return "successChangeUserStatus";
	}
	
	/**
	 * Agrega el identificador del usuario dado a la lista de usuarios
	 * desactivados guardada en el contexto de aplicacion ('DEACTIVATED_USERS').
	 * @param user usuario que se desactiva
	 */
	private void putUserDeactivatedInAppContext(User user) {
		Map<String, Object> application = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
		// Obtener, del contexto Aplicacion, la lista "DEACTIVATED_USERS":
		@SuppressWarnings("unchecked")
		List<Long> deactivatedUsersIDs =
				(List<Long>) application.get("DEACTIVATED_USERS");
		// Si no existia, la genera ahora:
		if( deactivatedUsersIDs == null ){
			deactivatedUsersIDs = new ArrayList<Long>();
		}
		// Guardar el usuario desactivado en la lista "DEACTIVATED_USERS":
		deactivatedUsersIDs.add(user.getId());
		// Guardar, en el contexto Aplicacion, la lista "DEACTIVATED_USERS":
		application.put("DEACTIVATED_USERS", deactivatedUsersIDs);
	}
	
	/**
	 * Elimina el identificador del usuario dado de la lista de usuarios
	 * desactivados guardada en el contexto de aplicacion ('DEACTIVATED_USERS').
	 * @param user usuario que se desactiva
	 */
	private void deleteUserActivatedFromAppContext(User user) {
		Map<String, Object> application = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
		// Obtener, del contexto Aplicacion, la lista "DEACTIVATED_USERS":
		@SuppressWarnings("unchecked")
		List<Long> deactivatedUsersIDs =
				(List<Long>) application.get("DEACTIVATED_USERS");
		// Si existe la lista "DEACTIVATED_USER", buscar el usuario activado:
		if( deactivatedUsersIDs != null ){
			// Si encuentra el usuario activado en la lista "DEACTIVATED_USERS",
			// lo elimina de la lista
			if( deactivatedUsersIDs.contains(user.getId()) ){ 
				deactivatedUsersIDs.remove( user.getId() );
			}
			// Guardar, en el contexto Aplicacion, la lista "DEACTIVATED_USERS":
			application.put("DEACTIVATED_USERS", deactivatedUsersIDs);
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/**
	 * Es el bean {@link BeanUserData} quien almacena en un
	 * Map&lt;Long,Language&gt; los lenguages practicados por el usuario.
	 * Este metodo obtiene las claves (Long) de dicho Map.
	 * @return
	 * la lista de los IDs de los idiomas (Languages) practicados
	 * por el usuario.
	 */
	public List<Long> getListPracticingLanguagesIDs(){
		Map<Long, Language> mapPracticingLanguages = 
				beanUserData.getMapPracticingLanguages();
		return new ArrayList<Long>( mapPracticingLanguages.keySet() );
	}
	public void setListPracticingLanguagesIDs(List<Long> languagesIDs){
		listPracticingLanguagesIDs = languagesIDs;
	}
	
	/**
	 * Es el bean {@link BeanUserData} quien almacena en un
	 * Map&lt;Long,Language&gt; los lenguages practicados por el usuario.
	 * Este metodo obtiene las claves (Long) de dicho Map.
	 * @return
	 * la lista de los IDs de los idiomas (Languages) maternos del usuario.
	 */
	public List<Long> getListNativeLanguagesIDs(){
		Map<Long, Language> mapNativeLanguages = 
				beanUserData.getMapNativeLanguages();
		return new ArrayList<Long>( mapNativeLanguages.keySet() );
	}
	public void setListNativeLanguagesIDs(List<Long> languagesIDs){
		listNativeLanguagesIDs = languagesIDs;
	}
}
