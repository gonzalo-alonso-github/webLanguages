package com.loqua.crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.RedirectionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.loqua.scheduler.SchedulerCrawler;

import com.loqua.logging.LoquaLogger;
import com.loqua.remote.RestTarget;
import com.loqua.model.Feed;
import com.loqua.model.ForumThread;
import com.loqua.remote.services.RestServiceFeed;
import com.loqua.remote.services.RestServiceForumThread;;

/**
 * Se encarga de descargar la noticias de las fuentes (que almacena en un
 * {@link NodeList}) y de invocar al componente {@link Parser}
 * para procesarlas y devolverlas como objetos {@link ForumThread}.
 * @author Gonzalo
 */
public class Gatherer {
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Gatherer(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Descarga y procesa las noticias, convirtiendolas en objetos
	 * {@link ForumThread} y generando una lista de ellos
	 * @return lista de hilos del foro obtenidos a partir de las noticias
	 * descargadas
	 * @throws Exception
	 */
	public List<ForumThread> downloadAllNews()
			throws Exception{
		List<Feed> feeds = requestAllFeeds();
		List<ForumThread> threadsParsedInLastHour
			= requestThreadsParsedInLastHour();
		return processRssByListOfFeeds(feeds, threadsParsedInLastHour);
	}

	/**
	 * Invoca al cliente REST para hallar la lista de fuentes de noticias.
	 * @return lista de los objetos {@link Feed} disponibles
	 * (fuentes de noticias)
	 * @throws RedirectionException
	 * @throws NotAuthorizedException
	 */
	private List<Feed> requestAllFeeds()
			throws RedirectionException, NotAuthorizedException{
		RestServiceFeed serviceFeed = RestTarget.getServiceFeedStatic();
		/* La siguiente orden hace una peticion REST a la aplicacion web.
		Si la peticion no pasa el FilterREST de la aplicacion web,
		este lanzara RedirectionException o NotAuthorizedException,
		en cuyo caso se desea que este programa termine */
		return serviceFeed.getAllFeeds();
	}
	
	/**
	 * Invoca al cliente REST para hallar la lista de hilos del foro
	 * que han sido creados en la ultima hora (es decir, en la anterior
	 * ejecucion programada por el {@link SchedulerCrawler}
	 * @return lista de los objetos ForumThread
	 * que han sido creados en la ultima hora
	 * @throws RedirectionException
	 * @throws NotAuthorizedException
	 */
	private List<ForumThread> requestThreadsParsedInLastHour()
			throws RedirectionException, NotAuthorizedException{
		RestServiceForumThread serviceThread =
				RestTarget.getServiceForumThreadStatic();
		/* La siguiente orden hace una peticion REST a la aplicacion web.
		Si la peticion no pasa el FilterREST de la aplicacion web,
		este lanzara RedirectionException o NotAuthorizedException,
		en cuyo caso se desea que este programa termine */
		return serviceThread.getAllForumThreadsInLastHour();
	}
	
	/**
	 * Decarga y procesa todas las noticias de la lista de fuentes
	 * ({@link Feed}) indicada, invocando al componente {@link Parser}
	 * para convertirlas en objetos {@link ForumThread}
	 * @param allFeeds lista de todas las fuentes de las que se
	 * deben descargar las noticias
	 * @param threadsParsedInLastJob lista de hilos del foro creados en
	 * la ultima hora (en la anterior ejecucion programada por el
	 * {@link SchedulerCrawler}. Se utiliza para comprobar que no se procesan
	 * noticias repetidas
	 * @return lita de objetos ForumThread que se acaban de obtener a partir
	 * de los datos descargados de las fuentes de noticias
	 * @throws Exception
	 */
	private List<ForumThread> processRssByListOfFeeds(
			List<Feed> allFeeds, List<ForumThread> threadsParsedInLastJob)
			throws Exception{
		List<ForumThread> allThreads = new ArrayList<ForumThread>();
		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Parser parser = new Parser(threadsParsedInLastJob);
		for( Feed feed : allFeeds ){
			int numAllParsedThreads = allThreads.size();
			URL urlFeed = new URL(feed.getUrl());
			// Se obtiene la respuesta xml, es decir los datos publicados
			// por la fuente:
			Document doc = builder.parse(urlFeed.openStream());
			// Se obtienen los elementos 'item', es decir las noticias
			// de la fuente:
			NodeList newsOfFeed = doc.getElementsByTagName("item");
			parser.setFeed(feed);
			parser.parseRawNewsOfFeed( newsOfFeed );
			allThreads = parser.getThreadsParsedInCurrentJob();
			int numParsedThreadsOfFeed = allThreads.size()-numAllParsedThreads;
			// Realmente nunca sera menor que cero, pero si puede ser igual:
			if( numParsedThreadsOfFeed<=0 ){
				// Entonces el actual Feed no ha generado noticias esta hora
				String msg = "The Feed '" + feed.getName() + "' "
						+ "has not generated any new Thread at this job";
				log.info("'processRssByListOfFeeds()': " + msg);
			}
		}
		if( allThreads.isEmpty() ){
			// Entonces ningun Feed ha generado noticias esta hora
			String msg="None of the Feeds has generated new Threads at this job";
			log.warn("'processRssByListOfFeeds()': " + msg);
		}
		return allThreads;
	}
}
