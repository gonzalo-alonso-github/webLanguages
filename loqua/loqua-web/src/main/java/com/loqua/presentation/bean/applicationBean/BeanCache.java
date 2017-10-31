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
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de recoger una sola vez los datos menos cambiantes
 * de la persistencia (ej: los lenguajes guardados, o los paises)
 * y mantenerlos accesibles desde las vistas .xhtml, sin que haya necesidad
 * de repetir mas veces las consultas de tales datos a la persistencia. 
 * @author Gonzalo
 */
public class BeanCache implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Lista de todos los lenguajes disponibles */
	private List<Language> allLanguages;
	
	/** Objeto Map&lt;Long, Country&gt;, que almacena todos los paises
	 * disponibles, donde la clave es el atributo 'id' del Country,
	 * y el valor es el propio Country */
	private Map<Long, Country> allCountries;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa la lista de lenguajes
	 * y los paises disponibles. */
	public BeanCache() {
		loadAllLanguages();
		loadAllCountries();
	}
	
	/** Destructor del bean */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Carga la lista de todos los lenguajes disponibles (atributo
	 * {@link #allLanguages} de la clase).
	 */
	public void loadAllLanguages(){
		// se usa desde snippets/profile/profile_edit.xhtml
		// no confundir con beanSettingsSession.getAllLanguagesFromProperties()
		// aquel metodo obtiene, del fichero .properties,
		// los idiomas de las vistas del sitio web; mientras que aqui obtiene,
		// de la tabla Language, los idiomas que manejan los usuarios en el foro
		allLanguages = new ArrayList<Language>();
		try{
			allLanguages = new ArrayList<Language>(Factories.getService()
					.getServiceLanguage().getAllLanguagesFromDB());
		}catch (Exception e){
			log.error("Unexpected Exception at 'getAllLanguagesFromDB()'");
		}
	}
	
	/**
	 * Metodo 'get' del atributo {@link #allLanguages}.
	 * @return atributo {@link #allLanguages}
	 */
	public List<Language> getAllLanguages(){
		return allLanguages;
	}
	/**
	 * Version estatica del metodo {@link #getAllLanguages}.
	 * @return atributo {@link #allLanguages}
	 */
	public static List<Language> getAllLanguagesStatic(){
		return new BeanCache().allLanguages;
	}
	
	/**
	 * Carga el Map de todos los paises disponibles (atributo
	 * {@link #allCountries} de la clase).
	 */
	public void loadAllCountries(){
		try{
			allCountries = 
				Factories.getService().getServiceCountry().getAllCountries();
		}catch (Exception e){
			log.error("Unexpected Exception at 'getAllLanguagesFromDB()'");
		}
	}
	
	/**
	 * Proporciona a las vistas .xhtml una lista de elementos para los
	 * controles 'h:selectOneMenu' de JSF. Dichos elementos son objetos
	 * SelectItem creados a partir de las claves del Map {@link #allCountries}.
	 * @return
	 * lista de objetos SelectItem
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