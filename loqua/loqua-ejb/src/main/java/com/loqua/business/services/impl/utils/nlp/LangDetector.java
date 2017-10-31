package com.loqua.business.services.impl.utils.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.loqua.business.exception.BusinessRuntimeException;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

/**
 * Aporta los metodos necesarios para detectar el idioma de un texto,
 * haciendo uso de la API de Language Detector (de Optimaize). <br>
 * Ya que los lenguages soportados por la aplicacion (los lenguajes de las
 * noticias) solo son ingles, frances y epa&ntilde;ol, el detector solo
 * utilizara esos idiomas, con el fin de aprovechar al maximo los recursos
 * consumidos por la aplicacion. <br>
 * La API tiene algunas imprecisiones. Como ejemplo, las siguientes frases:
 * <ul><li>
 * 'Pasame la sal': detecta asturiano y occitano,
 * y epa&ntilde;ol en ultimo lugar.
 * </li><li>
 * 'Tengo una vaca lechera': solo detecta el aragones y no el epa&ntilde;ol.
 * </li><li>
 * 'Astonished significa sorprendido': solo detecta italiano y no epa&ntilde;ol.
 * </li></ul>
 * Para corregir esas imprecisiones de la API todas las lenguas latinas
 * peninsulares son consideradas epa&ntilde;ol, la unica lengua peninsular
 * utilizada.
 * @author Gonzalo
 */
public class LangDetector{
	
	/** Instancia de la clase, para implementar el patron Singleton */
	private static LangDetector instance = new LangDetector();
	/** Lista de idiomas que soporta el detector de lenguajes */
	List<String> usedLanguages;
	/** Objeto de la API Language Detector que provee los metodos necesarios
	 * para detectar el idioma de un texto dado */
	private LanguageDetector langDetector;
	/** Objeto de la API Language Detector que representa el texto
	 * cuyo idioma se va a detectar */
	private TextObjectFactory textFactory;

	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor sin parametros que inicializa los atributos de la clase. */
	public LangDetector(){
		initUsedLanguages();
		initLanguageDetector();
		initTextFactory();
	}
	
	/**
	 * Inicializa la propiedad {@link #usedLanguages}. Los lenguages
	 * soportados son ingles, frances y epa&ntilde;ol, pero tambien se incluyen
	 * las demas lenguas latinas peninsulares, que para evitar las
	 * imprecisiones de la API, seran consideradas epa&ntilde;ol.
	 * Los codigos de los idiomas de la API siguen la norma ISO-639:
	 * <ul><li>'an' = aragones</li>
	 * <li>'ast' = asturiano</li><li>ca = catalan</li><li>'en' = ingles</li>
	 * <li>'es' = epa&ntilde;ol</li><li>'fr' = frances</li>
	 * <li>'gl' = gallego</li><li>'it' = italiano</li><li>'oc' = occitano</li>
	 * <li>'pt' = portugues</li></ul>
	 */
	private void initUsedLanguages() {
		usedLanguages = new ArrayList<String>(Arrays.asList(
				"an", "ast", "ca", "en", "es", "fr", "gl", "it", "oc", "pt"));
	}
	
	/** Inicializa la propiedad {@link #langDetector} */
	private void initLanguageDetector() {
		List<LanguageProfile> languageProfiles=new ArrayList<LanguageProfile>();
		try {
			languageProfiles = new LanguageProfileReader().read(usedLanguages);
		} catch (IOException e) {
			throw new BusinessRuntimeException(e);
		}
		langDetector=LanguageDetectorBuilder.create(NgramExtractors.standard())
		        .withProfiles(languageProfiles)
		        .build();
	}
	
	/** Inicializa la propiedad {@link #textFactory} */
	private void initTextFactory() {
		textFactory = CommonTextObjectFactories.forDetectingOnLargeText();
	}
	
	/**
	 * Implementa el patron Singleton para hallar la misma instancia de esta
	 * clase desde cualquier parte del codigo y desde cualquier hilo de
	 * ejecucion
     * @return unica instancia de la propia clase
     */
	public static LangDetector getInstance() {
		return instance;
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Halla la lista de posibles idiomas en que esta escrito el texto dado
	 * @param text texto cuyo idioma se va a detectar
	 * @return la lista de posibles idiomas obtenida
	 */
	public List<String> getDetectedLanguages(String text) {
		List<String> result = new ArrayList<String>();
		TextObject textObject = textFactory.forText( text );
		List<DetectedLanguage> probLangs =
				langDetector.getProbabilities(textObject);
		for( DetectedLanguage probableLang : probLangs ){
			String lang = probableLang.getLocale().getLanguage();
			if( lang.equals("es") || lang.equals("an")
					|| lang.equals("ast") || lang.equals("ca")
					|| lang.equals("gl") || lang.equals("it")
					|| lang.equals("oc") || lang.equals("pt")){
				/* La libreria puede fallar al detectar una de estas lenguas,
				cuando en realidad deberia ser espanol.
				Por eso en todos esos casos se considera espanol: */
				result.add("es");
			}else{
				result.add( lang );
			}
		}
		return result;
	}
}
