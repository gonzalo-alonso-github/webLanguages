package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionMessage;
import com.loqua.business.services.serviceLocal.LocalServiceMessage;
import com.loqua.business.services.serviceRemote.RemoteServiceMessage;
import com.loqua.model.Message;
import com.loqua.model.User;

@Stateless
@WebService(name="ServiceMessage")
public class EjbServiceMessage 
		implements LocalServiceMessage, RemoteServiceMessage {

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
