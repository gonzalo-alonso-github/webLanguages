package test.java.util.classesToTest;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.utils.nlp.NodeParsedSentence;
import com.loqua.business.services.impl.utils.nlp.ParserNLP;
import com.loqua.model.Suggestion;

import test.java.util.coreNLP.XmlReaderForSuggestions;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de sugerencias de correcciones sintacticas.
 * @author Gonzalo
 */
public class BeanSuggestionToTest implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
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
	
	/** Almacena las Sugerencias cuyo atributo
	 * {@link Suggestion#decimalCorrectText} o
	 * {@link Suggestion#decimalWrongText} coincida con la clave
	 * {@link #decimalCode}. <br> Cada valor de la lista es un Object[]
	 * de dos elementos: el primero es el identificador de la Suggestion;
	 * el segundo es la medida de similitud que esta presenta
	 * respecto a la frase introducida ({@link #sentence}). */
	private List<Object[]> suggestions_identical;
	
	/** Almacena las Sugerencias obtenidas (sin usar indice de buquedas),
	 * y su similitud respecto a la frase introucida.
	 * <br> Cada valor de la lista es un Object[] de dos elementos:
	 * el primero es el identificador de la Suggestion;
	 * el segundo es la medida de similitud que esta presenta
	 * respecto a la frase introducida ({@link #sentence}). */
	private List<Object[]> suggestions_random;
	
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
	
	private List<Suggestion> allSuggestions;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	public BeanSuggestionToTest(String sent, String lang){
		sentence = sent;
		language = lang;
		loadAllSuggestions();
	}
	
	private void loadAllSuggestions(){
		allSuggestions = new ArrayList<Suggestion>();
		XmlReaderForSuggestions xmlReader = new XmlReaderForSuggestions();
		allSuggestions = xmlReader.loadSuggestions();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Halla la sugerencia que se va a mostrar en la vista ({@link
	 * #bestSuggToReturn}), inicializando para ello las listas de sugerencias
	 * ordenadas por prioridad.
	 * @throws EntityNotFoundException
	 */
	public void loadBestSuggestions(){
		try{
			if( verifyIsFirstSearch() || noMoreSuggestions ){
				initializeAttributes();
				if( decimalCode.equals(BigInteger.valueOf(0)) ) return;
				loadIndenticalSuggestions();
				loadRandomSuggestions();
			}
			bestSuggToReturn = loadBestSuggToReturn();
		} catch (Exception e) {
			e.printStackTrace();
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
	 * Carga la lista de sugerencias sintacticamente identicas a la frase
	 * introducida por el usuario ({@link #sentence}).
	 */
	private void loadIndenticalSuggestions() {
		List<Suggestion> suggsToAdd = getIndenticalSuggestions();
		for( Suggestion suggToAdd : suggsToAdd ){
			Object[] infoToAdd = {suggToAdd, new Float(100)};
			suggestions_identical.add( infoToAdd );
		}
	}
	
	private List<Suggestion> getIndenticalSuggestions() {
		List<Suggestion> result = new ArrayList<Suggestion>();
		for( Suggestion sugg : allSuggestions ){
			if( sugg.getDecimalWrongText().equals(decimalCode)
					|| sugg.getDecimalCorrectText().equals(decimalCode) ){
				result.add(sugg);
			}
		}
		return result;
	}
	
	/**
	 * Carga la lista de sugerencias mas sintacticamente similares a la frase
	 * introducida por el usuario ({@link #sentence}).
	 */
	private void loadRandomSuggestions() {
		for( Suggestion suggToAdd : allSuggestions ){
			String parsedSentenceStr = parsedSentence.getNodeString();
			float similarity = ParserNLP.getInstance().getMaxSimilarity(
					suggToAdd, parsedSentenceStr);
			addRandomSuggestionInOrder(suggToAdd, similarity);
		}
		//printSimilarityValues();
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
			Suggestion sugg, float similarityOfSuggToAdd) {
		Object[] infoToAdd = {sugg, similarityOfSuggToAdd};
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
	/*
	private void printSimilarityValues() {
		for( int i=0; i<suggestions_random.size(); i++ ){
			Suggestion bestSugg = (Suggestion) (suggestions_random.get(i)[0]);
			Float similarity = (Float) (suggestions_random.get(i)[1]);
			System.out.println( bestSugg.getCorrectText() );
			System.out.println( similarity );
		}
	}
	*/
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
		Suggestion bestSugg = null;
		if( ! suggestions_identical.isEmpty() ){
			bestSugg = (Suggestion) (suggestions_identical.get(0)[0]);
			suggestions_identical.remove(0);
		}else if( ! suggestions_random.isEmpty() ){
			bestSugg = (Suggestion) (suggestions_random.get(0)[0]);
			suggestions_random.remove(0);
		}else{ noMoreSuggestions=true; }
		return bestSugg;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public Suggestion getBestSuggToReturn() {
		return bestSuggToReturn;
	}
	public void setBestSuggToReturn(Suggestion suggestion) {
		this.bestSuggToReturn = suggestion;
	}
}
