package com.loqua.persistence;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.Suggestion;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Suggestion}
 * @author Gonzalo
 */
public class SuggestionJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'Suggestion' en la base de datos */
	private static final String SUGGESTION_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Suggestion' entity not found"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al repetirse la entidad 'Suggestion'
	 * en la base de datos */
	private static final String SUGGESTION_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Suggestion' entity already"
			+ " found at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Suggestion.getSuggestionById'
	 * @param suggId atributo 'id' del Suggestion que se consulta
	 * @return objeto Suggestion cuyo atributo 'id' coincide
	 * con el parametro dado
	 * @throws EntityNotPersistedException
	 */
	public Suggestion getSuggestionById(Long suggId)
			throws EntityNotPersistedException {
		Suggestion result = new Suggestion();
		try{
			result = (Suggestion) JPA.getManager()
					.createNamedQuery("Suggestion.getSuggestionById")
					.setParameter(1, suggId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					SUGGESTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Suggestion.getSuggestionsByDecimalCode',
	 * aplicando un offset y limitando el numero de elementos devueltos.
	 * @param offset offset de los Suggestion devueltos
	 * @param limitSuggestions limite maximo de Suggestion devueltos
	 * @param decimalValue atributo 'decimalWrongText' de los Suggestion
	 * que se consultan
	 * @return lista de Suggestion cuyo atributo 'decimalWrongText' coincide
	 * con el parametro dado
	 */
	@SuppressWarnings("unchecked")
	public List<Suggestion> getSuggestionsByDecimalCode(int offset,
			int limitSuggestions, BigInteger decimalValue){
		List<Suggestion> result = new ArrayList<Suggestion>();
		try{
			result = (List<Suggestion>) JPA.getManager()
					.createNamedQuery("Suggestion.getSuggestionsByDecimalCode")
					.setParameter(1, decimalValue)
					.setFirstResult(offset) // offset
					.setMaxResults(limitSuggestions) // limit
					.getResultList();				
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Suggestion.getAllSuggestionsByLang',
	 * aplicando un offset y limitando el numero de elementos devueltos.
	 * @param offset offset de los Suggestion devueltos
	 * @param limitSuggestions limite maximo de Suggestion devueltos
	 * @return lista de Suggestion, aplicando el offset y el limite
	 * de elementos indicado
	 */
	@SuppressWarnings("unchecked")
	public List<Suggestion> getAllSuggestionsByLang(
			int offset, int limitSuggestions, String lang){
		List<Suggestion> result = new ArrayList<Suggestion>();
		try{
			result = (List<Suggestion>) JPA.getManager()
					.createNamedQuery("Suggestion.getAllSuggestionsByLang")
					.setParameter(1, lang)
					.setFirstResult(offset) // offset
					.setMaxResults(limitSuggestions) // limit
					.getResultList();				
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Suggestion.getNumTotalSuggestionsByDecimalCode'
	 * @param decimalCode valor para filtrar las busquedas de sugerencias
	 * segun las claves indexadas {@link Suggestion#decimalWrongText} y
	 * {@link Suggestion#decimalCorrectText}
	 * @return cantidad de Suggestion donde alguno de sus atributos
	 * {@link Suggestion#decimalWrongText} o
	 * {@link Suggestion#decimalCorrectText} coincida con el valor decimal
	 * indicado
	 * @throws EntityNotPersistedException
	 */
	public Integer getNumTotalSuggestionsByDecimalCode(BigInteger decimalCode)
			throws EntityNotPersistedException {
		Long result = 0l;
		try{
			result = (Long) JPA.getManager().createNamedQuery(
							"Suggestion.getNumTotalSuggestionsByDecimalCode")
					.setParameter(1, decimalCode)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					SUGGESTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		
		return result.intValue();
	}
	
	/**
	 * Realiza la consulta JPQL 'Suggestion.getNumTotalSuggestionsByLang'
	 * @param lang idioma de las sugerencias consultadas
	 * @return cantidad de Suggestion existentes en la base de datos
	 * @throws EntityNotPersistedException
	 */
	public Integer getNumTotalSuggestionsByLang(String lang)
			throws EntityNotPersistedException {
		Long result = 0l;
		try{
			result = (Long) JPA.getManager().createNamedQuery(
							"Suggestion.getNumTotalSuggestionsByLang")
					.setParameter(1, lang)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					SUGGESTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	/**
	 * Agrega a la base de datos el objeto Suggestion dado
	 * @param suggestion objeto Suggestion que se desea guardar
	 * @return objeto Suggestion generado
	 * @throws EntityAlreadyPersistedException
	 */
	public Suggestion createSuggestion(Suggestion suggestion) 
			throws EntityAlreadyPersistedException {
		try{
			JPA.getManager().persist( suggestion );
			JPA.getManager().flush();
			JPA.getManager().refresh(suggestion);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					SUGGESTION_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return suggestion;
	}
	
	/**
	 * Elimina la Sugerencia indicada de la base de datos
	 * @param suggestionId identificador de la sugerencia que se agrega
	 * @throws EntityNotPersistedException
	 */
	public void deleteSuggestionById(Long suggestionId)
			throws EntityNotPersistedException{
		try{
			/* Suggestion suggestion = 
					Jpa.getManager().find(Suggestion.class, suggestion.getId());
			Jpa.getManager().remove(suggestionId); */
			JPA.getManager()
				.createNamedQuery("Suggestion.deleteById")
				.setParameter(1, suggestionId)
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					SUGGESTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
