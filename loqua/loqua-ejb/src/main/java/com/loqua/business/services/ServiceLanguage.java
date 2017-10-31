package com.loqua.business.services;

import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.model.UserNativeLanguage;
import com.loqua.model.UserPracticingLanguage;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link Language}, {@link UserNativeLanguage}
 * y {@link UserPracticingLanguage}
 * @author Gonzalo
 */
public interface ServiceLanguage {
	
	/**
	 * Consulta lenguajes segun su atributo 'name'
	 * @param name atributo homonimo del Language que se consulta
	 * @return objeto Language cuyo atributo 'name' coincide
	 * con el parametro dado
	 */
	Language getLanguageByName(String name);
	
	/**
	 * Consulta todos los lenguajes disponibles
	 * @return lista de todos los Language que maneja la aplicacion
	 */
	List<Language> getAllLanguagesFromDB();
	
	/**
	 * Halla los Language cuyo atributo 'id' coincide con alguno de los valores
	 * de la lista indicada
	 * @param languagesIDs  lista de atributos 'id' de los Language
	 * que se consultan
	 * @return Map&lt;Long, Language&gt;, donde la clave es el atributo 'id'
	 * del Language, y donde el valor es el propio Language
	 */
	Map<Long, Language> getMapLanguagesByIdsFromDB(List<Long> languagesIDs);
	
	/**
	 * Halla los lenguajes maternos de un usuario dado
	 * @param userID atributo 'id' del User que se consulta
	 * @return lista de Language, asociados a los UserNativeLanguage
	 * pertenecientes al User que se recibe por paramero
	 */
	List<Language> getNativeLanguagesByUser(Long userID);

	/**
	 * Halla los lenguajes practicados por un usuario dado
	 * @param userID atributo 'id' del User que se consulta
	 * @return lista de Language, asociados a los UserPracticingLanguage
	 * pertenecientes al User que se recibe por paramero
	 */
	List<Language> getPracticingLanguagesByUser(Long userID);
	
	/**
	 * Actualiza algun dato del lenguaje dado 
	 * @param language objeto Language que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updateLanguage(Language language) throws EntityNotFoundException;
	
	/**
	 * Actualiza la lista de lenguajes nativos del usuario.
	 * @param user User asociado a los Language indicados
	 * @param originalNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, antes de ser editada en los formularios de la pagina
	 * @param editedNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	public void updateNativeLanguages(User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException,EntityNotFoundException;
	
	/**
	 * Crea las asociaciones UserNativeLanguage entre el User dado y
	 * los Language indicados (es decir, la relacion de 'Laguage es el idioma
	 * materno de User')
	 * @param user User asociado a los Language indicados
	 * @param originalNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, antes de ser editada en los formularios de la pagina
	 * @param editedNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 *//*
	void createUserNativeLanguage(User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException;*/
	
	/**
	 * Elimina las asociaciones UserNativeLanguage entre el User dado y
	 * los Language indicados (es decir, la relacion de 'Laguage es el idioma
	 * materno de User')
	 * @param user User asociado a los Language indicados
	 * @param originalNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, antes de ser editada en los formularios de la pagina
	 * @param editedNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityNotFoundException
	 */
	/*
	void deleteUserNativeLanguage(User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException,EntityNotFoundException;
	*/
	
	/**
	 * Actualiza la lista de lenguajes practicados del usuario.
	 * @param user User asociado a los Language indicados
	 * @param originalNativeLanguagesIDs lista de lenguajes practicados del
	 * usuario, antes de ser editada en los formularios de la pagina
	 * @param editedNativeLanguagesIDs lista de lenguajes practicados del
	 * usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	public void updatePracticedLanguages(User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException,EntityNotFoundException;
	
	/**
	 * Crea las asociaciones (UserPracticingLanguage) entre el User dado y
	 * los Language indicados (es decir, la relacion de 'Laguage es
	 * practicado por User')
	 * @param user User asociado a los Language indicados
	 * @param originalPracticedLanguagesIDs lista de lenguajes practicados por
	 * el usuario, antes de ser editada en los formularios de la pagina
	 * @param editedPracticedLanguagesIDs lista de lenguajes practicados por
	 * el usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	/*
	void createUserPracticedLanguage(User user,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	*/
	/**
	 * Elimina las asociaciones (UserPracticingLanguage) entre el User dado y
	 * los Language indicados (es decir, la relacion de 'Laguage es
	 * practicado por User')
	 * @param user User asociado a los Language indicados
	 * @param originalPracticedLanguagesIDs lista de lenguajes practicados por
	 * el usuario, antes de ser editada en los formularios de la pagina
	 * @param editedPracticedLanguagesIDs lista de lenguajes practicados por
	 * el usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityNotFoundException
	 */
	/*
	void deleteUserPracticedLanguage(User user,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException,EntityNotFoundException;
	 */
	/**
	 * Elimina todas las relaciones UserNativeLanguage del usuario dado
	 * (es decir, elimina el conjunto de lenguajes nativos de un usuario)
	 * @param user User del que se eliminan los Languages asociados
	 * @throws EntityNotFoundException
	 */
	void deleteNativeLanguagesByUser(User user) throws EntityNotFoundException;

	/**
	 * Elimina todas las relaciones UserPracticingLanguage del usuario dado
	 * (es decir, elimina el conjunto de lenguajes practicados por un usuario)
	 * @param user User del que se eliminan los Languages asociados
	 * @throws EntityNotFoundException
	 */
	void deletePracticedLanguagesByUser(User user) throws EntityNotFoundException;
}