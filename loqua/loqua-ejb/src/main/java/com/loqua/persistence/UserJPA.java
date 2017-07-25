package com.loqua.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import org.mindrot.jbcrypt.BCrypt;

import com.loqua.business.services.impl.MapEntityCounterByDate;
import com.loqua.model.User;
import com.loqua.model.types.TypeUserRole;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;
import com.loqua.persistence.util.NotJPA;

public class UserJPA {
	
	private static final String USER_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'User' entity not found"
			+ " at Persistence layer";
	private static final String ENTITY_NOT_PERSISTED_EXCEPTION =
			"EntityNotPersistedException: Entity not found"
			+ " at Persistence layer";
	private static final String USER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'User' entity already"
			+ " found at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	private static final String MySQL_USER_INDIVIDUAL_CLASIFICATION = 
			NotJPA.getInstance().getString("MySQL_USER_INDIVIDUAL_CLASIFICATION");
	private static final String MySQL_USER_SMALL_CLASIFICATION = 
			NotJPA.getInstance().getString("MySQL_USER_SMALL_CLASIFICATION");
	
	public User getUserById(Long userId) throws EntityNotPersistedException {
		User result = new User();
		try{
			// result = (User) Jpa.getManager().find(User.class, userId);
			result = (User) JPA.getManager()
					.createNamedQuery("User.getUserById")
					.setParameter(1, userId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public User getUserNotRemovedByEmail(String email)
			throws EntityNotPersistedException {
		User result = new User();
		try{
			result = (User) JPA.getManager()
				.createNamedQuery("User.getUserNotRemovedByEmail")
				.setParameter(1, email)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public User getUserNotRemovedByNick(String nick)
			throws EntityNotPersistedException {
		User result = new User();
		try{
			result = (User) JPA.getManager()
				.createNamedQuery("User.getUserNotRemovedByNick")
				.setParameter(1, nick)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public User getUserToLogin(String email, String password) 
			throws EntityNotPersistedException {
		User result = new User();
		try{
			result = (User) JPA.getManager()
				.createNamedQuery("User.getUserToLoginByEmail")
				.setParameter(1, email)
				.setParameter(2, TypeUserRole.ADMINISTRATOR)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		boolean matched = BCrypt.checkpw(password, result.getPassword());
		if( matched==false ){
			throw new EntityNotPersistedException(USER_NOT_PERSISTED_EXCEPTION);
		}
		return result;
	}
	
	public Integer getNumLoginFails(String email)
			throws EntityNotPersistedException {
		Integer result = 0;
		try{
			result = (Integer) JPA.getManager()
					.createNamedQuery("User.getNumLoginFails")
					.setParameter(1, email)
					.setParameter(2, TypeUserRole.ADMINISTRATOR)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public int getNumRegisteredUsersAndAdmin()
			throws EntityNotPersistedException {
		Long result = 0l;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery("User.getNumRegisteredUsersAndAdmin")
					.setParameter(1, TypeUserRole.ADMINISTRATOR)
					.getSingleResult();
			// NOTA: En esta consulta descartamos los usuarios 'removed'.
			// Si se le hiciera algun cambio, convendria aplicarlo tambien
			// a las consultas del metodo 'getSmallClasificationByUser'
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	public void create(User userToCreate)
			throws EntityAlreadyPersistedException {
		try{
			verifyUserRemovedNotAlreadyExists(userToCreate);
			JPA.getManager().persist( userToCreate );
			JPA.getManager().persist( userToCreate.getPrivacityData() );
			JPA.getManager().persist( userToCreate.getUserInfo() );
			JPA.getManager().persist( userToCreate.getUserInfoPrivacity() );
			JPA.getManager().flush();
			JPA.getManager().refresh(userToCreate);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					USER_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void updateAllDataByUser(User userToUpdate)
			throws EntityNotPersistedException {
		try{
			JPA.getManager().merge( userToUpdate.getPrivacityData() );
			JPA.getManager().merge( userToUpdate.getUserInfo() );
			JPA.getManager().merge( userToUpdate.getUserInfoPrivacity() );
			JPA.getManager().merge( userToUpdate );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}//catch( Exception ex ){}
	}
	
	public void updateDataByUser(User userToUpdate)
			throws EntityNotPersistedException {
		try{
			JPA.getManager().merge( userToUpdate );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}//catch( Exception ex ){}
	}

	// Comprueba que no existe otro usuario en estado removed=false
	// con el mismo email. Si es asi, lanza EntityAlreadyPersistedException.
	private void verifyUserRemovedNotAlreadyExists(User userToCreate) 
			throws EntityAlreadyPersistedException {
		User repeated = null;
		try{
			repeated = (User) JPA.getManager()
				.createNamedQuery("User.getUserNotRemovedByEmail")
				.setParameter(1, userToCreate.getEmail())
				.getSingleResult();
		}catch( NoResultException ex ){
			repeated = null;
		}
		if( repeated!=null ){
			throw new EntityAlreadyPersistedException();
		}
	}

	public String getLocaleByUser(User loggedUser) 
			throws EntityNotPersistedException {
		String result = null;
		try{
			result = (String) JPA.getManager()
				.createNamedQuery("User.getLocaleByUser")
				.setParameter(1, loggedUser.getId())
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public MapEntityCounterByDate getNumLastRegistrations() 
			throws EntityNotPersistedException {
		MapEntityCounterByDate result = new MapEntityCounterByDate();
		Date currentDate = new Date();
		long currentDateLong = currentDate.getTime();
		long lastMinute = currentDateLong-60000;
		long lastFiveMinutes = currentDateLong-300000;
		long lastQuarter = currentDateLong-900000;
		long lastHour = currentDateLong-3600000;
		long lastDay = currentDateLong-86400000;
		long lastWeek = currentDateLong-604800000;
		long lastMonth = currentDateLong-2592000000l;
		currentDate.setTime(lastMinute);
		result.setOccurrencesLastMinute(
				queryNumLastRegistrations(currentDate) );
		currentDate.setTime(lastFiveMinutes);
		result.setOccurrencesLastFiveMinutes(
				queryNumLastRegistrations(currentDate) );
		currentDate.setTime(lastQuarter);
		result.setOccurrencesLastQuarter(
				queryNumLastRegistrations(currentDate) );
		currentDate.setTime(lastHour);
		result.setOccurrencesLastHour(
				queryNumLastRegistrations(currentDate) );
		currentDate.setTime(lastDay);
		result.setOccurrencesLastDay(
				queryNumLastRegistrations(currentDate) );
		currentDate.setTime(lastWeek);
		result.setOccurrencesLastWeek(
				queryNumLastRegistrations(currentDate) );
		currentDate.setTime(lastMonth);
		result.setOccurrencesLastMonth(
				queryNumLastRegistrations(currentDate) );
		return result;
	}

	private int queryNumLastRegistrations(Date currentDate) 
			throws EntityNotPersistedException{
		Long result = 0l;
		try{
		result = (Long) JPA.getManager()
			.createNamedQuery("User.getNumLastRegistrations")
			.setParameter(1, currentDate)
			.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	public User getUserByUrlConfirm(String urlConfirm) 
			throws EntityNotPersistedException{
		User result = new User();
		try{
			result = (User) JPA.getManager()
				.createNamedQuery("User.getUserByUrlConfirm")
				.setParameter(1, urlConfirm)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<User> getMostValuedUsersOfTheMonth() {
		List<User> result = new ArrayList<User>();
		List<Object[]> query = new ArrayList<Object[]>();
		try{
			query = (List<Object[]>) JPA.getManager()
				.createNamedQuery("User.getMostValuedUsersOfTheMonth")
				.setMaxResults(4) // limit
				.getResultList();
			for( Object[] userInQuery : query ){
				result.add((User)userInQuery[0]);
			}
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getMostActiveUsersOfTheMonth() {
		List<User> result = new ArrayList<User>();
		List<Object[]> query = new ArrayList<Object[]>();
		try{
			query = (List<Object[]>) JPA.getManager()
				.createNamedQuery("User.getMostActiveUsersOfTheMonth")
				.setMaxResults(4) // limit
				.getResultList();
			for( Object[] userInQuery : query ){
				result.add((User)userInQuery[0]);
			}
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public int getSingleClasificationByUser(User user)
			throws EntityNotPersistedException {
		int userPosition = 0;
		try{
			//NOTA: Si en la consulta de "numRegisteredUsers" descartamos los
			//usuarios 'removed', en esta consulta debemos descartarlos tambien
			userPosition = ( (Double)JPA.getManager()
				.createNativeQuery(MySQL_USER_INDIVIDUAL_CLASIFICATION)
				.setParameter(1, user.getId())
				.getSingleResult() ).intValue();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return userPosition;
	}
		
	@SuppressWarnings("unchecked")
	public Map<Integer, User> getSmallClasificationByUser(
			User user, int lastPosition)
			throws EntityNotPersistedException{
		Map<Integer, User> mapUsersClasification = new HashMap<Integer, User>();
		List<Object[]> dataOfusersClasification = new ArrayList<Object[]>();
		int userPosition, higherPosition, lowerPosition = 0;
		try{
			userPosition = getSingleClasificationByUser(user);
			// Siempre se van a devolver 5 elementos:
			if( userPosition<=3 ){
				higherPosition = 1; lowerPosition = 5;
			}else if( userPosition>=lastPosition-2 ){
				higherPosition = lastPosition-5+1; lowerPosition = lastPosition;
			}else{
				higherPosition = userPosition-2; lowerPosition = userPosition+2;
			}
			//NOTA: Si en la consulta de "numRegisteredUsers" descartamos los
			//usuarios 'removed', en esta consulta debemos descartarlos tambien
			dataOfusersClasification = (List<Object[]>) JPA.getManager()
				.createNativeQuery(MySQL_USER_SMALL_CLASIFICATION)
				.setParameter(1, higherPosition)
				.setParameter(2, lowerPosition)
				.getResultList();
			mapUsersClasification = 
				convertToClasificationMap(dataOfusersClasification);
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return mapUsersClasification;
	}

	private Map<Integer, User> convertToClasificationMap(
			List<Object[]> listDataUsers) throws EntityNotPersistedException {
		Map<Integer, User> result = new HashMap<Integer, User>();
		for(Object[] userData : listDataUsers){
			User currentUser=getUserById( ((Integer)userData[0]).longValue() );
			Integer userClasification = ((Double)userData[1]).intValue();
			result.put(userClasification, currentUser);
		}
		return result;
	}
	
	public void deleteUserAccount(User user) throws EntityNotPersistedException{
		try{
			/*
			UserInfo userInfoToRemove = 
					Jpa.getManager().find(UserInfo.class, ui.getId());
			Jpa.getManager().remove(userInfoToRemove);
			*/
			JPA.getManager()
				.createNamedQuery("User.deleteUserInfo")
				.setParameter(1, user.getUserInfo().getId())
				.executeUpdate();
			JPA.getManager()
				.createNamedQuery("User.deleteUserInfoPrivacity")
				.setParameter(1, user.getUserInfoPrivacity().getId())
				.executeUpdate();
			JPA.getManager()
				.createNamedQuery("User.deletePrivacityData")
				.setParameter(1, user.getPrivacityData().getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					ENTITY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
