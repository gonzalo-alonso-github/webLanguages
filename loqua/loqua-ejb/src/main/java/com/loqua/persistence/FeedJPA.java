package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Feed} y {@link FeedCategory}
 * @author Gonzalo
 */
public class FeedJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Feed'
	 * en la base de datos */
	private static final String FEED_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Feed' entity not found"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al repetirse la entidad 'FeedCategory'
	 * en la base de datos */
	private static final String FEEDCATEGORY_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'FeedCategory' entity already"
			+ " found at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Feed.getFeedById'
	 * @param feedId atributo 'id' del Feed que se consulta
	 * @return Feed cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
	public Feed getFeedById(Long feedId) throws EntityNotPersistedException {
		Feed result = new Feed();
		try{
			// result = (Feed) Jpa.getManager().find(Feed.class, feedID);
			result = (Feed) JPA.getManager()
					.createNamedQuery("Feed.getFeedById")
					.setParameter(1, feedId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					FEED_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	/**
	 * Realiza la consulta JPQL 'Feed.getAllFeeds'
	 * @return lista de todos los Feed de la base de datos
	 */
	@SuppressWarnings("unchecked")
	public List<Feed> getAllFeeds() {
		List<Feed> result = new ArrayList<Feed>();
		try{
			result = (List<Feed>) JPA.getManager()
				.createNamedQuery("Feed.getAllFeeds")
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Feed.getAllFeedCategoriesIds'
	 * @return lista de todos los atributos 'id' de los FeedCategories 
	 * de la base de datos
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getAllFeedCategoriesIds() {
		List<Long> result = new ArrayList<Long>();
		try{
			result = (List<Long>) JPA.getManager()
				.createNamedQuery("Feed.getAllFeedCategoriesIds")
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Feed.getAllFeedCategories'
	 * @return lista de todos los FeedCategories de la base de datos
	 */
	@SuppressWarnings("unchecked")
	public List<FeedCategory> getAllFeedCategories() {
		List<FeedCategory> result = new ArrayList<FeedCategory>();
		try{
			result = (List<FeedCategory>) JPA.getManager()
				.createNamedQuery("Feed.getAllFeedCategories")
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Agrega a la base de datos el objeto FeedCategory dado
	 * @param feedCategory objeto FeedCategory que se desea guardar
	 * @throws EntityAlreadyPersistedException
	 */
	public void createFeedCategory(FeedCategory feedCategory)
			throws EntityAlreadyPersistedException {
		try{
			JPA.getManager().persist( feedCategory );
			JPA.getManager().flush();
			JPA.getManager().refresh( feedCategory );
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	/**
	 * Actualiza en la base de datos el objeto FeedCategory dado
	 * @param feedCategory objeto FeedCategory que se desea guardar
	 * @throws EntityNotPersistedException
	 */
	public void updateFeedCategory(FeedCategory feedCategory)
			throws EntityNotPersistedException {
		try{	
			JPA.getManager().merge( feedCategory );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					FEEDCATEGORY_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
