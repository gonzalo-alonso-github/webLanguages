package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.loqua.model.Message;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link 'Message'} y {@link 'MessageReceiver'}
 * @author Gonzalo
 */
public class MessageJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'Message' en la base de datos */
	private static final String MESSAGE_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Message' entity not found"
			+ " at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Message.getMessageById'
	 * @param messageId atributo 'id' del Message que se consulta
	 * @return objeto Message cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Message.getMessagesSentByUser'
	 * @param userSenderID atributo 'id' del User que envio el Message
	 * @return lista de Message cuyo atributo 'user' coincide con el
	 * parametro recibido
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Message.getMessagesReceivedByUser'
	 * @param userReceiverID atributo 'id' del User destinatario del Message
	 * @return lista de Message cuyo atributo 'receiver' coincide con el
	 * parametro recibido
	 */
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

	/**
	 * Realiza la consulta JPQL 'Message.getNumUnreadMessagesReceivedByUser'
	 * @param userReceiverID atributo 'id' del User al que pertenecen los
	 * Message que se consultan
	 * @return cantidad de Message cuyo atributo 'receiver' coincide
	 * con el User dado y cuyo atributo 'read' es 'false'
	 * @throws EntityNotPersistedException
	 */
	public Integer getNumUnreadMessagesReceivedByUser(Long userReceiverID)
			throws EntityNotPersistedException {
		Long result = 0l;
		try{
			result = (Long) JPA.getManager().createNamedQuery(
							"Message.getNumUnreadMessagesReceivedByUser")
					.setParameter(1, userReceiverID)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					MESSAGE_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}

	/**
	 * Elimina de la base de datos todos los objetos Message cuyo atributo
	 * 'user' coincide con el parametro recibido
	 * (es decir: elimina todos los Message enviados por un User)
	 * @param user User al que pertenecen los Message que se desean eliminar
	 * @throws EntityNotPersistedException
	 */
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
