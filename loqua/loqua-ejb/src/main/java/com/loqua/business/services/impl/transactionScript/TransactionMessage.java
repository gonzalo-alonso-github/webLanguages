package com.loqua.business.services.impl.transactionScript;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Message;
import com.loqua.model.MessageReceiver;
import com.loqua.model.User;
import com.loqua.persistence.MessageJPA;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link Message} y {@link MessageReceiver}.<br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionMessage {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link Message} y {@link MessageReceiver} */
	private static final MessageJPA messageJPA = new MessageJPA();
	
	
	/**
	 * Consulta mensajes segun su atributo 'id'
	 * @param messageID atributo 'id' del Message que se consulta
	 * @return objeto Message cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public Message getMessageByID(Long messageID) throws EntityNotFoundException{
		Message result = new Message();
		try {
			result = messageJPA.getMessageById(messageID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Halla los mensajes enviados por un usuario
	 * @param userSenderID atributo 'id' del User que se consulta
	 * @return lista de Message cuyo atributo 'user' coincide con el
	 * parametro recibido
	 */
	public List<Message> getMessagesSentByUser(Long userSenderID){
		return messageJPA.getMessagesSentByUser(userSenderID);
	}
	
	/**
	 * Halla los mensajes recibidos por un usuario
	 * @param userReceiverID atributo 'id' del User que se consulta
	 * @return lista de Message cuyo atributo 'receiver' coincide con el
	 * parametro recibido
	 */
	public List<Message> getMessagesReceivedByUser(Long userReceiverID){
		return messageJPA.getMessagesReceivedByUser(userReceiverID);
	}

	/**
	 * Halla el numero de mensajes sin leer recibidos por un usuario
	 * @param userReceiverID atributo 'id' del User que se consulta
	 * @return cantidad de Message cuyo atributo 'receiver' coincide
	 * con el User dado y cuyo atributo 'read' es 'false
	 * @throws EntityNotFoundException
	 */
	public Integer getNumUnreadMessagesReceivedByUser(Long userReceiverID)
			throws EntityNotFoundException {
		Integer result = 0;
		try {
			result = messageJPA.getNumUnreadMessagesReceivedByUser(
					userReceiverID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}

	/**
	 * Elimina todos los mensajes enviados por un usuario
	 * @param user User cuyos mensaje  se eliminan
	 * @throws EntityNotFoundException
	 */
	public void deleteSentMessagesByUser(User user) 
			throws EntityNotFoundException {
		try {
			messageJPA.deleteSentMessagesByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
}
