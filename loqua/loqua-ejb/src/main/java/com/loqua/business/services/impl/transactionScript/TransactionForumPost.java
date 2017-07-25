package com.loqua.business.services.impl.transactionScript;

import com.loqua.model.ForumPost;
import com.loqua.persistence.ForumPostJPA;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionForumPost {

	private static final ForumPostJPA forumPostJPA = new ForumPostJPA();
	
	public ForumPost getForumPostByID(Long forumPostId){
		ForumPost result = new ForumPost();
		try{
			result = forumPostJPA.getForumPostByID(forumPostId);
		}catch (EntityNotPersistedException ex) {
			return null;
		}
		return result;
	}
	/*
	public List<ForumPost> getForumPostsByUser(User user){
		return forumPostJPA.getForumPostsByUser(user);
	}
	*/
}
