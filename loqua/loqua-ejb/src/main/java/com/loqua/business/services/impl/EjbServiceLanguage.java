package com.loqua.business.services.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionLanguage;
import com.loqua.business.services.serviceLocal.LocalServiceLanguage;
import com.loqua.business.services.serviceRemote.RemoteServiceLanguage;
import com.loqua.model.Language;
import com.loqua.model.User;

@Stateless
@WebService(name="ServiceLanguage")
public class EjbServiceLanguage 
		implements LocalServiceLanguage, RemoteServiceLanguage {
	
	private static final TransactionLanguage transactionLanguage = 
			new TransactionLanguage();
	
	@Override
	public Language getLanguageByName(String name){
		Language result = new Language();
		try{
			result = transactionLanguage.getLanguageByName(name);
		}catch( EntityNotFoundException e ){
			result = null;
		}
		return result;
	}

	
	@Override
	public List<Language> getAllLanguagesFromDB(){
		return transactionLanguage.getAllLanguagesFromDB();
	}
	
	@Override
	public Map<Long, Language> getAllLanguagesFromMemory(){
		return transactionLanguage.getAllLanguagesFromMemory();
	}
	
	@Override
	public Language getLanguageByIdFromMemory(Long languageID){
		return transactionLanguage.getLanguageByIdFromMemory(languageID);
	}
	
	@Override
	public List<Language> getListLanguagesByIdsFromMemory(
			List<Long> languagesIDs){
		return transactionLanguage.getListLanguagesByIdsFromMemory(languagesIDs);
	}
	
	@Override
	public Map<Long, Language> getMapLanguagesByIdsFromMemory(
			List<Long> languagesIDs){
		return transactionLanguage.getMapLanguagesByIdsFromMemory(languagesIDs);
	}
	
	@Override
	public List<Language> getNativeLanguagesByUser(Long userID) {
		return transactionLanguage.getNativeLanguagesByUser(userID);
	}

	@Override
	public List<Language> getPracticingLanguagesByUser(Long userID) {
		return transactionLanguage.getPracticingLanguagesByUser(userID);
	}
	/*
	@Override
	public List<Language> getLanguagesByCountry(String country) {
		return transactionLanguage.getLanguagesByCountry(country);
	}
	*/
	@Override
	public void updateLanguage(Language language) throws EntityNotFoundException{
		transactionLanguage.updateLanguage(language);
	}
	
	@Override
	public void createUserNativeLanguage( User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException {
		transactionLanguage.createUserNativeLanguage(
				user, originalNativeLanguagesIDs, editedNativeLanguagesIDs);
	}
	
	@Override
	public void deleteUserNativeLanguage(User userLogged,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityNotFoundException {
		transactionLanguage.deleteUserNativeLanguage(userLogged,
				originalNativeLanguagesIDs, editedNativeLanguagesIDs);
	}
	
	@Override
	public void createUserPracticedLanguage(User user,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException {
		transactionLanguage.createUserPracticedLanguage(user,
				originalPracticedLanguagesIDs, editedPracticedLanguagesIDs);
	}
	
	@Override
	public void deleteUserPracticedLanguage(User userLogged,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs) 
			throws EntityNotFoundException {
		transactionLanguage.deleteUserPracticedLanguage(userLogged,
				originalPracticedLanguagesIDs, editedPracticedLanguagesIDs);
	}
	
	@Override
	public void deleteNativeLanguagesByUser(User user)
			throws EntityNotFoundException {
		transactionLanguage.deleteNativeLanguagesByUser(user);
	}
	
	@Override
	public void deletePracticedLanguagesByUser(User user)
			throws EntityNotFoundException {
		transactionLanguage.deletePracticedLanguagesByUser(user);
	}
}
