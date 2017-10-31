package com.loqua.rest.impl;

import java.util.List;

import com.loqua.business.services.ServiceFeed;
import com.loqua.infrastructure.Factories;
import com.loqua.model.Feed;
import com.loqua.presentation.logging.LoquaLogger;
import com.loqua.rest.RestServiceFeed;

/** Implementa la interfaz {@link RestServiceFeed}
 * y atiende las peticiones HTTP enviadas por el cliente REST a dicha interfaz
 */
public class ImplRestServiceFeed implements RestServiceFeed{

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Servicio {@link ServiceFeed} que se invoca en los metodos
	 * de esta clase */
	ServiceFeed service = Factories.getService().getServiceFeed();
	
	@Override
	public Feed getFeedById(Long feedId){
		Feed result = null;
		try{
			result = service.getFeedByID(feedId);
		}catch(Exception e){
			// Excepcion inesperada
			log.error("Unexpected Exception at 'getFeedById()'");
		}
		return result;
	}
	
	@Override
	public List<Feed> getAllFeeds(){
		return service.restGetAllFeeds();
	}
}
