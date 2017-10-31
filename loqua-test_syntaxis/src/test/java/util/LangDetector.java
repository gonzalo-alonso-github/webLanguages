package test.java.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * haciendo uso de la API de Language Detector (de Optimaize). <br/>
 * Con el fin de aprovechar al maximo los recursos utilizados por la aplicacion,
 * esta clase configura la API de tal modo que solo soporte los
 * idiomas que utiliza el sitio web (ingles, frances y espanol).
 * @author Gonzalo
 */
public class LangDetector {

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
	
	/** Constructor sin parametros que inicializa los atributos de la clase */
	public LangDetector(){
		initUsedLanguages();
		initLanguageDetector();
		initTextFactory();
	}
	
	/**
	 * Inicializa la propiedad {@link #usedLanguages}. Los lenguages soportados
	 * son ingles, frances y espanol. Las demas lenguas latinas peninsulares
	 * son consideradas espanol (lo cual ayuda a corregir las imprecisiones
	 * de la API).
	 */
	private void initUsedLanguages() {
		usedLanguages = new ArrayList<String>(Arrays.asList(
				"an", "ast", "ca", "en", "es", "fr", "gl", "it", "oc", "pt"));
		/* an = aragones
		 * ast = asturiano
		 * ca = catalan
		 * en = ingles
		 * es = espanol
		 * fr = frances
		 * gl = gallego
		 * it = italiano
		 * oc = occitano
		 * pt = portugues */
	}
	
	private void initLanguageDetector() {
		List<LanguageProfile> languageProfiles=new ArrayList<LanguageProfile>();
		try {
			languageProfiles = new LanguageProfileReader().read(usedLanguages);
			// = new LanguageProfileReader().readAllBuiltIn();
		} catch (IOException e) {
			// TODO
		}
		langDetector = 
				LanguageDetectorBuilder.create(NgramExtractors.standard())
		        .withProfiles(languageProfiles)
		        .build();
	}
	
	private void initTextFactory() {
		textFactory = CommonTextObjectFactories.forDetectingOnLargeText();
	}

	// // // //
	// METODOS
	// // // //
	
	public static void main(String[] args){
		try {
			new LangDetector().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run() throws IOException {
		/* Fallan algunas pruebas como:
		'Pasame la sal' (detecta el asturiano, occitano, y el espanol el ultimo)
		'Tengo una vaca lechera' (solo detecta el aragones y no el espanol),
		'Astonished significa sorprendido' (solo detecta italiano y no espanol)
		Por todo ello, y ya que todas las lenguas peninsulares
		siguen unas reglas sintacticas muy parecidas al espanol,
		cada vez que se detecte una de esas lenguas
		se considerara que es espanol */
		getDetectedLanguages("Astonished significa sorprendido");
	}

	public List<String> getDetectedLanguages(String text) {
		List<String> result = new ArrayList<String>();
		TextObject textObject = textFactory.forText( text );
		List<DetectedLanguage> probLangs =
				langDetector.getProbabilities(textObject);
		boolean hispanicLangs = false;
		//System.out.println("Probable langs: " + probLangs.size());
		for( DetectedLanguage probableLang : probLangs ){
			/* System.out.println( probableLang.getLocale().getLanguage()
					+ ": " + probableLang.getProbability()); */
			if( probableLang.equals("an")
					|| probableLang.equals("ast") || probableLang.equals("ca")
					|| probableLang.equals("gl") || probableLang.equals("it")
					|| probableLang.equals("oc") || probableLang.equals("pt")){
				/* La libreria suele fallar, y cuando 'detecta'
				una de estas lenguas, en realidad deberia ser espanol.
				Por eso se usa este booleano: */
				hispanicLangs=true;
			}
			result.add( probableLang.getLocale().getLanguage() );
		}
		if( hispanicLangs /*&& !probLangs.contains("es")*/ ){result.add("es");}
		return result;
	}
}
