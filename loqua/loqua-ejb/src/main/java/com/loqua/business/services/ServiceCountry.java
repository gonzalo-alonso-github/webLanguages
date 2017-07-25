package com.loqua.business.services;

import java.util.Map;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Country;

public interface ServiceCountry {
	
	Map<Long, Country> getAllCountries();

	Country getCountryById(Long countryId) throws EntityNotFoundException;
	
	/*
	Country getCountryOriginByUser(Long userID) throws EntityNotFoundException;
	
	Country getCountryLocationByUser(Long userID) throws EntityNotFoundException;
	*/
}