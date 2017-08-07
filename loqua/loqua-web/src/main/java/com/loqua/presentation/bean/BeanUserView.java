package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.Country;
import com.loqua.model.Publication;
import com.loqua.model.User;
import com.loqua.model.types.TypePrivacity;
import com.loqua.presentation.bean.applicationBean.BeanUtils;

/**
 * Administra los datos del usuario que son manejados en las vistas.
 * A diferencia del beanUserData, este bean almacena datos que deben persistir
 * unicamente durante el ambito de vista
 * Las vistas (paginas y snippets) donde el usuario edita sus propios datos
 * utilizan este bean para guardarlos de forma temporal,
 * mientras no termine con exito la actualizacion de los mismos en base de datos.
 * <br/>
 * Por ejemplo: en el snippet 'modal_windows.xhtml' (incluido en la pagina
 * 'profile_edit.xhtml'), al editar el email de usuario, ese dato se almacena
 * temporalmente en la propiedad user.email de este beanUserView antes de
 * efectuar dicha operacion en la base de datos.
 * @author Gonzalo
 *
 */
public class BeanUserView implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Cadena de texto utilizada como parametro en la URL para poder visitar
	 * el perfil de un usuario concreto
	 */
	private String urlUserId;
	private User user;
	private Long countryOriginId;
	private Long countryLocationId;
	private String level;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		user=new User();
	}
	
	private void initBeanLogin() {
		// Buscamos el BeanLogin en la sesion.
		beanLogin = null;
		beanLogin = (BeanLogin)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanLogin");
		// si no existe lo creamos e inicializamos:
		if( beanLogin == null ){
			beanLogin = new BeanLogin();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanLogin", beanLogin);
		}
	}

	@PreDestroy
	public void end(){
		//clearStatus();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Obtiene el numero de usuarios de la tabla User, pero exceptuando
	 * aquellos que estan en estado 'removed'.
	 * @return numero total de usuarios no eliminados.
	 */
	public int getNumRegisteredUsers(){
		int result = 0;
		try{
			result = Factories.getService().getServiceUser()
					.getNumRegisteredUsersAndAdminFromDB(); // FromCache
		}catch (Exception e){
			// TODO Log
		}
		return result;
	}
	
	public int getNumOnlineUsers(){
		Map<String, Object> application = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
		int result = 0;
		if( application.containsKey("NUM_LOGGED_USERS") ){
			result = (Integer)application.get("NUM_LOGGED_USERS");
		}
		return result;
	}
	
	public User getUserById(Long userId){
		User result = null;
		try{
			result=Factories.getService().getServiceUser().getUserById(userId);
		} catch (Exception e) {
			// TODO Log
		}
		return result;
	}
	
	public static User getUserByIdStatic(Long userId){
		// Este metoo se usa desde el "FilterPrileUser"
		// y desde el "FilterProfileContactsUser"
		User result = null;
		try{
			result=Factories.getService().getServiceUser().getUserById(userId);
		} catch (Exception e) {
			// TODO Log
		}
		return result;
	}
	
	public String getOutputLinkToProfile(Long userId){
		User loggedUser = beanLogin.getLoggedUser();
		if( loggedUser.getId().equals(userId) ){
			return BeanUtils.getUrlUserPages() + "profile_me.xhtml";
		}else{
			return BeanUtils.getUrlUserPages() + "profile_user.xhtml?"
					+ "user="+userId;
		}
	}
	
	public String getOutputLinkToContacts(Long userId){
		User loggedUser = beanLogin.getLoggedUser();
		if( loggedUser.getId().equals(userId) ){
			return BeanUtils.getUrlUserPages() + "profile_contacts_me.xhtml";
		}else{
			return BeanUtils.getUrlUserPages() + "profile_contacts_user.xhtml"
					+ "?" + "user="+userId;
		}
	}
	
	public String getActionLinkToIndex(/*Long userId*/){
		String result="errorUnexpected";
		//User user = getUserById(userId);
		if( beanLogin.getLoggedUser()==null ){
			return "linkAnonymousIndex";
		}
		else{
			if( beanLogin.getLoggedUser().getId()==(user.getId()) ){
				if( user.getRole() == User.USER )return "linkRegisteredIndex";
				else if(user.getRole()==User.ADMINISTRATOR)return "linkAdminIndex";
			}else{
				return "linkAnonymousIndex";
			}
		}
		return result;
	}
	
	public boolean existsNotRemovedUser(){
		boolean result = false;
		if(user != null){
			result = ! user.getRemoved();
		}
		return result;
	}
	
	/**
	 * Evalua si se debe permitir al usuario logueado
	 * editar la privacidad de una publicacion dada.
	 * Para ello comprueba si el usuario logueado es administrador,
	 * o si es el autor de la publicacion dada, en cuyo caso comprueba tambien
	 * el valor de la propiedad "editablePrivacity".
	 * @param authorOfData usuario autor del da
	 * @param privacityOfData
	 * @return true si la privacidad de la publicacion dada puede ser editada
	 */
	public boolean showEditablePrivacity(Publication publication){
		User authorOfData = publication.getUser();
		// comprueba si quien lee la publicacion es administrador:
		if( loggedUserIsAdmin() ){ return true; }
		// comprueba si quien lee la publicacion es el autor de la misma:
		if( loggedUserIsAuthorOfData(authorOfData) ){
			if( publication.getSelfGenerated()==false ){
				// todas las publicaciones no automaticas se pueden editar:
				return true;
			}
			if( publication.getEvent().getEditablePrivacity()==true ){
				// todas las publicaciones 'editablePrivacity' se pueden editar:
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Evalua si una publicacion/dato/imagen sera mostrada al usuario logueado.
	 * Para ello comprueba si el usuario logueado es administrador,
	 * o si es el autor de la publicacion dada, en cuyo caso comprueba tambien
	 * el nivel de privacidad del dato que se evalua.
	 * @param authorOfData usuario autor del da
	 * @param privacityOfData
	 * @return true si la publicacion dada sera visible para el usuario logueado
	 */
	public boolean shouldBeShownByPrivacity(User authorOfData,
			TypePrivacity privacityOfData){
		User loggedUser = beanLogin.getLoggedUser();
		// si quien lee la publicacion es administrador:
		if( loggedUserIsAdmin() ){ return true; }
		// si quien lee la publicacion/dato/imagen no es el autor de la misma:
		if( ! loggedUserIsAuthorOfData(authorOfData) ){
			if( privacityOfData == TypePrivacity.PRIVATE ){
				// la publicacion/dato/imagen es de nivel privado
				return false;
			}else if( privacityOfData == TypePrivacity.CONTACTS ){
				// la publicacion/dato/imagen es de nivel de contactos
				if( ! BeanUserContacts.areContacts(loggedUser, authorOfData) ){
					// quien lee la publicacion/dato no es contacto del autor
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean shouldBeShownByPrivacityStatic(
			User loggedUser, User authorOfData, TypePrivacity privacityOfData){
		// Este metodo estatico se usa desde el applicationBean.BeanUserImages
		if(loggedUser!=null && loggedUser.getRole().equals(User.ADMINISTRATOR)){
			// quien lee la publicacion/dato/imagen es usuario administrador
			return true;
		}
		if(loggedUser==null || !loggedUser.getId().equals(authorOfData.getId())){
			// quien lee la publicacion/dato/imagen no es el autor de la misma
			if( privacityOfData == TypePrivacity.PRIVATE ){
				// la publicacion/dato/imagen es de nivel privado
				return false;
			}else if( privacityOfData == TypePrivacity.CONTACTS ){
				// la publicacion/dato/imagen es de nivel de contactos
				if( ! BeanUserContacts.areContacts(loggedUser, authorOfData) ){
					// quien lee la publicacion/dato no es contacto del autor
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean loggedUserIsAdminOrAuthorOfData(User authorOfData){
		return loggedUserIsAdmin() || loggedUserIsAuthorOfData(authorOfData);
	}
	
	public boolean loggedUserIsAdmin(){
		User loggedUser = beanLogin.getLoggedUser();
		if(loggedUser!=null && loggedUser.getRole().equals(User.ADMINISTRATOR)){
			return true;
		}
		return false;
	}
	
	public boolean loggedUserIsAuthorOfData(User authorOfData){
		User loggedUser = beanLogin.getLoggedUser();
		if(loggedUser!=null && loggedUser.getId().equals(authorOfData.getId())){
			return true;
		}
		return false;
	}
	
	/*
	private void clearStatus() {
		urlUserId = null;
		user=null;
		mapPracticingLanguages = null;
		mapNativeLanguages = null;
	}
	*/
	
	public String getUserLevel(User user) {
		int userPoints = user.getUserInfo().getPoints();
		if( userPoints<50 ){
			level=BeanSettingsSession.getTranslationStatic("titleLevelLearner");
		}else if( userPoints<1000 ){
			level=BeanSettingsSession.getTranslationStatic("titleLevelStudent");
		}else if( userPoints<5000 ){
			level=BeanSettingsSession.getTranslationStatic("titleLevelSpeaker");
		}else{
			level=BeanSettingsSession.getTranslationStatic("titleLevelMaster");
		}
		return level;
	}
	public void setUserLevel(String level) {
		this.level = level;
	}

	public boolean isCountryOriginAvailable(User user){
		boolean result = false;
		Country origin = user.getUserInfoPrivacity().getCountryOrigin();
		if( origin!=null ){
			return true;
		}
		return result;
	}
	public boolean isCountryLocationAvailable(User user){
		boolean result = false;
		Country location = user.getUserInfoPrivacity().getCountryLocation();
		if( location!=null ){
			return true;
		}
		return result;
	}
	public boolean showCountryLocation(User user){
		// En el snippet "profile_header_userData" se llama a este metodo asi:
		// beanUserData.showCountryLocation(beanUserData.user)
		// En ese caso puede parecer innecesario pasar por parametro el User,
		// puesto que bastaria con acceder aqui a la variable local "user",
		// sin embargo en el futuro probablemente mostremos el pais de origen y de
		// ubicacion en cada post que el usuario publique en la pagina del foro
		// en cuyo caso quiza hariamos algo como
		// beanUserData.showCountryLocation(comment.user)
		boolean result = false;
		Country origin = user.getUserInfoPrivacity().getCountryOrigin();
		Country location = user.getUserInfoPrivacity().getCountryLocation();
		if( location!=null && location.equals(origin)==false ){
			return true;
		}
		return result;
	}
	
	public String getCountryOriginName(User user){
		String country = 
				user.getUserInfoPrivacity().getCountryOrigin().getName();
		return BeanSettingsSession.getTranslationCountriesStatic(country);
	}
	public String getCountryLocationName(User user){
		String location = 
				user.getUserInfoPrivacity().getCountryLocation().getName();
		return BeanSettingsSession.getTranslationCountriesStatic(location);
	}
	
	public Long getCountryOriginId() {
		Country countryOrigin = user.getUserInfoPrivacity()
				.getCountryOrigin();
		if( countryOrigin!=null ){
			countryOriginId = countryOrigin.getId();
		}
		return countryOriginId;
	}
	public void setCountryOriginId(Long id) {
		try {
			// Actualizamos la propiedad countryOrigin de este bean
			countryOriginId = id;
			// Actualizamos la propiedad user de este bean
			Country country = Factories.getService().getServiceCountry()
					.getCountryById(countryOriginId);
			user.getUserInfoPrivacity().setCountryOrigin(country);
		} catch (EntityNotFoundException e) {
			// TODO
		}
	}
	
	public Long getCountryLocationId() {
		Country countryLocation=user.getUserInfoPrivacity()
				.getCountryLocation();
		if( countryLocation!=null ){
			countryLocationId = countryLocation.getId();
		}
		return countryLocationId;
	}
	public void setCountryLocationId(Long id) {
		try {
			// Actualizamos la propiedad countryOrigin de este bean
			countryLocationId = id;
			// Actualizamos la propiedad user de este bean
			Country country = Factories.getService().getServiceCountry()
					.getCountryById(countryLocationId);
			user.getUserInfoPrivacity().setCountryLocation(country);
		} catch (EntityNotFoundException e) {
			// TODO
		}
	}
	
	public TypePrivacity getPrivacityForUserCountries() {
		return user.getPrivacityData().getCountryOrigin();
	}
	public void setPrivacityForUserCountries(TypePrivacity countriesPrivacity) {
		user.getPrivacityData().setCountryOrigin(countriesPrivacity);
		user.getPrivacityData().setCountryLocation(countriesPrivacity);
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
	
	public String getUrlUserId() {
		return urlUserId;
	}
	public void setUrlUserId(String urlUserId) {
		this.urlUserId = urlUserId;
	}
	
	public void setUserByGivenUrl() {
		this.user = getUserById( Long.parseLong(urlUserId) );
	}
	
	public String getTypeUser(){
		return User.USER;
	}
	public String getTypeAdministrator(){
		return User.ADMINISTRATOR;
	}
}
