package com.loqua.persistence;

import javax.persistence.NoResultException;

import com.loqua.model.ForumPost;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class ForumPostJPA {
	
	private static final String PHORUMPOST_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'ForumPost' entity not found"
			+ " at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ "at Persistence layer";

	public ForumPost getForumPostByID(Long forumPostId) 
			throws EntityNotPersistedException {
		ForumPost result = new ForumPost();
		try{
			/*
			result = (ForumPost) Jpa.getManager().find(
					ForumPost.class, forumPostId);
			*/
			result = (ForumPost) JPA.getManager()
					.createNamedQuery("ForumPost.getForumPostById")
					.setParameter(1, forumPostId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					PHORUMPOST_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/*
	@SuppressWarnings("unchecked")
	public List<ForumPost> getForumPostsByUser( User user ) {
		List<ForumPost> res = new ArrayList<ForumPost>();
		try{
		res = (List<ForumPost>) JPA.getManager()
			.createNamedQuery("ForumPost.getForumPostByUser")
			.setParameter(1, user.getEmail().toString())
			.setParameter(2, user.getDateRegistered())
			.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return res;
	}
	*/
}
