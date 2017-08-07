package com.loqua.business.services.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionUser;
import com.loqua.business.services.serviceLocal.LocalServiceUser;
import com.loqua.business.services.serviceRemote.RemoteServiceUser;
import com.loqua.model.User;

@Stateless
@WebService(name="ServiceUser")
public class EjbServiceUser
		implements LocalServiceUser, RemoteServiceUser {

	private static final TransactionUser transactionUser = 
			new TransactionUser();
	
	@Override
	public User getUserById(Long userID) throws EntityNotFoundException {
		return transactionUser.getUserById(userID);
	}
	
	@Override
	public User getUserNotRemovedByEmail(String email){
		User result = new User();
		try {
			result = transactionUser.getUserNotRemovedByEmail(email);
		} catch (EntityNotFoundException e) {
			result = null;
		}
		return result;
	}
	
	@Override
	public User getUserNotRemovedByNick(String nick){
		User result = new User();
		try {
			result = transactionUser.getUserNotRemovedByNick(nick);
		} catch (EntityNotFoundException e) {
			result = null;
		}
		return result;
	}
	
	@Override
	public User getUserToLogin(String email, String password) {
		User result = new User();
		try {
			result = transactionUser.getUserToLogin(email, password);
		} catch (EntityNotFoundException e) {
			result = null;
		}
		return result;
	}
	
	@Override
	public Integer getNumLoginFails(String email) {
		return transactionUser.getNumLoginFails(email);
	}
	
	@Override
	public void incrementLoginFails(String email) 
			throws EntityNotFoundException {
		transactionUser.incrementLoginFails(email);
	}
	
	@Override
	public void resetLoginFails(User userToUpdate) 
			throws EntityNotFoundException {
		transactionUser.resetLoginFails(userToUpdate);
	}
	
	@Override
	public void createUser(User user) throws EntityAlreadyFoundException {
		transactionUser.create(user);
	}
	
	@Override
	public void updateAllDataByUser(User userToUpdate, boolean justNow) 
			throws EntityNotFoundException {
		transactionUser.updateAllDataByUser(userToUpdate, justNow);
	}
	
	@Override
	public void updateDataByUser(User userToUpdate) 
			throws EntityNotFoundException {
		transactionUser.updateDataByUser(userToUpdate);
	}
	
	@Override
	public String getLocaleByUser(User loggedUser) 
			throws EntityNotFoundException {
		return transactionUser.getLocaleByUser(loggedUser);
	}
	
	@Override
	public User getUserByUrlConfirm(String urlConfirm){
		return transactionUser.getUserByUrlConfirm(urlConfirm);
	}
	
	@Override
	public List<User> getMostValuedUsersOfTheMonthFromDB() {
		return transactionUser.getMostValuedUsersOfTheMonthFromDB();
	}
	@Override
	public List<User> getMostValuedUsersOfTheMonthFromCache() {
		return transactionUser.getMostValuedUsersOfTheMonthFromCache();
	}
	
	@Override
	public List<User> getMostActiveUsersOfTheMonthFromDB() {
		return transactionUser.getMostActiveUsersOfTheMonthFromDB();
	}
	@Override
	public List<User> getMostActiveUsersOfTheMonthFromCache() {
		return transactionUser.getMostActiveUsersOfTheMonthFromCache();
	}
	
	@Override
	public Map<Integer, User> getSmallClasificationByUser(User user) {
		return transactionUser.getSmallClasificationByUser(user);
	}
	
	@Override
	public int getNumRegisteredUsersAndAdminFromDB() {
		return transactionUser.getNumRegisteredUsersAndAdminFromDB();
	}
	@Override
	public int getNumRegisteredUsersAndAdminFromCache() {
		return transactionUser.getNumRegisteredUsersAndAdminFromCache();
	}
	
	// // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO SE REGISTRE
	// // // // // // // // // // // // // // //
	
	@Override
	public String sendEmailForRegister(User user,
			List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits){
		return transactionUser.sendEmailForRegister(
				user,contentSchema,subject,mapActionsLimits);
	}
	
	@Override
	public MapEntityCounterByDate getNumLastRegistrationsFromDB(){
		return transactionUser.getNumLastRegistrationsFromDB();
	}
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO ELIMINE SU CUENTA DE USUARIO
	// // // // // // // // // // // // // // // // // // // //	
	
	@Override
	public void sendEmailForRemoveUser(User user,
			List<String> contentSchema, String subject)
					throws EntityNotFoundException {
		transactionUser.sendEmailForRemoveUser(user, contentSchema, subject);
	}

	@Override
	public void deleteUserAccount(User user) throws EntityNotFoundException {
		transactionUser.deleteUserAccount(user);
	}
}
