package com.loqua.rest.impl;

import java.util.List;

import com.loqua.business.services.ServiceFeed;
import com.loqua.infrastructure.Factories;
import com.loqua.model.Feed;
import com.loqua.rest.RestServiceFeed;

public class ImplRestServiceFeed implements RestServiceFeed{

	ServiceFeed service = Factories.getService().getServiceFeed();

	@Override
	public List<Feed> getAllFeeds(){
		return service.restGetAllFeeds();
	}
}
