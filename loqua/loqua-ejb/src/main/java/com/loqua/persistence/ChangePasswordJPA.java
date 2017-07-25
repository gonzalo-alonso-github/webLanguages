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

public class ChangePasswordJPA {
	
	private static final String ENTITY_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'ChangePassword' entity not found"
			+ " at Persistence layer";
	private static final String ENTITY_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ChangePassword' entity already"
			+ " found at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ "at Persistence layer";
	
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
	
	public MapEntityCounterByDate getNumLastPasswordChangesByUser(
			Long userID, String typeChange) 
			throws EntityNotPersistedException {
		MapEntityCounterByDate result = new MapEntityCounterByDate();
		Date currentDate = new Date();
		long currentDateLong = currentDate.getTime();
		long lastHour = currentDateLong-3600000;
		long lastDay = currentDateLong-86400000;
		long lastWeek = currentDateLong-604800000;
		long lastMonth = currentDateLong-2592000000L;
		long lastYear = currentDateLong-31536000000L;
		currentDate.setTime(lastHour);
		result.setOccurrencesLastHour(
			queryNumLastPasswordRestoresByUser(userID,currentDate,typeChange) );
		currentDate.setTime(lastDay);
		result.setOccurrencesLastDay(
			queryNumLastPasswordRestoresByUser(userID,currentDate,typeChange) );
		currentDate.setTime(lastWeek);
		result.setOccurrencesLastWeek(
			queryNumLastPasswordRestoresByUser(userID,currentDate,typeChange) );
		currentDate.setTime(lastMonth);
		result.setOccurrencesLastMonth(
			queryNumLastPasswordRestoresByUser(userID,currentDate,typeChange) );
		currentDate.setTime(lastYear);
		result.setOccurrencesLastYear(
			queryNumLastPasswordRestoresByUser(userID,currentDate,typeChange) );
		return result;
	}

	private int queryNumLastPasswordRestoresByUser(
			Long userID, Date currentDate, String typeChangePassword) 
			throws EntityNotPersistedException{
		Long result = 0l;
		try{
		result = (Long) JPA.getManager()
			.createNamedQuery("ChangePassword.getNumLastPasswordChangesByUser")
			.setParameter(1, currentDate)
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
