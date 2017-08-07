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
import com.loqua.presentation.bean.applicationBean.BeanUserImages;
import com.loqua.presentation.bean.requestBean.BeanActionResult;

public class BeanUserEditProfile implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private List<Long> listNativeLanguagesIDs;
	private List<Long> listPracticingLanguagesIDs;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanUserData}") 
	private BeanUserData beanUserData;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanUser();
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
	
	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SUS LISTAS DE LENGUAGES
	// // // // // // // // // // // // // // // // // // // // //
	
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
			// TODO
			return "errorUnexpected";
		}
		beanActionResult.setFinish(true);
		beanActionResult.setSuccess(true);
		return null;
	}
	
	private boolean verifyLanguagesSelected(){
		for( Long practicedLanguageID : listPracticingLanguagesIDs ){
			if( listNativeLanguagesIDs.contains(practicedLanguageID) ){
				return false;
			}
		}
		return true;
	}
	
	private void saveUserPracticedLanguages(
			List<Long> beanUserPracticedLanguagesIDs) throws
			EntityAlreadyFoundException, EntityNotFoundException {
		User user = beanLogin.getLoggedUser();
		// Crear los UserPracticedLanguages pertinentes en bdd:
		Factories.getService().getServiceLanguage().createUserPracticedLanguage(
				user, beanUserPracticedLanguagesIDs,listPracticingLanguagesIDs);
		// Eliminar los UserPracticedLanguages pertinentes en bdd:
		Factories.getService().getServiceLanguage().deleteUserPracticedLanguage(
				user, beanUserPracticedLanguagesIDs,listPracticingLanguagesIDs);
		// Actualizar el mapPracticingLanguages de beanUserData
		updatePracticingLanguagesInSession();
	}
	
	private void saveUserNativeLanguages(
			List<Long> beanUserNativeLanguagesIDs) throws
			EntityAlreadyFoundException, EntityNotFoundException {
		User user = beanLogin.getLoggedUser();
		// Crear los UserNativeLanguages pertinentes en bdd:
		Factories.getService().getServiceLanguage().createUserNativeLanguage(
				user, beanUserNativeLanguagesIDs,listNativeLanguagesIDs);
		// Eliminar los UserNativeLanguages pertinentes en bdd:
		Factories.getService().getServiceLanguage().deleteUserNativeLanguage(
				user, beanUserNativeLanguagesIDs,listNativeLanguagesIDs);
		// Actualizar el mapNativeLanguages de beanUserData
		updateNativeLanguagesInSession();
	}
	
	/**
	 * Actualiza el Map<Long,Languages> mapPracticingLanguages de beanUserData,
	 * sobreescribiendolo con los valores de la lista listPracticingLanguagesIDs
	 */
	private void updatePracticingLanguagesInSession() {
		Map<Long,Language> mapPracticedLanguages=new HashMap<Long,Language>();
		mapPracticedLanguages = Factories.getService().getServiceLanguage()
				.getMapLanguagesByIdsFromDB(listPracticingLanguagesIDs);
				//FromCache
		beanUserData.setMapPracticingLanguages(mapPracticedLanguages);
	}
	
	/**
	 * Actualiza el Map<Long,Languages> mapNativeLanguages de beanUserData,
	 * sobreescribiendolo con los valores de la lista listNativeLanguagesIDs
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
	
	public String editProfilePrivacity(
			User userView, BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		// Obtener los datos originales del usuario desde el beanLogin:
		User updatedUser = beanLogin.getLoggedUser();
		// Actualizar los datos originales con los datos editados en la vista:
		updatedUser.getPrivacityData().setPublications(
				userView.getPrivacityData().getPublications());
		updatedUser.getPrivacityData().setContactsList(
				userView.getPrivacityData().getContactsList());
		updatedUser.getPrivacityData().setAppearingInSearcher(
				userView.getPrivacityData()
				.getAppearingInSearcher());
		updatedUser.getPrivacityData().setReceivingCorrectionRequests(
				userView.getPrivacityData()
				.getReceivingCorrectionRequests());
		updatedUser.getPrivacityData().setReceivingMessages(
				userView.getPrivacityData().getReceivingMessages());
		// Actualizar base de datos:
		updateUser( updatedUser, beanActionResult );
		// Actualizar el beanLogin:
		beanLogin.setLoggedUser( updatedUser );
		// limpiar los atributos usados  para renderizar la pagina:
		beanActionResult.setFinish(true);
		beanActionResult.setSuccess(true);
		return null;
	}
	
	private void updateUser(User user, BeanActionResult beanActionResult) {
		try{
			Factories.getService().getServiceUser().updateAllDataByUser(
					user, true);
		} catch (EntityNotFoundException e) {
			beanActionResult.setMsgActionResult("errorUnknownUrl");
			// TODO log
		}
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU IMAGEN
	// // // // // // // // // // // // // // // // // //
	
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
	
	public String changeUserStatus(User editUser){
		boolean newStatus = !editUser.getActive();
		editUser.setActive(newStatus);
		try {
			Factories.getService().getServiceUser().updateAllDataByUser(
					editUser, true);
			// Si se desactiva un usuario se agrega a la lista de desactivados:
			if( newStatus==false ){ putUserDeactivatedInAppContext(editUser); }
			// Si se activa un usuario se elimina de la lista de desactivados:
			if( newStatus==true ){deleteUserActivatedFromAppContext(editUser);}
		} catch (Exception e) {
			return "errorUnexpectedAdmin";
			// TODO log
		}
		return "successChangeUserStatus";
	}
	
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
	 * Es el bean beanUserData quien almacena en un Map&lt;Long,Language&gt; los
	 * lenguages practicados por el usuario.
	 * Este metodo obtiene las claves (Long) de dicho Map.
	 * @return
	 * la lista de los IDs de los idiomas (Languages) practicados por el usuario.
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
	 * Es el bean beanUserData quien almacena en un Map&lt;Long,Language&gt; los
	 * lenguages maternos del usuario.
	 * Este metodo obtiene las claves (Long) de dicho Map.
	 * @return
	 * la lista de los IDs de los idiomas (Languages) maternos del usuario.
	 */
	public List<Long> getListNativeLanguagesIDs(){
		Map<Long, Language> mapNativeLanguages = beanUserData.getMapNativeLanguages();
		return new ArrayList<Long>( mapNativeLanguages.keySet() );
	}
	public void setListNativeLanguagesIDs(List<Long> languagesIDs){
		listNativeLanguagesIDs = languagesIDs;
	}
}
