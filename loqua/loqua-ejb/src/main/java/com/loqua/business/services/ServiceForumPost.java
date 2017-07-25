package com.loqua.business.services;

import com.loqua.model.ForumPost;

public interface ServiceForumPost {
	
	ForumPost getForumPostById(Long phorumPostId);
	
	//List<PhorumPost> getForumPostsByUser(User user);
}