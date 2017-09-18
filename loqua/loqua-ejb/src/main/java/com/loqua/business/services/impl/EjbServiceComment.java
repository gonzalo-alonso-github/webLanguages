package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceComment;
import com.loqua.business.services.impl.transactionScript.TransactionComment;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceComment;
import com.loqua.business.services.serviceRemote.RemoteServiceComment;
import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.ForumThread;
import com.loqua.model.User;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link Comment} y {@link CommentQuoteTo}. <br/>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br/>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceComment} y {@link RemoteServiceComment},
 * que heredan de {@link ServiceComment}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceComment")
public class EjbServiceComment
		implements LocalServiceComment, RemoteServiceComment {

	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Comment} y {@link CommentQuoteTo},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionComment transactionComment = 
			new TransactionComment();
	
	@Override
	public List<CommentQuoteTo> getCommentsQuotedByComment(Long actorComment) {
		return transactionComment.getCommentsQuotedByComment(actorComment);
	}

	@Override
	public List<Comment> getCommentsByThread(Long newThreadId,
			Integer offset, Integer limitNumComments ){
		return transactionComment.getCommentsByThread(newThreadId,
				offset, limitNumComments );
	}
	
	@Override
	public List<Comment> getCommentsByThreadReverseOrder(Long newThreadId,
			Integer offsetPage, Integer numCommentsPerPage){
		return transactionComment.getCommentsByThreadReverseOrder(newThreadId,
				offsetPage, numCommentsPerPage);
	}

	@Override
	public Integer getNumCommentsByThread(Long threadId) 
			throws EntityNotFoundException{
		return transactionComment.getNumCommentsByThread(threadId);
	}
	
	@Override
	public boolean commentAlreadyVotedByUser(Long userId, Long commentId)
			throws EntityNotFoundException {
		return transactionComment.commentAlreadyVotedByUser(userId, commentId);
	}
	
	@Override
	public Comment voteComment(Long userId, Comment commentToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		return transactionComment.voteComment(userId, commentToVote);
	}
	
	@Override
	public Comment sendComment(ForumThread thread, String text, String code,
			User user)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		return transactionComment.sendComment(thread, text, code, user);
	}
	
	@Override
	public void deleteComment(Comment comment) throws EntityNotFoundException {
		transactionComment.deleteComment(comment);
	}
	
	@Override
	public void updateTextComment(Comment commentToUpdate, String text,
			String code) throws EntityNotFoundException {
		transactionComment.updateTextComment(commentToUpdate, text, code);
	}
	
	@Override
	public Comment quoteComment(Comment commentToQuote, String text,
			String code, User user)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		return transactionComment.quoteComment(commentToQuote,text,code,user);
	}
}
