package com.loqua.business.services;

import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Language;
import com.loqua.model.User;

public interface ServiceLanguage {

	/* A priori no se va a utilizar ninguno de los metodos
	que contienen el sufijo 'FromCache'. Si se utilizan, entonces conviene
	descomentar el uso de la Cache aquellos metodos de la clase
	TansactionLanguage que realicen creaciones y actualizaciones:
	createUserNativeLanguage(User, List<Long>, List<Long>)
	deleteUserNativeLanguage
	createUserPracticedLanguage(User, List<Long>, List<Long>)
	deleteUserPracticedLanguage*/
	
	Language getLanguageByName(String name);
	
	List<Language> getListAllLanguagesFromDB();
	Map<Long, Language> getAllLanguagesFromCache();
	
	//List<Language> getListLanguagesByIdsFromCache(List<Long> languagesIDs);
	
	Map<Long, Language> getMapLanguagesByIdsFromDB(List<Long> languagesIDs);
	Map<Long, Language> getMapLanguagesByIdsFromCache(List<Long> languagesIDs);
	
	List<Language> getNativeLanguagesByUser(Long userID);

	List<Language> getPracticingLanguagesByUser(Long userID);

	//List<Language> getLanguagesByCountry(String country);
	
	public void updateLanguage(Language language) throws EntityNotFoundException;
	
	void createUserNativeLanguage(User user,
			List<Long> beanUserNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	void deleteUserNativeLanguage(User user,
			List<Long> beanUserNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException,EntityNotFoundException;
	
	void createUserPracticedLanguage(User user,
			List<Long> beanUserPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	void deleteUserPracticedLanguage(User user,
			List<Long> beanUserPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException,EntityNotFoundException;

	void deleteNativeLanguagesByUser(User user) throws EntityNotFoundException;

	void deletePracticedLanguagesByUser(User user) throws EntityNotFoundException;
}