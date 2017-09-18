package com.loqua.rest.impl;

import java.util.List;

import com.loqua.business.services.ServiceFeed;
import com.loqua.infrastructure.Factories;
import com.loqua.model.Feed;
import com.loqua.rest.RestServiceFeed;

/** Implementa la interfaz {@link RestServiceFeed}
 * y atiende las peticiones HTTP enviadas por el cliente REST a dicha interfaz
 */
public class ImplRestServiceFeed implements RestServiceFeed{

	/** Servicio {@link ServiceFeed} que se invoca en los metodos
	 * de esta clase */
	ServiceFeed service = Factories.getService().getServiceFeed();

	@Override
	public List<Feed> getAllFeeds(){
		return service.restGetAllFeeds();
	}
}
