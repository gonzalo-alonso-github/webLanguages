package com.loqua.business.services.impl.transactionScript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Country;
import com.loqua.persistence.CountryJPA;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de la entidad
 * {@link Country}. <br/>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionCountry {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a la entidad {@link Country} */
	private static final CountryJPA countryJPA = new CountryJPA();
	
	
	/**
	 * Consulta todos los paises disponibles
	 * @return Map&lt;Long, Country&gt;, donde la clave es el atributo 'id'
	 * del Country, y donde el valor es el propio Country
	 */
	public Map<Long, Country> getAllCountries() {
		List<Country> allCountries = countryJPA.getAllCountries();
		Map<Long,Country> mapCountries = new HashMap<Long,Country>();
		for(Country language : allCountries){
			mapCountries.put(language.getId(), language);
		}
		return mapCountries;
	}
	
	/**
	 * Consulta paises segun su atributo 'id'
	 * @param countryId atributo 'id' del Country que se consulta
	 * @return Country cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public Country getCountryById(Long countryId) 
			throws EntityNotFoundException {
		Country result = new Country();
		try {
			result = countryJPA.getCountryById(countryId);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/*
	 * Obtene el pais de origen del usuario dado
	 * @param userId atributo 'id' del UserInfoPrivacity al cual pertenece
	 * el Country que se consulta
	 * @return Country asociado al atributo 'countryOrigin' del User dado
	 * @throws EntityNotFoundException
	public Country getCountryOriginByUser(Long userId)
			throws EntityNotFoundException {
		Country result = new Country();
		try {
			result = countryJPA.getCountryOriginByUser(userId);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	 */
	/*
	 * Obtene el pais de ubicacion del usuario dado
	 * @param userId atributo 'id' del UserInfoPrivacity al cual pertenece
	 * el Country que se consulta
	 * @return Country asociado al atributo 'countryLocation' del User dado
	 * @throws EntityNotFoundException
	public Country getCountryLocationByUser(Long userID)
			throws EntityNotFoundException {
		Country result = new Country();
		try {
			result = countryJPA.getCountryLocationByUser(userID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	*/
}
