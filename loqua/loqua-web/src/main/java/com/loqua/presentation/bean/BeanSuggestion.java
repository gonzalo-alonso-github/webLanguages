package com.loqua.presentation.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedProperty;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.utils.nlp.LangDetector;
import com.loqua.business.services.impl.utils.nlp.NodeParsedSentence;
import com.loqua.business.services.impl.utils.nlp.ParserNLP;
import com.loqua.infrastructure.Factories;
import com.loqua.model.ForumThread;
import com.loqua.model.Language;
import com.loqua.model.Suggestion;
import com.loqua.presentation.bean.applicationBean.BeanCache;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de sugerencias de correcciones sintacticas.
 * @author Gonzalo
 */
public class BeanSuggestion implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Frase introducida por el usuario, de la cual se buscan Sugerencias */
	private String sentence;
	/** Frase introducida por el usuario, una vez analizada sintacticamente.
	<br> Es el valor de {@link #sentence} convertido a 'part-of-speech tagging'
	(tambien llamado 'POS tagging'). */
	private NodeParsedSentence parsedSentence;
	/** Frase introducida por el usuario, una vez codificada. Resulta util
	 * como indice para agilizar las busquedas de Suggestions. <br>
	 * Es el valor de {@link #parsedSentence} tras convertirlo
	 * a su valor decimal. */
	private BigInteger decimalCode;
	/** Frase introducida por el usuario en la anterior peticion. Resulta util
	 * para conocer si en la peticion actual el usuario ha cambiado la frase. */
	private String sentenceOfPreviousRequest;
	
	/** Cantidad de Sugerencias ya consultadas cuyo atributo
	 * {@link Suggestion#decimalCorrectText} o
	 * {@link Suggestion#decimalWrongText} coincide con la clave
	 * {@link #decimalCode}. <br> Son Sugerencias que, en esta peticion
	 * y anteriores, fueron ya almacenadas en este Bean y por tanto no
	 * hay que consultarlas de nuevo en la base de datos.*/
	private int numProcessedIdenticalSuggs;
	/** Cantidad de Sugerencias existentes en la base de datos, cuyo atributo
	 * {@link Suggestion#decimalCorrectText} o
	 * {@link Suggestion#decimalWrongText} coincide con la clave
	 * {@link #decimalCode}. */
	private int numTotalIdenticalSuggs;
	
	/** Cantidad de Sugerencias ya consultadas. <br> Son Sugerencias que,
	 * en esta peticion y anteriores, fueron ya almacenadas en este Bean
	 * y por tanto no hay que consultarlas de nuevo en la base de datos.*/
	private int numProcessedRandomSuggs;
	/** Cantidad de Sugerencias existentes en la base de datos */
	private int numTotalRandomSuggs;
	
	/** Almacena las Sugerencias cuyo atributo
	 * {@link Suggestion#decimalCorrectText} o
	 * {@link Suggestion#decimalWrongText} coincida con la clave
	 * {@link #decimalCode}. <br> Cada valor de la lista es un Object[]
	 * de dos elementos: el primero es el identificador de la Suggestion;
	 * el segundo es la medida de similitud que esta presenta
	 * respecto a la frase introducida ({@link #sentence}). */
	private List<Object[]> suggestions_identical;
	// No es un Map porque es necesario que mantenga un orden
	// private Map<Long, Float> suggestions_identical;
	
	/** Almacena las Sugerencias obtenidas (sin usar indice de buquedas),
	 * y su similitud respecto a la frase introucida.
	 * <br> Cada valor de la lista es un Object[] de dos elementos:
	 * el primero es el identificador de la Suggestion;
	 * el segundo es la medida de similitud que esta presenta
	 * respecto a la frase introducida ({@link #sentence}). */
	private List<Object[]> suggestions_random;
	// No es un Map porque es necesario que mantenga un orden
	// private Map<Long, Float> suggestions_random;
	
	/** Si es 'true', indica que es la primera vez que el usuario solicita
	 * una sugerencia para una misma frase. <br>
	 * Si es 'false', indica que el usuario ha solicitado consecutivamente
	 * varias sugerencias para una misma frase. */
	private boolean isFirstSearch;
	/** Indica si no quedan sugerencias que mostrar para una frase dada,
	 * bien porque ya se hayan mostrado todas (en sucesivas peticiones http
	 * realizadas consecutivamente por el mismo usuario), o bien porque no
	 * haya sugerencias en la base de datos */
	private boolean noMoreSuggestions;
	
	/** ISO-639 del idioma seleccionado en el formulario */
	private String language;
	
	/** ISO-639 del idioma seleccionado en la anterior peticion. Resulta util
	 * para conocer si en la peticion actual el usuario ha cambiado el idioma
	 * seleccionado en el formulario. */
	private String languageOfPreviousRequest;
	
	/** La sugerencia mas sintacticamente similar a la frase introducida
	 * ({@link #sentence}), descartando las sugerencias
	 * que ya se hayan mostrado. */
	Suggestion bestSuggToReturn;
	
	/** Inyeccion de dependencia del {@link BeanForumThread} */
	@ManagedProperty(value="#{beanForumThread}")
	private BeanForumThread beanForumThread;
	
	/** Indica el resultado de comprobar si el lenguaje
	 * seleccionado ({@link #language}) coincide con el idioma detectado
	 * automaticamente de la frase ({@link #sentence}). */
	private BeanActionResult actionVerifyLanguage;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor sin parametros del bean. <br>
	 * Este bean no necesita beans inyectados, asi que se construye como
	 * las demas clases POJO corrientes.*/
	public BeanSuggestion(){}
	
	/** Inicializa el atributo {@link #language} para que el SelectItem
	 * del formulario de solicitud de sugerencias muestre por defecto
	 * el idioma del hilo recibido. <br>
	 * Va destinado a ser invocado desde la la seccion 'f:metadata'
	 * de la vista forum_thread_comment.xhtml.
	 * @param currentThreadId indica el identificador
	 * del hilo del foro que se ha consultado. <br> Conocer en este bean
	 * cual es el hilo del foro consultado permite obtener el
	 * idioma del mismo, y con ello suponer el idioma del texto de
	 * {@link #sentence}. Asi ese idioma sera el que aparecera seleccionado por
	 * defecto en el SelectItem del formulario de solicitud de sugerencias. */
	public void onLoad(Long currentThreadId) {
		if( currentThreadId==null ) return;
		ForumThread thread=BeanForumThread.getThreadByIdStatic(currentThreadId);
		language = thread.getFeed().getLanguage().getLocale();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Halla el enlace necesario para recargar la vista actual, tras usar
	 * el CommandLink que ejecuta la busqueda de una sugerencia de ayuda.
	 * @return Si la busqueda se realiza correctamente, devuelve la URL de la
	 * pagina actual. <br> Si se produce algun error, devuelve la URL de la
	 * página de error por defecto.
	 */
	public String getCommandLinkBestSuggestions(){
		String result = null;
		try{
			loadBestSuggestions();
			result = BeanUtilsView.renderViewAgainFromCommandLinkStatic(null);
		}catch( Exception e ){
			log.error("Unexpected Exception at 'loadBestSuggestions()'");
			return "errorUnexpected";
		}
		return result;
	}
	
	/**
	 * Halla la sugerencia que se va a mostrar en la vista ({@link
	 * #bestSuggToReturn}), inicializando para ello las listas de sugerencias
	 * ordenadas por prioridad.
	 * @throws EntityNotFoundException
	 */
	public void loadBestSuggestions() throws EntityNotFoundException{
		if( ! verifyLanguage() ) return;
		if( verifyIsFirstSearch() || noMoreSuggestions ){
			initializeAttributes();
		}
		if( decimalCode.equals(BigInteger.valueOf(0)) ) return;
		if( ! alreadyReadAllIdenticalSuggsFromDB() ){
			loadIndenticalSuggestions();
		}else if( ! alreadyReadAllSuggsFromDB() ){
			loadRandomSuggestions();
		}
		bestSuggToReturn = loadBestSuggToReturn();
	}
	
	/**
	 * Comprueba que el lenguaje introducido por el usuario ({@link #language})
	 * es un valor esperado (coincide con uno de los {@link Language} presentes
	 * en la base de datos). <br> El usuario ha seleccionado el lenguaje desde
	 * una lista desplegable cuyos valores ya estan predefinidos y que no
	 * permite valores nulos. No obstante, este metodo comprueba dichas
	 * condiciones en el lado del servidor como medida adicional de validacion.
	 * @return 'true' si el el lenguaje seleccionado se considera correcto
	 * <br> 'false' si el el lenguaje seleccionado es 'null' o desconocido.
	 */
	private boolean verifyLanguage() {
		boolean languageIsCorrect = false;
		if( ! language.isEmpty() ){
			for( Language langFromDB : BeanCache.getAllLanguagesStatic() ){
				languageIsCorrect = ( language.equals(langFromDB.getLocale()) );
				if(languageIsCorrect) break;
			}
		}
		if( !languageIsCorrect ) return false;
		/* Aunque el lenguaje sea un valor correcto, ahora se comprueba
		si coincide con el idioma detectado de la frase introducida.
		En caso contrario simplemente se imprimira un aviso en la vista. */
		warningForSelectedLanguage();
		return languageIsCorrect;
	}

	/**
	 * Detecta el idioma de la frase introducida en la vista ({@link
	 * #sentence}) y comprueba si coincide con el lenguaje seleccionado
	 * ({@link #language}); si no es así, invoca a un
	 * {@link BeanActionResult} que imprimira un aviso en la vista.
	 */
	private void warningForSelectedLanguage() {
		actionVerifyLanguage = new BeanActionResult();
		List<String> detectedLangs =
				LangDetector.getInstance().getDetectedLanguages(sentence); 
		if( !detectedLangs.isEmpty() && !detectedLangs.contains(language) ){
			String langName = BeanSettingsSession.getTranslationCountriesStatic(
					detectedLangs.get(0) );
			String warnPart1 = BeanSettingsSession.getTranslationStatic(
					"descriptionDifferentLang1");
			String warnPart2 = BeanSettingsSession.getTranslationStatic(
					"descriptionDifferentLang2");
			String warn = warnPart1 + "\n" + warnPart2 + " " + langName;
			actionVerifyLanguage.setMsgActionResultExact(warn);
		}
	}
	
	/**
	 * Inicializa todas las propiedades de la clase que se van a emplear para
	 * hallar la mejor sugerencia que se ha de mostrar en la vista.
	 */
	private void initializeAttributes() {
		noMoreSuggestions = false;
		parsedSentence = ParserNLP.getInstance().parseToPOStree(
				sentence, language);
		decimalCode = ParserNLP.getInstance().convertParsedToDecimal(
				parsedSentence.getNodeString() );
		numTotalIdenticalSuggs = Factories.getService().getServiceSuggestion()
				.getNumTotalSuggestionsByDecimalCode(decimalCode.toString());
		numProcessedIdenticalSuggs = 0;
		numTotalRandomSuggs = Factories.getService().getServiceSuggestion()
				.getNumTotalSuggestionsByLang(language);
		numProcessedRandomSuggs = 0;
		suggestions_identical = new ArrayList<Object[]>();
		suggestions_random = new ArrayList<Object[]>();
		sentenceOfPreviousRequest = sentence;
		languageOfPreviousRequest = language;
	}
	
	/**
	 * Comprueba si no es la primera vez que el usuario solicita sugerencias
	 * para una misma frase.
	 * @return 'true' si la frase introducida por el usuario ({@link #sentence})
	 * o bien el lenguaje elegido ({@link #language}) han cambiado respecto a
	 * su anterior solicitud de sugerencias. <br>
	 * 'false' si la frase introducida ({@link #sentence}) y el lenguaje elegido
	 * ({@link #language}) no han cambiado respecto a la anterior peticion.
	 */
	private boolean verifyIsFirstSearch(){
		boolean sentenceChanged = (!sentence.equals(sentenceOfPreviousRequest));
		boolean languageChanged = (!language.equals(languageOfPreviousRequest));
		isFirstSearch=(sentenceChanged || languageChanged);
		return isFirstSearch;
	}
	
	/**
	 * Comprueba si ya se han obtenido de la base de datos todas las 
	 * sugerencias sintacticamente identicas a la frase introducida
	 * ({@link #sentence}).
	 * @return 'true' si ya no quedan mas sugerencias identicas que obtener
	 * de la base de datos. <br>
	 * 'false' si aun quedan mas sugerencias identicas que obtener
	 * de la base de datos.
	 */
	private boolean alreadyReadAllIdenticalSuggsFromDB(){
		return (numProcessedIdenticalSuggs >= numTotalIdenticalSuggs);
	}
	
	/**
	 * Comprueba si ya se han obtenido de la base de datos todas las 
	 * sugerencias existentes.
	 * @return 'true' si ya no quedan mas sugerencias que obtener de la base de
	 * datos. <br>
	 * 'false' si aun quedan mas sugerencias que obtener de la base de datos.
	 */
	private boolean alreadyReadAllSuggsFromDB(){
		return (numProcessedRandomSuggs >= numTotalRandomSuggs);
	}
	
	/**
	 * Carga la lista de sugerencias sintacticamente identicas a la frase
	 * introducida por el usuario ({@link #sentence}).
	 */
	private void loadIndenticalSuggestions() {
		int numSuggsToQuery = 50-suggestions_identical.size();
		List<Suggestion> suggsToAdd = Factories.getService()
				.getServiceSuggestion().getSuggestionsByDecimalCode(
						numProcessedIdenticalSuggs, numSuggsToQuery,
						decimalCode.toString());
		numProcessedIdenticalSuggs += suggsToAdd.size();
		for( Suggestion suggToAdd : suggsToAdd ){
			Object[] infoToAdd = {suggToAdd.getId(), new Float(100)};
			suggestions_identical.add( infoToAdd );
		}
	}
	
	/**
	 * Carga la lista de sugerencias mas sintacticamente similares a la frase
	 * introducida por el usuario ({@link #sentence}).
	 */
	private void loadRandomSuggestions() {
		int numSuggsToQuery = 50-suggestions_random.size();
		List<Suggestion> suggsToAdd = Factories.getService()
		 		.getServiceSuggestion().getSuggestionsByLang(
		 			numProcessedRandomSuggs, numSuggsToQuery, language);
		numProcessedRandomSuggs += suggsToAdd.size();
		for( Suggestion suggToAdd : suggsToAdd ){
			String parsedSentenceStr = parsedSentence.getNodeString();
			float similarity = ParserNLP.getInstance().getMaxSimilarity(
					suggToAdd, parsedSentenceStr);
			addRandomSuggestionInOrder(suggToAdd.getId(), similarity);
		}
	}

	/**
	 * Introduce en la lista de sugerencias ({@link #suggestions_random})
	 * un nuevo elemento en la posicion correspondiente, de tal forma
	 * que la lista quede ordenada presentando, en las primeras posiciones,
	 * las sugerencias mas sintacticamente similares a la frase del usuario
	 * ({@link #sentence}).
	 * @param suggId atributo 'id' de la Suggestion que se introduce en la lista
	 * @param similarityOfSuggToAdd similitud de la sugerencia dada,
	 * respecto a la frase del usuario ({@link #sentence})
	 */
	private void addRandomSuggestionInOrder(
			long suggId, float similarityOfSuggToAdd) {
		Object[] infoToAdd = {suggId, similarityOfSuggToAdd};
		boolean added = false;
		for( int i=0; i<suggestions_random.size(); i++ ){
			float similarityOfProcessedSugg=(float)suggestions_random.get(i)[1];
			if( similarityOfSuggToAdd >= similarityOfProcessedSugg ){
				suggestions_random.add(i, infoToAdd);
				added = true;
				break;
			}
		}
		if( !added ){ suggestions_random.add(infoToAdd); }
	}
	
	/**
	 * Halla sugerencia mas sintacticamente similar a la frase introducida
	 * ({@link #sentence}), de entre todas las sugerencias almacenadas en las
	 * listas {@link #suggestions_identical} y {@link #suggestions_random}.
	 * @return Si la lista 'suggestions_identical' no esta vacia,
	 * devuelve su primer elemento Suggestion. <br>
	 * De lo contrario, y si la lista 'suggestions_random' no esta vacia,
	 * devuelve su primer elemento Suggestion. <br>
	 * Si ambas listas estan vacias, devuelve valor 'null'.
	 * @throws EntityNotFoundException
	 */
	private Suggestion loadBestSuggToReturn() throws EntityNotFoundException {
		Suggestion result = null;
		Long bestSuggestionId = null;
		if( ! suggestions_identical.isEmpty() ){
			bestSuggestionId = (Long) (suggestions_identical.get(0)[0]);
			suggestions_identical.remove(0);
		}else if( ! suggestions_random.isEmpty() ){
			bestSuggestionId = (Long) (suggestions_random.get(0)[0]);
			suggestions_random.remove(0);
		}else{ noMoreSuggestions=true; }
		if( bestSuggestionId!=null ){
			result = Factories.getService().getServiceSuggestion()
						.getSuggestionById( bestSuggestionId );
		}
		return result;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	
	public String getSentenceOfPreviousRequest() {
		return sentenceOfPreviousRequest;
	}
	public void setSentenceOfPreviousRequest(String sentence) {
		this.sentenceOfPreviousRequest = sentence;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLanguageOfPreviousRequest() {
		return languageOfPreviousRequest;
	}
	public void setLanguageOfPreviousRequest(String language) {
		this.languageOfPreviousRequest = language;
	}
	
	public Suggestion getBestSuggToReturn() {
		return bestSuggToReturn;
	}
	public void setBestSuggToReturn(Suggestion suggestion) {
		this.bestSuggToReturn = suggestion;
	}
	
	public BeanActionResult getActionVerifyLanguage() {
		return actionVerifyLanguage;
	}
	public void setActionVerifyLanguage(BeanActionResult actionVerifyLanguage) {
		this.actionVerifyLanguage = actionVerifyLanguage;
	}
}
