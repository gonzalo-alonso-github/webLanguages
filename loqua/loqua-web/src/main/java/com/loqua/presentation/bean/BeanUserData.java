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
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Administra los datos del usuario logueado que son manejados durante
 * toda la sesion, a diferencia del {@link BeanUserView},
 * que persiste durante el ambito de vista. <br>
 * Los datos que este bean administra son las listas de lenguages y las imagenes
 * de usuario, cuyo manejo, por su complejidad, conviene separar de otros beans
 * de sesion. <br>
 * Ademas de eso, este bean se encarga de efectuar operaciones relativas
 * a las consultas de usuarios en general, por ejemplo devolver el numero de
 * usuarios registrados en la aplicacion.
 * @author Gonzalo
 */
public class BeanUserData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Almacena los lenguajes practicados por el usuario
	 * que ha iniciado la sesion */
	private Map<Long,Language> mapPracticingLanguages;
	
	/** Almacena los lenguajes maternos del usuario
	 * que ha iniciado la sesion */
	private Map<Long,Language> mapNativeLanguages;
	
	/** Imagen de perfil del usuario */
	private Part imageProfile;
	
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
		clearStatus();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Si el usuario ha iniciado sesion, inicializa sus listas de lenguages
	 * practicados y de lenguages maternos.<br>
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

	/**
	 * Inicializa el atributo {@link #mapPracticingLanguages}, consultando
	 * la lista de lenguajes utilizados por el usuario dado a nivel de
	 * practicante.
	 * @param loggedUser el usuario que ha iniciado sesion
	 */
	private void loadPracticedLanguages(User loggedUser) {
		mapPracticingLanguages = new HashMap<Long,Language>();
		// Haciendo esto obtendriamos los Languages desde la base de datos:
		List<Language> listPracticingLanguages = Factories.getService()
				.getServiceLanguage().getPracticingLanguagesByUser(
						loggedUser.getId());
		mapPracticingLanguages = listPracticingLanguages.stream().collect(
					Collectors.toMap(Language::getId, Function.identity()));
	}
	
	/**
	 * Inicializa el atributo {@link #mapPracticingLanguages}, consultando
	 * la lista de lenguajes utilizados por el usuario dado a nivel nativo.
	 * @param loggedUser el usuario que ha iniciado sesion
	 */
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
	 * Agrega al {@link #mapPracticingLanguages} (de tipo
	 * Map&lt;Long, Language&gt;) el objeto {@link Language}
	 * que corresponde al Locale del navegador. <br>
	 * Pero si el locale del navegador no se correspode con ningun
	 * {@link Language} de la base de datos, se establece por defecto a ingles.
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
	 * Obtiene el numero de usuarios existentes, pero exceptuando
	 * aquellos que estan en estado 'removed'.
	 * @return numero total de usuarios no eliminados.
	 */
	public int getNumRegisteredUsers(){
		int result = 0;
		try{
			result = Factories.getService().getServiceUser()
					.getNumRegisteredUsersAndAdminFromDB(); // FromCache
		}catch (Exception e){
			log.error("Unexpected Exception at 'getNumRegisteredUsers()'");
		}
		return result;
	}
	
	/**
	 * Halla la URL necesaria para acceder a la pagina de notificaciones.
	 * @return enlace a la pagina de notificaciones, que puede ser empleado
	 * desde los componentes OutputLink de las vistas
	 */
	public String getOutputLinkToNotifications(){
		return BeanUtils.getUrlUserPages() + "notifications-768screen.xhtml";
	}

	/**
	 * Borra el estado del Bean sobreescribiendo las propiedades del mismo
	 * con sus valores por defecto.
	 */
	private void clearStatus() {
		mapPracticingLanguages = null;
		mapNativeLanguages = null;
	}
	
	// // // // // // // // // // // // // //
	// GETTERS & SETTERS CON LOGICA DE NEGOCIO
	// // // // // // // // // // // // // //
	
	/**
	 * Inicializa, si no lo esta ya, el atributo
	 * {@link #mapPracticingLanguages}, y lo convierte a una lista de objetos
	 * {@link Language}.
	 * @return lista de lenguajes que practica el usuario con sesion iniciada
	 */
	public List<Language> getListUserLanguages() {
		if( mapPracticingLanguages==null ){ loadUserLanguages(); }
		List<Language> result = new ArrayList<Language>(
				mapPracticingLanguages.values() );
		return result;
	}
	
	/**
	 * Inicializa, si no lo esta ya, el atributo
	 * {@link #mapNativeLanguages}, y lo convierte a una lista de objetos
	 * {@link Language}.
	 * @return lista de lenguajes nativos del usuario con sesion iniciada
	 */
	public List<Language> getListNativeLanguages(){
		if( mapNativeLanguages==null ){ loadUserLanguages(); }
		List<Language> result = new ArrayList<Language>(
				mapNativeLanguages.values() );
		return result;
	}
	
	public List<Language> getListPracticingLanguages(){
		return getListUserLanguages();
	}
	
	/**
	 * Convierte la lista dada de objetos {@link Language} en un
	 * Map&lt;Long, Language&gt;, y lo utiliza para sobreescribir el valor del
	 * atributo {@link #mapPracticingLanguages}
	 * @param practicingLanguages lista de lenguajes practicados por el usuario
	 */
	public void setListPracticingLanguages(List<Language> practicingLanguages){
		Map<Long,Language> mapLanguages = new HashMap<Long,Language>();
		mapLanguages = practicingLanguages.stream().collect(
				Collectors.toMap(Language::getId, Function.identity()));
		mapPracticingLanguages = mapLanguages;
	}
	
	/**
	 * Convierte la lista dada de objetos {@link Language} en un
	 * Map&lt;Long, Language&gt;, y lo utiliza para sobreescribir el valor del
	 * atributo {@link #mapNativeLanguages}
	 * @param nativeLanguages lista de lenguajes nativos del usuario
	 */
	public void setListNativeLanguages(List<Language> nativeLanguages){
		Map<Long,Language> mapLanguages = new HashMap<Long,Language>();
		mapLanguages = nativeLanguages.stream().collect(
				Collectors.toMap(Language::getId, Function.identity()));
		mapNativeLanguages = mapLanguages;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
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
