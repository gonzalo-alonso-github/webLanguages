package com.loqua.remote.services;

import java.util.List;

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

import com.loqua.model.ForumThread;

/** Proxy REST a traves del cual esta aplicacion cliente envia peticiones
 * a la interfaz remota RestServiceForumThread
 * (implementada en el proyecto 'loqua-web'). <br>
 * Mediante las anotaciones de JAX-RS de cada metodo, la especificacion
 * RESTEasy podra convertir las llamadas a ellos en peticiones HTTP
 * dirigidas a la URL que se haya especificado.
 */
@Path("/RestServiceForumThread")
public interface RestServiceForumThread {
	
	/**
	 * Realiza una peticion HTTP al metodo 'getAllForumThreadGUIDsInLastHour'
	 * del servicio remoto 'RestServiceForumThread'
	 * (implementado en el proyecto 'loqua-web').
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
	 * <br> Realiza una peticion HTTP al metodo 'createForumThread'
	 * del servicio remoto 'RestServiceForumThread'
	 * (implementado en el proyecto 'loqua-web').
	 * @param forumThread objeto ForumThread que se agrega
	 */
	@PUT
	@Path("createForumThread")
	@Consumes({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	void createForumThread(ForumThread forumThread);

	/**
	 * Agrega al sistema todos los objetos ForumThread indicados
	 * (nuevos hilos del foro).
	 * <br> Realiza una peticion HTTP al metodo 'createForumThreadsByList'
	 * del servicio remoto 'RestServiceForumThread'
	 * (implementado en el proyecto 'loqua-web').
	 * @param forumThreads lista de objetos ForumThread que se agregan
	 */
	@PUT
	@Path("createForumThreadByList")
	@Consumes({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	void createForumThreadsByList(List<ForumThread> forumThreads);
	
	/**
	 * Elimina del sistema todos los objetos ForumThread indicados
	 * (hilos del foro).
	 * <br> Realiza una peticion HTTP al metodo 'deleteForumThreadsByList'
	 * del servicio remoto 'RestServiceForumThread'
	 * (implementado en el proyecto 'loqua-web').
	 * @param forumThreads lista de objetos ForumThread que se eliminan
	 */
	@DELETE
	@Path("deleteForumThreadByList")
	@Consumes({ 
		MediaType.APPLICATION_XML + ";charset=UTF-8",
		MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	void deleteForumThreadsByList(List<ForumThread> forumThreads);
}