package com.loqua.business.services.impl.transactionScript;

import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.CommentVoter;
import com.loqua.model.ForumThread;
import com.loqua.model.User;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.CommentJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionComment {
	
	private static final CommentJPA commentJPA = new CommentJPA();
	private static final TransactionUser transactionUser = 
			new TransactionUser();
	private static final TransactionPublication transactionPub = 
			new TransactionPublication();
	private static final TransactionThread transactionThread = 
			new TransactionThread();
	
	public List<CommentQuoteTo> getCommentsQuotedByComment(Long actorComment){
		return commentJPA.getCommentsQuotedByComment(actorComment);
	}

	public List<Comment> getCommentsByThread(Long newThreadId,
			Integer offsetComments, Integer numCommentsPerPage){
		List<Comment> result = null;
		offsetComments=(offsetComments==null || offsetComments==0)? 
				0 : (offsetComments-1)*numCommentsPerPage;
		result = commentJPA.getCommentsByThread(
				newThreadId, offsetComments, numCommentsPerPage);
		return result;
	}
	
	public List<Comment> getCommentsByThreadReverseOrder(Long newThreadId,
			Integer offsetComments, Integer numCommentsPerPage){
		List<Comment> result = null;
		offsetComments=(offsetComments==null || offsetComments==0)? 
				0 : (offsetComments-1)*numCommentsPerPage;
		result = commentJPA.getCommentsByThreadReverseOrder(
				newThreadId, offsetComments, numCommentsPerPage);
		return result;
	}

	public Integer getNumCommentsByThread(Long newThreadId)
			throws EntityNotFoundException {
		Integer result = 0;
		try{
			result = commentJPA.getNumCommentsByThread(newThreadId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public List<CommentVoter> getCommentVoters(Long commentId) {
		return commentJPA.getCommentVoters(commentId);
	}
	
	public boolean commentAlreadyVotedByUser(Long userId, Long commentId){
		List<CommentVoter> commentVoters = getCommentVoters(commentId);
		for( CommentVoter commentVoter : commentVoters ){
			if( commentVoter.getUser().getId() == userId ) return true;
		}
		return false;
	}
	
	public Comment voteComment(Long userId, Comment commentToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			// Primero: actualiza en bdd el Comment (numero de votos)
			commentToVote.setNumVotes( commentToVote.getNumVotes()+1 );
			commentJPA.updateComment( commentToVote );
			
			// Segundo: genera y guarda en bdd el nuevo CommentVoter:
			commentJPA.createCommentVoter( userId, commentToVote.getId() );
			
			// Ahora se actualizan los puntos del usuario:
			transactionUser.incrementCommentVotes(commentToVote.getUser());
			// Y, si corresponde, se crean las Publication necesarias:
			generatePublicationsToCommentVote(commentToVote);
			
			// Por ultimo devuelve el Comment editado:
			return commentToVote;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	private void generatePublicationsToCommentVote(Comment comment)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		User userComment = comment.getUser();
		TypePrivacity privacity=userComment.getPrivacityData().getPublications();
		if( comment.getNumVotes()==1 ){
			// si es el primer voto en este comentario, genera publicacion:
			transactionPub.generatePublication(
					privacity, comment.getId() ,102L, userComment);
		}
		Integer totalVotes = userComment.getUserInfo().getCountVotesComments();
		if( totalVotes%100 == 0 ){
			// si los votos recibidos en todos los comments son multiplo de 100:
			transactionPub.generatePublication(
					privacity, totalVotes.longValue(), 3L, userComment);
		}
		// si el autor llega al top-100/50/25/20/15/10/5... tambien la genera:
		transactionUser.generatePublicationForTopUsers( userComment );
	}
	
	public Comment sendComment(ForumThread thread, String text, String code,
			User user)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			int posIndex = getNumCommentsByThread(thread.getId())+1;
			Comment comment = commentJPA.createComment(
					thread, posIndex, text, code, user);
			// Se incrementa el numero de comentarios de la noticia:
			transactionThread.incrementCountComments(thread);
			// Se actualiza la fecha de 'dateLastComment' de la noticia:
			thread.setDateLastComment(new Date());
			transactionThread.updateDataByThread(thread, true);
			// Se incrementan los puntos del usuario:
			transactionUser.incrementCountComments(user);
			// Y, si corresponde, se crean las Publication necesarias:
			generatePublicationsToSendComment(comment);
			return comment;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	private void generatePublicationsToSendComment(Comment comment)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		User userComment = comment.getUser();
		TypePrivacity privacity=userComment.getPrivacityData().getPublications();
		
		Integer numComments = userComment.getUserInfo().getCountComments();
		if( reachedXComments(numComments) ){
			// comprueba si ese numero ha sido alcanzado otras veces anteriores
			// (puede darse si habia borrado comentarios)
			if( transactionPub.achievementNumCommentsAlreadyPassedByUser(
					numComments.longValue(), 1L, userComment.getId()) ){
				// en cuyo caso no genera la publicacion:
				return;
			}// else
			// si el logro aun no habia sido alcanzado, genera la publicacion:
			transactionPub.generatePublication(
					privacity, numComments.longValue(), 1L, userComment);
		}
		// ya se han incrementado los puntos del usuario. Y ahora ademas,
		// si el autor llega al top-100/50/25/20/15/10/5... genera publicacion:
		transactionUser.generatePublicationForTopUsers( userComment );
	}

	private boolean reachedXComments(Integer numComments) {
		// si el num de comentarios es 50/100/500/1000/... devuelve 'true':
		int reduction = 0;
		if( numComments >= 50 ){
			String numCommentsString = numComments.toString();
			// exp. regular para uno o mas ceros al final de un numero: [0]+$
			numCommentsString = numCommentsString.replaceAll("[0]+$", "");
			reduction = Integer.parseInt(numCommentsString);
			if( reduction==1 || reduction==5 ){
				return true;
			}
		}
		return false;
	}
	
	public void deleteComment(Comment comm) throws EntityNotFoundException {
		try {
			transactionPub.editPubsForDeletedPost(comm);
			commentJPA.deleteComment(comm);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void updateTextComment(Comment commentToUpdate, String text,
			String code) throws EntityNotFoundException {
		try {
			commentToUpdate.setTextThis(text).setTextHtmlThis(code);
			commentJPA.updateComment(commentToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	public Comment quoteComment(
			Comment commentToQuote, String text, String code, User user)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			// Se crea el Comment llamando a sendComment:
			Comment newComment = sendComment(
					commentToQuote.getForumThread(), text, code, user);
			// Se crea el CommentQuoteTo (la relacion de 'cita' entre Comments):
			commentJPA.createQuote(commentToQuote, newComment);
			// Se crea la Publication necesaria:
			generatePublicationToQuoteComm(commentToQuote, user);
			return newComment;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	private void generatePublicationToQuoteComm(
			Comment commentToQuote, User user)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		// comprobar  si el usuario esta citando un comentario de si mismo
		// (eso se permite), en cuyo caso no se genera la publicacion:
		if( ! user.equals( commentToQuote.getUser() ) ){
			transactionPub.generatePublication(TypePrivacity.PRIVATE,
					commentToQuote.getId(), 103L, commentToQuote.getUser());
		}
	}
}
