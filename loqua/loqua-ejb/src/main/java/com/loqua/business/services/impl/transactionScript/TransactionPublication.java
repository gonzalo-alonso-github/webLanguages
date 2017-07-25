package com.loqua.business.services.impl.transactionScript;

import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Achievement;
import com.loqua.model.Event;
import com.loqua.model.ForumPost;
import com.loqua.model.ForumThread;
import com.loqua.model.Publication;
import com.loqua.model.User;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.PublicationJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionPublication {

	private static final PublicationJPA publicationJPA = new PublicationJPA();
	
	public Publication getPublicationByID(Long publicationID)
			throws EntityNotFoundException {
		Publication result = new Publication();
		try {
			result = publicationJPA.getPublicationById(publicationID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public Event getEventById(Long eventID)
			throws EntityNotFoundException {
		Event result = new Event();
		try {
			result = publicationJPA.getEventById(eventID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}

	public void deletePublicationsByUser(User user) 
			throws EntityNotFoundException {
		try {
			publicationJPA.deletePublicationsByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public Integer getNumUnreadNotificationsByUser(Long userId) 
			throws EntityNotFoundException {
		Integer result = 0;
		try{
			result = publicationJPA.getNumUnreadNotificationsByUser(userId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public List<Publication> getLastNotificationsByUser(
			Long userReceiverID, Integer limit) {
		return publicationJPA.getLastNotificationsByUser(
				userReceiverID, limit);
	}

	public List<Publication> getPublicationsByUser(
			Long userId, Integer offset, Integer elementsPerPage){
		List<Publication> result = null;
		if(offset==null) offset=0;
		result = publicationJPA.getPublicationsByUser(
				userId, offset, elementsPerPage);
		return result;
	}
	
	public Integer getNumPublicationsByUser(Long userId) 
			throws EntityNotFoundException {
		Integer result = 0;
		try{
			result = publicationJPA.getNumPublicationsByUser(userId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public List<Publication> getPublicationsByUserAndContacts(
			Long userId, Integer offset, Integer elementsPerPage){
		List<Publication> result = null;
		if(offset==null) offset=0;
		result = publicationJPA.getPublicationsByUserAndContacts(
				userId, offset, elementsPerPage);
		return result;
	}
	
	public Integer getNumPublicationsByUserAndContacts(Long userId) 
			throws EntityNotFoundException {
		Integer result = 0;
		try{
			result = publicationJPA.getNumPublicationsByUserAndContacts(userId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}

	public void createPublication(Publication publication) 
			throws EntityAlreadyFoundException {
		try {
			publicationJPA.createPublication(publication);
			//createPublicationReceivers(publication);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	/*
	private void createPublicationReceivers( Publication pub )
			throws EntityAlreadyPersistedException{
		User userPublication = pub.getUser();
		Event eventPublication = pub.getEvent();
		// El propio autor de la publicacion la recibe como notificacion:
		publicationJPA.createPublicationReceiver(pub, pub.getUser());
		if( pub.getPrivacity()==TypePrivacity.CONTACTS
				|| pub.getPrivacity()==TypePrivacity.PUBLIC ){
			// Si se cumplen todas estas condiciones,
			// cada contacto del usuario tambien recibe una notificacion:
			List<User> usersContacts = transactionUser.getUserContactsByUser(
					userPublication.getId());
			for(User userContact : usersContacts){
				publicationJPA.createPublicationReceiver(pub,userContact);
			}
		}
	}
	*/
	public void generatePublication(TypePrivacity privacity, Long eventValue,
			Long eventType, User user)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		// Este metodo es utilizado desde otros transaction scripts
		// al igual que "TransactionPublication.generatePublicationForTopUsers()"
		Event event = getEventById(eventType);
		// Crear la Publication:
		Publication pub = new Publication();
		pub.setDatePubThis(new Date()).setSelfGeneratedThis(true)
			.setPrivacityThis(privacity).setReadPubThis(false)
			.setEventValueThis(eventValue)
			.setEventThis(event).setUserThis(user);
		createPublication(pub);
		// Crear el Achievement:
		if( event.getIsAchievement() ){
			Achievement achievement = new Achievement();
			achievement.setDateAchievementThis(new Date())
				.setEventValueThis(eventValue)
				.setEventThis(event).setUserThis(user);
			createAchievement(achievement);
		}
	}
	
	private void createAchievement(Achievement achievement) 
			throws EntityAlreadyFoundException {
		try {
			publicationJPA.createAchievement(achievement);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	public boolean achievementTopUsersAlreadyPassedByUser(
			Long eventValue, Long eventType, Long userId) {
		// Si devuelve false el logro no se ha superado aun
		// Si devuelve true el logro ya se supero y no generara mas Publications
		Achievement achievement = null;
		try {
			achievement = publicationJPA.achievementTopUsersAlreadyPassedByUser(
					eventValue, eventType, userId);
		} catch (EntityNotPersistedException ex) {
			return false;
		}
		if( achievement==null ){ return false;}
		return true;
	}
	
	public boolean achievementNumCommentsAlreadyPassedByUser(
			Long eventValue, Long eventType, Long userId) {
		// Si devuelve false el logro no se ha superado aun
		// Si devuelve true el logro ya se supero y no generara mas Publications
		Achievement achievement = null;
		try {
			achievement=publicationJPA.achievementNumCommentsAlreadyPassedByUser(
					eventValue, eventType, userId);
		} catch (EntityNotPersistedException ex) {
			return false;
		}
		if( achievement==null ){ return false;}
		return true;
	}
	
	public boolean achievementNumCorrectionsAlreadyPassedByUser(
			Long eventValue, Long eventType, Long userId) {
		// Si devuelve false el logro no se ha superado aun
		// Si devuelve true el logro ya se supero y no generara mas Publications
		Achievement achievement = null;
		try {
			achievement=publicationJPA.achievementNumCorrsAlreadyPassedByUser(
					eventValue, eventType, userId);
		} catch (EntityNotPersistedException ex) {
			return false;
		}
		if( achievement==null ){ return false;}
		return true;
	}

	public void updatePublication(Publication pub)
			throws EntityNotFoundException {
		try {
			publicationJPA.updatePublication(pub);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void setNotificationsToRead(Long userId)
			throws EntityNotFoundException {
		try {
			publicationJPA.setNotificationsToRead(userId);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void deletePublication(Publication pub)
			throws EntityNotFoundException {
		try {
			publicationJPA.deletePublication(pub);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	public void editPubsForDeletedPost(ForumPost post)
			throws EntityNotFoundException {
		// Buscar las Publications cuyo eventValue
		// apunte a este post (post = un comentario o correccion):
		List<Publication> pubsForDeletedPost = 
				publicationJPA.getPubsForDeletedPost(post);
		for( Publication pub : pubsForDeletedPost ){
			ForumThread thread = post.getForumThread();
			// Si tambien se ha borrado el hilo donde estaba el post,
			// se elimina la publicacion
			if( thread==null ){ deletePublication(pub); }
			else{
				// En caso contrario se edita la publicacion:
				// eventValue apuntara al hilo y no al Comment eliminado...
				pub.setEventValue(thread.getId());
				// ...y se cambia a otro Event cuyo tipo estara entre 401 y 500
				long currentEventType = pub.getEvent().getType();
				Event event = getEventForDeletedPost(currentEventType);
				pub.setEvent(event);
				// Se envia la orden a bdd
				updatePublication(pub);
			}
		}
	}
	
	private Event getEventForDeletedPost(long currentEventType) 
			throws EntityNotFoundException {
		// El Event 101 es 'analogo' al Event 401 si se elimina el Comment
		// El Event 102 es 'analogo' al Event 402 si se elimina el Comment
		// El Event 103 es 'analogo' al Event 403 si se elimina el Comment
		// El Event 201 es 'analogo' al Event 451 si se elimina el Correction
		// El Event 202 es 'analogo' al Event 452 si se elimina el Correction
		// El Event 203 es 'analogo' al Event 453 si se elimina el Correction
		// El Event 204 es 'analogo' al Event 454 si se elimina el Correction
		long newEventType = -1;
		if( currentEventType>=101 &&  currentEventType<201 ){
			newEventType = currentEventType+300;
		}else if( currentEventType>=201 &&  currentEventType<301 ){
			newEventType = currentEventType+250;
		}
		return getEventById( newEventType );
	}
}
