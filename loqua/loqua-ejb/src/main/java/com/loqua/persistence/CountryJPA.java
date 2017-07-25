package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.loqua.model.Country;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class CountryJPA {
	
	private static final String COUNTRY_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Country' entity not found"
			+ " at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";

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
