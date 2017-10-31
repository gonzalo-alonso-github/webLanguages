package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionSuggestion;
import com.loqua.business.services.serviceLocal.LocalServiceSuggestion;
import com.loqua.business.services.serviceRemote.RemoteServiceSuggestion;
import com.loqua.model.Correction;
import com.loqua.model.Suggestion;

@Stateless
@WebService(name="ServiceSuggestion")
public class EjbServiceSuggestion 
		implements LocalServiceSuggestion , RemoteServiceSuggestion  {

	/** Objeto de la capa de negocio que realiza la logica relativa a la
	 * entidad {@link Suggestion}, incluyendo procedimientos 'CRUD'
	 * de dicha entidad */
	private static final TransactionSuggestion transactionSugg = 
			new TransactionSuggestion();
	
	@Override
	public Suggestion getSuggestionById(Long suggestionId)
			throws EntityNotFoundException{
		return transactionSugg.getSuggestionById(suggestionId);
	}
	
	@Override
	public List<Suggestion> getSuggestionsByDecimalCode(int offset,
			int limitSuggestions, String decimalCodeStr) {
		return transactionSugg.getSuggestionsByDecimalCode(
				offset, limitSuggestions, decimalCodeStr);
	}

	@Override
	public List<Suggestion> getSuggestionsByLang(int offset,
			int limitSuggestions, String lang) {
		return transactionSugg.getAllSuggestionsByLang(
				offset, limitSuggestions, lang);
	}

	@Override
	public int getNumTotalSuggestionsByDecimalCode(String decimalCode) {
		return transactionSugg.getNumTotalSuggestionsByDecimalCode(decimalCode);
	}

	@Override
	public int getNumTotalSuggestionsByLang(String lang) {
		return transactionSugg.getNumTotalSuggestionsByLang(lang);
	}
	
	@Override
	public Suggestion createSuggestionForTest(Suggestion suggestion)
			throws EntityAlreadyFoundException {
		return transactionSugg.createSuggestion(suggestion);
	}
	
	@Override
	public void createSuggestions(List<String> originalSentences,
			List<String> correctedSentences, Correction correction)
			throws EntityAlreadyFoundException {
		transactionSugg.createSuggestions(originalSentences,
				correctedSentences, correction);
	}
	
	@Override
	public void deleteSuggestionByIdForTest(Long suggestionId)
			throws EntityNotFoundException {
		transactionSugg.deleteSuggestionById(suggestionId);
	}
}
