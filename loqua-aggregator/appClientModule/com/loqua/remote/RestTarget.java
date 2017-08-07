package com.loqua.remote;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.loqua.remote.services.RestServiceFeed;
import com.loqua.remote.services.RestServiceForumThread;

public class RestTarget {

	private static RestServiceForumThread serviceForumThread;
	private static RestServiceFeed serviceFeed;

	// // // //
	// METODOS
	// // // //
	
	public static void initializeRestServices(String url, String key) {
		Client client = ClientBuilder.newClient();
		ResteasyWebTarget target = (ResteasyWebTarget) client.target(url);
		client.register(new FilterAddAuthHeadersRequest(key));
		serviceForumThread = target.proxy(RestServiceForumThread.class);
		serviceFeed = target.proxy(RestServiceFeed.class);
	}
	
	// // // //
	// GETTERS
	// // // //
	
	public static RestServiceForumThread getServiceForumThreadStatic() {
		return serviceForumThread;
	}

	public static RestServiceFeed getServiceFeedStatic() {
		return serviceFeed;
	}
}
