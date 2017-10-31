package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServicePublication;
import com.loqua.business.services.impl.transactionScript.TransactionPublication;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceMessage;
import com.loqua.business.services.serviceLocal.LocalServicePublication;
import com.loqua.business.services.serviceRemote.RemoteServiceMessage;
import com.loqua.business.services.serviceRemote.RemoteServicePublication;
import com.loqua.model.Event;
import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link Publication} y {@link PublicationReceiver}.<br>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceMessage} y {@link RemoteServiceMessage},
 * que heredan de {@link ServicePublication}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServicePublication")
public class EjbServicePublication 
		implements LocalServicePublication, RemoteServicePublication {

	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Publication} y {@link PublicationReceiver},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionPublication transactionPublication = 
			new TransactionPublication();
	
	
	@Override
	public Publication getPublicationByID(Long publicationID)
			throws EntityNotFoundException {
		return transactionPublication.getPublicationByID(publicationID);
	}
	
	@Override
	public Event getEventById(Long eventID)
			throws EntityNotFoundException {
		return transactionPublication.getEventById(eventID);
	}
	
	@Override
	public void deletePublicationsByUser(User user)
			throws EntityNotFoundException {
		transactionPublication.deletePublicationsByUser(user);
	}
	
	@Override
	public Integer getNumUnreadNotificationsByUser(Long userId) 
			throws EntityNotFoundException{
		return transactionPublication.getNumUnreadNotificationsByUser(userId);
	}
	
	@Override
	public List<Publication> getLastNotificationsByUser(
			Long userReceiverId, Integer limitNumPubs) {
		return transactionPublication
				.getLastNotificationsByUser(userReceiverId, limitNumPubs);
	}
	
	@Override
	public List<Publication> getPublicationsByUser(
			Long userId, Integer offset, Integer limitNumPubs){
		return transactionPublication.getPublicationsByUser(
				userId, offset, limitNumPubs);
	}
	
	@Override
	public Integer getNumPublicationsByUser(Long userId) 
			throws EntityNotFoundException{
		return transactionPublication.getNumPublicationsByUser(userId);
	}
	
	@Override
	public List<Publication> getPublicationsByUserAndContacts(Long userId,
			Integer offset, Integer limitNumPubs) {
		return transactionPublication.getPublicationsByUserAndContacts(
				userId, offset, limitNumPubs);
	}
	
	@Override
	public Integer getNumPublicationsByUserAndContacts(Long userId) 
			throws EntityNotFoundException{
		return transactionPublication
				.getNumPublicationsByUserAndContacts(userId);
	}
	
	@Override
	public void createPublication(Publication publication)
			throws EntityAlreadyFoundException {
		transactionPublication.createPublication(publication);
	}
	
	@Override
	public void updatePublication(Publication publication)
			throws EntityNotFoundException {
		transactionPublication.updatePublication(publication);
	}
	
	@Override
	public void setNotificationsToRead(Long userId)
			throws EntityNotFoundException {
		transactionPublication.setNotificationsToRead(userId);
	}
	
	@Override
	public void deletePublication(Publication publication)
			throws EntityNotFoundException {
		transactionPublication.deletePublication(publication);
	}
}
