package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.cache.Cache;
import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.persistence.LanguageJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionLanguage {

	private static final LanguageJPA languageJPA = new LanguageJPA();
	
	public Language getLanguageByName(String name) 
			throws EntityNotFoundException {
		Language result = new Language();
		try {
			result = languageJPA.getLanguageByName(name);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	
	public List<Language> getAllLanguagesFromDB() {
		return languageJPA.getAllLanguages();
	}
	
	public Map<Long, Language> getAllLanguagesFromCache(){
		// A diferencia de this.getAllLanguagesFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Cache
		Map<Long, Language> result = 
				Cache.getCacheListsLanguages().getAllLanguages();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsLanguages().changed();
			Cache.updateCacheListsLanguages();
			result = Cache.getCacheListsLanguages().getAllLanguages();
		}
		return result;
	}
	
	public List<Language> getListLanguagesByIdsFromCache(
			List<Long> languagesIDs){
		List<Language> result = new ArrayList<Language>();
		Map<Long, Language> filteredLanguages = 
				getMapLanguagesByIdsFromCache(languagesIDs);
		result = new ArrayList<Language>(filteredLanguages.values());
		return result;
	}
	
	public Map<Long, Language> getMapLanguagesByIdsFromDB(
			List<Long> languagesIDs){
		List< Language> listLanguagesByIds = 
				languageJPA.getLanguagesByIds(languagesIDs);
		Map<Long,Language> mapLanguages = new HashMap<Long,Language>();
		for(Language language : listLanguagesByIds){
			mapLanguages.put(language.getId(), language);
		}
		return mapLanguages;
	}
	
	public Map<Long, Language> getMapLanguagesByIdsFromCache(
			List<Long> languagesIDs){
		Map<Long, Language> filteredLanguages = new HashMap<Long, Language>();
		Map<Long, Language> allLanguages = getAllLanguagesFromCache();
		filteredLanguages = (allLanguages.entrySet()
	            .stream().filter(l -> languagesIDs.contains(l.getKey()))
	            .collect(Collectors.toMap(l->l.getKey(), l->l.getValue()))
	            );
		return filteredLanguages;
	}
	
	public List<Language> getNativeLanguagesByUser(Long userID){
		return languageJPA.getNativeLanguagesByUser(userID);
	}
	
	public List<Language> getPracticingLanguagesByUser(Long userID){
		return languageJPA.getPracticingLanguagesByUser(userID);
	}
	/*
	public List<Language> getLanguagesByCountry(String country){
		return languageJPA.getLanguagesByCountry(country);
	}
	*/
	public void updateLanguage(Language language) throws EntityNotFoundException{
		try {
			languageJPA.updateLanguage(language);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void createUserNativeLanguage( User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			List<Long> languagesIDsToUpdate = 
					new ArrayList<Long>(editedNativeLanguagesIDs);
			languagesIDsToUpdate.removeAll(originalNativeLanguagesIDs);
			
			for( Long languageId : languagesIDsToUpdate ){
				//Language lang = getLanguageByIdFromCache(languageId);
				Language lang = languageJPA.getLanguageById(languageId);
				languageJPA.createUserNativeLanguage(user, lang);
			}
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void deleteUserNativeLanguage(User userLogged,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityNotFoundException {
		try {
			ArrayList<Long> languagesIDsToDelete = 
					new ArrayList<Long>(originalNativeLanguagesIDs);
			languagesIDsToDelete.removeAll(editedNativeLanguagesIDs);
			
			for( Long languageId : languagesIDsToDelete ){
				//Language lang = getLanguageByIdFromCache(languageId);
				Language lang = languageJPA.getLanguageById(languageId);
				languageJPA.deleteUserNativeLanguage(userLogged, lang);
			}
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void createUserPracticedLanguage(User user, Language language)
			throws EntityAlreadyFoundException {
		try {
			languageJPA.createUserPracticedLanguage(user, language);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	public void createUserPracticedLanguage(User user,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			List<Long> languagesIDsToUpdate = 
					new ArrayList<Long>(editedPracticedLanguagesIDs);
			languagesIDsToUpdate.removeAll(originalPracticedLanguagesIDs);
			
			for( Long languageId : languagesIDsToUpdate ){
				//Language lang = getLanguageByIdFromCache(languageId);
				Language lang = languageJPA.getLanguageById(languageId);
				languageJPA.createUserPracticedLanguage(user, lang);
			}
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void deleteUserPracticedLanguage(User userLogged,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs) 
			throws EntityNotFoundException {
		try {
			ArrayList<Long> languagesIDsToDelete = 
					new ArrayList<Long>(originalPracticedLanguagesIDs);
			languagesIDsToDelete.removeAll(editedPracticedLanguagesIDs);
			
			for( Long languageId : languagesIDsToDelete ){
				// Language lang = getLanguageByIdFromCache(languageId);
				Language lang = languageJPA.getLanguageById(languageId);
				languageJPA.deleteUserPracticedLanguage(userLogged, lang);
			}
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/*
	private Language getLanguageByIdFromCache(Long languageID){
		Language result = new Language();
		Map<Long, Language> allLanguages = getAllLanguagesFromCache();
		result = allLanguages.get(languageID);
		return result;
	}
	*/
	
	public void deleteNativeLanguagesByUser(User user)
			throws EntityNotFoundException {
		try {
			languageJPA.deleteNativeLanguagesByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	public void deletePracticedLanguagesByUser(User user)
			throws EntityNotFoundException {
		try {
			languageJPA.deletePracticedLanguagesByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
}
