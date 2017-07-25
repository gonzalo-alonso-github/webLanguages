package com.loqua.business.services.impl.transactionScript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Country;
import com.loqua.persistence.CountryJPA;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionCountry {

	private static final CountryJPA countryJPA = new CountryJPA();
	
	public Map<Long, Country> getAllCountries() {
		List<Country> allCountries = countryJPA.getAllCountries();
		Map<Long,Country> mapCountries = new HashMap<Long,Country>();
		for(Country language : allCountries){
			mapCountries.put(language.getId(), language);
		}
		return mapCountries;
	}
	
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
	public Country getCountryOriginByUser(Long userID)
			throws EntityNotFoundException {
		Country result = new Country();
		try {
			result = countryJPA.getCountryOriginByUser(userID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}

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
