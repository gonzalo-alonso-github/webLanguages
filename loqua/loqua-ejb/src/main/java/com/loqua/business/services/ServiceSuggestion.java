package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Correction;
import com.loqua.model.Suggestion;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de la entidad {@link Suggestion}
 * @author Gonzalo
 */
public interface ServiceSuggestion {
	
	/**
	 * Consulta sugerencias segun su atributo 'id'
	 * @param suggestionId atributo 'id' del Suggestion que se consulta
	 * @return objeto Suggestion cuyo atributo 'id' coincide
	 * con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public Suggestion getSuggestionById(Long suggestionId)
			throws EntityNotFoundException;
	
	/**
	 * Halla las sugerencias, aplicando un offset y limitando su numero,
	 * cuya frase incorrecta o corregida tenga una sintaxis identica
	 * a la que se recibe por parametro (comparando este con las propiedades
	 * {@link Suggestion#decimalWrongText}
	 * y {@link Suggestion#decimalCorrectText})
	 * @param offset offset de los Suggestion devueltos
	 * @param limitSuggestions limite maximo de Suggestion devueltos
	 * @param decimalCodeStr valor para filtrar las busquedas de sugerencias
	 * segun las mencionadas claves indexadas
	 * @return lista de Suggestion cuyo atributo 'decimalWrongText'
	 * coincide con el parametro recibido
	 */
	public List<Suggestion> getSuggestionsByDecimalCode(int offset,
			int limitSuggestions, String decimalCodeStr);
	
	/**
	 * Consulta todas las sugerencias aplicando un offset y limitando su numero
	 * @param offset de los Suggestion devueltos
	 * @param limitSuggestions limite maximo de Suggestion devueltos
	 * @param lang idioma de las sugerencias consultadas
	 * @return lista de Suggestion, aplicando el offset y el limite
	 * de elementos indicado
	 */
	List<Suggestion> getSuggestionsByLang(
			int offset, int limitSuggestions, String lang);
	
	/**
	 * Halla el numero de sugerencias donde alguno de sus atributos
	 * {@link Suggestion#decimalWrongText} o
	 * {@link Suggestion#decimalCorrectText} coincida con el valor decimal
	 * indicado.
	 * @param decimalCode valor para filtrar las busquedas de sugerencias
	 * segun las mencionadas claves indexadas
	 * @return numero de sugerencias obtenido
	 */
	public int getNumTotalSuggestionsByDecimalCode(String decimalCode);

	/**
	 * Halla la cantidad total de sugerencias existentes
	 * @param lang idioma de las sugerencias consultadas
	 * @return numero de sugerencias obtenido
	 */
	public int getNumTotalSuggestionsByLang(String lang);
	
	/**
	 * Agrega una sugerencia a la base de datos 
	 * @param suggestion objeto Suggestion que se desea guardar
	 * @return objeto Suggestion generado
	 * @throws EntityAlreadyFoundException
	 */
	Suggestion createSuggestionForTest(Suggestion suggestion)
			throws EntityAlreadyFoundException;
	
	/**
	 * Genera objetos {@link Suggestion} a partir de los parametros recibidos,
	 * y los agrega a la base de datos.
	 * @param originalSentences las frases originales en las que se divide el
	 * comentario que se ha corregido
	 * @param correctedSentences las frases del comentario, una vez corregidas
	 * por el usuario 
	 * @param correction objeto Correction al que pertenecen las frases
	 * @throws EntityAlreadyFoundException
	 */
	void createSuggestions(List<String> originalSentences,
			List<String> correctedSentences, Correction correction)
			throws EntityAlreadyFoundException;
	
	/**
	 * Elimina la Sugerencia indicada de la base de datos
	 * @param suggestionId identificador de la sugerencia que se agrega
	 * @throws EntityNotFoundException
	 */
	void deleteSuggestionByIdForTest(Long suggestionId)
			throws EntityNotFoundException;
}