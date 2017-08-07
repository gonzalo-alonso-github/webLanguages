package com.loqua.rest.impl;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.services.ServiceForumThread;
import com.loqua.infrastructure.Factories;
import com.loqua.model.ForumThread;
import com.loqua.rest.RestServiceForumThread;

public class ImplRestServiceForumThread implements RestServiceForumThread{

	ServiceForumThread service = Factories.getService().getServiceThread();

	@Override
	public List<ForumThread> getAllForumThreadGUIDsInLastHour(){
		return service.getAllForumThreadGUIDsInLastHour();
	}
	
	@Override
	public void createForumThread(ForumThread forumThread){
		try{
			service.restCreateForumThread(forumThread, true);
		}catch(EntityAlreadyFoundException e){
			// TODO Log: noticias con Unique key repetido
		}catch(Exception e){
			// TODO Log: excepcion inesperada
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
			Pero de esta manera, como al EJB solo le enviamos un thread,
			solo se descartaria ese en concreto. */
			for( ForumThread threadToCreate : threadsToCreate ){
				service.restCreateForumThread(threadToCreate, true);
			}
		}catch(EntityAlreadyFoundException e){
			// TODO Log: noticias con Unique key repetido
		}catch(Exception e){
			// TODO Log: excepcion inesperada
		}
	}
}
