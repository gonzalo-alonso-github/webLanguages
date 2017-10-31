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
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Publication} y {@link PublicationReceiver}
 * @author Gonzalo
 */
public class PublicationJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ "at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Message'
	 * en la base de datos */
	private static final String PUBLICATION_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Publication' entity not found"
			+ " at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad 'Publication'
	 * en la base de datos */
	private static final String PUBLICATION_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Publication' entity already"
			+ " found at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Event'
	 * en la base de datos */
	private static final String EVENT_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Event' entity not found"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'Achievement' en la base de datos */
	private static final String ACHIEVEMENT_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Achievement' entity not found"
			+ " at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Publication.getPublicationById'
	 * @param publicationId atributo 'id' de la Publication que se consulta
	 * @return Publication cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
	public Publication getPublicationById(Long publicationId) 
			throws EntityNotPersistedException {
		Publication result = new Publication();
		try{
			/* result = (Publication) Jpa.getManager().find(
					Publication.class, publicationId); */
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
	
	/**
	 * Realiza la consulta JPQL 'Publication.getEventById'
	 * @param eventId atributo 'id' del Event que se consulta
	 * @return Event cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
	public Event getEventById(Long eventId) throws EntityNotPersistedException {
		Event result = new Event();
		try{
			/* result = (Publication) Jpa.getManager().find(
					Publication.class, publicationId); */
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
	
	/**
	 * Elimina de la base de datos todos los objetos Publication
	 * cuyo atributo 'user' coincide con el parametro recibido
	 * @param user User al que pertenecen las Publication qe se desean eliminar 
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Publication.getNumUnreadNotificationsByUser'
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se cosultan
	 * @return cantidad de Publication, pertenecientes al User dado,
	 * cuyo atributo 'readPub' es 'false' y cuyo Event asociado tiene su
	 * atributo 'showAsNotification' igual a 'true'
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Publication.getLastNotificationsByUser'
	 * @param userReceiverID atributo 'id' del User asociado con las
	 * Publication que se consultan
	 * @param limitNotifications limite maximo de Publication devueltas
	 * @return lista de Publication, pertenecientes al User dado, cuyo
	 * Event asociado tiene su atributo 'showAsNotification' igual a 'true',
	 * aplicando el limite maximo de elementos indicado
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Publication.getPublicationsByUser'
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se cosultan
	 * @param offset offset de las Publication devueltas
	 * @param limitNumPubs limite maximo de Publication devueltas
	 * @return lista de Publication pertenecientes al User dado,
	 * cuyo atributo 'selfGenerated' es 'false'
	 * o bien cuyo Event asociado tiene su atributo 'showAsPublication' igual a
	 * 'true', aplicando el offset dado y el limite maximo de elementos
	 * indicado
	 */
	@SuppressWarnings("unchecked")
	public List<Publication> getPublicationsByUser(
			Long userId, Integer offset, Integer limitNumPubs){
		List<Publication> result = new ArrayList<Publication>();
		try{
			result = (List<Publication>) JPA.getManager()
				.createNamedQuery(
						"Publication.getPublicationsByUser")
				.setParameter(1, userId)
				.setFirstResult(offset) // offset
				.setMaxResults(limitNumPubs) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Publication.getNumPublicationsByUser'
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se cosultan
	 * @return cantidad de Publication pertenecientes al User dado,
	 * cuyo atributo 'selfGenerated' es 'false' o bien cuyo Event asociado
	 * tiene su atributo 'showAsPublication' igual a 'true'
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Publication.getPublicationsByUserAndContacts'
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se cosultan
	 * @param offset offset de las Publication devueltas
	 * @param limitNumPubs limite maximo de Publication devueltas
	 * @return lista de Publication, que pertenecen al User dado
	 * o a los Contact de este, cuyo atributo 'selfGenerated' es 'false'
	 * o bien cuyo Event asociado tiene su atributo 'showAsPublication' igual a
	 * 'true', aplicando el offset dado y el limite maximo de elementos
	 * indicado
	 */
	@SuppressWarnings("unchecked")
	public List<Publication> getPublicationsByUserAndContacts(
			Long userId, Integer offset, Integer limitNumPubs){
		List<Publication> result = new ArrayList<Publication>();
		try{
			result = (List<Publication>) JPA.getManager()
				.createNamedQuery(
						"Publication.getPublicationsByUserAndContacts")
				.setParameter(1, userId)
				.setFirstResult(offset) // offset
				.setMaxResults(limitNumPubs) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	/**
	 * Realiza la consulta 'Publication.getNumPublicationsByUserAndContacts'
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se cosultan
	 * @return cantidad de Publication, pertenecientes al User dado
	 * o a los Contact de este, cuyo atributo 'selfGenerated' es 'false'
	 * o bien cuyo Event asociado tiene su atributo 'showAsPublication'
	 * igual a 'true'
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Agrega a la base de datos el objeto Publication dado
	 * @param publication objeto Publication que se desea guardar
	 * @throws EntityAlreadyPersistedException
	 */
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
	 * Genera un objeto PublicationReceiver a partir de los
	 * parametros recibidos y lo agrega a la base de datos
	 * @param publication Publication asociada al PublicationReceiver
	 * que se genera
	 * @param receiver User asociado al PublicationReceiver que se genera
	 * @throws EntityAlreadyPersistedException
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
	
	/**
	 * Agrega a la base de datos el objeto Achievement dado
	 * @param achievement objeto Achievement que se desea guardar
	 * @throws EntityAlreadyPersistedException
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
	
	/**
	 * Realiza la consulta 'Publication.achievementTopUsersAlreadyPassedByUser'
	 * para hallar el Achievement del usuario, que representa el logro de
	 * alcanzar un Top en la clasificacion de usuarios
	 * @param eventValue atributo homonimo del Achievement que se consulta
	 * @param userId atributo 'id' del User al que pertenece el Achievement
	 * que se consulta
	 * @return Achievement, perteneciente al User dado, cuyos atributos
	 * coinciden con los parametros recibidos 
	 * @throws EntityNotPersistedException
	 */
	public Achievement achievementTopUsersAlreadyPassedByUser(Long eventValue,
			Long userId) throws EntityNotPersistedException {
		String namedQuery =
				"Publication.achievementTopUsersAlreadyPassedByUser";
		return achievementAlreadyPassedByUser(
				eventValue, 4L, userId, namedQuery);
	}
	
	/**
	 * Realiza la consulta JPQL
	 * 'Publication.achievementNumCommentsAlreadyPassedByUser'
	 * para hallar el Achievement del usuario, que representa el logro de
	 * alcanzar cierto numero de comentarios publicados
	 * @param eventValue atributo homonimo del Achievement que se consulta
	 * @param userId atributo 'id' del User al que pertenece el Achievement
	 * que se consulta
	 * @return Achievement, perteneciente al User dado, cuyos atributos
	 * coinciden con los parametros recibidos 
	 * @throws EntityNotPersistedException
	 */
	public Achievement achievementNumCommentsAlreadyPassedByUser(
			Long eventValue, Long userId)
			throws EntityNotPersistedException {
		String namedQuery =
				"Publication.achievementNumCommentsAlreadyPassedByUser";
		return achievementAlreadyPassedByUser(
				eventValue, 1L, userId, namedQuery);
	}
	
	/**
	 * Realiza la consulta JPQL
	 * 'Publication.achievementNumCorrsAlreadyPassedByUser'
	 * para hallar el Achievement del usuario, que representa el logro de
	 * alcanzar cierto numero de correcciones aprobadas
	 * @param eventValue atributo homonimo del Achievement que se consulta
	 * @param userId atributo 'id' del User al que pertenece el Achievement
	 * que se consulta
	 * @return Achievement, perteneciente al User dado, cuyos atributos
	 * coinciden con los parametros recibidos 
	 * @throws EntityNotPersistedException
	 */
	public Achievement achievementNumCorrsAlreadyPassedByUser(Long eventValue,
			Long userId) throws EntityNotPersistedException {
		String namedQuery =
				"Publication.achievementNumCorrsAlreadyPassedByUser";
		return achievementAlreadyPassedByUser(
				eventValue, 2L, userId, namedQuery);
	}
	
	/**
	 * Realiza la consulta JPQL que se recibe por parametro
	 * @param eventValue atributo homonimo del Achievement que se consulta
	 * @param eventType atributo homonimo del Event del Achievement
	 * que se consulta
	 * @param userId atributo 'id' del User al que pertenece el Achievement
	 * que se consulta
	 * @param namedQuery consulta JPQL que se realiza a la base de datos
	 * @return Achievement, perteneciente al User dado, cuyos atributos
	 * coinciden con los parametros recibidos 
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Actualiza en la base de datos el objeto Publication dado
	 * @param pub objeto Publication que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Actualiza en la base de datos todos los objetos Publication,
	 * cuyo atributo 'user' coincide con el parametro recibido,
	 * estableciendo su atribuo 'read' igual a 'true'
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se desean actualizar
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Elimina de la base de datos el objeto Publication dado
	 * @param pub objeto Publication que se desea eliminar
	 * @throws EntityNotPersistedException
	 */
	public void deletePublication(Publication pub)
			throws EntityNotPersistedException{
		try{
			/* Publication pub = 
					Jpa.getManager().find(Publication.class, pub.getId());
			Jpa.getManager().remove(pub); */
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
	
	/**
	 * Realiza la consulta JPQL 'Publication.getPubsForDeletedComment'
	 * para hallar todas las Publication que hacen referencia a un ForumPost
	 * (bien Comment o bien Correction)
	 * @param post ForumPost asociado a las Publication que se consultan
	 * @return lista de Publication que hacen referencia al ForumPost dado
	 */
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
	 * Realiza la consulta JPQL 'Publication.getAllEventsTypes'
	 * @return lista de los atributos 'type' de los Event
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
