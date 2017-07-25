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
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class BeanTranslator implements Serializable {

	private static final long serialVersionUID = 1;
	private String inputLanguageName;
	private String outputLanguageName;
	private String inputText;
	private String outputText;
	private String error;

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
		if (beanLogin == null) { 
			beanLogin = new BeanLogin();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanLogin", beanLogin);
		}
	}
	
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	public void translate() {
		// En este bean no es necesario utilizar las variables finish y success
		// como se hace en otros, dado que es de ambito request.
		// 
		if( beanLogin.getLoggedUser()==null ){
			setError("errorLoginRequired");
			return;
		}
		Language inputLanguage = Language.fromString(inputLanguageName);
		Language outputLanguage = Language.fromString(outputLanguageName);
		try {
			microsoftTranslatorText(inputLanguage, outputLanguage);
		} catch(Exception e) {
			// TODO Log
			setError("errorTranslator");
		}
		//return null;
	}
	
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
	
	public String getError() {
		String message = error;
		error = null;
		return message;
	}
	public void setError(String errorName) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication()
				.getResourceBundle(facesContext, "msgs");
		this.error = bundle.getString(errorName);
	}
}
