package com.loqua.rest;

import java.util.List;

import javax.ejb.Stateless;
//import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.loqua.model.Feed;

@Stateless
@Path("/RestServiceFeed")
public interface RestServiceFeed {
	
	@GET
	@Path("getAllFeeds")
	@Produces({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	List<Feed> getAllFeeds();
}