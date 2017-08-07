package com.loqua.remote.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.loqua.remote.model.Feed;

@Path("/RestServiceFeed")
public interface RestServiceFeed {
	
	@GET
	@Path("getAllFeeds")
	@Produces({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	List<Feed> getAllFeeds();
}