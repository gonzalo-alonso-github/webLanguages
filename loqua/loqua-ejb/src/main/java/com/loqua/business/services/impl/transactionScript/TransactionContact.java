package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.ContactJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link Contact} y {@link ContactRequest}. <br/>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionContact {
	
	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link Contact} y {@link ContactRequest} */
	private static final ContactJPA contactJPA = new ContactJPA();
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Publication} y {@link PublicationReceiver},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionPublication transactionPub = 
			new TransactionPublication();

	/**
	 * Consulta los contactos del usuario dado
	 * @param userId atributo 'id' del User que se consulta
	 * @return lista de Contact que pertenecen al User dado
	 */
	public List<Contact> getContactsByUser( Long userId ) {
		return contactJPA.getContactsByUser(userId);
	}
	
	/**
	 * Consulta las peticiones de contacto enviadas por el usuario dado
	 * @param user User que se consulta
	 * @return lista de ContactRequest que pertenecen al User dado
	 */
	public List<ContactRequest> getContactRequestByUser( User user ) {
		if( user==null ) return new ArrayList<ContactRequest>();
		return contactJPA.getContactRequestByUser(user);
	}

	/**
	 * Consulta las peticiones de contacto enviadas y recibidas respectivamente
	 * por los usuarios indicados
	 * @param userReceiverId atributo 'id' del User que ha recibido
	 * la solicitud de contacto
	 * @param userSenderId atributo 'id' del User que ha enviado
	 * la solicitud de contacto
	 * @return lista de ContactRequest asociadas a los usuarios indicados
	 */
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
	
	/**
	 * Consulta las peticiones de contacto recibidas por el usuario dado
	 * @param user atributo 'id' del User que se consulta
	 * @return lista de ContactRequest cuyo atributo 'userReceiver'
	 * coincide con el parametro dado
	 */
	public List<ContactRequest> getContactRequestsReceivedByUser(Long userId){
		return contactJPA.getContactRequestsReceivedByUser(userId);
	}
	
	/**
	 * Elimina los objetos Contact cuyo atributo 'user' o cuyo atributo
	 * 'userContact' sea igual al User dado (es decir: elimina todas las
	 * relaciones de contacto de un usuario)
	 * @param user User asociado a los Contact que se desean eliminar
	 * @throws EntityNotFoundException
	 */
	public void deleteAllReciprocalContactsByUser(User user)
			throws EntityNotFoundException{
		try {
			contactJPA.deleteAllReciprocalContactsByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Elimina los objetos ContactRequest cuyo atributo 'user' o cuyo atributo
	 * 'userContact' sea igual al User dado (es decir: elimina todas las
	 * peticiones de contacto enviadas o recibidas por un usuario)
	 * @param user User asociado a los Contact que se desean eliminar
	 * @throws EntityNotFoundException
	 */
	public void deleteAllContactRequestsByUser(User u)
			throws EntityNotFoundException {
		try {
			contactJPA.deleteAllContactRequestsByUser(u);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Elimina los dos objetos Contact cuyo atributo 'user' y cuyo atributo
	 * 'userContact' sean iguales respectivamente al parametro 'userID' y
	 * al parametro 'userContactID', o viceversa. (es decir: elimina las dos
	 * relaciones de contacto entre dos usuarios)
	 * @param userID filtro de busqueda para el atributo homonimo de Contact
	 * @param userContactID filtro de busqueda para el atributo homonimo
	 * de Contact
	 * @throws EntityNotFoundException
	 */
	public void deleteReciprocalContact(Long userID, Long userContactID)
			throws EntityNotFoundException{
		try {
			contactJPA.deleteReciprocalContact(userID, userContactID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	/**
	 * Crea la asociacion (ContactRequest) entre los dos User indicados,
	 * genera una publicacion para el evento
	 * y actualiza todos los cambios en la base de datos
	 * @param userSender usuario que envia la solicitud de contacto
	 * @param userReceiver usuario que recibe la solicitud de contacto
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
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
	
	/**
	 * Genera dos objetos Contact, a partir de los parametros recibidos
	 * (es decir, genera la relacion de contacto reciproca entre dos usuarios)
	 * @param userReceiverId usuario que recibe la solicitud de contacto
	 * @param userSenderId usuario que envia la solicitud de contacto
	 * @throws EntityAlreadyFoundException
	 */
	private void createContact(Long userReceiverId, Long userSenderId)
			throws EntityAlreadyFoundException {
		try {
			contactJPA.createContact(userReceiverId, userSenderId);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	/**
	 * Acepta una peticion de contacto. Para ello genera las relaciones
	 * (Contact) entre los usuaros indicados y despues elimina la solicitud
	 * de contacto, que estaba pendiente de aceptacion
	 * @param userReceiverId usuario que recibe la solicitud de contacto
	 * @param userSenderId usuario que envia la solicitud de contacto
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
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
	
	/**
	 * Elimina una solicitud de contacto
	 * @param userReceiverId usuario que recibe la solicitud de contacto
	 * @param userSenderId usuario que envia la solicitud de contacto
	 * @throws EntityNotFoundException
	 */
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
