package com.loqua.persistence;

import java.util.Date;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.business.services.impl.MapEntityCounterByDate;
import com.loqua.model.ChangeEmail;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class ChangeEmailJPA {
	
	private static final String CHANGEEMAIL_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'ChangeEmail' entity not found"
			+ " at Persistence layer";
	private static final String CHANGEEMAIL_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ChangeEmail' entity already"
			+ " found at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ "at Persistence layer";
	
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
	
	public MapEntityCounterByDate getNumLastEmailChangesByUser(Long userID) 
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
				queryNumLastEmailChangesByUser(userID,currentDate) );
		currentDate.setTime(lastDay);
		result.setOccurrencesLastDay(
				queryNumLastEmailChangesByUser(userID,currentDate) );
		currentDate.setTime(lastWeek);
		result.setOccurrencesLastWeek(
				queryNumLastEmailChangesByUser(userID,currentDate) );
		currentDate.setTime(lastMonth);
		result.setOccurrencesLastMonth(
				queryNumLastEmailChangesByUser(userID,currentDate) );
		currentDate.setTime(lastYear);
		result.setOccurrencesLastYear(
				queryNumLastEmailChangesByUser(userID,currentDate) );
		return result;
	}
	
	private int queryNumLastEmailChangesByUser(Long userID,Date currentDate) 
			throws EntityNotPersistedException{
		Long result = 0l;
		try{
		result = (Long) JPA.getManager()
			.createNamedQuery("ChangeEmail.getNumLastEmailChangesByUser")
			.setParameter(1, currentDate)
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
