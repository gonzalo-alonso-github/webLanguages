package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Event;
import com.loqua.model.Publication;
import com.loqua.model.User;

public interface ServicePublication {

	Publication getPublicationByID(Long publicationID) 
			throws EntityNotFoundException;
	
	Event getEventById(Long eventID) throws EntityNotFoundException;
	
	void deletePublicationsByUser(User user) throws EntityNotFoundException;
	
	List<Publication> getLastNotificationsByUser(
			Long userReceiverID, Integer limit);
	
	List<Publication> getPublicationsByUser(
			Long userID, Integer offset, Integer elementsPerPage);
	Integer getNumPublicationsByUser(Long userId)
			throws EntityNotFoundException;

	List<Publication> getPublicationsByUserAndContacts(
			Long userId, Integer offset, Integer elementsPerPage);
	Integer getNumPublicationsByUserAndContacts(Long userId)
			throws EntityNotFoundException;
	
	Integer getNumUnreadNotificationsByUser(Long userId)
			throws EntityNotFoundException;
	
	void createPublication(Publication publicationToCreate)
			throws EntityAlreadyFoundException;

	void updatePublication(Publication publication)
			throws EntityNotFoundException;
	
	void setNotificationsToRead(Long userId) throws EntityNotFoundException;

	void deletePublication(Publication publication)
			throws EntityNotFoundException;
}