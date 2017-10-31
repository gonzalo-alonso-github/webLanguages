package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.CommentVoter;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Comment} y {@link CommentQuoteTo}
 * @author Gonzalo
 */
public class CommentJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Comment'
	 * en la base de datos */
	private static final String COMMENT_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Comment' entity not found"
			+ " at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad 'Comment'
	 * en la base de datos */
	private static final String COMMENT_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Comment' entity already"
			+ " found at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Thread'
	 * en la base de datos */
	private static final String THREAD_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Thread' entity not found"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'CommentVoter' en la base de datos */
	private static final String COMMENTVOTER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'CommentVoter' entity already"
			+ " found at Persistence layer";
	
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'CommentQuote' en la base de datos */
	private static final String QUOTE_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'CommentQuote' entity already"
			+ " found at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Comment.getCommentsQuotedByComment'
	 * @param actorComment filtro de busqueda para el atributo homonimo de
	 * CommentQuoteTo
	 * @return lista de CommentQuoteTo cuyo atributo 'actorComment'
	 * coincide con el parametro recibido
	 */
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

	/**
	 * Realiza la consulta JPQL 'Comment.getCommentsByThread'
	 * @param newThreadId atributo 'id' del ForumThread al que pertenecen los
	 * Comment que se consultan
	 * @param offsetComments offset de los Comments devueltos
	 * @param limitNumComments limite maximo de Comment devueltos
	 * @return lista de Comment que pertenecen al ForumThread dado, ordenados
	 * ascendentemente por su atributo 'date' hetedado de ForumPost
	 */
	@SuppressWarnings("unchecked")
	public List<Comment> getCommentsByThread(Long newThreadId,
			Integer offsetComments, Integer limitNumComments){
		List<Comment> result = new ArrayList<Comment>();
		try{
			result = (List<Comment>) JPA.getManager()
				.createNamedQuery("Comment.getCommentsByThread")
				.setParameter(1, newThreadId)
				.setFirstResult(offsetComments) // offset
				.setMaxResults(limitNumComments) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Comment.getCommentsByThreadReverseOrder'
	 * @param newThreadId atributo 'id' del ForumThread al que pertenecen los
	 * Comment que se consultan
	 * @param offsetComments offset de los Comments devueltos
	 * @param limitNumComments limite maximo de Comment devueltos
	 * @return lista de Comment que pertenecen al ForumThread dado, ordenados
	 * descendentemente por su atributo 'date' hetedado de ForumPost
	 */
	@SuppressWarnings("unchecked")
	public List<Comment> getCommentsByThreadReverseOrder(Long newThreadId,
			Integer offsetComments, Integer limitNumComments){
		List<Comment> result = new ArrayList<Comment>();
		try{
			result = (List<Comment>) JPA.getManager()
				.createNamedQuery("Comment.getCommentsByThreadReverseOrder")
				.setParameter(1, newThreadId)
				.setFirstResult(offsetComments) // offset
				.setMaxResults(limitNumComments) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}	
	
	/**
	 * Realiza la consulta JPQL 'Comment.getNumCommentsByThread'
	 * @param newThreadId  atributo 'id' del ForumThread al que pertenecen los
	 * Comment que se consultan
	 * @return cantidad de Comment que pertenecen al ForumThread dado
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Comment.getCommentVoters'
	 * @param commentId atributo 'id' del Comment al que pertenecen los
	 * CommentVoter que se consultan
	 * @return lista de CommentVoter asociados al Comment dado
	 */
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
	
	/**
	 * Genera un objeto Comment a partir de los parametros recibidos
	 * y lo agrega a la base de datos
	 * @param comment el Comment que se va a agregar a la base de datos
	 * @return el Comment agregado a la base de datos
	 * @throws EntityAlreadyPersistedException
	 * @throws EntityNotPersistedException
	 */
	public Comment createComment(Comment comment)
			throws EntityAlreadyPersistedException, EntityNotPersistedException {
		try{
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
	
	/**
	 * Elimina de la base de datos el objeto Comment dado
	 * @param comm objeto Comment que se desea eliminar
	 * @throws EntityNotPersistedException
	 */
	public void deleteComment(Comment comm) throws EntityNotPersistedException {
		try{
			/* Comment comm = 
					Jpa.getManager().find(Comment.class, comm.getId());
			Jpa.getManager().remove(comm); */
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
	
	/**
	 * Genera un objeto CommentVoter a partir de los parametros recibidos
	 * y lo agrega a la base de datos
	 * @param userId atributo 'id' del User autor del CommentVoter que se genera
	 * @param commentId atributo 'id' del Comment asociado al CommentVoter
	 * que se genera
	 * @throws EntityAlreadyPersistedException
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Actualiza en la base de datos el objeto Comment dado
	 * @param commentToUpdate objeto Comment que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Genera un objeto CommentQuoteTo a partir de los parametros recibidos
	 * y lo agrega a la base de datos
	 * @param commentToQuote atributo 'quotedComment' del CommentQuoteTo
	 * que se genera (es decir: comentario que queda citado)
	 * @param newComment atributo 'actorComment' del CommentQuoteTo
	 * que se genera (es decir: comentario que realiza la cita)
	 * @throws EntityAlreadyPersistedException
	 */
	public void createQuote(Comment commentToQuote,Comment newComment) 
			throws EntityAlreadyPersistedException {
		try{
			/* Comment commentToQuote =
				JPA.getManager().find(Comment.class, commentToQuoteId);
			Comment newComment = 
				JPA.getManager().find(Comment.class, newCommentId); */
			
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
