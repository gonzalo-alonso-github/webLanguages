package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Message;
import com.loqua.model.User;

public interface ServiceMessage {

	Message getMessageByID(Long messageID) 
			throws EntityNotFoundException;

	List<Message> getMessagesSentByUser(Long userSenderID);

	List<Message> getMessagesReceivedByUser(Long userReceiverID);

	Integer getNumUnreadMessagesReceivedByUser(Long userReceiverID);

	void deleteSentMessagesByUser(User user) throws EntityNotFoundException;
}