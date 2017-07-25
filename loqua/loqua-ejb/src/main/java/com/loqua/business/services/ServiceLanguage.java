package com.loqua.business.services;

import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Language;
import com.loqua.model.User;

public interface ServiceLanguage {

	Language getLanguageByName(String name);
	
	List<Language> getAllLanguagesFromDB();
	Map<Long, Language> getAllLanguagesFromMemory();
	
	Language getLanguageByIdFromMemory(Long languageID);
	
	List<Language> getListLanguagesByIdsFromMemory(List<Long> languagesIDs);
	
	Map<Long, Language> getMapLanguagesByIdsFromMemory(List<Long> languagesIDs);
	
	List<Language> getNativeLanguagesByUser(Long userID);

	List<Language> getPracticingLanguagesByUser(Long userID);

	//List<Language> getLanguagesByCountry(String country);
	
	public void updateLanguage(Language language) throws EntityNotFoundException;
	
	void createUserNativeLanguage(User user,
			List<Long> beanUserNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException;
	void deleteUserNativeLanguage(User user,
			List<Long> beanUserNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException,EntityNotFoundException;
	
	void createUserPracticedLanguage(User user,
			List<Long> beanUserPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException;
	void deleteUserPracticedLanguage(User user,
			List<Long> beanUserPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException,EntityNotFoundException;

	void deleteNativeLanguagesByUser(User user) throws EntityNotFoundException;

	void deletePracticedLanguagesByUser(User user) throws EntityNotFoundException;
}