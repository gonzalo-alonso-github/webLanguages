package com.loqua.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
//import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
//import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.loqua.business.services.ServiceForumThread;
import com.loqua.model.ForumThread;
import com.loqua.rest.impl.ImplRestServiceForumThread;

/** Proxy REST a traves del cual esta aplicacion cliente recibe peticiones
 * que seran atendidas por la implementacion de esta interfaz
 * ({@link ImplRestServiceForumThread}). <br>
 * Mediante las anotaciones de JAX-RS de cada metodo, la especificacion
 * RESTEasy podra convertir las llamadas a ellos en peticiones HTTP
 * dirigidas a la URL indicada en el fichero 'web.xml'
 */
@Stateless
@Path("/RestServiceForumThread")
public interface RestServiceForumThread {
	
	/**
	 * Invoca al metodo 'getAllForumThreadGUIDsInLastHour'
	 * del servicio {@link ServiceForumThread}
	 * (implementado en el proyecto 'loqua-ejb').
	 * @return lista de hilos del foro que han sido creados en la ultima hora
	 */
	@GET
	@Path("getAllForumThreadGUIDsInLastHour")
	@Produces({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	List<ForumThread> getAllForumThreadsInLastHour();
	
	/**
	 * Agrega al sistema el objeto ForumThread indicado (nuevo hilo del foro).
	 * <br>
	 * Invoca al metodo 'createForumThread'
	 * del servicio {@link ServiceForumThread}
	 * (implementado en el proyecto 'loqua-ejb').
	 * @param forumThread objeto ForumThread que se agrega
	 */
	@PUT
	@Path("createForumThread")
	@Consumes({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	void createForumThread(ForumThread forumThread);
	
	/**
	 * Agrega al sistema los objetos ForumThread indicados
	 * (nuevos hilos del foro).<br>
	 * Invoca al metodo 'createForumThreadsByList'
	 * del servicio {@link ServiceForumThread}
	 * (implementado en el proyecto 'loqua-ejb').
	 * @param forumThreads lista de objetos ForumThread que se agregan
	 */
	@PUT
	@Path("createForumThreadByList")
	@Consumes({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	void createForumThreadsByList(List<ForumThread> forumThreads);
	
	/**
	 * Elimina del sistema los objetos ForumThread indicados
	 * (nuevos hilos del foro).<br>
	 * Invoca al metodo 'createForumThreadsByList'
	 * del servicio {@link ServiceForumThread}
	 * (implementado en el proyecto 'loqua-ejb').
	 * @param forumThreads lista de objetos ForumThread que se agregan
	 */
	@DELETE
	@Path("deleteForumThreadByList")
	@Consumes({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	void deleteForumThreadsByList(List<ForumThread> forumThreads);
}