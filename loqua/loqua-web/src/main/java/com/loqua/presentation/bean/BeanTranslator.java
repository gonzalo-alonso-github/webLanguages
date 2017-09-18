package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.loqua.business.services.impl.TranslatorMicrosoftKey;
import com.loqua.presentation.logging.LoquaLogger;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo del componente traductor de las vistas.
 * @author Gonzalo
 */
public class BeanTranslator implements Serializable {

	private static final long serialVersionUID = 1;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Nombre del idioma, elegido por el usuario en el componente SelectItem
	 * de la vista, que indica el idioma original del texto
	 * que se desea traducir.<br/>
	 * Los idiomas de dicho SelectItem no tienen nada que ver con la entidad
	 * {@link com.loqua.model.Language}, sino que vienen dados por la API
	 * de Microsoft Translator. */
	private String inputLanguageName;
	
	/** Nombre del idioma, elegido por el usuario en el componente SelectItem
	 * de la vista, que indica el idioma al cual se desea traducir el texto
	 * introducido.<br/>
	 * Los idiomas de dicho SelectItem no tienen nada que ver con la entidad
	 * {@link com.loqua.model.Language}, sino que vienen dados por la API
	 * de Microsoft Translator. */
	private String outputLanguageName;
	
	/** Texto, introducido por el usuario, que se desea traducir. */
	private String inputText;
	/** Texto ya traducido del atributo {@link #inputText}. */
	private String outputText;
	/** Mensaje de error tras intentar hacer uso del traductor. Si un usuario
	 * que no ha iniciado sesion trata de usar el traductor, se impedira
	 * la accion y este atributo mostrara un mensaje de aviso. */
	private String error;

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
		if (beanLogin == null) { 
			beanLogin = new BeanLogin();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanLogin", beanLogin);
		}
	}
	
	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Inicializa los valores necesarios para el lenguaje de origen y de
	 * destino de la frase que se traducira en el traductor, e invoca
	 * al metodo {@link #microsoftTranslatorText} para efectuar la traduccion.
	 */
	public void translate() {
		if( beanLogin.getLoggedUser()==null ){
			setError("errorLoginRequired");
			return;
		}
		Language inputLanguage = Language.fromString(inputLanguageName);
		Language outputLanguage = Language.fromString(outputLanguageName);
		try {
			microsoftTranslatorText(inputLanguage, outputLanguage);
		} catch(Exception e) {
			setError("errorTranslator");
			log.error("Unexpected Exception at 'setLocaleLanguage()'");
		}
	}
	
	/**
	 * Traduce el texto de {#inputText} guardando el resultado en {#outputText}.
	 * Para ello emplea la API de Text Translator de Microsoft
	 * Cognitive Services.
	 * @param inputLanguage indica el idioma original del texto que se desea 
	 * @param outputLanguage indica el idioma al cual se desea traducir
	 * el texto de {#inputText}
	 */
	private void microsoftTranslatorText(
			Language inputLanguage, Language outputLanguage) throws Exception {
		String appId = TranslatorMicrosoftKey.getAppId();
		if( appId==null || appId.isEmpty() ){
			appId = TranslatorMicrosoftKey.getNewAppId();
		}
		Translate.setKey("Bearer" + " " + appId);
		outputText = Translate.execute(
				inputText, inputLanguage, outputLanguage);
		if( outputText.contains("ArgumentException") ){
			// Si se obtiene "ArgumentException"
			// entonces hay que obtener una nueva "appId"
			// (caduca automaticamente cada 10 minutos)
			appId = TranslatorMicrosoftKey.getNewAppId();
			Translate.setKey("Bearer" + " " + appId);
			outputText = Translate.execute(
					inputText, inputLanguage, outputLanguage);
		}
	}

	/**
	 * Metodo 'get' del atributo {@link #error}. No solo obtiene el valor
	 * del atributo, sino que ademas lo inicializa a su valor por defecto,
	 * con el fin de evitar que el mensaje de error se siga mostrando
	 * durante mas peticiones Ajax.
	 * @return el atributo {@link #error}
	 */
	public String getError() {
		String message = error;
		error = null;
		return message;
	}
	/**
	 * Metodo 'set' del atributo {@link #error}. No solo sobrescribe el valor
	 * del atributo, sino que lo traduce al lenguaje indicado por el Locale
	 * de {@link BeanSettingsSession#locale}.
	 * @param errorName nombre de la propiedad, en el fichero
	 * 'bundle.properties' de internacionalizacion, cuyo valor sobrescribe al
	 * atributo {@link #error}
	 * 
	 */
	public void setError(String errorName) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication()
				.getResourceBundle(facesContext, "msgs");
		this.error = bundle.getString(errorName);
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //

	public String getInputLanguageName() {
		return inputLanguageName;
	}
	public void setInputLanguageName(String inputLanguageName) {
		this.inputLanguageName = inputLanguageName;
	}
	public String getOutputLanguageName() {
		return outputLanguageName;
	}
	public void setOutputLanguageName(String outputLanguageName) {
		this.outputLanguageName = outputLanguageName;
	}
	public String getInputText() {
		return inputText;
	}
	public void setInputText(String inputText) {
		this.inputText = inputText;
	}
	public String getOutputText() {
		return outputText;
	}
	public void setOutputText(String outputText) {
		this.outputText = outputText;
	}
	
	/**
	 * Proporciona a las vistas .xhtml la lista de lenguajes
	 * soportados por la API de Text Translator de Microsoft Cognitive Services.
	 * Los elementos de dicha lista son objetos
	 * SelectItem creados a partir de los lemguajes soportados por la API.
	 * @return
	 * Una lista de objetos SelectItem
	 */
	public List<SelectItem> getListLanguages() {
		List<SelectItem> listSelectLanguages = new ArrayList<SelectItem>();
		// Trata de cargar todos los idiomas incluidos en java.util.Locale
		for( String keyLanguage : Locale.getISOLanguages() ) {
			// Si el idioma de Locale es soportado por el traductor, se agrega
			Language validLanguage = Language.fromString(keyLanguage);
			if( validLanguage!=null ){
				String langName = BeanSettingsSession
						.getTranslationCountriesStatic(keyLanguage);
				listSelectLanguages.add(new SelectItem(keyLanguage, langName));
			}
		}
		return listSelectLanguages;
	}
}
