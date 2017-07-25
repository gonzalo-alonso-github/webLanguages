package com.loqua.business.services.impl;

import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionCountry;
import com.loqua.business.services.serviceLocal.LocalServiceCountry;
import com.loqua.business.services.serviceRemote.RemoteServiceCountry;
import com.loqua.model.Country;

@Stateless
@WebService(name="ServiceCountry")
public class EjbServiceCountry 
		implements LocalServiceCountry, RemoteServiceCountry {

	private static final TransactionCountry transactionCountry = 
			new TransactionCountry();
	
	@Override
	public Map<Long,Country> getAllCountries() {
		return transactionCountry.getAllCountries();
	}

	@Override
	public Country getCountryById(Long countryId)
			throws EntityNotFoundException {
		return transactionCountry.getCountryById(countryId);
	}
	
	/*
	@Override
	public Country getCountryOriginByUser(Long userID) 
			throws EntityNotFoundException {
		return transactionCountry.getCountryOriginByUser(userID);
	}

	@Override
	public Country getCountryLocationByUser(Long userID) 
			throws EntityNotFoundException {
		return transactionCountry.getCountryLocationByUser(userID);
	}
	*/
}
