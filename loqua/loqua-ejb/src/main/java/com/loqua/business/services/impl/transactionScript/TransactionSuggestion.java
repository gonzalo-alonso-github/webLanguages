package com.loqua.business.services.impl.transactionScript;

import java.math.BigInteger;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.utils.nlp.LangDetector;
import com.loqua.model.Correction;
import com.loqua.model.Suggestion;
import com.loqua.persistence.SuggestionJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de la entidad {@link Suggestion}. <br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionSuggestion {
	
	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidad {@link Suggestion} */
	private static final SuggestionJPA suggJPA = new SuggestionJPA();
	
	/**
	 * Consulta sugerencias segun su atributo 'id'
	 * @param suggId atributo 'id' del Suggestion que se consulta
	 * @return objeto Suggestion cuyo atributo 'id' coincide
	 * con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public Suggestion getSuggestionById(Long suggId)
			throws EntityNotFoundException {
		Suggestion result = new Suggestion();
		try{
			result = suggJPA.getSuggestionById(suggId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Halla las sugerencias, aplicando un offset y limitando su numero,
	 * cuya frase incorrecta (propiedad
	 * {@link Suggestion#decimalWrongText}) tenga una sintaxis identica
	 * a la que se recibe por parametro.
	 * @param offset offset de los Suggestion devueltos
	 * @param limitSuggestions limite maximo de Suggestion devueltos
	 * @param decimalCodeStr atributo 'decimalWrongText' de los Suggestion
	 * que se consultan
	 * @return lista de Suggestion cuyo atributo 'decimalWrongText' coincide
	 * con el parametro dado
	 */
	public List<Suggestion> getSuggestionsByDecimalCode(int offset,
			int limitSuggestions, String decimalCodeStr) {
		BigInteger decimalCode = new BigInteger(decimalCodeStr);
		return suggJPA.getSuggestionsByDecimalCode(
				offset, limitSuggestions, decimalCode);
	}
	
	/**
	 * Consulta todas las sugerencias aplicando un offset y limitando su numero
	 * @param offset offset de los Suggestion devueltos
	 * @param limitSuggestions limite maximo de Suggestion devueltos
	 * @param lang idioma de las sugerencias consultadas
	 * @return lista de Suggestion, aplicando el offset y el limite
	 * de elementos indicado
	 */
	public List<Suggestion> getAllSuggestionsByLang(int offset,
			int limitSuggestions, String lang) {
		return suggJPA.getAllSuggestionsByLang(offset, limitSuggestions, lang);
	}
	
	/**
	 * Halla el numero de sugerencias donde alguno de sus atributos
	 * {@link Suggestion#decimalWrongText} o
	 * {@link Suggestion#decimalCorrectText} coincida con el valor decimal
	 * indicado.
	 * @param decimalCodeStr valor para filtrar las busquedas de sugerencias
	 * segun las mencionadas claves indexadas
	 * @return numero de sugerencias obtenido
	 */
	public int getNumTotalSuggestionsByDecimalCode(String decimalCodeStr) {
		Integer result = null;
		BigInteger decimalCode = new BigInteger(decimalCodeStr);
		try {
			result = suggJPA.getNumTotalSuggestionsByDecimalCode(decimalCode);
		} catch (EntityNotPersistedException e) {
			result = 0;
		}
		return result;
	}
	
	/**
	 * Halla la cantidad total de sugerencias existentes
	 * @param lang idioma de las sugerencias consultadas
	 * @return numero de sugerencias obtenido
	 */
	public int getNumTotalSuggestionsByLang(String lang) {
		Integer result = null;
		try {
			result = suggJPA.getNumTotalSuggestionsByLang(lang);
		} catch (EntityNotPersistedException e) {
			result = 0;
		}
		return result;
	}
	
	/**
	 * Agrega una sugerencia a la base de datos 
	 * @param suggestion objeto Suggestion que se desea guardar
	 * @return objeto Suggestion generado
	 * @throws EntityAlreadyFoundException
	 */
	public Suggestion createSuggestion(Suggestion suggestion) 
			throws EntityAlreadyFoundException {
		Suggestion result = null;
		try {
			result = suggJPA.createSuggestion(suggestion);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Comprueba todos los pares de frases de una correccion (frase original /
	 * frase corregida) para determinar si es adecuado crear una sugerencia
	 * por cada uno de ellos, en cuyo caso se guarda en la base de datos.
	 * @param originalSentences las frases originales en las que se divide el
	 * comentario que se ha corregido
	 * @param correctedSentences las correcciones de las frases corregidas
	 * @param correction objeto Correction al que pertenecen las frases
	 * @throws EntityAlreadyFoundException
	 */
	public void createSuggestions(List<String> originalSentences,
			List<String> correctedSentences, Correction correction) 
			throws EntityAlreadyFoundException {
		String knownLangOfSuggestions = getLanguageOfSuggestions(correction);
		if( knownLangOfSuggestions==null ){ return; }
		int numSentences = originalSentences.size();
		for(int i=0; i<numSentences; i++){
			String wrongText = originalSentences.get(i);
			String correctText = correctedSentences.get(i);
			if( verifySuitableToSuggest(wrongText, correctText) ){
				generateSuggestion(wrongText, correctText, correction,
						knownLangOfSuggestions);
			}
		}
	}
	
	/**
	 * Determina el lenguaje de las sugerencias aportadas por una correccion,
	 * segun el lenguaje en que esta ha sido publicada. <br>
	 * Se espera que la correccion haya sido publicada en el mismo idioma del
	 * hilo del foro al que pertenece, por tanto ese sera el idioma elegido
	 * en caso de que la API 'Language Detector' detecte varios
	 * idiomas posibles a elegir.
	 * @param correction correccion que ha aportado las sugerencias
	 * @return codigo ISO-639 del lenguaje detectado
	 */
	private String getLanguageOfSuggestions(Correction correction) {
		String result = null;
		String text = correction.getComment().getText();
		String supposedLanguage = 
				correction.getForumThread().getFeed().getLanguage().getLocale();
		List<String> detectedLangs = 
				LangDetector.getInstance().getDetectedLanguages(text);
		if( detectedLangs.contains(supposedLanguage) ){ result=supposedLanguage;
		}else if( ! detectedLangs.isEmpty() ){ result = detectedLangs.get(0); }
		return result;
	}

	/**
	 * Comprueba si la correccion realizada sobre una frase dada es adecuada
	 * para ser almacenada como una 'Sugerencia'.
	 * @param originalSentence la frase original, antes de ser corregida
	 * @param correctedSentence la frase una vez corregida
	 * @return
	 * 'true' si la frase original y la corregida no son la misma,
	 * y si ambas respetan ciertos limites de longitud de caracteres segun
	 * {@link #verifySentenceLength}. <br>
	 * 'false' si no se cumple alguna de dichas condiciones.
	 * @see #verifySentenceLength
	 */
	private boolean verifySuitableToSuggest(String originalSentence,
			String correctedSentence) {
		return verifySentenceLength(originalSentence)
				&& verifySentenceLength(correctedSentence)
				&& ! originalSentence.equals(correctedSentence);
	}

	/**
	 * Comprueba si la frase dada contiene cierta longitud de caracteres
	 * considerada adecuada (entre 3 y 100 caracteres). <br>
	 * Estableciendo unos limites a la longitud de las frases se consigue
	 * evitar que existan infinitas 'Sugerencias' sintacticamente distintas,
	 * lo que comprometeria la eficiencia y eficacia de las busquedas
	 * de sugerencias solicitadas por un usuario.
	 * @param sentence la frase cuya longitud se comprueba
	 * @return
	 * 'true' si la frase contiene entre 3 y 100 caracteres. <br>
	 * 'false' si el numero de caracteres no esta comprendido entre 3 y 40.
	 */
	private boolean verifySentenceLength(String sentence) {
		int minLimit = 3;
		int maxLimit = 100;
		return ( sentence.length()>=minLimit && sentence.length()<=maxLimit );
	}
	
	/**
	 * Genera un objeto {@link Suggestion} a partir de los parametros recibidos
	 * y lo agrega a la base de datos.
	 * @param wrongText atributo {@link Suggestion#wrongText}
	 * del objeto Suggestion generado
	 * @param correctText atributo {@link Suggestion#correctText}
	 * del objeto Suggestion generado
	 * @param correction atributo {@link Suggestion#correction}
	 * del objeto Suggestion generado
	 * @param language atributo {@link Suggestion#language}
	 * del objeto Suggestion generado
	 * @throws EntityAlreadyFoundException
	 */
	private void generateSuggestion(String wrongText, String correctText,
			Correction correction, String language)
			throws EntityAlreadyFoundException {
		Suggestion sugg = new Suggestion(wrongText, correctText,
				correction, language, false);
		if( sugg.verifyDecimalCodes() ){ return; }
		try {
			suggJPA.createSuggestion(sugg);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}

	/**
	 * Elimina la Sugerencia indicada de la base de datos
	 * @param suggestionId identificador de la sugerencia que se agrega
	 * @throws EntityNotFoundException
	 */
	public void deleteSuggestionById(Long suggestionId)
			throws EntityNotFoundException {
		try {
			suggJPA.deleteSuggestionById(suggestionId);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
}
