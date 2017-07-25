package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.ForumThread;
import com.loqua.model.User;

public interface ServiceComment {
	
	List<CommentQuoteTo> getCommentsQuotedByComment(Long actorComment);
	
	List<Comment> getCommentsByThread(
			Long newThreadId, Integer offsetPage, Integer numCommentsPerPage);
	List<Comment> getCommentsByThreadReverseOrder(
			Long newThreadId, Integer offsetPage, Integer numCommentsPerPage);

	Integer getNumCommentsByThread(Long newThreadId)
			throws EntityNotFoundException;
	
	boolean commentAlreadyVotedByUser(Long userId, Long commentId)
			throws EntityNotFoundException;
	
	Comment voteComment(Long userId, Comment commentToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	Comment sendComment(ForumThread thread, String text, String code, User user)
			throws EntityAlreadyFoundException, EntityNotFoundException;

	void deleteComment(Comment comment) throws EntityNotFoundException;
	
	void updateTextComment(Comment commentToUpdate, String plainTextComment,
			String textCodeComment) throws EntityNotFoundException;

	Comment quoteComment(Comment commentToQuote, String plainTextComment,
			String textCodeComment, User loggedUser)
			throws EntityAlreadyFoundException, EntityNotFoundException;
}