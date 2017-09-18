package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.User;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link Contact} y {@link ContactRequest}
 * @author Gonzalo
 */
public interface ServiceContact {

	/**
	 * Consulta los contactos del usuario dado
	 * @param userId atributo 'id' del User que se consulta
	 * @return lista de Contact que pertenecen al User dado
	 */
	List<Contact> getContactsByUser(Long userId);

	/**
	 * Consulta las peticiones de contacto enviadas por el usuario dado
	 * @param user User que se consulta
	 * @return lista de ContactRequest que pertenecen al User dado
	 */
	List<ContactRequest> getContactRequestByUser(User user);
	
	/**
	 * Consulta las peticiones de contacto recibidas por el usuario dado
	 * @param user atributo 'id' del User que se consulta
	 * @return lista de ContactRequest cuyo atributo 'userReceiver'
	 * coincide con el parametro dado
	 */
	List<ContactRequest> getContactRequestsReceivedByUser(Long userId);
	
	/**
	 * Elimina los objetos Contact cuyo atributo 'user' o cuyo atributo
	 * 'userContact' sea igual al User dado (es decir: elimina todas las
	 * relaciones de contacto de un usuario)
	 * @param user User asociado a los Contact que se desean eliminar
	 * @throws EntityNotFoundException
	 */
	void deleteAllReciprocalContactsByUser(User user)
			throws EntityNotFoundException;

	/**
	 * Elimina los objetos ContactRequest cuyo atributo 'user' o cuyo atributo
	 * 'userContact' sea igual al User dado (es decir: elimina todas las
	 * peticiones de contacto enviadas o recibidas por un usuario)
	 * @param user User asociado a los Contact que se desean eliminar
	 * @throws EntityNotFoundException
	 */
	void deleteAllContactRequestsByUser(User user)
			throws EntityNotFoundException;
	
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
	void deleteReciprocalContact(Long userID, Long userContactID)
			throws EntityNotFoundException;
	
	/**
	 * Crea la asociacion (ContactRequest) entre los dos User indicados,
	 * genera una publicacion para el evento
	 * y actualiza todos los cambios en la base de datos
	 * @param userSender usuario que envia la solicitud de contacto
	 * @param userReceiver usuario que recibe la solicitud de contacto
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	void createContactRequest(User userSender, User userReceiver)
			throws EntityAlreadyFoundException, EntityNotFoundException;

	/**
	 * 
	 * @param userReceiverId
	 * @param userSenderId
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	void acceptRequest(Long userReceiverId, Long userSenderId)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	/**
	 * Acepta una peticion de contacto. Para ello genera las relaciones
	 * (Contact) entre los usuaros indicados y despues elimina la solicitud
	 * de contacto, que estaba pendiente de aceptacion
	 * @param userReceiverId usuario que recibe la solicitud de contacto
	 * @param userSenderId usuario que envia la solicitud de contacto
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	void deleteRequest(Long userReceiverId, Long userSenderId)
			throws EntityNotFoundException;
}