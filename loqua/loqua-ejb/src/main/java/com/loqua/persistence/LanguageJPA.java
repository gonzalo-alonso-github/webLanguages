package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.model.UserNativeLanguage;
import com.loqua.model.UserPracticingLanguage;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class LanguageJPA {
	
	private static final String LANGUAGE_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Language' entity not found"
			+ " at Persistence layer";
	private static final String NATIVEUSER_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'UserNativeLanguage' entity"
			+ " not found at Persistence layer";
	private static final String PRACTICINGUSER_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'UserPracticingLanguage' entity"
			+ " not found at Persistence layer";
	private static final String NATIVEUSER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'UserNativeLanguage' entity"
			+ " already found at Persistence layer";
	private static final String PRACTICINGUSER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'UserPracticingLanguage' entity"
			+ " already found at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	public Language getLanguageByName(String name) 
			throws EntityNotPersistedException {
		Language result = new Language();
		try{
			result = (Language) JPA.getManager()
				.createNamedQuery("Language.getLanguageByName")
				.setParameter(1, name)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					LANGUAGE_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Language> getAllLanguages() {
		List<Language> result = new ArrayList<Language>();
		try{
			result = (List<Language>) JPA.getManager()
				.createNamedQuery("Language.getAllLanguages")
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Language> getNativeLanguagesByUser( Long userID ){
		List<Language> result = new ArrayList<Language>();
		try{
			result = (List<Language>) JPA.getManager()
				.createNamedQuery("Language.getNativeLanguagesByUser")
				.setParameter(1, userID)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Language> getPracticingLanguagesByUser( Long userID ){
		List<Language> result = new ArrayList<Language>();
		try{
			result = (List<Language>) JPA.getManager()
				.createNamedQuery("Language.getPracticingLanguagesByUser")
				.setParameter(1, userID)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	/*
	@SuppressWarnings("unchecked")
	public List<Language> getLanguagesByCountry( String country ){
		List<Language> result = new ArrayList<Language>();
		try{
			result = (List<Language>) JPA.getManager()
				.createNamedQuery("Language.getLanguagesByCountry")
				.setParameter(1, country)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	*/
	public void updateLanguage(Language language)
			throws EntityNotPersistedException {
		try{	
			JPA.getManager().merge( language );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					LANGUAGE_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void createUserNativeLanguage(User user, Language language)
			throws EntityAlreadyPersistedException {
		try{
			UserNativeLanguage unl = new UserNativeLanguage();
			unl.setUser(user);
			unl.setLanguage(language);
			JPA.getManager().persist(unl);
			JPA.getManager().flush();
			JPA.getManager().refresh(unl);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					NATIVEUSER_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void deleteUserNativeLanguage(User user, Language language)
			throws EntityNotPersistedException {
		try{	
			JPA.getManager()
				.createNamedQuery("Language.deleteUserNativeLanguages")
				.setParameter(1, user.getId())
				.setParameter(2, language.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					NATIVEUSER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void createUserPracticedLanguage(User user, Language language)
			throws EntityAlreadyPersistedException {
		try{
			UserPracticingLanguage upl = new UserPracticingLanguage();
			upl.setUser(user);
			upl.setLanguage(language);
			JPA.getManager().persist(upl);
			JPA.getManager().flush();
			JPA.getManager().refresh(upl);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					PRACTICINGUSER_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void deleteUserPracticedLanguage(User user, Language language)
			throws EntityNotPersistedException {
		try{	
			JPA.getManager()
				.createNamedQuery("Language.deleteUserPracticedLanguages")
				.setParameter(1, user.getId())
				.setParameter(2, language.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PRACTICINGUSER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	public void deleteNativeLanguagesByUser(User user) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Language.deleteNativeLanguagesByUser")
				.setParameter(1, user.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					NATIVEUSER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void deletePracticedLanguagesByUser(User user) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Language.deletePracticedLanguagesByUser")
				.setParameter(1, user.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					NATIVEUSER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
