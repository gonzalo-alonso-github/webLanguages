package com.loqua.persistence;

import java.util.Date;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.business.services.impl.MapEntityCounterByDate;
import com.loqua.model.ChangePassword;
import com.loqua.model.types.TypeChangePassword;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link ChangePassword}
 * @author Gonzalo
 */
public class ChangePasswordJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ "at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'ChangePassword' en la base de datos */
	private static final String ENTITY_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'ChangePassword' entity not found"
			+ " at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'ChangePassword' en la base de datos */
	private static final String ENTITY_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ChangePassword' entity already"
			+ " found at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'ChangePassword.getPasswordChangeByUrlConfirm'
	 * @param urlConfirm filtro de busqueda para el atributo homonimo
	 * de ChangePassword
	 * @param typeChangePassword filtro de busqueda para el atributo homonimo
	 * de ChangePassword, que es un Enumeration que puede ser 'RESTORE' o 'EDIT'
	 * @return objeto ChangePassword cuyos atributos coinciden
	 * con los parametros recibidos
	 * @throws EntityNotPersistedException
	 */
	public ChangePassword getPasswordChangeByUrlConfirm(String urlConfirm,
			String typeChangePassword) 
			throws EntityNotPersistedException{
		ChangePassword result = new ChangePassword();
		try{
			result = (ChangePassword) JPA.getManager()
				.createNamedQuery("ChangePassword.getPasswordChangeByUrlConfirm")
				.setParameter(1, urlConfirm)
				.setParameter(2, 
						Enum.valueOf(TypeChangePassword.class,typeChangePassword))
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					ENTITY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Genera una instancia de MapEntityCounterByDate y carga dicho Map
	 * realizando varias consultas a la base de datos
	 * @param userID atributo 'id' del usuario que se desea consultar
	 * @param typeChange filtro de busqueda para el atributo homonimo de
	 * ChangePassword
	 * @return una instancia de MapEntityCounterByDate que almacena la 
	 * cantidad de veces que el usuario dado ha agregado elementos
	 * ChangePassword a base de datos a lo largo de varios lapsos de tiempo
	 * (el Map clasifica los siguientes lapsos: por hora, por dia, por semana,
	 * por mes y por a&ntilde;o)
	 * @throws EntityNotPersistedException
	 */
	public MapEntityCounterByDate getNumLastPasswordChangesByUser(
			Long userID, String typeChange) 
			throws EntityNotPersistedException {
		MapEntityCounterByDate result = new MapEntityCounterByDate();
		Date periodToSearch = new Date();
		long currentDateLong = periodToSearch.getTime();
		long lastHour = currentDateLong-3600000;
		long lastDay = currentDateLong-86400000;
		long lastWeek = currentDateLong-604800000;
		long lastMonth = currentDateLong-2592000000L;
		long lastYear = currentDateLong-31536000000L;
		periodToSearch.setTime(lastHour);
		result.setOccurrencesLastHour(
			queryNumLastPasswordRestoresByUser(userID,periodToSearch,typeChange) );
		periodToSearch.setTime(lastDay);
		result.setOccurrencesLastDay(
			queryNumLastPasswordRestoresByUser(userID,periodToSearch,typeChange) );
		periodToSearch.setTime(lastWeek);
		result.setOccurrencesLastWeek(
			queryNumLastPasswordRestoresByUser(userID,periodToSearch,typeChange) );
		periodToSearch.setTime(lastMonth);
		result.setOccurrencesLastMonth(
			queryNumLastPasswordRestoresByUser(userID,periodToSearch,typeChange) );
		periodToSearch.setTime(lastYear);
		result.setOccurrencesLastYear(
			queryNumLastPasswordRestoresByUser(userID,periodToSearch,typeChange) );
		return result;
	}

	/**
	 * Realiza la consulta JPQL 'ChangePassword.getNumLastPasswordChangesByUser'
	 * @param userID  atributo 'id' del usuario que se desea consultar
	 * @param periodToSearch se compara con la fecha de los ChangePassword
	 * del usuario, y si aquella es mas antigua se incrementara el resultado
	 * devuelto
	 * @param typeChangePassword filtro de busqueda para el atributo homonimo
	 * de ChangePassword
	 * @return cantidad de veces que el usuario dado ha agregado elementos
	 * ChangePassword a base de datos, a partir de la fecha dada
	 * (periodToSearch)
	 * @throws EntityNotPersistedException
	 */
	private int queryNumLastPasswordRestoresByUser(
			Long userID, Date periodToSearch, String typeChangePassword) 
			throws EntityNotPersistedException{
		Long result = 0l;
		try{
		result = (Long) JPA.getManager()
			.createNamedQuery("ChangePassword.getNumLastPasswordChangesByUser")
			.setParameter(1, periodToSearch)
			.setParameter(2, 
					Enum.valueOf(TypeChangePassword.class, typeChangePassword))
			.setParameter(3, userID)
			.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					ENTITY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	/**
	 * Agrega a la base de datos el objeto ChangePassword dado
	 * @param objectChangePassword objeto ChangePassword que se desea guardar
	 * @return el atributo 'id' del elemento ChangePassword introducido
	 * @throws EntityAlreadyPersistedException
	 */
	public Long createPasswordChange(ChangePassword objectChangePassword) 
			throws EntityAlreadyPersistedException {
		try{
			JPA.getManager().persist(objectChangePassword);
			JPA.getManager().flush();
			JPA.getManager().refresh(objectChangePassword);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					ENTITY_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return objectChangePassword.getId();
	}
	
	/**
	 * Actualiza en la base de datos el objeto ChangePassword dado
	 * @param objectChangePassword objeto ChangePassword que se desea actualizar
	 * @throws EntityAlreadyPersistedException
	 */
	public void updatePasswordChange(ChangePassword objectChangePassword)
			throws EntityNotPersistedException {
		try{
			JPA.getManager().merge( objectChangePassword );
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
