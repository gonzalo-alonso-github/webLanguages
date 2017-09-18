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
import com.loqua.rest.impl.ImplRestServiceFeed;
import com.loqua.business.services.ServiceFeed;

/** Proxy REST a traves del cual esta aplicacion cliente recibe peticiones
 * que seran atendidas por la implementacion de esta interfaz
 * ({@link ImplRestServiceFeed}). <br/>
 * Mediante las anotaciones de JAX-RS de cada metodo, la especificacion
 * RESTEasy podra convertir las llamadas a ellos en peticiones HTTP
 * dirigidas a la URL indicada en el fichero 'web.xml'
 */
@Stateless
@Path("/RestServiceFeed")
public interface RestServiceFeed {
	
	/**
	 * Invoca al metodo 'getAllFeeds' del servicio {@link ServiceFeed}
	 * (implementado en el proyecto 'loqua-ejb').
	 * @return todos los objetos Feed disponibles (fuentes de noticias)
	 */
	@GET
	@Path("getAllFeeds")
	@Produces({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	List<Feed> getAllFeeds();
}