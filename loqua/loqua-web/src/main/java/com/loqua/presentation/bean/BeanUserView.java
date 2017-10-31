package com.loqua.presentation.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.Country;
import com.loqua.model.Publication;
import com.loqua.model.Event;
import com.loqua.model.User;
import com.loqua.model.types.TypePrivacity;
import com.loqua.presentation.bean.applicationBean.BeanUtils;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Administra los datos del usuario que son manejados en las vistas.
 * A diferencia del {@link BeanUserData}, este bean almacena
 * datos que deben persistir unicamente durante el ambito de vista. <br>
 * Las vistas (paginas y snippets) donde el usuario edita sus propios datos
 * utilizan este bean para guardarlos de forma temporal, mientras
 * no termine con exito la actualizacion de los mismos en base de datos. <br>
 * Por ejemplo: en el snippet 'modal_windows.xhtml' (incluido en la pagina
 * 'profile_edit.xhtml'), al editar el email de usuario, ese dato se almacena
 * temporalmente en la propiedad user.email de este BeanUserView antes de
 * efectuar dicha operacion en la base de datos.
 * @author Gonzalo
 */
public class BeanUserView implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Parametro 'user' recibido en la URL, que indica el identificador
	 * del usuario cuya pagina de perfil se consulta. <br>
	 * Solo se utiliza cuando se visita un perfil ajeno. <br>
	 * Se inicializa en la vista 'profile_user.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private String urlUserId;
	
	/** Usuario (objeto {@link User}) cuyos datos se consultan. Si el atributo
	 * {@link #urlUserId} de esta clase esta inicializado se corresponde con
	 * el identificador de este objeto User. <br>
	 * En la vista 'profile_user.xhtml' se inicializa mediante
	 * el &lt;viewAction&gt; que invoca al metodo {@link #setUserByGivenUrl}.
	 * <br>
	 * En la vista 'profile_me.xhtml' se inicializa mediante
	 * el &lt;viewAction&gt; que invoca al metodo 'set' del atributo,
	 * y coincide con el usuario logueado almacenado en
	 * {@link BeanLogin#loggedUser} */
	private User user;
	
	/** Identificador del pais de origen del usuario */
	private Long countryOriginId;
	
	/** Identificador del pais de ubicacion del usuario */
	private Long countryLocationId;
	
	/** Una palabra que describe el nivel del usuario segun su puntuacion */
	private String level;
	
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
		user=new User();
	}
	
	/** Inicializa el objeto {@link BeanLogin} inyectado */
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

	/** Destructor del bean. */
	@PreDestroy
	public void end(){
		//clearStatus();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Halla el usuario cuyo identificador coincide con el parametro dado.
	 * @param userId atributo 'id' del User que se consulta.
	 * @return objeto User cuyo atributo 'id' coincide con el parametro dado,
	 * o valor 'null' si no existe
	 */
	public User getUserById(Long userId){
		return getUserByIdStatic(userId);
	}
	
	/**
	 * Version estatica del metodo {@link #getUserById}.
	 * @param userId atributo 'id' del User que se consulta.
	 * @return objeto User cuyo atributo 'id' coincide con el parametro dado,
	 * o valor 'null' si no existe
	 */
	public static User getUserByIdStatic(Long userId){
		// Este metodo se usa desde el "FilterProfileUser"
		// y desde el "FilterProfileContactsUser"
		User result = null;
		try{
			result=Factories.getService().getServiceUser().getUserById(userId);
		} catch (Exception e) {
			new BeanUserView().log.error("Unexpected Exception at "
					+ "'getUserByIdStatic()'");
		}
		return result;
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina del perfil de un
	 * usuario.
	 * @param userId identificador del usuario cuyo peril se visita
	 * @return enlace al perfil delusuario dado, que puede ser empleado
	 * desde los componentes OutputLink de las vistas
	 */
	public String getOutputLinkToProfile(Long userId){
		User loggedUser = beanLogin.getLoggedUser();
		if( loggedUser.getId().equals(userId) ){
			return BeanUtils.getUrlUserPages() + "profile_me.xhtml";
		}else{
			return BeanUtils.getUrlUserPages() + "profile_user.xhtml?"
					+ "user="+userId;
		}
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina de contactos de un
	 * usuario.
	 * @param userId identificador del usuario cuya lista de contactos se visita
	 * @return enlace al perfil delusuario dado, que puede ser empleado
	 * desde los componentes OutputLink de las vistas
	 */
	public String getOutputLinkToContacts(Long userId){
		User loggedUser = beanLogin.getLoggedUser();
		if( loggedUser.getId().equals(userId) ){
			return BeanUtils.getUrlUserPages() + "profile_contacts_me.xhtml";
		}else{
			return BeanUtils.getUrlUserPages() + "profile_contacts_user.xhtml"
					+ "?" + "user="+userId;
		}
	}
	
	/**
	 * Halla el enlace necesario para acceder a la pagina de inicio del
	 * usuario logueado.
	 * @return Si la sesion ha caducado, o si por cualquier motivo se ha cerrado
	 * la sesion del usuario, devuelve la regla de navegacion que conduce a la
	 * pagina de error por defecto. <br>
	 * De lo contrario, devuelve la regla de navegacion que conduce a la pagina
	 * de inicio del usuario logueado.
	 */
	public String getActionLinkToIndex(/*Long userId*/){
		String result="errorUnexpected";
		//User user = getUserById(userId);
		if( beanLogin.getLoggedUser()==null ){
			return "linkAnonymousIndex";
		}
		else{
			if( beanLogin.getLoggedUser().getId()==(user.getId()) ){
				if( user.getRole() == User.USER )return "linkRegisteredIndex";
				else if(user.getRole()==User.ADMINISTRATOR)
					return "linkAdminIndex";
			}else{
				return "linkAnonymousIndex";
			}
		}
		return result;
	}
	
	/**
	 * Comprueba si el usuario cuyos datos se consultan (atributo {@link #user}
	 * del Bean) no se halla en estado eliminado.
	 * @return
	 * 'true' si el usuario esta eliminado <br>
	 * 'false' si el usuario no esta eliminado
	 */
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
	 * el valor de la propiedad {@link Event#editablePrivacity}.
	 * @param publication publicacion que se consulta
	 * @return
	 * 'true' si la privacidad de la publicacion dada puede ser editada <br>
	 * 'false' si la privacidad no puede ser editada
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
	 * el nivel de privacidad del dato que se consulta.
	 * @param authorOfData usuario autor del dato que se consulta
	 * @param privacityOfData privacidad del dato que se consulta
	 * @return
	 * 'true' si la publicacion dada es visible para el usuario logueado <br>
	 * 'false' si la publicacion no es visible
	 */
	public boolean shouldBeShownByPrivacity(User authorOfData,
			TypePrivacity privacityOfData){
		return shouldBeShownByPrivacityStatic(
				beanLogin.getLoggedUser(),authorOfData,privacityOfData);
	}
	
	/**
	 * Version estatica del metodo {@link #shouldBeShownByPrivacity}
	 * @param loggedUser usuario que consulta el dato que se comprueba
	 * @param authorOfData usuario autor del dato
	 * @param privacityOfData privacidad del dato
	 * @return
	 * 'true' si la publicacion dada es visible para el usuario logueado <br>
	 * 'false' si la publicacion no es visible
	 * @see #shouldBeShownByPrivacity
	 */
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
	
	/**
	 * Comprueba si el usuario logueado es administrador o autor del dato
	 * indicado
	 * @param authorOfData el usuario que si es autor del dato que se comprueba
	 * @return
	 * 'true' si el usuario que consulta la informacion es administrador
	 * o autor del dato indicado <br>
	 * 'false' si el usuario no es administrador ni autor del dato
	 */
	public boolean loggedUserIsAdminOrAuthorOfData(User authorOfData){
		return loggedUserIsAdmin() || loggedUserIsAuthorOfData(authorOfData);
	}
	
	/**
	 * Comprueba si el usuario logueado es administrador
	 * @return
	 * 'true' si el usuario es administrador <br>
	 * 'false' si el usuario no es administrador
	 */
	public boolean loggedUserIsAdmin(){
		User loggedUser = beanLogin.getLoggedUser();
		if(loggedUser!=null && loggedUser.getRole().equals(User.ADMINISTRATOR)){
			return true;
		}
		return false;
	}
	
	/**
	 * Comprueba si el usuario logueado es el autor del dato indicado
	 * @param authorOfData el usuario que si es autor del dato que se comprueba
	 * @return
	 * 'true' si el usuario que consulta la informacion es autor
	 * del dato indicado <br>
	 * 'false' si el usuario no es autor del dato
	 */
	public boolean loggedUserIsAuthorOfData(User authorOfData){
		User loggedUser = beanLogin.getLoggedUser();
		if(loggedUser!=null && loggedUser.getId().equals(authorOfData.getId())){
			return true;
		}
		return false;
	}
	
	/*
	 * Borra el estado del Bean sobreescribiendo las propiedades del mismo
	 * con sus valores por defecto.
	private void clearStatus() {
		urlUserId = null;
		user=null;
	}
	*/
	
	/**
	 * Determina cual es el nivel del usuario dado segun su puntuacion,
	 * describiendolo en una palabra clave
	 * @param user usuario que se consulta
	 * @return una palabra que indica el nivel de puntuacion del usuario:
	 * <ul><li>si tiene 49 puntos o menos: 'titleLevelLearner'
	 * </li><li>si tiene entre 50 puntos y 999: 'titleLevelStudent'
	 * </li><li>si tiene entre 1000 puntos y 4999: 'titleLevelSpeaker'
	 * </li><li>si tiene 5000 puntos o mas: 'titleLevelMaster'
	 * </li></ul>
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
	/**
	 * Sobreescribe el valor del atributo {@link #level}
	 * @param level valor que se sobreescribe
	 */
	public void setUserLevel(String level) {
		this.level = level;
	}

	/**
	 * Evalua si el pais de origen del usuario dado sera mostrado
	 * al usuario logueado (al usuario que lo consulta),
	 * segun el nivel de privacidad de dicho dato.
	 * @param user usuario cuyo pais de origen se consulta
	 * @return
	 * 'true' si el pais de origen del usuario dado es visible
	 * para el usuario logueado <br>
	 * 'false' si el pais de origen no es visible
	 */
	public boolean isCountryOriginAvailable(User user){
		boolean result = false;
		Country origin = user.getUserInfoPrivacity().getCountryOrigin();
		if( origin!=null ){
			return true;
		}
		return result;
	}
	
	/**
	 * Evalua si el pais de ubicacion del usuario dado sera mostrado
	 * al usuario logueado (al usuario que lo consulta),
	 * segun el nivel de privacidad de dicho dato.
	 * @param user usuario cuyo pais de ubicacion se consulta
	 * @return
	 * 'true' si el pais de ubicacion del usuario dado es visible
	 * para el usuario logueado <br>
	 * 'false' si el pais de ubicacion no es visible
	 */
	public boolean isCountryLocationAvailable(User user){
		boolean result = false;
		Country location = user.getUserInfoPrivacity().getCountryLocation();
		if( location!=null ){
			return true;
		}
		return result;
	}
	
	/**
	 * Comprueba si el pais de ubicacion del usuario dado sera mostrado
	 * al usuario logueado (al usuario que lo consulta), segun coincida con
	 * el pais de origen. <br>
	 * Este metodo existe porque, por cuestion estetica, desde las vistas solo
	 * se muestra el pais de ubicacion del usuario si es distinto del de origen.
	 * @param user usuario cuyo pais de ubicacion se consulta
	 * @return true si el pais de ubicacion de un perfil es visible para
	 * el usuario que lo consulta
	 */
	public boolean showCountryLocation(User user){
		// En el snippet "profile_header_userData" se llama a este metodo asi:
		// beanUserData.showCountryLocation(beanUserData.user)
		// En ese caso puede parecer innecesario pasar por parametro el User,
		// puesto que bastaria con acceder aqui a la variable local "user",
		// sin embargo en el futuro probablemente mostremos el pais de origen o
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
	
	/**
	 * Obtiene el nombre (traducido al idioma del Locale) del pais de origen
	 * del usuario dado 
	 * @param user usuario cuyo pais de origen se consulta
	 * @return el nombre del pais de origen del usuario
	 */
	public String getCountryOriginName(User user){
		String country = 
				user.getUserInfoPrivacity().getCountryOrigin().getName();
		return BeanSettingsSession.getTranslationCountriesStatic(country);
	}
	
	/**
	 * Obtiene el nombre (traducido al idioma del Locale) del pais de ubicacion
	 * del usuario dado 
	 * @param user usuario cuyo pais de ubicacion se consulta
	 * @return el nombre del pais de ubicacion del usuario
	 */
	public String getCountryLocationName(User user){
		String location = 
				user.getUserInfoPrivacity().getCountryLocation().getName();
		return BeanSettingsSession.getTranslationCountriesStatic(location);
	}
	
	/**
	 * Halla el identificador del pais de origen del usuario cuyos datos
	 * se consultan (@link #user).
	 * @return pais de origen del usuario
	 */
	public Long getCountryOriginId() {
		Country countryOrigin = user.getUserInfoPrivacity()
				.getCountryOrigin();
		if( countryOrigin!=null ){
			countryOriginId = countryOrigin.getId();
		}
		return countryOriginId;
	}
	/**
	 * Sobreescribe los atributos {@link #countryOriginId} y {@link #user}
	 * consultando el pais de origen segun su identificador
	 * recibido por parametro.
	 * @param id identificador del pais de origen del usuario cuyos datos
	 * se consultan (atributo {@link #user})
	 */
	public void setCountryOriginId(Long id) {
		try {
			// Actualizamos la propiedad countryOrigin de este bean
			countryOriginId = id;
			// Actualizamos la propiedad user de este bean
			Country country = Factories.getService().getServiceCountry()
					.getCountryById(countryOriginId);
			user.getUserInfoPrivacity().setCountryOrigin(country);
		} catch (EntityNotFoundException e) {
			log.error("EntityNotFoundException at 'getUserByIdStatic()'");
		}
	}
	
	/**
	 * Halla el identificador del pais de ubicacion del usuario cuyos datos
	 * se consultan (@link #user).
	 * @return pais de ubicacion del usuario
	 */
	public Long getCountryLocationId() {
		Country countryLocation=user.getUserInfoPrivacity()
				.getCountryLocation();
		if( countryLocation!=null ){
			countryLocationId = countryLocation.getId();
		}
		return countryLocationId;
	}
	/**
	 * Sobreescribe los atributos {@link #countryLocationId} y {@link #user}
	 * consultando el pais de ubicacion segun su identificador
	 * recibido por parametro.
	 * @param id identificador del pais de ubicacion del usuario cuyos datos
	 * se consultan (atributo {@link #user})
	 */
	public void setCountryLocationId(Long id) {
		try {
			// Actualizamos la propiedad countryOrigin de este bean
			countryLocationId = id;
			// Actualizamos la propiedad user de este bean
			Country country = Factories.getService().getServiceCountry()
					.getCountryById(countryLocationId);
			user.getUserInfoPrivacity().setCountryLocation(country);
		} catch (EntityNotFoundException e) {
			log.error("EntityNotFoundException at 'getUserByIdStatic()'");
		}
	}
	
	/**
	 * Halla el nivel de privacidad de los paises de origen y de ubicacion
	 * del usuario que se consulta (atributo {@link #user}).
	 * @return el nivel de privacidad de los paises del usuario
	 */
	public TypePrivacity getPrivacityForUserCountries() {
		return user.getPrivacityData().getCountryOrigin();
	}
	/**
	 * Sobrescribe el nivel de privacidad de los paises de origen y de ubicacion
	 * del usuario que se consulta (atributo {@link #user}).
	 * @param countriesPrivacity el nivel de privacidad que se sobreescribe
	 */
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
	
	/** Inicializa la propiedad {@link #user} asignandole el objeto de tipo
	 * {@link User} cuyo atributo 'id' coincide con la propiedad
	 * {@link#urlUserId}. */
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
