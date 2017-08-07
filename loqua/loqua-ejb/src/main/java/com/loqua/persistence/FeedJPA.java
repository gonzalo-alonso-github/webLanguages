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

public class FeedJPA {
	
	private static final String FEED_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Feed' entity not found"
			+ " at Persistence layer";
	private static final String FEEDCATEGORY_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'FeedCategory' entity already"
			+ " found at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
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
