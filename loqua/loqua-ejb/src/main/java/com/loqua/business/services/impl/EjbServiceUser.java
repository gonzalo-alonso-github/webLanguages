package com.loqua.business.services.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.impl.transactionScript.TransactionUser;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceUser;
import com.loqua.business.services.serviceRemote.RemoteServiceUser;
import com.loqua.model.PrivacityData;
import com.loqua.model.User;
import com.loqua.model.UserInfo;
import com.loqua.model.UserInfoPrivacity;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
 * y {@link PrivacityData}.<br/>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br/>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceUser} y {@link RemoteServiceUser},
 * que heredan de {@link ServiceUser}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceUser")
public class EjbServiceUser
		implements LocalServiceUser, RemoteServiceUser {

	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
	 * y {@link PrivacityData},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
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
		transactionUser.createUser(user);
	}
	
	@Override
	public void updateAllDataByUser(User userToUpdate) 
			throws EntityNotFoundException {
		transactionUser.updateAllDataByUser(userToUpdate);
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
	public List<User> getMostActiveUsersOfTheMonthFromDB() {
		return transactionUser.getMostActiveUsersOfTheMonthFromDB();
	}
	
	@Override
	public Map<Integer, User> getSmallClasificationByUser(User user) {
		return transactionUser.getSmallClasificationByUser(user);
	}
	
	@Override
	public int getNumRegisteredUsersAndAdminFromDB() {
		return transactionUser.getNumRegisteredUsersAndAdminFromDB();
	}
	
	// // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO SE REGISTRE
	// // // // // // // // // // // // // // //
	
	@Override
	public String sendEmailForRegister(User user,
			String content, String subject,
			Map<String, Integer> mapActionsLimits){
		return transactionUser.sendEmailForRegister(
				user,content,subject,mapActionsLimits);
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
			String content, String subject) throws EntityNotFoundException {
		transactionUser.sendEmailForRemoveUser(user, content, subject);
	}

	@Override
	public void deleteUserAccount(User user) throws EntityNotFoundException {
		transactionUser.deleteUserAccount(user);
	}
}
