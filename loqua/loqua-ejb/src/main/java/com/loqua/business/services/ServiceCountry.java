package com.loqua.business.services;

import java.util.Map;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Country;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de la entidad {@link Country}
 * @author Gonzalo
 */
public interface ServiceCountry {
	
	/**
	 * Consulta todos los paises disponibles
	 * @return Map&lt;Long, Country&gt;, donde la clave es el atributo 'id'
	 * del Country, y donde el valor es el propio Country
	 */
	Map<Long, Country> getAllCountries();
	
	/**
	 * Consulta paises segun su atributo 'id'
	 * @param countryId atributo 'id' del Country que se consulta
	 * @return Country cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	Country getCountryById(Long countryId) throws EntityNotFoundException;
	
	/*
	 * Obtene el pais de origen del usuario dado
	 * @param userId atributo 'id' del UserInfoPrivacity al cual pertenece
	 * el Country que se consulta
	 * @return Country asociado al atributo 'countryOrigin' del User dado
	 * @throws EntityNotFoundException
	Country getCountryOriginByUser(Long userID) throws EntityNotFoundException;
	*/
	
	/*
	 * Obtene el pais de ubicacion del usuario dado
	 * @param userId atributo 'id' del UserInfoPrivacity al cual pertenece
	 * el Country que se consulta
	 * @return Country asociado al atributo 'countryLocation' del User dado
	 * @throws EntityNotFoundException
	Country getCountryLocationByUser(Long userID) throws EntityNotFoundException;
	*/
}