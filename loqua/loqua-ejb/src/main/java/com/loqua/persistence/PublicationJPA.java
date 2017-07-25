package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.Achievement;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.Event;
import com.loqua.model.ForumPost;
import com.loqua.model.Publication;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class PublicationJPA {
	
	private static final String PUBLICATION_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Publication' entity not found"
			+ " at Persistence layer";
	private static final String EVENT_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Event' entity not found"
			+ " at Persistence layer";
	private static final String ACHIEVEMENT_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Achievement' entity not found"
			+ " at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ "at Persistence layer";
	private static final String PUBLICATION_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Publication' entity already"
			+ " found at Persistence layer";
	
	public Publication getPublicationById(Long publicationId) 
			throws EntityNotPersistedException {
		Publication result = new Publication();
		try{
			/*
			result = (Publication) Jpa.getManager().find(
					Publication.class, publicationId);
			*/
			result = (Publication) JPA.getManager()
					.createNamedQuery("Publication.getPublicationById")
					.setParameter(1, publicationId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PUBLICATION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public Event getEventById(Long eventId) throws EntityNotPersistedException {
		Event result = new Event();
		try{
			/*
			result = (Publication) Jpa.getManager().find(
					Publication.class, publicationId);
			*/
			result = (Event) JPA.getManager()
					.createNamedQuery("Publication.getEventById")
					.setParameter(1, eventId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					EVENT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public void deletePublicationsByUser(User user) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Publication.deletePublicationsByUser")
				.setParameter(1, user.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PUBLICATION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public Integer getNumUnreadNotificationsByUser(Long userId)
			throws EntityNotPersistedException {
		Long result = 0l;
		String query = "Publication.getNumUnreadNotificationsByUser";
		try{
			result = (Long) JPA.getManager().createNamedQuery(query)
					.setParameter(1, userId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PUBLICATION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Publication> getLastNotificationsByUser(
			Long userReceiverID, Integer limitNotifications){
		List<Publication> result = new ArrayList<Publication>();
		try{
			result = (List<Publication>) JPA.getManager()
				.createNamedQuery(
						"Publication.getLastNotificationsByUser")
				.setParameter(1, userReceiverID)
				.setMaxResults(limitNotifications) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Publication> getPublicationsByUser(
			Long userId, Integer offset, Integer elementsPerPage){
		List<Publication> result = new ArrayList<Publication>();
		try{
			result = (List<Publication>) JPA.getManager()
				.createNamedQuery(
						"Publication.getPublicationsByUser")
				.setParameter(1, userId)
				.setFirstResult(offset) // offset
				.setMaxResults(elementsPerPage) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public Integer getNumPublicationsByUser(Long userId)
			throws EntityNotPersistedException {
		Long result = 0l;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery("Publication.getNumPublicationsByUser")
					.setParameter(1, userId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PUBLICATION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Publication> getPublicationsByUserAndContacts(
			Long userId, Integer offset, Integer elementsPerPage){
		List<Publication> result = new ArrayList<Publication>();
		try{
			result = (List<Publication>) JPA.getManager()
				.createNamedQuery(
						"Publication.getPublicationsByUserAndContacts")
				.setParameter(1, userId)
				.setFirstResult(offset) // offset
				.setMaxResults(elementsPerPage) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	public Integer getNumPublicationsByUserAndContacts(Long userId)
			throws EntityNotPersistedException {
		Long result = 0l;
		try{
			result = (Long) JPA.getManager().createNamedQuery(
						"Publication.getNumPublicationsByUserAndContacts")
					.setParameter(1, userId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PUBLICATION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	public void createPublication(Publication publication) 
			throws EntityAlreadyPersistedException {
		try{
			JPA.getManager().persist( publication );
			JPA.getManager().flush();
			JPA.getManager().refresh(publication);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					PUBLICATION_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	/*
	public void createPublicationReceiver(Publication publication, User receiver) 
			throws EntityAlreadyPersistedException {
		try{
			// Debemos obtener dichas entidades desde la bdd mediante JPA,
			// para evitar que esten en estado 'detatched' al hacer el persist
			Publication pub = JPA.getManager().find(Publication.class, publication.getId());
			User user = JPA.getManager().find(User.class, receiver.getId());
			
			PublicationReceiver pubReceiver = new PublicationReceiver();
			pubReceiver.setUser(user);
			pubReceiver.setPublication(pub);
			
			JPA.getManager().persist( pubReceiver );
			JPA.getManager().flush();
			JPA.getManager().refresh( pubReceiver );
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					PUBLICATIONRECEIVER_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	*/
	public void createAchievement(Achievement achievement) 
			throws EntityAlreadyPersistedException {
		try{
			JPA.getManager().persist( achievement );
			JPA.getManager().flush();
			JPA.getManager().refresh( achievement );
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					PUBLICATION_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public Achievement achievementTopUsersAlreadyPassedByUser(Long eventValue,
			Long eventType, Long userId) throws EntityNotPersistedException {
		String namedQuery =
				"Publication.achievementTopUsersAlreadyPassedByUser";
		return achievementAlreadyPassedByUser(
				eventValue, eventType, userId, namedQuery);
	}
	
	public Achievement achievementNumCommentsAlreadyPassedByUser(Long eventValue,
			Long eventType, Long userId) throws EntityNotPersistedException {
		String namedQuery =
				"Publication.achievementNumCommentsAlreadyPassedByUser";
		return achievementAlreadyPassedByUser(
				eventValue, eventType, userId, namedQuery);
	}
	
	public Achievement achievementNumCorrsAlreadyPassedByUser(Long eventValue,
			Long eventType, Long userId) throws EntityNotPersistedException {
		String namedQuery =
				"Publication.achievementNumCorrsAlreadyPassedByUser";
		return achievementAlreadyPassedByUser(
				eventValue, eventType, userId, namedQuery);
	}
	
	private Achievement achievementAlreadyPassedByUser(Long eventValue,
			Long eventType, Long userId, String namedQuery)
			throws EntityNotPersistedException {
		Achievement result = null;
		try{
			result = JPA.getManager().createNamedQuery(
					namedQuery,Achievement.class)
				.setParameter(1, eventValue)
				.setParameter(2, eventType)
				.setParameter(3, userId)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					ACHIEVEMENT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public void updatePublication(Publication pub)
			throws EntityNotPersistedException {
		try{
			JPA.getManager().merge( pub );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PUBLICATION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}catch( Exception ex ){}
	}
	
	public void setNotificationsToRead(Long userId)
			throws EntityNotPersistedException {
		String namedQuery = "Publication.setNotificationsToRead";
		try{
			JPA.getManager().createNamedQuery(namedQuery)
					.setParameter(1, userId)
					.executeUpdate();
			}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PUBLICATION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}catch( Exception ex ){}
	}
	
	public void deletePublication(Publication pub)
			throws EntityNotPersistedException{
		try{
			/*
			Publication pub = 
					Jpa.getManager().find(Publication.class, pub.getId());
			Jpa.getManager().remove(pub);
			*/
			JPA.getManager()
				.createNamedQuery("Publication.deleteById")
				.setParameter(1, pub.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PUBLICATION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Publication> getPubsForDeletedPost(ForumPost post){
		List<Publication> result = new ArrayList<Publication>();
		String namedQuery = "";
		if( post instanceof Comment ){
			namedQuery = "Publication.getPubsForDeletedComment";
		}else if( post instanceof Correction ){
			namedQuery = "Publication.getPubsForDeletedCorrection";
		}
		try{
			result = (List<Publication>) JPA.getManager()
				.createNamedQuery( namedQuery )
				.setParameter(1, post.getId())
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/*
	private List<Long> getAllEventsTypes(){
		List<Long> result = new ArrayList<Long>();
		try{
			result = (List<Long>) Jpa.getManager()
				.createNamedQuery(
						"Publication.getAllEventsTypes", Long.class)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	*/
}
