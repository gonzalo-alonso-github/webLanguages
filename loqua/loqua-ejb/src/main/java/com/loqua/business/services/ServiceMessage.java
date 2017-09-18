package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Message;
import com.loqua.model.User;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link 'Message'} y {@link 'MessageReceiver'}
 * @author Gonzalo
 */
public interface ServiceMessage {

	/**
	 * Consulta mensajes segun su atributo 'id'
	 * @param messageID atributo 'id' del Message que se consulta
	 * @return objeto Message cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	Message getMessageByID(Long messageID) 
			throws EntityNotFoundException;

	/**
	 * Halla los mensajes enviados por un usuario
	 * @param userSenderID atributo 'id' del User que se consulta
	 * @return lista de Message cuyo atributo 'user' coincide con el
	 * parametro recibido
	 */
	List<Message> getMessagesSentByUser(Long userSenderID);

	/**
	 * Halla los mensajes recibidos por un usuario
	 * @param userReceiverID atributo 'id' del User que se consulta
	 * @return lista de Message cuyo atributo 'receiver' coincide con el
	 * parametro recibido
	 */
	List<Message> getMessagesReceivedByUser(Long userReceiverID);

	/**
	 * Halla el numero de mensajes sin leer recibidos por un usuario
	 * @param userReceiverID atributo 'id' del User que se consulta
	 * @return cantidad de Message cuyo atributo 'receiver' coincide
	 * con el User dado y cuyo atributo 'read' es 'false
	 * @throws EntityNotFoundException
	 */
	Integer getNumUnreadMessagesReceivedByUser(Long userReceiverID);

	/**
	 * Elimina todos los mensajes enviados por un usuario
	 * @param user User cuyos mensaje  se eliminan
	 * @throws EntityNotFoundException
	 */
	void deleteSentMessagesByUser(User user) throws EntityNotFoundException;
}