package com.loqua.business.services.impl;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.services.impl.transactionScript.TransactionForumPost;
import com.loqua.business.services.serviceLocal.LocalServiceForumPost;
import com.loqua.business.services.serviceRemote.RemoteServiceForumPost;
import com.loqua.model.ForumPost;

@Stateless
@WebService(name="ServiceForumPost")
public class EjbServiceForumPost
		implements LocalServiceForumPost, RemoteServiceForumPost {

	private static final TransactionForumPost transactionForumPost = 
			new TransactionForumPost();
	
	@Override
	public ForumPost getForumPostById(Long forumPostId){
		return transactionForumPost.getForumPostByID(forumPostId);
	}
	/*
	@Override
	public List<ForumPost> getForumPostsByUser(User user) {
		return transactionForumPost.getForumPostsByUser(user);
	}
	*/
}
