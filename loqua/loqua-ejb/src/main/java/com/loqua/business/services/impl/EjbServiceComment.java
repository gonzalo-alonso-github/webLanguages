package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionComment;
import com.loqua.business.services.serviceLocal.LocalServiceComment;
import com.loqua.business.services.serviceRemote.RemoteServiceComment;
import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.ForumThread;
import com.loqua.model.User;

@Stateless
@WebService(name="ServiceComment")
public class EjbServiceComment
		implements LocalServiceComment, RemoteServiceComment {

	private static final TransactionComment transactionComment = 
			new TransactionComment();
	
	@Override
	public List<CommentQuoteTo> getCommentsQuotedByComment(Long actorComment) {
		return transactionComment.getCommentsQuotedByComment(actorComment);
	}

	@Override
	public List<Comment> getCommentsByThread(Long newThreadId,
			Integer offsetPage, Integer numCommentsPerPage){
		return transactionComment.getCommentsByThread(newThreadId,
				offsetPage, numCommentsPerPage);
	}
	
	@Override
	public List<Comment> getCommentsByThreadReverseOrder(Long newThreadId,
			Integer offsetPage, Integer numCommentsPerPage){
		return transactionComment.getCommentsByThreadReverseOrder(newThreadId,
				offsetPage, numCommentsPerPage);
	}

	@Override
	public Integer getNumCommentsByThread(Long newThreadId) 
			throws EntityNotFoundException{
		return transactionComment.getNumCommentsByThread(newThreadId);
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
