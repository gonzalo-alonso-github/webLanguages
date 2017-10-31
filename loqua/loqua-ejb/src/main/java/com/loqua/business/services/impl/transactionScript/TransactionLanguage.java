package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.model.UserNativeLanguage;
import com.loqua.model.UserPracticingLanguage;
import com.loqua.persistence.LanguageJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link Language}, {@link UserNativeLanguage}
 * y {@link UserPracticingLanguage}.<br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionLanguage {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link Language}, {@link UserNativeLanguage}
	 * y {@link UserPracticingLanguage} */
	private static final LanguageJPA languageJPA = new LanguageJPA();
	
	/**
	 * Consulta lenguajes segun su atributo 'name'
	 * @param name atributo homonimo del Language que se consulta
	 * @return objeto Language cuyo atributo 'name' coincide
	 * con el parametro dado
	 */
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
	
	/**
	 * Consulta todos los lenguajes disponibles
	 * @return lista de todos los Language que maneja la aplicacion
	 */
	public List<Language> getAllLanguagesFromDB() {
		return languageJPA.getAllLanguages();
	}
	
	/**
	 * Halla los Language cuyo atributo 'id' coincide con alguno de los valores
	 * de la lista indicada
	 * @param languagesIDs  lista de atributos 'id' de los Language
	 * que se consultan
	 * @return Map&lt;Long, Language&gt;, donde la clave es el atributo 'id'
	 * del Language, y donde el valor es el propio Language
	 */
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
	
	/**
	 * Halla los lenguajes maternos de un usuario dado
	 * @param userID atributo 'id' del User que se consulta
	 * @return lista de Language, asociados a los UserNativeLanguage
	 * pertenecientes al User que se recibe por paramero
	 */
	public List<Language> getNativeLanguagesByUser(Long userID){
		return languageJPA.getNativeLanguagesByUser(userID);
	}
	
	/**
	 * Halla los lenguajes practicados por un usuario dado
	 * @param userID atributo 'id' del User que se consulta
	 * @return lista de Language, asociados a los UserPracticingLanguage
	 * pertenecientes al User que se recibe por paramero
	 */
	public List<Language> getPracticingLanguagesByUser(Long userID){
		return languageJPA.getPracticingLanguagesByUser(userID);
	}
	
	/*
	 * Halla los lenguajes asociados a un pais dado
	 * @param country atributo 'name' del Country al que pertenecen los
	 * Language que se consultan
	 * @return lista de Language pertenecientes al Country recibido por
	 * parametro
	public List<Language> getLanguagesByCountry(String country){
		return languageJPA.getLanguagesByCountry(country);
	}
	*/
	
	/**
	 * Actualiza algun dato del lenguaje dado 
	 * @param language objeto Language que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updateLanguage(Language language) throws EntityNotFoundException{
		try {
			languageJPA.updateLanguage(language);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Actualiza las asociaciones UserNativeLanguage entre el User dado y
	 * los Language indicados (es decir, la relacion de 'Laguage es el idioma
	 * materno de User').
	 * @param user User asociado a los Language indicados
	 * @param originalNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, antes de ser editada en los formularios de la pagina
	 * @param editedNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	public void updateNativeLanguages( User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		createUserNativeLanguage(user,
				originalNativeLanguagesIDs, editedNativeLanguagesIDs);
		deleteUserNativeLanguage(user,
				originalNativeLanguagesIDs, editedNativeLanguagesIDs);
	}
	
	/**
	 * Crea las asociaciones UserNativeLanguage entre el User dado y
	 * los Language indicados (es decir, la relacion de 'Laguage es el idioma
	 * materno de User').
	 * @param user User asociado a los Language indicados
	 * @param originalNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, antes de ser editada en los formularios de la pagina
	 * @param editedNativeLanguagesIDs lista de lenguajes maternos del
	 * usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	private void createUserNativeLanguage( User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		/* La vista presenta los lenguajes maternos
		como una lista desplegable de componentes check.
		(Un componente check puede estar en 'estado' marcado o desmarcado).
		- editedNativeLanguagesIDs = lista elementos cuyo 'estado' ha sido
		editado por el usuario.
		- originalNativeLanguagesIDs = lista elementos que originalmente estaban
		en 'estado' marcado, antes de editar la lista desplegable.
		Es decir, "editedNativeLanguages.removeAll(originalNativeLanguages)"
		devolvera la lista de lenguajes que el usuario ha pasado de
		'estado' desmarcado a marcado.
		Es decir, son los lenguages que el usuario selecciona como maternos. */
		try {
			List<Long> languagesIDsToUpdate = 
					new ArrayList<Long>(editedNativeLanguagesIDs);
			languagesIDsToUpdate.removeAll(originalNativeLanguagesIDs);
			
			// Agrega los lenguajes que el usuario selecciona como maternos:
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
	private void deleteUserNativeLanguage(User user,
			List<Long> originalNativeLanguagesIDs,
			List<Long> editedNativeLanguagesIDs)
			throws EntityNotFoundException {
		/* La vista presenta los lenguajes maternos
		como una lista desplegable de componentes check.
		(Un componente check puede estar en 'estado' marcado o desmarcado).
		- editedNativeLanguagesIDs = lista elementos cuyo 'estado' ha sido
		editado por el usuario.
		- originalNativeLanguagesIDs = lista elementos que originalmente estaban
		en 'estado' marcado, antes de editar la lista desplegable.
		Es decir, "originalNativeLanguages.removeAll(editedNativeLanguages)"
		devolvera la lista de lenguajes que el usuario ha pasado de estado
		marcado a desmarcado.
		Es decir, son los lenguages que el usuario elimina de su lista. */
		try {
			ArrayList<Long> languagesIDsToDelete = 
					new ArrayList<Long>(originalNativeLanguagesIDs);
			languagesIDsToDelete.removeAll(editedNativeLanguagesIDs);
			
			// Elimina los lenguajes que el usuario elimina de su lista:
			for( Long languageId : languagesIDsToDelete ){
				//Language lang = getLanguageByIdFromCache(languageId);
				Language lang = languageJPA.getLanguageById(languageId);
				languageJPA.deleteUserNativeLanguage(user, lang);
			}
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Actualiza las asociaciones UserPracticedLanguage entre el User dado y
	 * los Language indicados (es decir, la relacion de 'Laguage es el idioma
	 * practicado por User').
	 * @param user User asociado a los Language indicados
	 * @param originalPracticedLanguagesIDs lista de lenguajes practicados del
	 * usuario, antes de ser editada en los formularios de la pagina
	 * @param editedPracticedLanguagesIDs lista de lenguajes practicados del
	 * usuario, despues de ser editada en los formularios de la pagina
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	public void updatePracticedLanguages( User user,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		createUserPracticedLanguage(user,
				originalPracticedLanguagesIDs, editedPracticedLanguagesIDs);
		deleteUserPracticedLanguage(user,
				originalPracticedLanguagesIDs, editedPracticedLanguagesIDs);
	}
	
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
	private void createUserPracticedLanguage(User user,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		/* La vista presenta los lenguajes practicados
		como una lista desplegable de componentes check.
		(Un componente check puede estar en 'estado' marcado o desmarcado).
		- editedPracticedLanguagesIDs = lista elementos cuyo 'estado' ha sido
		editado por el usuario.
		- originalPracticedLanguagesIDs = lista elementos que originalmente
		estaban en 'estado' marcado, antes de editar la lista desplegable.
		Es decir, 
		"editedPracticedLanguages.removeAll(originalPracticedLanguages)"
		devolvera la lista de lenguajes que el usuario ha pasado de estado
		desmarcado a marcado.
		Es decir, son los lenguages que el usuario elige como practicados. */
		try {
			List<Long> languagesIDsToUpdate = 
					new ArrayList<Long>(editedPracticedLanguagesIDs);
			languagesIDsToUpdate.removeAll(originalPracticedLanguagesIDs);
			
			// Agrega los lenguajes que el usuario elige como practicados:
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
	private void deleteUserPracticedLanguage(User user,
			List<Long> originalPracticedLanguagesIDs,
			List<Long> editedPracticedLanguagesIDs) 
			throws EntityNotFoundException {
		/* La vista presenta los lenguajes practicados
		como una lista desplegable de componentes check.
		(Un componente check puede estar en 'estado' marcado o desmarcado).
		- editedPracticedLanguagesIDs = lista elementos cuyo 'estado' ha sido
		editado por el usuario.
		- originalPracticedLanguagesIDs = lista elementos que originalmente
		estaban en 'estado' marcado, antes de editar la lista desplegable.
		Es decir, 
		"originalPracticedLanguages.removeAll(editedPracticedLanguages)"
		devolvera la lista de lenguajes que el usuario ha pasado de estado
		marcado a desmarcado.
		Es decir, son los lenguages que el usuario elimina de su lista. */
		try {
			ArrayList<Long> languagesIDsToDelete = 
					new ArrayList<Long>(originalPracticedLanguagesIDs);
			languagesIDsToDelete.removeAll(editedPracticedLanguagesIDs);
			
			// Elimina los lenguajes que el usuario elimina de su lista:
			for( Long languageId : languagesIDsToDelete ){
				// Language lang = getLanguageByIdFromCache(languageId);
				Language lang = languageJPA.getLanguageById(languageId);
				languageJPA.deleteUserPracticedLanguage(user, lang);
			}
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Elimina todas las relaciones UserNativeLanguage del usuario dado
	 * (es decir, elimina el conjunto de lenguajes nativos de un usuario)
	 * @param user User del que se eliminan los Languages asociados
	 * @throws EntityNotFoundException
	 */
	public void deleteNativeLanguagesByUser(User user)
			throws EntityNotFoundException {
		try {
			languageJPA.deleteNativeLanguagesByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	/**
	 * Elimina todas las relaciones UserPracticingLanguage del usuario dado
	 * (es decir, elimina el conjunto de lenguajes practicados por un usuario)
	 * @param user User del que se eliminan los Languages asociados
	 * @throws EntityNotFoundException
	 */
	public void deletePracticedLanguagesByUser(User user)
			throws EntityNotFoundException {
		try {
			languageJPA.deletePracticedLanguagesByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
}
