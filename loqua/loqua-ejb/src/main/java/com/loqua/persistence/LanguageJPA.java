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

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Language}, {@link UserNativeLanguage} y {@link UserPracticingLanguage}
 * @author Gonzalo
 */
public class LanguageJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Language'
	 * en la base de datos */
	private static final String LANGUAGE_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Language' entity not found"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'UserNativeLanguage' en la base de datos */
	private static final String NATIVEUSER_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'UserNativeLanguage' entity"
			+ " not found at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'UserNativeLanguage' en la base de datos */
	private static final String NATIVEUSER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'UserNativeLanguage' entity"
			+ " already found at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'UserPracticingLanguage' en la base de datos */
	private static final String PRACTICINGUSER_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'UserPracticingLanguage' entity"
			+ " not found at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'UserPracticingLanguage' en la base de datos */
	private static final String PRACTICINGUSER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'UserPracticingLanguage' entity"
			+ " already found at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Language.getLanguageByName'
	 * @param name atributo homonimo del Language que se consulta
	 * @return Language cuyo atributo 'name' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Language.getAllLanguages'
	 * @return lista de todos los Language de la base de datos
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Language.getLanguageById'
	 * @param languageId atributo 'id' del Language que se consulta
	 * @return Language cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
	public Language getLanguageById(Long languageId) 
			throws EntityNotPersistedException {
		Language result = new Language();
		try{
			result = (Language) JPA.getManager()
				.createNamedQuery("Language.getLanguageById")
				.setParameter(1, languageId)
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
	
	/**
	 * Realiza la consulta JPQL 'Language.getLanguagesByIds'
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * que se consultan
	 * @return lista de Language cuyos atributos 'id' coinciden con los de la
	 * lista recibida por parametro
	 */
	@SuppressWarnings("unchecked")
	public List<Language> getLanguagesByIds(List<Long> listLanguagesIDs) {
		List<Language> result = new ArrayList<Language>();
		try{
			result = (List<Language>) JPA.getManager()
					.createNamedQuery("Language.getLanguagesByIds")
					.setParameter(1, listLanguagesIDs)
					.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Language.getNativeLanguagesByUser'
	 * @param userId atributo 'id' del User al que pertenecen los
	 * UserNativeLanguage que se consultan
	 * @return lista de Language, asociados a los UserNativeLanguage
	 * pertenecientes al User que se recibe por paramero
	 */
	@SuppressWarnings("unchecked")
	public List<Language> getNativeLanguagesByUser( Long userId ){
		List<Language> result = new ArrayList<Language>();
		try{
			result = (List<Language>) JPA.getManager()
				.createNamedQuery("Language.getNativeLanguagesByUser")
				.setParameter(1, userId)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Language.getPracticingLanguagesByUser'
	 * @param userId atributo 'id' del User al que pertenecen los
	 * UserPracticingLanguages que se consultan
	 * @return lista de Language, asociados a los UserPracticingLanguages
	 * pertenecientes al User que se recibe por paramero
	 */
	@SuppressWarnings("unchecked")
	public List<Language> getPracticingLanguagesByUser( Long userId ){
		List<Language> result = new ArrayList<Language>();
		try{
			result = (List<Language>) JPA.getManager()
				.createNamedQuery("Language.getPracticingLanguagesByUser")
				.setParameter(1, userId)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/*
	 * Realiza la consulta JPQL 'Language.getLanguagesByCountry'
	 * @param country atributo 'name' del Country al que pertenecen los
	 * Language que se consultan
	 * @return lista de Language pertenecientes al Country recibido por
	 * parametro
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
	
	/**
	 * Actualiza en la base de datos el objeto Language dado
	 * @param language objeto Language que se desea guardar
	 * @throws EntityNotPersistedException
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
	
	/**
	 * Genera un objeto UserNativeLanguage a partir de los parametros recibidos
	 * y lo agrega a la base de datos
	 * @param user User asociado al UserNativeLanguage que se genera
	 * @param language Language asociado al UserNativeLanguage que se genera
	 * @throws EntityAlreadyPersistedException
	 */
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
	
	/**
	 * Elimina de la base de datos el objeto UserNativeLanguage
	 * a partir de los parametros recibidos
	 * (es decir: elimina la relacion de 'User tiene un Language materno')
	 * @param user User asociado al UserNativeLanguage que se elimina
	 * @param language Language asociado al UserNativeLanguage que se elimina
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Genera un objeto UserPracticingLanguage a partir de los
	 * parametros recibidos y lo agrega a la base de datos
	 * @param user User asociado al UserPracticingLanguage que se genera
	 * @param language Language asociado al UserPracticingLanguage que se genera
	 * @throws EntityAlreadyPersistedException
	 */
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
	
	/**
	 * Elimina de la base de datos el objeto UserPracticingLanguage
	 * a partir de los parametros recibidos
	 * (es decir: elimina la relacion de 'User practica un Language')
	 * @param user User asociado al UserPracticingLanguage que se elimina
	 * @param language Language asociado al UserPracticingLanguage
	 * que se elimina
	 * @throws EntityAlreadyPersistedException
	 */
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
	
	/**
	 * Elimina de la base de datos todos los objetos UserNativeLanguage
	 * cuyo atributo 'user' coincide con el parametro recibido
	 * (es decir: elimina el conjunto de todos los Language maternos de User)
	 * @param user User asociado a los UserNativeLanguage que se eliminan
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Elimina de la base de datos todos los objetos UserPracticingLanguage
	 * cuyo atributo 'user' coincide con el parametro recibido
	 * (es decir: elimina el conjunto de todos los Language practicados
	 * por User)
	 * @param user User asociado a los UserPracticingLanguage que se eliminan
	 * @throws EntityNotPersistedException
	 */
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
