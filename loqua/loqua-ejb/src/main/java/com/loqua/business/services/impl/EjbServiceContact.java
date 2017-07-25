package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionContact;
import com.loqua.business.services.serviceLocal.LocalServiceContact;
import com.loqua.business.services.serviceRemote.RemoteServiceContact;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.User;

@Stateless
@WebService(name="ServiceContact")
public class EjbServiceContact
		implements LocalServiceContact, RemoteServiceContact {
	
	private static final TransactionContact transactionContact = 
			new TransactionContact();
	
	@Override
	public List<Contact> getContactsByUser( Long userId ) {
		return transactionContact.getContactsByUser(userId);
	}
	
	@Override
	public List<ContactRequest> getContactRequestByUser( User user ) {
		return transactionContact.getContactRequestByUser(user);
	}

	@Override
	public List<ContactRequest> getContactRequestsReceivedByUser(Long userId){
		return transactionContact.getContactRequestsReceivedByUser(userId);
	}
	
	@Override
	public void deleteAllReciprocalContactsByUser(User u)
			throws EntityNotFoundException{
		transactionContact.deleteAllReciprocalContactsByUser(u);
	}
	
	@Override
	public void deleteAllContactRequestsByUser(User u)
			throws EntityNotFoundException{
		transactionContact.deleteAllContactRequestsByUser(u);
	}
	
	@Override
	public void deleteReciprocalContact(Long userID, Long userContactID)
			throws EntityNotFoundException{
		transactionContact.deleteReciprocalContact(userID, userContactID);
	}
	
	@Override
	public void createContactRequest(User userSender, User userReceiver)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		transactionContact.createContactRequest(userSender,userReceiver);
	}
	
	@Override
	public void acceptRequest(Long userReceiverId, Long userSenderId)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		transactionContact.acceptRequest(userReceiverId, userSenderId);
	}
	
	@Override
	public void deleteRequest(Long userReceiverId, Long userSenderId)
			throws EntityNotFoundException{
		transactionContact.deleteRequest(userReceiverId, userSenderId);
	}
}
