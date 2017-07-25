package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionPublication;
import com.loqua.business.services.serviceLocal.LocalServicePublication;
import com.loqua.business.services.serviceRemote.RemoteServicePublication;
import com.loqua.model.Event;
import com.loqua.model.Publication;
import com.loqua.model.User;

@Stateless
@WebService(name="ServicePublication")
public class EjbServicePublication 
		implements LocalServicePublication, RemoteServicePublication {

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
			Long userReceiverID, Integer limit) {
		return transactionPublication
				.getLastNotificationsByUser(userReceiverID, limit);
	}
	
	@Override
	public List<Publication> getPublicationsByUser(
			Long userId, Integer offset, Integer elementsPerPage){
		return transactionPublication.getPublicationsByUser(
				userId, offset, elementsPerPage);
	}
	
	@Override
	public Integer getNumPublicationsByUser(Long userId) 
			throws EntityNotFoundException{
		return transactionPublication.getNumPublicationsByUser(userId);
	}
	
	@Override
	public List<Publication> getPublicationsByUserAndContacts(Long userId,
			Integer offset, Integer elementsPerPage) {
		return transactionPublication.getPublicationsByUserAndContacts(
				userId, offset, elementsPerPage);
	}
	
	@Override
	public Integer getNumPublicationsByUserAndContacts(Long userId) 
			throws EntityNotFoundException{
		return transactionPublication.getNumPublicationsByUserAndContacts(userId);
	}
	
	@Override
	public void createPublication(Publication publication)
			throws EntityAlreadyFoundException {
		transactionPublication.createPublication(publication);
	}
	
	@Override
	public void updatePublication(Publication pub)
			throws EntityNotFoundException {
		transactionPublication.updatePublication(pub);
	}
	
	@Override
	public void setNotificationsToRead(Long userId)
			throws EntityNotFoundException {
		transactionPublication.setNotificationsToRead(userId);
	}
	
	@Override
	public void deletePublication(Publication pub)
			throws EntityNotFoundException {
		transactionPublication.deletePublication(pub);
	}
}
