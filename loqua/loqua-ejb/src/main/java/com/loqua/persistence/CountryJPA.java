package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.loqua.model.Country;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Country}
 * @author Gonzalo
 */
public class CountryJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Country'
	 * en la base de datos */
	private static final String COUNTRY_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Country' entity not found"
			+ " at Persistence layer";

	/**
	 * Realiza la consulta JPQL 'Comment.getAllCountries'
	 * @return lista de todos los Country de la base de datos
	 */
	@SuppressWarnings("unchecked")
	public List<Country> getAllCountries() {
		List<Country> result = new ArrayList<Country>();
		try{
			result = (List<Country>) JPA.getManager()
				.createNamedQuery("Country.getAllCountries")
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	/**
	 * Realiza la consulta JPQL 'Country.getCountryById'
	 * @param countryId  atributo 'id' del Country que se consulta
	 * @return Country cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
	public Country getCountryById(Long countryId) 
			throws EntityNotPersistedException {
		Country result = new Country();
		try{
			result = (Country) JPA.getManager()
				.createNamedQuery("Country.getCountryById")
				.setParameter(1, countryId)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					COUNTRY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/*
	 * Realiza la consulta JPQL 'Country.getCountryOriginByUser'
	 * @param userId atributo 'id' del UserInfoPrivacity al cual pertenece
	 * el Country que se consulta
	 * @return Country asociado al atributo 'countryOrigin' del User dado
	 * @throws EntityNotPersistedException
	 * 
	public Country getCountryOriginByUser(Long userID) 
			throws EntityNotPersistedException {
		Country result = new Country();
		try{
			result = (Country) JPA.getManager()
				.createNamedQuery("Country.getCountryOriginByUser")
				.setParameter(1, userID)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					COUNTRY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	*/
	
	/*
	 * Realiza la consulta JPQL 'Country.getCountryLocationByUser'
	 * @param userID atributo 'id' del UserInfoPrivacity al cual pertenece
	 * el Country que se consulta
	 * @return Country asociado al atributo 'countryLocation' del User dado
	 * @throws EntityNotPersistedException
	 *
	public Country getCountryLocationByUser(Long userID) 
			throws EntityNotPersistedException {
		Country result = new Country();
		try{
			result = (Country) JPA.getManager()
				.createNamedQuery("Country.getCountryLocationByUser")
				.setParameter(1, userID)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					COUNTRY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	*/
}
