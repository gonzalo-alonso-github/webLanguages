package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceMessage;
import com.loqua.business.services.impl.transactionScript.TransactionMessage;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceMessage;
import com.loqua.business.services.serviceRemote.RemoteServiceMessage;
import com.loqua.model.Message;
import com.loqua.model.MessageReceiver;
import com.loqua.model.User;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link Message} y {@link MessageReceiver}.<br/>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br/>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceMessage} y {@link RemoteServiceMessage},
 * que heredan de {@link ServiceMessage}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceMessage")
public class EjbServiceMessage 
		implements LocalServiceMessage, RemoteServiceMessage {

	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Message} y {@link MessageReceiver},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionMessage transactionMessage = 
			new TransactionMessage();
	
	@Override
	public Message getMessageByID(Long messageID)
			throws EntityNotFoundException {
		return transactionMessage.getMessageByID(messageID);
	}

	@Override
	public List<Message> getMessagesSentByUser(Long userSenderID) {
		return transactionMessage.getMessagesSentByUser(userSenderID);
	}
	
	@Override
	public List<Message> getMessagesReceivedByUser(Long userReceiverID) {
		return transactionMessage.getMessagesReceivedByUser(userReceiverID);
	}

	@Override
	public Integer getNumUnreadMessagesReceivedByUser(Long userReceiverID) {
		Integer result = 0;
		try{
			result = transactionMessage.getNumUnreadMessagesReceivedByUser(
					userReceiverID);
		}catch( EntityNotFoundException e ){
			result = null;
		}
		return result;
	}

	@Override
	public void deleteSentMessagesByUser(User user)
			throws EntityNotFoundException {
		transactionMessage.deleteSentMessagesByUser(user);
	}
}
