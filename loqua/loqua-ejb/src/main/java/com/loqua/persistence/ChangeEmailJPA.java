package com.loqua.persistence;

import java.util.Date;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.business.services.impl.utils.security.MapOccurrCounterByDate;
import com.loqua.model.ChangeEmail;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link ChangeEmail}
 * @author Gonzalo
 */
public class ChangeEmailJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ "at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'ChangeEmail' en la base de datos */
	private static final String CHANGEEMAIL_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'ChangeEmail' entity not found"
			+ " at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'ChangeEmail' en la base de datos */
	private static final String CHANGEEMAIL_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ChangeEmail' entity already"
			+ " found at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'ChangeEmail.getEmailChangeByUrlConfirm'
	 * @param urlConfirm filtro de busqueda para el atributo homonimo de
	 * ChangeEmail
	 * @return objeto ChangeEmail cuyo atributo 'urlConfirm' coincide con el
	 * parametro recibido
	 * @throws EntityNotPersistedException
	 */
	public ChangeEmail getEmailChangeByUrlConfirm(String urlConfirm) 
			throws EntityNotPersistedException{
		ChangeEmail result = new ChangeEmail();
		try{
			result = (ChangeEmail) JPA.getManager()
				.createNamedQuery("ChangeEmail.getEmailChangeByUrlConfirm")
				.setParameter(1, urlConfirm)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CHANGEEMAIL_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Genera una instancia de {@link MapOccurrCounterByDate} y carga dicho Map
	 * realizando varias consultas a la base de datos
	 * @param userID atributo 'id' del usuario que se desea consultar
	 * @return una instancia de MapOccurrCounterByDate que almacena la 
	 * cantidad de veces que el usuario dado ha agregado elementos ChangeEmail
	 * a base de datos a lo largo de varios lapsos de tiempo (el Map clasifica
	 * los siguientes lapsos: por hora, por dia, por semana, por mes
	 * y por a&ntilde;o)
	 * @throws EntityNotPersistedException
	 */
	public MapOccurrCounterByDate getNumLastEmailChangesByUser(Long userID) 
			throws EntityNotPersistedException {
		MapOccurrCounterByDate result = new MapOccurrCounterByDate();
		Date periodToSearch = new Date();
		long currentDateLong = periodToSearch.getTime();
		long lastHour = currentDateLong-3600000;
		long lastDay = currentDateLong-86400000;
		long lastWeek = currentDateLong-604800000;
		long lastMonth = currentDateLong-2592000000L;
		long lastYear = currentDateLong-31536000000L;
		periodToSearch.setTime(lastHour);
		result.setOccurrencesLastHour(
				queryNumLastEmailChangesByUser(userID,periodToSearch) );
		periodToSearch.setTime(lastDay);
		result.setOccurrencesLastDay(
				queryNumLastEmailChangesByUser(userID,periodToSearch) );
		periodToSearch.setTime(lastWeek);
		result.setOccurrencesLastWeek(
				queryNumLastEmailChangesByUser(userID,periodToSearch) );
		periodToSearch.setTime(lastMonth);
		result.setOccurrencesLastMonth(
				queryNumLastEmailChangesByUser(userID,periodToSearch) );
		periodToSearch.setTime(lastYear);
		result.setOccurrencesLastYear(
				queryNumLastEmailChangesByUser(userID,periodToSearch) );
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'ChangeEmail.getNumLastEmailChangesByUser'
	 * @param userID atributo 'id' del usuario que se desea consultar
	 * @param periodToSearch se compara con la fecha de los ChangeEmail
	 * del usuario, y si aquella es mas antigua se incrementara el resultado
	 * devuelto
	 * @return cantidad de veces que el usuario dado ha agregado elementos
	 * ChangeEmail a base de datos, a partir de la fecha dada (periodToSearch)
	 * @throws EntityNotPersistedException
	 */
	private int queryNumLastEmailChangesByUser(Long userID,Date periodToSearch) 
			throws EntityNotPersistedException{
		Long result = 0l;
		try{
		result = (Long) JPA.getManager()
			.createNamedQuery("ChangeEmail.getNumLastEmailChangesByUser")
			.setParameter(1, periodToSearch)
			.setParameter(2, userID)
			.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CHANGEEMAIL_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	/**
	 * Agrega a la base de datos el objeto ChangeEmail dado
	 * @param objectChangeEmail objeto ChangeEmail que se desea guardar
	 * @return el atributo 'id' del elemento ChangeEmail introducido
	 * @throws EntityAlreadyPersistedException
	 */
	public Long createEmailChange(ChangeEmail objectChangeEmail) 
			throws EntityAlreadyPersistedException {
		try{
			JPA.getManager().persist(objectChangeEmail);
			JPA.getManager().flush();
			JPA.getManager().refresh(objectChangeEmail);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					CHANGEEMAIL_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return objectChangeEmail.getId();
	}
	
	/**
	 * Actualiza en la base de datos el objeto ChangeEmail dado
	 * @param objectChangeEmail objeto ChangeEmail que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
	public void updateEmailChange(ChangeEmail objectChangeEmail)
			throws EntityNotPersistedException {
		try{
			JPA.getManager().merge( objectChangeEmail );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CHANGEEMAIL_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
