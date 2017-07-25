package com.loqua.business.services.impl.transactionScript;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceContact;
import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.ServiceMessage;
import com.loqua.business.services.ServicePublication;
import com.loqua.business.services.impl.ManagementEmail;
import com.loqua.business.services.impl.MapEntityCounterByDate;
import com.loqua.business.services.impl.memory.Memory;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.model.User;
import com.loqua.model.UserInfo;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.UserJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionUser {

	private static final UserJPA userJPA = new UserJPA();
	private static final TransactionPublication transactionPub = 
			new TransactionPublication();
	
	public User getUserById(Long userID)throws EntityNotFoundException {
		User result = new User();
		try{
			result = userJPA.getUserById(userID);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public User getUserNotRemovedByEmail(String email)
			throws EntityNotFoundException  {
		User result = new User();
		try {
			result = userJPA.getUserNotRemovedByEmail(email);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public User getUserNotRemovedByNick(String nick) throws EntityNotFoundException {
		User result = new User();
		try{
			result = userJPA.getUserNotRemovedByNick(nick);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public User getUserToLogin(String email, String password) 
			throws EntityNotFoundException {
		User result = new User();
		try{
			result = userJPA.getUserToLogin(email, password);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public Integer getNumLoginFails(String email) {
		Integer result;
		try {
			result = userJPA.getNumLoginFails(email);
		} catch (EntityNotPersistedException ex) {
			result = null;
		}
		return result;
	}
	
	public void incrementLoginFails(String email) 
			throws EntityNotFoundException {
		User userToUpdate = new User();
		userToUpdate = getUserNotRemovedByEmail(email);
		userToUpdate.setLoginFails( userToUpdate.getLoginFails()+1 );
		updateAllDataByUser(userToUpdate, false);
	}
	
	public void resetLoginFails(User userToUpdate) 
			throws EntityNotFoundException {
		userToUpdate.setLoginFails( 0 );
		updateAllDataByUser(userToUpdate, false);
	}
	
	public void incrementCommentVotes(User userToUpdate) 
			throws EntityNotFoundException {
		UserInfo userInfoToUpdate = userToUpdate.getUserInfo();
		userInfoToUpdate.incrementVotesComments();
		userToUpdate.setUserInfo(userInfoToUpdate);
		updateAllDataByUser(userToUpdate, true);
	}
	
	public void incrementCountComments(User userToUpdate) 
			throws EntityNotFoundException {
		UserInfo userInfoToUpdate = userToUpdate.getUserInfo();
		userInfoToUpdate.incrementPointsBySentComment();
		userToUpdate.setUserInfo(userInfoToUpdate);
		updateAllDataByUser(userToUpdate, true);
	}
	
	public void create(User userToCreate) throws EntityAlreadyFoundException {
		try {
			userJPA.create(userToCreate);
			Memory.getMemoryListsUsers().changed();
			Memory.updateMemoryListsUsers(true);
			Memory.getMemoryListsUsers().updateNumLastRegistrationsFromDB();
			Memory.getMemoryListsUsers().updateNumRegisteredUsersFromDB();
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	public void updateAllDataByUser(User userToUpdate, boolean justNow)
			throws EntityNotFoundException {
		try {
			userJPA.updateAllDataByUser(userToUpdate);
			Memory.getMemoryListsUsers().changed();
			Memory.updateMemoryListsUsers(justNow);
			Memory.getMemoryListsUsers().updateNumRegisteredUsersFromDB();
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void updateDataByUser(User userToUpdate)
			throws EntityNotFoundException {
		try {
			userJPA.updateDataByUser(userToUpdate);
			Memory.getMemoryListsUsers().changed();
			Memory.updateMemoryListsUsers(true);
			Memory.getMemoryListsUsers().updateNumRegisteredUsersFromDB();
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	public String getLocaleByUser(User loggedUser) 
			throws EntityNotFoundException {
		String result = null;
		try {
			result = userJPA.getLocaleByUser(loggedUser);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}

	public List<User> getMostValuedUsersOfTheMonthFromDB() {
		return userJPA.getMostValuedUsersOfTheMonth();
	}

	public List<User> getMostValuedUsersOfTheMonthFromMemory() {
		// A diferencia de this.getMostValuedUsersOfTheMonth(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<User> result = Memory.getMemoryListsUsers()
				.getMostValuedUsersOfTheMonth();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsUsers().changed();
			Memory.updateMemoryListsUsers(true);
			result = Memory.getMemoryListsUsers()
					.getMostValuedUsersOfTheMonth();
		}
		return result;
	}
	
	public List<User> getMostActiveUsersOfTheMonthFromDB() {
		return userJPA.getMostActiveUsersOfTheMonth();
	}
	
	public List<User> getMostActiveUsersOfTheMonthFromMemory() {
		// A diferencia de this.getMemoryMostActiveUsersOfTheMonth(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<User> result = Memory.getMemoryListsUsers()
				.getMostActiveUsersOfTheMonth();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsUsers().changed();
			Memory.updateMemoryListsUsers(true);
			result = Memory.getMemoryListsUsers()
					.getMostActiveUsersOfTheMonth();
		}
		return result;
	}
	
	public int getSingleClasificationByUser(User user){
		int result;
		try{
			result = userJPA.getSingleClasificationByUser(user);
		} catch (EntityNotPersistedException ex) {
			result = 0;
		}
		return result;
	}
	
	public Map<Integer, User> getSmallClasificationByUser(User user){
		Map<Integer, User> result = null;
		int lastPosition = getNumRegisteredUsersAndAdminFromMemory();
		try{
			result = userJPA.getSmallClasificationByUser(user, lastPosition);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = new HashMap<Integer, User>();
		}
		return result;
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO SE REGISTRE EN LOQUA
	// // // // // // // // // // // // // // // // // //
	
	public String sendEmailForRegister(User user,
			List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits){
		String result = validateNumLastRegistrations(mapActionsLimits);
		if( ! result.equals("noError") ){
			return result;
		}
		String content = contentSchema.get(0) + user.getUrlConfirm();
		ManagementEmail.sendEmail(user, content, subject);
		return result;
	}
	
	private String validateNumLastRegistrations(
			Map<String, Integer> mapActionsLimits){
		String result = "noError";
		MapEntityCounterByDate numOccurrences =
				getNumLastRegistrationsFromMemory();
		if( numOccurrences.getOccurrencesLastMinute() >= 
				mapActionsLimits.get("limitRegistrationsAtLastMinute") 
			|| numOccurrences.getOccurrencesLastFiveMinutes() >=
				mapActionsLimits.get("limitRegistrationsAtLastFiveMinutes") 
			|| numOccurrences.getOccurrencesLastQuarter() >=
				mapActionsLimits.get("limitRegistrationsAtLastQuarter") 
			|| numOccurrences.getOccurrencesLastHour() >=
				mapActionsLimits.get("limitRegistrationsAtLastHour") 
			|| numOccurrences.getOccurrencesLastDay() >=
				mapActionsLimits.get("limitRegistrationsAtLastDay") 
			|| numOccurrences.getOccurrencesLastWeek() >=
				mapActionsLimits.get("limitRegistrationsAtLastWeek") 
			|| numOccurrences.getOccurrencesLastMonth() >=
				mapActionsLimits.get("limitRegistrationsAtLastMonth") ){
			return "limitTooRegistrations";
		}
		return result;
	}
	
	public MapEntityCounterByDate getNumLastRegistrationsFromDB()
			/*throws EntityNotFoundException*/ {
		MapEntityCounterByDate result = new MapEntityCounterByDate();
		try {
			result = userJPA.getNumLastRegistrations();
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = new MapEntityCounterByDate();
		}
		return result;
	}
	
	private MapEntityCounterByDate getNumLastRegistrationsFromMemory() {
		// A diferencia de this.getNumLastRegistrationsFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve el Map guardado en Memory
		MapEntityCounterByDate result = Memory.getMemoryListsUsers()
				.getNumLastRegistrations();
		if( result.isEmpty() ){
			// Pero si esta vacio, entonces si lo cargamos desde base de datos
			result = getNumLastRegistrationsFromDB();
			Memory.getMemoryListsUsers().setNumLastRegistrations( result );
		}
		return result;
	}

	public User getUserByUrlConfirm(String urlConfirm) 
			/*throws EntityNotFoundException*/ {
		User result = new User();
		try {
			result = userJPA.getUserByUrlConfirm(urlConfirm);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = null;
		}
		return result;
	}
	
	public int getNumRegisteredUsersAndAdminFromDB() {
		int result;
		try {
			result = userJPA.getNumRegisteredUsersAndAdmin();
		} catch (EntityNotPersistedException ex) {
			result = 0;
		}
		return result;
	}
	public int getNumRegisteredUsersAndAdminFromMemory() {
		// A diferencia de this.getNumRegisteredUsersFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve el valor guardado en Memory
		int result = Memory.getMemoryListsUsers().getNumRegisteredUsers();
		if( result==-1 ){
			// Pero si esta vacio, entonces si lo cargamos desde base de datos
			result = getNumRegisteredUsersAndAdminFromDB();
			Memory.getMemoryListsUsers().setNumRegisteredUsers( result );
		}
		return result;
	}
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO ELIMINE SU CUENTA DE USUARIO
	// // // // // // // // // // // // // // // // // // // //	
	
	// http://www.codecodex.com/wiki/Generate_a_random_password_or_random_string
	// 128 bits son considerados suficientes para una codificacion segura,
	// pero en base32 cada digito codifica 5 bits
	// asi que utilizamos el siguiente multiplo de 5: 130
	// Por tanto: con 26 caracteres (son 130/5) tenemos una cadena segura
	public void sendEmailForRemoveUser(User user,
			List<String> contentSchema, String subject)
					throws EntityNotFoundException {
		String urlConfirm=new BigInteger(130, new SecureRandom()).toString(32);
		String content = contentSchema.get(0) + urlConfirm;
		ManagementEmail.sendEmail(user, content, subject);
		updateUserToRemove(user, urlConfirm);
	}
	
	private void updateUserToRemove(User user, String urlConfirm)
			throws EntityNotFoundException {
		user.setUrlConfirm(urlConfirm);
		updateAllDataByUser(user, true);
	}
	
	public void deleteUserAccount(User user) throws EntityNotFoundException {
		try {
			userJPA.deleteUserAccount(user);
			user.getUserInfo().unlink();
			user.getUserInfoPrivacity().unlink();
			user.getPrivacityData().unlink();
			deleteAllContacts(user);
			deleteAllContactRequests(user);
			deleteAllUserLanguages(user);
			// no se eliminan las participaciones del usuario en el foro
			// ni los 'votos' que haya dado a las participaciones de otros.
			// Tampoco se elimina como destinatario de sus mensajes recibidos,
			// aunque si se elimina como destinatario de las publicaciones recibidas,
			// y tammbien eliminan los mensajes que el envio
			// y las publicaciones/notificaciones creadas/logradas por el
			deleteSentMessagesByUser(user);
			deletePublicationsByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	private void deleteAllContacts(User user) throws EntityNotFoundException {
		ServiceContact serviceContact =
				new LocatorLocalEjbServices().getServiceContact();
		serviceContact.deleteAllReciprocalContactsByUser(user);
	}
	
	private void deleteAllContactRequests(User user)
			throws EntityNotFoundException {
		ServiceContact serviceContact =
				new LocatorLocalEjbServices().getServiceContact();
		serviceContact.deleteAllContactRequestsByUser(user);
	}
	
	private void deleteAllUserLanguages(User user) 
			throws EntityNotFoundException {
		ServiceLanguage serviceLanguage =
				new LocatorLocalEjbServices().getServiceLanguage();
		serviceLanguage.deleteNativeLanguagesByUser(user);
		serviceLanguage.deletePracticedLanguagesByUser(user);
	}
	
	private void deleteSentMessagesByUser(User user) 
			throws EntityNotFoundException {
		ServiceMessage serviceMessage =
				new LocatorLocalEjbServices().getServiceMessage();
		serviceMessage.deleteSentMessagesByUser(user);
	}
	
	private void deletePublicationsByUser(User user) 
			throws EntityNotFoundException {
		ServicePublication servicePublication =
				new LocatorLocalEjbServices().getServicePublication();
		servicePublication.deletePublicationsByUser(user);
	}

	public void generatePublicationForTopUsers(User user)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		// Este metodo es utilizado desde otros transaction scripts
		// al igual que "TransactionPublication.generatePublication()"
		TypePrivacity privacity = user.getPrivacityData().getPublications();
		Integer clasificationOfUser = getSingleClasificationByUser(user);
		Integer topReached = reachedTopClasification(clasificationOfUser);
		if( topReached != -1 ){
			// si el autor entra en el top-100/50/25/20/15/10/5/(...)/1:
			// comprueba si el logro ha sido alcanzado otras veces anteriores
			if( transactionPub.achievementTopUsersAlreadyPassedByUser(
					topReached.longValue(), 4L, user.getId()) ){
				// en cuyo caso no genera la publicacion:
				return;
			}// else
			// si el logro aun no habia sido alcanzado, genera la publicacion:
			transactionPub.generatePublication(
					privacity, topReached.longValue(), 4L, user);
		}
	}
	
	private Integer reachedTopClasification(int clasification) {
		// existen los top-100/50/25/20/15/10/5/(...)/1
		// si el valor de clasification es menor o igual que uno de ellos,
		// se devuelve dicho top
		if( clasification>100 ){ return -1; }
		for(int topUsers=1; topUsers<=5; topUsers++)
			if( clasification==topUsers ){ return topUsers; }
		for(int topUsers=10; topUsers<=25; topUsers+=5)
			if( clasification<=topUsers ){ return topUsers; }
		if( clasification<=50 ){ return 50; }
		else if( clasification<=100 ){ return 100; }
		return -1;
	}
}
