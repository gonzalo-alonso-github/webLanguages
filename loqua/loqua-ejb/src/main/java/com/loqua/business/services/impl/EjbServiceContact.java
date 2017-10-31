package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionContact;
import com.loqua.business.services.locator.*;
import com.loqua.business.services.serviceLocal.LocalServiceContact;
import com.loqua.business.services.serviceRemote.RemoteServiceContact;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.User;
import com.loqua.business.services.ServiceContact;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link Contact} y {@link ContactRequest}. <br>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceContact} y {@link RemoteServiceContact},
 * que heredan de {@link ServiceContact}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceContact")
public class EjbServiceContact
		implements LocalServiceContact, RemoteServiceContact {
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Contact} y {@link ContactRequest},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
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
	public void acceptRequest(Long userSenderId, Long userReceiverId)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		transactionContact.acceptRequest(userSenderId, userReceiverId);
	}
	
	@Override
	public void deleteRequest(Long userSenderId, Long userReceiverId)
			throws EntityNotFoundException{
		transactionContact.deleteRequest(userSenderId, userReceiverId);
	}
}
