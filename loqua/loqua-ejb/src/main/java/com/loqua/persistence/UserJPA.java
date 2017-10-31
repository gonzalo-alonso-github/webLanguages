package com.loqua.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import org.mindrot.jbcrypt.BCrypt;

import com.loqua.business.services.impl.utils.security.MapOccurrCounterByDate;
import com.loqua.model.PrivacityData;
import com.loqua.model.User;
import com.loqua.model.UserInfo;
import com.loqua.model.UserInfoPrivacity;
import com.loqua.model.types.TypeUserRole;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;
import com.loqua.persistence.util.NotJPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
 * y {@link PrivacityData}
 * @author Gonzalo
 */
public class UserJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'User'
	 * en la base de datos */
	private static final String USER_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'User' entity not found"
			+ " at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad 'User'
	 * en la base de datos */
	private static final String USER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'User' entity already"
			+ " found at Persistence layer";
	
	/** Consulta MySQL para obtener, de la base de datos, la posicion de un
	 * usuario en la clasificacion de puntos de usuarios */
	private static final String MySQL_USER_INDIVIDUAL_CLASIFICATION = 
			NotJPA.getInstance().getString(
					"MySQL_USER_INDIVIDUAL_CLASIFICATION");
	/** Consulta MySQL para obtener, de la base de datos, la lista
	 * clasificatoria de los 5 usuarios mas proximos al usuario dado,
	 * incluyendo este*/
	private static final String MySQL_USER_SMALL_CLASIFICATION = 
			NotJPA.getInstance().getString(
					"MySQL_USER_SMALL_CLASIFICATION");
	
	/**
	 * Realiza la consulta JPQL 'User.getUserById'
	 * @param userId atributo 'id' del User que se consulta
	 * @return objeto User cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'User.getUserNotRemovedByEmail'
	 * @param email atributo 'email' del User que se desea consultar
	 * @return User cuyo atributo 'email' coincide con el parametro recibido
	 * y cuyo atributo 'removed' es 'false'
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'User.getUserNotRemovedByNick'
	 * @param nick atributo 'nick' del User que se desea consultar
	 * @return User cuyo atributo 'nick' coincide con el parametro recibido
	 * y cuyo atributo 'removed' es 'false'
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'User.getUserToLoginByEmail'
	 * @param email atributo 'email' del User que se desea consultar
	 * @param password atributo 'password' del User que se desea consultar
	 * @return User cuyo atributo 'email' coincide con el parametro homologo,
	 * y cuyo atributo 'password', una vez descifrado, coincide con 
	 * el parametro homologo, y cuyo atributo 'removed' es 'false' o bien
	 * tenga 'role' igual a 'ADMINISTRATOR'
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'User.getNumLoginFails' para hallar la
	 * cantidad de intentos fallidos de inicio de sesion del usuario dado
	 * @param email atributo 'email' del User que se desea consultar
	 * @return atributo 'loginFails' del User cuyo 'email' coincide con el
	 * parametro dado
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'User.getNumRegisteredUsersAndAdmin'
	 * @return cantidad de User cuyo atributo 'removed' es 'false' o cuyo
	 * atributo 'role' es 'ADMINISTRATOR'
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Agrega a la base de datos el User recibido por parametro, ademas de
	 * su UserInfo, UserInfoPrivacity y PrivacityData asociados.
	 * @param userToCreate User que se desea guardar
	 * @return el usuario agregado
	 * @throws EntityAlreadyPersistedException
	 */
	public User create(User userToCreate)
			throws EntityAlreadyPersistedException {
		try{
			verifyUserNotAlreadyExists(userToCreate);
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
		return userToCreate;
	}
	
	/**
	 * Actualiza en la base de datos el User recibido por parametro, ademas
	 * de su UserInfo, UserInfoPrivacity y PrivacityData asociados
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Actualiza en la base de datos el User recibido por parametro
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
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

	/**
	 * Comprueba que no existe otro User en estado removed igual a 'false'
	 * con el mismo email. Si es asi, lanza EntityAlreadyPersistedException.
	 * @param userToCreate User cuyo email se comprueba
	 * @throws EntityAlreadyPersistedException
	 */
	private void verifyUserNotAlreadyExists(User userToCreate) 
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

	/**
	 * Realiza la consulta JPQL 'User.getLocaleByUser'
	 * @param loggedUser User que se desea consultar
	 * @return atributo 'locale' del User que se recibe por parametro
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Genera una instancia de {@link MapOccurrCounterByDate} y carga dicho Map
	 * realizando varias consultas a la base de datos.
	 * La intencion es restringir el numero de registros de usuarios, como
	 * medida de seguridad
	 * @return una instancia de MapOccurrCounterByDate que almacena la 
	 * cantidad de registros de usuario en la aplicacion a lo largo de
	 * distintos lapsos de tiempo (el Map clasifica
	 * los siguientes lapsos: por minuto, por cinco minutos, por cuarto de hora,
	 * por hora, por dia, por semana y por mes)
	 * @throws EntityNotPersistedException
	 */
	public MapOccurrCounterByDate getNumLastRegistrations() 
			throws EntityNotPersistedException {
		MapOccurrCounterByDate result = new MapOccurrCounterByDate();
		Date periodToSearch = new Date();
		long currentDateLong = periodToSearch.getTime();
		long lastMinute = currentDateLong-60000;
		long lastFiveMinutes = currentDateLong-300000;
		long lastQuarter = currentDateLong-900000;
		long lastHour = currentDateLong-3600000;
		long lastDay = currentDateLong-86400000;
		long lastWeek = currentDateLong-604800000;
		long lastMonth = currentDateLong-2592000000l;
		periodToSearch.setTime(lastMinute);
		result.setOccurrencesLastMinute(
				queryNumLastRegistrations(periodToSearch) );
		periodToSearch.setTime(lastFiveMinutes);
		result.setOccurrencesLastFiveMinutes(
				queryNumLastRegistrations(periodToSearch) );
		periodToSearch.setTime(lastQuarter);
		result.setOccurrencesLastQuarter(
				queryNumLastRegistrations(periodToSearch) );
		periodToSearch.setTime(lastHour);
		result.setOccurrencesLastHour(
				queryNumLastRegistrations(periodToSearch) );
		periodToSearch.setTime(lastDay);
		result.setOccurrencesLastDay(
				queryNumLastRegistrations(periodToSearch) );
		periodToSearch.setTime(lastWeek);
		result.setOccurrencesLastWeek(
				queryNumLastRegistrations(periodToSearch) );
		periodToSearch.setTime(lastMonth);
		result.setOccurrencesLastMonth(
				queryNumLastRegistrations(periodToSearch) );
		return result;
	}

	/**
	 * Realiza la consulta JPQL 'User.getNumLastRegistrations'
	 * @param periodToSearch se compara con la fecha del 'dateRegistered'
	 * de los User de la base de datos, y si aquella es mas antigua se
	 * incrementara el resultado devuelto
	 * @return cantidad de registros de usuario en la aplicacion,
	 * a partir de la fecha dada (periodToSearch)
	 * @throws EntityNotPersistedException
	 */
	private int queryNumLastRegistrations(Date periodToSearch) 
			throws EntityNotPersistedException{
		Long result = 0l;
		try{
		result = (Long) JPA.getManager()
			.createNamedQuery("User.getNumLastRegistrations")
			.setParameter(1, periodToSearch)
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
	
	/**
	 * Realiza la consulta JPQL 'User.getUserByUrlConfirm'
	 * @param urlConfirm atributo homonimo del User que se desea consultar
	 * @return User cuyo atributo 'urlConfirm' coincide con el parametro
	 * recibido
	 * @throws EntityNotPersistedException
	 */
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

	/**
	 * Realiza la consulta JPQL 'User.getMostValuedUsersOfTheMonth'
	 * @return lista de los User cuyo UserInfo asociado tiene los mayores
	 * valores del atributo 'pointsMonth'
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'User.getMostActiveUsersOfTheMonth'
	 * @return lista de los User cuyo UserInfo asociado tiene los mayores
	 * valores del atributo 'countCommentsMonth' sumado a
	 * 'countCorrectionsMonth'
	 */
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
	
	/**
	 * Realiza la consulta MySQL para hallar la posicion de un usuario en la
	 * clasificacion mensual de puntos de usuarios
	 * @param user User cuya clasificacion se desea consultar
	 * @return clasificacion del User dado segun el atributo 'points'
	 * de su UserInfo asociado
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta MySQL para hallar la lista clasificatoria de los
	 * 5 usuarios mas proximos al usuario dado, incluyendo este
	 * @param user User cuya clasificacion se desea consultar
	 * @param lastPosition la ultima posicion en la clasificacion
	 * @return lista de User mas proximos al usuario dado en la clasificacion
	 * de puntos
	 * @throws EntityNotPersistedException
	 */
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

	/**
	 * Genera un Map a partir de la lista que recibe por parametro
	 * @param listDataUsers lista de Object[]. Cada uno de estos es un array
	 * de 2 elementos donde el primero es el 'id' de un User y el segundo es
	 * la posicion clasificatoria de dicho User
	 * @return Map&lt;Integer, User&gt; , donde la key es la posicion
	 * clasificatoria de un User y el value es el propio User
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Elimina de la base de datos el objeto UserInfo, UserInfoPrivacity,
	 * y PrivacityData del User dado
	 * @param user User asociado a la informacion que se desea eliminar
	 * @throws EntityNotPersistedException
	 */
	public void deleteUserAccount(User user) throws EntityNotPersistedException{
		try{
			/* UserInfo userInfoToRemove = 
					Jpa.getManager().find(UserInfo.class, ui.getId());
			Jpa.getManager().remove(userInfoToRemove); */
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
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	/**
	 * Elimina de la base de datos el objeto User dado
	 * @param user User que se desea eliminar
	 * @throws EntityNotPersistedException
	 */
	public void deleteUser(User user) throws EntityNotPersistedException{
		try{
			/* UserInfo userInfoToRemove = 
					Jpa.getManager().find(UserInfo.class, ui.getId());
			Jpa.getManager().remove(userInfoToRemove); */
			JPA.getManager()
				.createNamedQuery("User.deleteUser")
				.setParameter(1, user.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					USER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
