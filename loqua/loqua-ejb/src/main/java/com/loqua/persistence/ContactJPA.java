package com.loqua.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class ContactJPA {
	
	private static final String ENTITY_NOT_PERSISTED_EXCEPTION =
			"EntityNotPersistedException: 'Correction' entity not found"
					+ " at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	private static final String CONTACTREQUEST_NOT_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ContactRequest' entity not"
			+ " found at Persistence layer";
	private static final String CONTACTREQUEST_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ContactRequest' entity already"
			+ " found at Persistence layer";
	private static final String CONTACT_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Contact' entity already"
			+ " found at Persistence layer";
	
	@SuppressWarnings("unchecked")
	public List<Contact> getContactsByUser( Long userId ){
		List<Contact> result = new ArrayList<Contact>();
		try{
			result = (List<Contact>) JPA.getManager()
				.createNamedQuery("Contact.getContactsByUser")
				.setParameter(1, userId)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<ContactRequest> getContactRequestByUser( User user ){
		List<ContactRequest> result = new ArrayList<ContactRequest>();
		try{
			result = (List<ContactRequest>) JPA.getManager()
				.createNamedQuery("Contact.getContactRequestsByUser")
				.setParameter(1, user.getEmail().toString())
				.setParameter(2, user.getDateRegistered())
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public ContactRequest getContactRequestByBothUsers(
			Long userReceiverId, Long userSenderId)
			throws EntityNotPersistedException {
		ContactRequest result = new ContactRequest();
		try{
			result = (ContactRequest) JPA.getManager()
				.createNamedQuery("Contact.getContactRequestByBothUsers")
				.setParameter(1, userReceiverId)
				.setParameter(2, userSenderId)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CONTACTREQUEST_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<ContactRequest> getContactRequestsReceivedByUser( Long userId ){
		List<ContactRequest> result = new ArrayList<ContactRequest>();
		try{
			result = (List<ContactRequest>) JPA.getManager()
				.createNamedQuery("Contact.getContactRequestsReceivedByUser")
				.setParameter(1, userId)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public void deleteAllReciprocalContactsByUser(User u) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Contact.deleteAllReciprocalContactsByUser")
				.setParameter(1, u.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					ENTITY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	public void deleteAllContactRequestsByUser(User u)
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Contact.deleteAllContactRequestsByUser")
				.setParameter(1, u.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					ENTITY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void deleteReciprocalContact(Long userID, Long userContactID) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Contact.deleteReciprocalContact")
				.setParameter(1, userID)
				.setParameter(2, userContactID)
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					ENTITY_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	public void createContactRequest(ContactRequest cr)
			throws EntityAlreadyPersistedException {
		try{
			JPA.getManager().persist( cr );
			JPA.getManager().flush();
			JPA.getManager().refresh( cr );
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					CONTACTREQUEST_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	public void createContact(Long receiverId, Long senderId) 
			throws EntityAlreadyPersistedException{ 
		try{
			// Debemos obtener dichas entidades desde la bdd mediante JPA,
			// para evitar que esten en estado 'detatched' al hacer el persist
			User userReceiver = JPA.getManager().find(User.class, receiverId);
			User userSender = JPA.getManager().find(User.class, senderId);
			
			Contact contact = new Contact();
			contact.setUserThis(userSender).setUserContactThis(userReceiver)
					.setDateJoinThis(new Date());
			JPA.getManager().persist( contact );
			JPA.getManager().flush();
			JPA.getManager().refresh( contact );
			
			Contact reciprocalContact = new Contact();
			reciprocalContact.setUserThis(userReceiver)
					.setUserContactThis(userSender).setDateJoinThis(new Date());
			JPA.getManager().persist( reciprocalContact );
			JPA.getManager().flush();
			JPA.getManager().refresh( reciprocalContact );
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					CONTACT_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void updateContactRequest(ContactRequest contactRequest)
			throws EntityNotPersistedException {
		try{
			JPA.getManager().merge( contactRequest );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CONTACTREQUEST_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void deleteRequest(ContactRequest cr)
			throws EntityNotPersistedException{
		try{
			/*
			ContactRequest contactRequest = 
					Jpa.getManager().find(ContactRequest.class, cr.getId());
			Jpa.getManager().remove(contactRequest);
			*/
			JPA.getManager()
				.createNamedQuery("ContactRequest.deleteById")
				.setParameter(1, cr.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CONTACTREQUEST_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
