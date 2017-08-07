package com.loqua.business.services;

import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.MapEntityCounterByDate;
import com.loqua.model.User;

public interface ServiceUser {

	/* A priori no se va a utilizar ninguno de los metodos
	que contienen el sufijo 'FromCache'. Si se utilizan, entonces conviene
	descomentar el uso de la Cache aquellos metodos de la clase
	TansactionUser que realicen creaciones y actualizaciones:
	TansactionUser.create
	TansactionUser.updateAllDataByUser
	TansactionUser.updateDataByUser
	TansactionUser.validateNumLastRegistrations */
	
	User getUserById(Long userID) throws EntityNotFoundException;
	
	User getUserNotRemovedByEmail(String email);
	
	User getUserNotRemovedByNick(String nick);
	
	public User getUserToLogin(String email, String password);	
	
	Integer getNumLoginFails(String email);
	
	void incrementLoginFails(String email) throws EntityNotFoundException;
	
	void resetLoginFails(User userToUpdate) throws EntityNotFoundException;

	void createUser(User userToCreate) throws EntityAlreadyFoundException;
	
	void updateAllDataByUser(User userToUpdate, boolean justNow)
			throws EntityNotFoundException;
	
	void updateDataByUser(User userToUpdate) throws EntityNotFoundException;

	String getLocaleByUser(User loggedUser) throws EntityNotFoundException;

	User getUserByUrlConfirm(String urlConfirm);

	List<User> getMostValuedUsersOfTheMonthFromDB();
	List<User> getMostValuedUsersOfTheMonthFromCache();

	List<User> getMostActiveUsersOfTheMonthFromDB();
	List<User> getMostActiveUsersOfTheMonthFromCache();
	
	Map<Integer, User> getSmallClasificationByUser(User user);
	
	int getNumRegisteredUsersAndAdminFromDB();
	int getNumRegisteredUsersAndAdminFromCache();
	
	String sendEmailForRegister(User user, List<String> content,
			String subject, Map<String, Integer> mapActionsLimits);

	MapEntityCounterByDate getNumLastRegistrationsFromDB();

	void sendEmailForRemoveUser(User user, List<String> content,
			String subject) throws EntityNotFoundException;
	
	void deleteUserAccount(User user) throws EntityNotFoundException;
}