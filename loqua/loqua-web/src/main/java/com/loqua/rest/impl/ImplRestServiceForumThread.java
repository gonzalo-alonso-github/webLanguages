package com.loqua.rest.impl;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.services.ServiceForumThread;
import com.loqua.infrastructure.Factories;
import com.loqua.model.ForumThread;
import com.loqua.presentation.logging.LoquaLogger;
import com.loqua.rest.RestServiceForumThread;

/** Implementa la interfaz {@link RestServiceForumThread}
 * y atiende las peticiones HTTP enviadas por el cliente REST a dicha interfaz
 */
public class ImplRestServiceForumThread implements RestServiceForumThread{

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Servicio {@link ServiceForumThread} que se invoca en los metodos
	 * de esta clase */
	ServiceForumThread service = Factories.getService().getServiceThread();

	@Override
	public List<ForumThread> getAllForumThreadsInLastHour(){
		return service.getAllForumThreadsInLastHour();
	}
	
	@Override
	public void createForumThread(ForumThread forumThread){
		try{
			service.restCreateForumThread(forumThread);
		}catch(EntityAlreadyFoundException e){
			// Noticias con Unique key repetido
			log.error("EntityAlreadyFoundException at 'createForumThread()'");
		}catch(Exception e){
			// Excepcion inesperada
			log.error("Unexpected Exception at 'createForumThread()'");
		}
	}
	
	@Override
	public void createForumThreadsByList(List<ForumThread> threadsToCreate){
		// En una sola peticion REST nos envian toda la lista de threads
		// (es mejor que hacer una peticion REST por cada thread)
		try{
			// Recorre la lista haciendo 1 transaccion create por cada 'thread'
			/* Si en lugar de recorrer aqui la lista la enviasemos al EJB,
			entonces en caso de producirse una excepcion (ej: 'guid' repetido),
			el rollback descartaria todos los threads de dicha lista.
			Pero de esta manera, como al EJB solo se le envia un thread,
			solo se descartaria ese en concreto. */
			for( ForumThread threadToCreate : threadsToCreate ){
				service.restCreateForumThread(threadToCreate);
			}
		}catch(EntityAlreadyFoundException e){
			// Noticias con Unique key repetido
			log.error("EntityAlreadyFoundException at "
					+ "'createForumThreadsByList()'");
		}catch(Exception e){
			log.error("Exception at 'createForumThreadsByList()'");
		}
	}
}
