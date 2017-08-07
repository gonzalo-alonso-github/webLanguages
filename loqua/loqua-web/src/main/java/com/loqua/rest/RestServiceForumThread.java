package com.loqua.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
//import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.loqua.model.ForumThread;

@Stateless
@Path("/RestServiceForumThread")
public interface RestServiceForumThread {
	
	@GET
	@Path("getAllForumThreadGUIDsInLastHour")
	@Produces({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	List<ForumThread> getAllForumThreadGUIDsInLastHour();
	
	@PUT
	@Path("createForumThread")
	@Consumes({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	void createForumThread(ForumThread forumThread);
	
	@PUT
	@Path("createForumThreadByList")
	@Consumes({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	void createForumThreadsByList(List<ForumThread> forumThreads);
}