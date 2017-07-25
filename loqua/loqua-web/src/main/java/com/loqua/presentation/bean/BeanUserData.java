package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import com.loqua.infrastructure.Factories;
import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanUtils;

/**
 * Administra los datos del usuario que son manejados durante toda la sesion,
 * a diferencia del beanUserView, que persiste durante el ambito de vista.
 * <br/>
 * Los datos que este bean administra son las listas de lenguages y las imagenes
 * de usuario, cuyo manejo, por su complejidad, conviene separar de otros beans
 * de sesion.
 * @author Gonzalo
 *
 */
public class BeanUserData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Cadena de texto utilizada como parametro en la URL para poder visitar
	 * el perfil de un usuario concreto
	 */
	private Map<Long,Language> mapPracticingLanguages;
	private Map<Long,Language> mapNativeLanguages;
	private Part imageProfile;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
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
		clearStatus();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Si el usuario ha iniciado sesion, carga sus listas de lenguages
	 * practicados y de lenguages maternos.<br/>
	 * Si el usuario es anonimo, comprueba si el lenguage del navegador esta
	 * presente en la tabla 'Language' de la base de datos, y si es asi lo carga
	 * en una lista de un solo elemento; si no es asi la lista quedara vacia. 
	 */
	private void loadUserLanguages(){
		User loggedUser = beanLogin.getLoggedUser();
		if( loggedUser!=null ){
			// si es usuario registrado, obtener su lista de lenguages elegidos:
			loadPracticedLanguages(loggedUser);
			loadNativeLanguages(loggedUser);
		}else{
			// si es anonimo, obtener el lenguage del navegador:
			loadBrowserLanguageIfExistsInDB();
		}
	}

	private void loadPracticedLanguages(User loggedUser) {
		mapPracticingLanguages = new HashMap<Long,Language>();
		// Haciendo esto obtendriamos los Languages desde la base de datos:
		List<Language> listPracticingLanguages = Factories.getService()
				.getServiceLanguage().getPracticingLanguagesByUser(
						loggedUser.getId());
		mapPracticingLanguages = listPracticingLanguages.stream().collect(
					Collectors.toMap(Language::getId, Function.identity()));
	}
	
	private void loadNativeLanguages(User loggedUser){
		mapNativeLanguages = new HashMap<Long,Language>();
		// Haciendo esto obtendriamos los Languages desde la base de datos:
		List<Language> listNativeLanguages = Factories.getService()
				.getServiceLanguage().getNativeLanguagesByUser(
						loggedUser.getId());
		mapNativeLanguages = listNativeLanguages.stream().collect(
				Collectors.toMap(Language::getId, Function.identity()));
	}
	
	/**
	 * Carga en el atributo Map<Long, Language> mapPracticingLanguages
	 * el objeto Language (guardado en la tabla Language de la base de datos)
	 * que corresponde al locale del navegador.
	 * Pero si el locale del navegador no se correspode con ninguno de la tabla
	 * Language de la base de datos, se establece por defecto a ingles.
	 */
	private void loadBrowserLanguageIfExistsInDB() {
		// no confundir con el metodo getBrowserLocaleIfExistsInProperties
		// del beanSettingsSession: aquel obtiene, del fichero .properties,
		// los idiomas de las vistas del sitio web; mientras que aqui obtenemos
		// de la tabla Language el idioma correspondiente al locale del navegador
		mapPracticingLanguages = new HashMap<Long,Language>();
		Locale browserLocale = 
				FacesContext.getCurrentInstance().getExternalContext()
				.getRequestLocale();
		Language browserlanguage = null;
		browserlanguage = Factories.getService().getServiceLanguage()
				.getLanguageByName(browserLocale.getLanguage());
		if( browserlanguage!=null ){
			mapPracticingLanguages.put(0L, browserlanguage);
		}
	}
	
	/**
	 * Obtiene el numero de usuarios de la tabla User, pero exceptuando
	 * aquellos que estan en estado 'removed'.
	 * @return numero total de usuarios no eliminados.
	 */
	public int getNumRegisteredUsers(){
		int result = 0;
		try{
			result = Factories.getService().getServiceUser()
					.getNumRegisteredUsersAndAdminFromMemory();
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
			result = Factories.getService()
				.getServiceUser().getUserById(userId);
		} catch (Exception e) {
			// TODO Log
		}
		return result;
	}
	
	public String getOutputLinkToNotifications(){
		return BeanUtils.getUrlUserPages() + "notifications-768screen.xhtml";
	}
	
	private void clearStatus() {
		mapPracticingLanguages = null;
		mapNativeLanguages = null;
	}
	
	// // // // // // // // // // // // // //
	// GETTERS & SETTERS CON LOGICA DE NEGOCIO
	// // // // // // // // // // // // // //
	
	public List<Language> getListUserLanguages() {
		if( mapPracticingLanguages==null ){ loadUserLanguages(); }
		List<Language> result = new ArrayList<Language>(
				mapPracticingLanguages.values() );
		return result;
	}
	
	public List<Language> getListPracticingLanguages(){
		return getListUserLanguages();
	}
	public void setListPracticingLanguages(List<Language> practicingLanguages){
		Map<Long,Language> mapLanguages = new HashMap<Long,Language>();
		mapLanguages = practicingLanguages.stream().collect(
				Collectors.toMap(Language::getId, Function.identity()));
		mapPracticingLanguages = mapLanguages;
	}
	
	public List<Language> getListNativeLanguages(){
		if( mapNativeLanguages==null ){ loadUserLanguages(); }
		List<Language> result = new ArrayList<Language>(
				mapNativeLanguages.values() );
		return result;
	}
	public void setListNativeLanguages(List<Language> nativeLanguages){
		Map<Long,Language> mapLanguages = new HashMap<Long,Language>();
		mapLanguages = nativeLanguages.stream().collect(
				Collectors.toMap(Language::getId, Function.identity()));
		mapNativeLanguages = mapLanguages;
	}
	
	public Map<Long,Language> getMapPracticingLanguages(){
		if( mapPracticingLanguages==null ){ loadUserLanguages(); }
		return mapPracticingLanguages;
	}
	public void setMapPracticingLanguages(Map<Long,Language> mapLanguages){
		mapPracticingLanguages = mapLanguages;
	}
	
	public Map<Long,Language> getMapNativeLanguages(){
		if( mapNativeLanguages==null ){ loadUserLanguages(); }
		return mapNativeLanguages;
	}
	public void setMapNativeLanguages(Map<Long,Language> mapLanguages){
		mapNativeLanguages = mapLanguages;
	}
	public Part getImageProfile() {
		return imageProfile;
	}
	public void setImageProfile(Part imageProfile) {
	    this.imageProfile = imageProfile;
	}
}
