package com.loqua.presentation.bean.applicationBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.faces.model.SelectItem;

import com.loqua.infrastructure.Factories;
import com.loqua.model.Country;
import com.loqua.model.Language;
import com.loqua.presentation.bean.BeanSettingsSession;

public class BeanCache implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<Long, Country> allCountries;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/**
	 * Construccion del bean
	 */
	public BeanCache() {
		loadAllCountries();
	}
	
	/**
	 * Destruccion del bean
	 */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	public List<Language> getAllLanguagesFromDB(){
		// se usa desde snippets/profile/profile_edit.xhtml
		// no confundir con beanSettingsSession.getAllLanguagesFromProperties()
		// aquel metodo obtiene, del fichero .properties,
		// los idiomas de las vistas del sitio web; mientras que aqui obtenemos,
		// de la tabla Language, los idiomas que manejan los usuarios en el foro
		
		List<Language> allLanguages = new ArrayList<Language>();
		try{
			allLanguages = new ArrayList<Language>(Factories.getService()
					.getServiceLanguage().getListAllLanguagesFromDB());
		}catch (Exception e){
			// TODO Log
		}
		return allLanguages;
	}
	/*
	public List<Language> getAllLanguagesFromCache(){
		// se usa desde snippets/profile/profile_edit.xhtml
		// no confundir con beanSettingsSession.getAllLanguagesFromProperties()
		// aquel metodo obtiene, del fichero .properties,
		// los idiomas de las vistas del sitio web; mientras que aqui obtenemos,
		// de la tabla Language, los idiomas que manejan los usuarios en el foro
		
		List<Language> allLanguages = new ArrayList<Language>();
		// podriamos almacenar la lista de lenguages en una variable
		// de este bean de application
		// pero ya que en el negocio tambien se necesita manejar la misma lista,
		// entonces la obtenemos del negocio, concretamente de la clase
		// business.services.impl.memory.MemoryListsLanguages,
		// mediante la siguiente llamada a ServiceLanguage:
		try{
			allLanguages = new ArrayList<Language>(Factories.getService()
					.getServiceLanguage().getAllLanguagesFromCache().values());
		}catch (Exception e){
			// TODO Log
		}
		return allLanguages;
	}
	*/
	
	public void loadAllCountries(){
		allCountries = 
				Factories.getService().getServiceCountry().getAllCountries();
	}
	
	/**
	 * Proporciona a las vistas .xhtml una lista de elementos para los
	 * controles 'h:selectOneMenu' de JSF. Dichos elementos son objetos
	 * SelectItem creados a partir de las claves del Map allCountries 
	 * @return
	 * Una lista de objetos SelectItem
	 */
	public List<SelectItem> getAllCountriesForSelect() {
		List<SelectItem> listSelectItemCountries = new ArrayList<SelectItem>();
		String countryName = "";
		for( Long countryId : allCountries.keySet() ) {
			countryName = allCountries.get(countryId).getName();
			countryName = BeanSettingsSession.getTranslationCountriesStatic(
					countryName);
			listSelectItemCountries.add(new SelectItem(countryId, countryName));
		}
		return listSelectItemCountries;
	}
}