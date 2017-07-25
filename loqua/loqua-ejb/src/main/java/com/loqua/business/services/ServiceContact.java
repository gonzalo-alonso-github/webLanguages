package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.User;

public interface ServiceContact {

	List<Contact> getContactsByUser(Long userId);

	List<ContactRequest> getContactRequestByUser(User user);
	
	List<ContactRequest> getContactRequestsReceivedByUser(Long userId);
	
	void deleteAllReciprocalContactsByUser(User user)
			throws EntityNotFoundException;

	void deleteAllContactRequestsByUser(User user)
			throws EntityNotFoundException;
	
	void deleteReciprocalContact(Long userID, Long userContactID)
			throws EntityNotFoundException;
	
	void createContactRequest(User userSender, User userReceiver)
			throws EntityAlreadyFoundException, EntityNotFoundException;

	void acceptRequest(Long userReceiverId, Long userSenderId)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	void deleteRequest(Long userReceiverId, Long userSenderId)
			throws EntityNotFoundException;
}