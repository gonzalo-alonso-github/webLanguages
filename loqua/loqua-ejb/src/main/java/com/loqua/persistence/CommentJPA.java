package com.loqua.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.CommentVoter;
import com.loqua.model.ForumThread;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class CommentJPA {
	
	private static final String COMMENT_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Comment' entity not found"
			+ " at Persistence layer";
	private static final String THREAD_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Thread' entity not found"
			+ " at Persistence layer";
	private static final String COMMENT_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Comment' entity already"
			+ " found at Persistence layer";
	private static final String COMMENTVOTER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'CommentVoter' entity already"
			+ " found at Persistence layer";
	private static final String QUOTE_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'CommentQuote' entity already"
			+ " found at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	@SuppressWarnings("unchecked")
	public List<CommentQuoteTo> getCommentsQuotedByComment( Long actorComment ){
		List<CommentQuoteTo> result = new ArrayList<CommentQuoteTo>();
		try{
			result = (List<CommentQuoteTo>) JPA.getManager()
				.createNamedQuery("Comment.getCommentsQuotedByComment")
				.setParameter(1, actorComment)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Comment> getCommentsByThread(Long newThreadId,
			Integer offsetComments, Integer numCommentsPerPage){
		List<Comment> result = new ArrayList<Comment>();
		try{
			result = (List<Comment>) JPA.getManager()
				.createNamedQuery("Comment.getCommentsByThread")
				.setParameter(1, newThreadId)
				.setFirstResult(offsetComments) // offset
				.setMaxResults(numCommentsPerPage) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Comment> getCommentsByThreadReverseOrder(Long newThreadId,
			Integer offsetComments, Integer numCommentsPerPage){
		List<Comment> result = new ArrayList<Comment>();
		try{
			result = (List<Comment>) JPA.getManager()
				.createNamedQuery("Comment.getCommentsByThreadReverseOrder")
				.setParameter(1, newThreadId)
				.setFirstResult(offsetComments) // offset
				.setMaxResults(numCommentsPerPage) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	public Integer getNumCommentsByThread(Long newThreadId) 
			throws EntityNotPersistedException {
		Long result = 0l;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery("Comment.getNumCommentsByThread")
					.setParameter(1, newThreadId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					THREAD_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<CommentVoter> getCommentVoters( Long commentId ){
		List<CommentVoter> result = new ArrayList<CommentVoter>();
		try{
			result = (List<CommentVoter>) JPA.getManager()
				.createNamedQuery("Comment.getCommentVoters")
				.setParameter(1, commentId)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public Comment createComment(ForumThread thread, int posIndex,
			String text, String code, User user)
			throws EntityAlreadyPersistedException, EntityNotPersistedException {
		Comment comment = null;
		try{
			comment = new Comment();
			comment.setTextThis(text).setTextHtmlThis(code).setNumVotesThis(0)
				.setPositionIndexThis(posIndex)
				.setUserThis(user).setForumThreadThis(thread)
				.setDateThis(new Date())
				/*.setPostType("TypeComment")*/;
			JPA.getManager().persist(comment);
			JPA.getManager().flush();
			JPA.getManager().refresh(comment);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					COMMENT_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return comment;
	}
	
	public void deleteComment(Comment comm) throws EntityNotPersistedException {
		try{
			/*
			Comment comm = 
					Jpa.getManager().find(Comment.class, comm.getId());
			Jpa.getManager().remove(comm);
			*/
			JPA.getManager()
				.createNamedQuery("Comment.deleteById")
				.setParameter(1, comm.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					COMMENT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void createCommentVoter(Long userId, Long commentId)
			throws EntityAlreadyPersistedException, EntityNotPersistedException {
		try{
			// En lugar de recibir por parametro el User y el Comment,
			// debemos obtener dichas entidades desde la bdd mediante JPA,
			// para evitar que esten en estado 'detatched' al hacer el persist
			User user = JPA.getManager().find(User.class, userId);
			Comment comment = JPA.getManager().find(Comment.class, commentId);
			
			// Generar el nuevo CommentVoter
			CommentVoter commentVoter = new CommentVoter();
			commentVoter.setUserThis(user).setCommentThis(comment);
			
			// Guardar en bdd el CommentVoter
			JPA.getManager().persist(commentVoter);
			JPA.getManager().flush();
			JPA.getManager().refresh(commentVoter);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					COMMENTVOTER_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void updateComment(Comment commentToUpdate)
			throws EntityNotPersistedException {
		try{	
			JPA.getManager().merge( commentToUpdate );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					COMMENT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	public void createQuote(Comment commentToQuote,Comment newComment) 
			throws EntityAlreadyPersistedException, EntityNotPersistedException{
		try{
			// En lugar de recibir por parametro el User y el Comment,
			// debemos obtener dichas entidades desde la bdd mediante JPA,
			// para evitar que esten en estado 'detatched' al hacer el persist
			//Comment quotedComment = JPA.getManager().find(Comment.class, quotedCommentId);
			//Comment newComment = JPA.getManager().find(Comment.class, newCommentId);
			
			CommentQuoteTo quote = new CommentQuoteTo();
			quote.setActorCommentThis(newComment)
				.setQuotedCommentThis(commentToQuote)
				.setQuotedTextThis(commentToQuote.getText())
				.setQuotedTextHtmlThis(commentToQuote.getTextHtml());
			
			JPA.getManager().persist(quote);
			JPA.getManager().flush();
			JPA.getManager().refresh(quote);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					QUOTE_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
