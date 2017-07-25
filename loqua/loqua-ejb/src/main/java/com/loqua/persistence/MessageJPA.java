package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.loqua.model.Message;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class MessageJPA {
	
	private static final String ENTITY_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Message' entity not found"
			+ " at Persistence layer";
	private static final String MESSAGE_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Message' entity not found"
			+ " at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	public Message getMessageById(Long messageId) 
			throws EntityNotPersistedException{
		Message result = new Message();
		try{
			//result = (Message) Jpa.getManager().find(Message.class, messageId);
			result = (Message) JPA.getManager()
					.createNamedQuery("Message.getMessageById")
					.setParameter(1, messageId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					MESSAGE_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Message> getMessagesSentByUser( Long userSenderID ){
		List<Message> result = new ArrayList<Message>();
		try{
			result = (List<Message>) JPA.getManager()
				.createNamedQuery("Message.getMessagesSentByUser")
				.setParameter(1, userSenderID)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Message> getMessagesReceivedByUser( Long userReceiverID ){
		List<Message> result = new ArrayList<Message>();
		try{
			result = (List<Message>) JPA.getManager()
				.createNamedQuery("Message.getMessagesReceivedByUser")
				.setParameter(1, userReceiverID)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	public Integer getNumUnreadMessagesReceivedByUser(Long userReceiverID)
			throws EntityNotPersistedException {
		Long result = 0l;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery("Message.getNumUnreadMessagesReceivedByUser")
					.setParameter(1, userReceiverID)
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

	public void deleteSentMessagesByUser(User user) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Message.deleteSentMessagesByUser")
				.setParameter(1, user.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					MESSAGE_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
