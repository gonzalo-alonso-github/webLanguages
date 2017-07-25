package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.User;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.ContactJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionContact {
	
	private static final ContactJPA contactJPA = new ContactJPA();
	
	private static final TransactionPublication transactionPub = 
			new TransactionPublication();

	
	public List<Contact> getContactsByUser( Long userId ) {
		return contactJPA.getContactsByUser(userId);
	}
	
	public List<ContactRequest> getContactRequestByUser( User user ) {
		if( user==null ) return new ArrayList<ContactRequest>();
		return contactJPA.getContactRequestByUser(user);
	}

	private ContactRequest getContactRequestByBothUsers(
			Long userReceiverId, Long userSenderId) {
		ContactRequest result = null;
		try {
			result = contactJPA.getContactRequestByBothUsers(
					userReceiverId, userSenderId);
		} catch (EntityNotPersistedException ex) {
			return null;
		}
		return result;
	}
	
	public List<ContactRequest> getContactRequestsReceivedByUser(Long userId){
		return contactJPA.getContactRequestsReceivedByUser(userId);
	}
	
	public void deleteAllReciprocalContactsByUser(User u)
			throws EntityNotFoundException{
		try {
			contactJPA.deleteAllReciprocalContactsByUser(u);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	public void deleteAllContactRequestsByUser(User u)
			throws EntityNotFoundException {
		try {
			contactJPA.deleteAllContactRequestsByUser(u);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void deleteReciprocalContact(Long userID, Long userContactID)
			throws EntityNotFoundException{
		try {
			contactJPA.deleteReciprocalContact(userID, userContactID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	public void createContactRequest(User userSender, User userReceiver)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			ContactRequest cr = new ContactRequest();
			cr.setUserSenderThis(userSender).setUserReceiverThis(userReceiver)
				.setDateRequestThis(new Date()).setRejectedThis(false);
			contactJPA.createContactRequest(cr);
			// Generar una publicacion privada:
			transactionPub.generatePublication(TypePrivacity.PRIVATE,
					userSender.getId(), 302L, userReceiver);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	private void createContact(Long userReceiverId, Long userSenderId)
			throws EntityAlreadyFoundException {
		try {
			contactJPA.createContact(userReceiverId, userSenderId);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	public void acceptRequest(Long userReceiverId, Long userSenderId)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		ContactRequest request = getContactRequestByBothUsers(
			userReceiverId, userSenderId);
		try{
			if( request==null ){ return; }
			// Aceptar una solicitud significa:
			// borrarla de la tabla ContactRequest
			contactJPA.deleteRequest(request);
			// y agregar dos entradas en la tabla Contact
			createContact(userReceiverId, userSenderId);
			//Una vez aceptada la solicitud se busca y elimina si esta repetida
			ContactRequest invertedRequest = getContactRequestByBothUsers(
					userSenderId, userReceiverId);
			if(invertedRequest!=null) contactJPA.deleteRequest(invertedRequest);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void deleteRequest(Long userReceiverId, Long userSenderId)
			throws EntityNotFoundException {
		ContactRequest request = getContactRequestByBothUsers(
				userReceiverId, userSenderId);
		try{
			if( request!=null ){
				request.setRejected(true);
				contactJPA.updateContactRequest(request);
			}
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
}
