package com.loqua.scheduler;

import java.util.List;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.RedirectionException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.loqua.crawler.Gatherer;
import com.loqua.remote.RestTarget;
import com.loqua.model.ForumThread;
import com.loqua.remote.services.RestServiceForumThread;

/**
 * Representa la tarea que es realizada cada vez que el componente
 * {@link SchedulerCrawler} lo determina. En este caso, la tarea es 
 * descargar las noticias de las fuentes, procesarlas y enviarlas al
 * servidor REST para que sean guardadas en la base de datos.
 * */
public class JobCrawler implements Job{

	/** Invoca al componente {@link Gatherer} para descargar, procesar
	 * y guardar las noticias de las fuentes. <br>
	 * Implementa el metodo 'execute' de la interfaz Job de la API Quartz.
	 * @param context contexto de ejecucion del Job programado
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try{
			Gatherer gatherer = new Gatherer();
			List<ForumThread> forumThreads = gatherer.downloadAllNews();
			createForumThreads(forumThreads);
		}catch( Exception e ){
			throw new JobExecutionException(e);
		}
	}

	/**
	 * Invoca al cliente REST para agregar al sistema todos los objetos
	 * {@link ForumThread} indicados (nuevos hilo del foro).
	 * @param forumThreads lista de hilos del foro que se desean enviar al
	 * servidor REST remoto para guardarlos en la base de datos
	 * @throws RedirectionException
	 * @throws NotAuthorizedException
	 */
	private void createForumThreads(List<ForumThread> forumThreads)
			throws RedirectionException, NotAuthorizedException{
		RestServiceForumThread serviceThread =
				RestTarget.getServiceForumThreadStatic();
		/* La siguiente orden hace una peticion REST a la aplicacion web.
		Si la peticion no pasa el FilterREST de la aplicacion web,
		este lanzara RedirectionException o NotAuthorizedException,
		en cuyo caso se desea que este programa termine */
		serviceThread.createForumThreadsByList(forumThreads);
	}
}
