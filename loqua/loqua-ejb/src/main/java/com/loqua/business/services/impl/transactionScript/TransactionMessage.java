package com.loqua.business.services.impl.transactionScript;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Message;
import com.loqua.model.User;
import com.loqua.persistence.MessageJPA;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionMessage {

	private static final MessageJPA messageJPA = new MessageJPA();
	
	public Message getMessageByID(Long messageID) throws EntityNotFoundException{
		Message result = new Message();
		try {
			result = messageJPA.getMessageById(messageID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public List<Message> getMessagesSentByUser(Long userSenderID){
		return messageJPA.getMessagesSentByUser(userSenderID);
	}
	
	public List<Message> getMessagesReceivedByUser(Long userReceiverID){
		return messageJPA.getMessagesReceivedByUser(userReceiverID);
	}

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

	public void deleteSentMessagesByUser(User user) 
			throws EntityNotFoundException {
		try {
			messageJPA.deleteSentMessagesByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
}
