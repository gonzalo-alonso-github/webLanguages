package com.loqua.business.services.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.impl.transactionScript.TransactionLanguage;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceLanguage;
import com.loqua.business.services.serviceRemote.RemoteServiceLanguage;
import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.model.UserNativeLanguage;
import com.loqua.model.UserPracticingLanguage;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link Language}, {@link UserNativeLanguage}
 * y {@link UserPracticingLanguage}.<br/>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br/>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceLanguage} y {@link RemoteServiceLanguage},
 * que heredan de {@link ServiceLanguage}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceLanguage")
public class EjbServiceLanguage 
		implements LocalServiceLanguage, RemoteServiceLanguage {
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Language}, {@link UserNativeLanguage}
	 * y {@link UserPracticingLanguage},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
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
	public Map<Long, Language> getMapLanguagesByIdsFromDB(
			List<Long> languagesIDs){
		return transactionLanguage.getMapLanguagesByIdsFromDB(languagesIDs);
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
			throws EntityAlreadyFoundException, EntityNotFoundException {
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
			throws EntityAlreadyFoundException, EntityNotFoundException {
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
