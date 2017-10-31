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

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Contact} y {@link ContactRequest}
 * @author Gonzalo
 */
public class ContactJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Contact'
	 * en la base de datos */
	private static final String CONTACT_NOT_PERSISTED_EXCEPTION =
			"EntityNotPersistedException: 'Contact' entity not found"
					+ " at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad 'Contact'
	 * en la base de datos */
	private static final String CONTACT_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Contact' entity already"
			+ " found at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'ContactRequest' en la base de datos */
	private static final String CONTACTREQUEST_NOT_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ContactRequest' entity not"
			+ " found at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'ContactRequest' en la base de datos */
	private static final String CONTACTREQUEST_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ContactRequest' entity already"
			+ " found at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Contact.getContactsByUser'
	 * @param userId atributo 'id' del User al que pertenecen los Contact
	 * que se consultan
	 * @return lista de Contact que pertenecen al User dado
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Contact.getContactRequestByUser'
	 * @param user User al que pertenecen los Contact que se consultan
	 * @return lista de ContactRequest que pertenecen al User dado
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Contact.getContactRequestByBothUsers'
	 * para hallar la solicitud de contacto (objeto ContactRequest) enviada
	 * por el usuario 'userSenderId' y recibida por 'userReceiverId'.
	 * @param userReceiverId filtro de busqueda para el atributo homonimo
	 * de ContactRequest
	 * @param userSenderId filtro de busqueda para el atributo homonimo
	 * de ContactRequest
	 * @return objeto ContactRequest cuyos atributos coinciden
	 * con los parametros recibidos
	 * @throws EntityNotPersistedException
	 */
	public ContactRequest getContactRequestByBothUsers(
			Long userSenderId, Long userReceiverId)
			throws EntityNotPersistedException {
		ContactRequest result = new ContactRequest();
		try{
			result = (ContactRequest) JPA.getManager()
				.createNamedQuery("Contact.getContactRequestByBothUsers")
				.setParameter(1, userSenderId)
				.setParameter(2, userReceiverId)
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
	
	/**
	 * Realiza la consulta JPQL 'Contact.getContactRequestsReceivedByUser'
	 * para hallar las solicitudes de contacto (objetos ContactRequest)
	 * recibidas por el usuario 'userId'.
	 * @param userId atributo 'id' del User al que pertenecen los
	 * ContactRequest que se consultan
	 * @return lista de ContactRequest cuyo atributo 'userReceiver' coincide
	 * con el parametro dado
	 */
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
	
	/**
	 * Elimina de la base de datos los objetos Contact cuyo atributo 'user' o
	 * cuyo atributo 'userContact' sea igual al User dado
	 * (es decir: elimina todas las relaciones de contacto de un usuario)
	 * @param user User asociado a los Contact que se desean eliminar
	 * @throws EntityNotPersistedException
	 */
	public void deleteAllReciprocalContactsByUser(User user) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Contact.deleteAllReciprocalContactsByUser")
				.setParameter(1, user.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CONTACT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	/**
	 * Elimina de la base de datos los objetos ContactRequest cuyo atributo
	 * 'userSender' o cuyo atributo 'userReceiver' sea igual al User dado
	 * (es decir: elimina las peticiones de contacto enviadas o recibidas 
	 * por un usuario)
	 * @param user User asociado a los ContactRequest que se desean eliminar
	 * @throws EntityNotPersistedException
	 */
	public void deleteAllContactRequestsByUser(User user)
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Contact.deleteAllContactRequestsByUser")
				.setParameter(1, user.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CONTACT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	/**
	 * Elimina de la base de datos los dos objetos Contact cuyo atributo 'user'
	 * y cuyo atributo 'userContact' sean iguales respectivamente al parametro
	 * 'userID' y al parametro 'userContactID', o viceversa.
	 * (es decir: elimina las dos relaciones de contacto entre dos usuarios)
	 * @param userID filtro de busqueda para el atributo homonimo de
	 * Contact
	 * @param userContactID filtro de busqueda para el atributo homonimo de
	 * Contact
	 * @throws EntityNotPersistedException
	 */
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
					CONTACT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	/**
	 * Agrega a la base de datos el objeto ContactRequest dado
	 * @param contactRequest objeto ContactRequest que se desea agregar
	 * @throws EntityAlreadyPersistedException
	 */
	public void createContactRequest(ContactRequest contactRequest)
			throws EntityAlreadyPersistedException {
		try{
			JPA.getManager().persist( contactRequest );
			JPA.getManager().flush();
			JPA.getManager().refresh( contactRequest );
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					CONTACTREQUEST_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	/**
	 * Genera dos objetos Contact, a partir de los parametros recibidos,
	 * y los agrega a la base de datos (es decir, genera la relacion de
	 * contacto reciproca entre dos usuarios)
	 * @param receiverId atributo 'userContact' del primer Contact que se genera
	 * y atributo 'user' del segundo Contact que se genera
	 * @param senderId atributo 'user' del primer Contact que se genera
	 * y atributo 'userContact' del segundo Contact que se genera
	 * @throws EntityAlreadyPersistedException
	 */
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
	
	/**
	 * Actualiza en la base de datos el objeto ContactRequest dado
	 * @param contactRequest objeto ContactRequest que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Elimina de la base de datos el objeto ContactRequest dado
	 * @param contactRequest objeto ContactRequest que se desea eliminar
	 * @throws EntityNotPersistedException
	 */
	public void deleteRequest(ContactRequest contactRequest)
			throws EntityNotPersistedException{
		try{
			/* ContactRequest contactRequest = 
					Jpa.getManager().find(ContactRequest.class, cr.getId());
			Jpa.getManager().remove(contactRequest); */
			JPA.getManager()
				.createNamedQuery("ContactRequest.deleteById")
				.setParameter(1, contactRequest.getId())
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
