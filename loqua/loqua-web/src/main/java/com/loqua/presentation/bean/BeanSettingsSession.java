package com.loqua.presentation.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsLocale;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Administra el lenguaje en el que cada usuario desea ver los 
 * formularios, menus y textos propios del sitio web
 * @author Gonzalo
 */
public class BeanSettingsSession implements Serializable {
	
	/**
	 * Numero de version de la clase serializable.
	 * @see Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/**
	 * Representa el idioma, al coleccion de caracteres y otras caracteristicas
	 * propias de una region determinada.
	 * @see Locale
	 */
	private static Locale locale;
	
	/**
	 * Inyeccion de dependencia del BeanLogin en esta clase.
	 */
	@ManagedProperty(value="#{beanLogin}") 
	private BeanLogin beanLogin;
	/**
	 * Inyeccion de dependencia del BeanSettingsLocale en esta clase.
	 */
	@ManagedProperty(value="#{beanSettingsLocale}") 
	private BeanSettingsLocale beanSettingsLocale;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/**
	 * Construccion del bean. Aqui se inicializa el BeanLogin,
	 * presente en esta clase por inyeccion de dependencia.
	 */
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanSettingsLocale();
		loadLocale();
	}
	
	/**
	 * Inicializa el BeanLogin perteneciente a esta clase.</br>
	 * Si el BeanLogin ya fue inicializado,
	 * simplemente se obtiene de la sesion.</br>
	 * Si el BeanLogin no existe en la sesion,
	 * se crea y se guarda en sesion bajo la clave 'beanLogin'.
	 */
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
	
	/**
	 * Inicializa el BeanSettingsLocale perteneciente a esta clase.</br>
	 * Si el BeanSettingsLocale ya fue inicializado,
	 * simplemente se obtiene del contexto de aplicacion.</br>
	 * Si el BeanSettingsLocale no existe en el contexto de aplicacion,
	 * se crea y se guarda en sesion bajo la clave 'beanSettingsLocale'.
	 */
	private void initBeanSettingsLocale() {
		// Buscamos el BeanLogin en la sesion.
		beanSettingsLocale = null;
		beanSettingsLocale = (BeanSettingsLocale)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanSettingsLocale");
		
		// si no existe lo creamos e inicializamos:
		if (beanSettingsLocale == null) { 
			beanSettingsLocale = new BeanSettingsLocale();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanSettingsLocale", beanSettingsLocale);
		}
	}

	/**
	 * Destruccion del bean
	 */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //	
	
	/**
	 * Proporciona a las vistas .xhtml una lista de elementos para los
	 * controles 'h:selectOneMenu' de JSF. Dichos elementos son objetos
	 * SelectItem creados a partir de las claves del Map mapLanguages 
	 * @return
	 * Una lista de objetos SelectItem
	 */
	public List<SelectItem> getAllLanguagesFromProperties() {
		List<SelectItem> listSelectItemLanguages = new ArrayList<SelectItem>();
		for( String key : BeanSettingsLocale.getMapLanguages().keySet() ) {
			String language = getTranslationCountries(key);
			listSelectItemLanguages.add(new SelectItem(key, language));
		}
		return listSelectItemLanguages;
	}
	
	/**
	 * Guarda en el Bean el objeto Locale que utiliza el sitio web.</br>
	 * Si el usuario de la sesion es anonimo, el Locale se obtiene a partir
	 * de la informacion del navegador.</br>
	 * Si el usuario de la sesion esta registrado, el Locale se obtiene a partir
	 * de su informacion asociada en la base de datos.
	 * @return
	 * Objeto Locale para el sitio web
	 */
	private void loadLocale() {
		if( beanLogin.getLoggedUser() == null ){
			// Si el usuario es anonimo devuelve el locale del navegador:
			locale = getBrowserLocaleIfExistsInProperties();
		}else{
			// Si el usuario esta registrado devuelve 'su' locale desde bdd:
			try {
				locale = getLoggedUserLocale();
			} catch (Exception e) {
				locale = getBrowserLocaleIfExistsInProperties();
				log.error("Unexpected Exception at 'loadLocale()'");
			}
		}
	}

	/**
	 * Obtiene, a partir de la informacion del navegador, el objeto Locale.
	 * Si el locale del navegador no coincide con los del fichero
	 * locales.properties, se establece por defecto a ingles.
	 * @return
	 * Objeto Locale del navegador
	 */
	private Locale getBrowserLocaleIfExistsInProperties() {
		Locale browserLocale = 
				FacesContext.getCurrentInstance().getExternalContext()
				.getRequestLocale();
		boolean browserLocaleIsAvailableInLoqua = false;
		for( String key : BeanSettingsLocale.getMapLanguages().keySet() ) {
			if( browserLocale.getLanguage().equals(key) ){
				browserLocaleIsAvailableInLoqua = true;
				break;
			}
		}
		if( browserLocaleIsAvailableInLoqua == false ){
			browserLocale = new Locale("en");
		}
		return browserLocale;
	}
	
	/**
	 * Obtiene, a partir de la informacion guardada en la base de datos, el
	 * objeto Locale correspondiente al usuario registrado del BeanLogin.
	 * @return
	 * Objeto Locale del usuario registrado
	 * @throws EntityNotFoundException
	 * @throws NullPointerException
	 */
	private Locale getLoggedUserLocale() 
			throws EntityNotFoundException, NullPointerException {
		Locale loggedUserlocale = null;
		loggedUserlocale = new Locale( Factories.getService().getServiceUser()
			.getLocaleByUser(beanLogin.getLoggedUser()) );
		return loggedUserlocale;
	}	
	
	/**
	 * Obtiene el objeto Locale guardado en el Bean.</br>
	 * Si el Bean no tiene inicializado dicho objeto, realiza una llamada al
	 * metodo encargado de cargar el Locale.
	 * @return
	 * Objeto Locale para el sitio web
	 */
	public Locale getLocale() {
		if( locale == null ){
			init();
		}
		return locale;
	}
	
	/**
	 * Obtiene el lenguaje del atributo 'locale'.
	 * @return
	 * Una cadena de texto, que es la clave ISO 639 del lenguage del sitio web
	 */
	public String getLocaleLanguage() {
		return locale.getLanguage();
	}
	
	/**
	 * Cambia el lenguage de las paginas del sitio web
	 * por el lenguaje seleccionado.
	 * Si el usuario esta registrado actualiza su locale en la base de datos.
	 * @param localeLanguage
	 * clave ISO 639 del lenguage seleccionado
	 */
	public void setLocaleLanguage(String localeLanguage){
		locale = new Locale(localeLanguage);
		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
		// Si el usuario esta registrado actualiza 'su' locale en bdd:
		User loggedUser = beanLogin.getLoggedUser();
		if( loggedUser!=null ){
			try {
				loggedUser.setLocale(localeLanguage);
				Factories.getService().getServiceUser()
					.updateAllDataByUser(loggedUser,false);
				beanLogin.setLoggedUser(loggedUser);
				/*FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().put("beanLogin", beanLogin);*/
			} catch (Exception e) {
				log.error("Unexpected Exception at 'setLocaleLanguage()'");
			}
		}
	}
	
	/**
	 * Obtiene la cadena de texto perteneciente a la clave dada, a partir
	 * de los datos presentes en el fichero 'bundle'
	 * del lenguage correspondiente.
	 * @param key
	 * clave, en el fichero 'bundle', de la cadena de texto solicitada
	 * @return
	 * Texto perteneciente a la clave dada
	 */
	public String getTranslation(String key) {
		ResourceBundle resourceBundle = 
				ResourceBundle.getBundle("i18n/bundle", locale);
		return resourceBundle.getString(key);
	}
	public static String getTranslationStatic(String key) {
		ResourceBundle resourceBundle = 
				ResourceBundle.getBundle("i18n/bundle", locale);
		return resourceBundle.getString(key);
	}
	
	public String getTranslationCountries(String key) {
		ResourceBundle resourceBundle = 
				ResourceBundle.getBundle("i18n/countriesAndLocales", locale);
		return resourceBundle.getString(key);
	}
	public static String getTranslationCountriesStatic(String key) {
		ResourceBundle resourceBundle = 
				ResourceBundle.getBundle("i18n/countriesAndLocales", locale);
		return resourceBundle.getString(key);
	}
	
	public String getTranslationEvents(String key) {
		ResourceBundle resourceBundle = 
				ResourceBundle.getBundle("i18n/events", locale);
		return resourceBundle.getString(key);
	}
	public static String getTranslationEventsStatic(String key) {
		ResourceBundle resourceBundle = 
				ResourceBundle.getBundle("i18n/events", locale);
		return resourceBundle.getString(key);
	}
	
	/**
	 * Traduce el formato de una fecha dada en funcion del atributo Locale
	 * @param date
	 * fecha que se va a formatear
	 * @return
	 * Cadena de texto correspondiente a la fecha formateada
	 */
	public String getFormattedDate(Date date){
		DateFormat dateFormat = 
				DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String formattedDate = dateFormat.format(date);
		return formattedDate;
	}
	
	/**
	 * Traduce el formato de una fecha y hora dada en funcion del atributo Locale
	 * @param date
	 * fecha y hora que se va a formatear
	 * @return
	 * Cadena de texto correspondiente a la fecha y hora formateada
	 */
	public String getFormattedDateTime(Date date){
		DateFormat dateFormat = DateFormat.getDateTimeInstance(
						DateFormat.SHORT, DateFormat.SHORT, locale);
		String formattedDate = dateFormat.format(date);
		return formattedDate;
	}
	
}