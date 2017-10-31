package com.loqua.remote.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.loqua.model.Feed;

/** Proxy REST a traves del cual esta aplicacion cliente envia peticiones
 * a la interfaz remota RestServiceFeed
 * (implementada en el proyecto 'loqua-web'). <br>
 * Mediante las anotaciones de JAX-RS de cada metodo, la especificacion
 * RESTEasy podra convertir las llamadas a ellos en peticiones HTTP
 * dirigidas a la URL que se haya especificado.
 */
@Path("/RestServiceFeed")
public interface RestServiceFeed {
	
	/**
	 * Realiza una peticion HTTP al metodo 'getFeedById' del servicio remoto
	 * 'RestServiceFeed' (implementado en el proyecto 'loqua-web').
	 * @return el objeto Feed (fuente de noticias) con el identificador indicado
	 */
	@GET
	@Path("getFeedById/{feedId}")
	@Produces({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	Feed getFeedById(@PathParam("feedId") Long feedId);
	
	/**
	 * Realiza una peticion HTTP al metodo 'getAllFeeds' del servicio remoto
	 * 'RestServiceFeed' (implementado en el proyecto 'loqua-web').
	 * @return todos los objetos Feed disponibles (fuentes de noticias)
	 */
	@GET
	@Path("getAllFeeds")
	@Produces({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	List<Feed> getAllFeeds();
}