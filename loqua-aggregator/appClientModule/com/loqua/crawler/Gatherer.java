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

import com.loqua.logging.LoquaLogger;
import com.loqua.remote.RestTarget;
import com.loqua.remote.model.Feed;
import com.loqua.remote.model.ForumThread;
import com.loqua.remote.services.RestServiceFeed;
import com.loqua.remote.services.RestServiceForumThread;;

public class Gatherer {
	
	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Gatherer(){}
	
	// // // //
	// METODOS
	// // // //
	
	public List<ForumThread> downloadAllNews()
			throws Exception{
		List<Feed> feeds = requestAllFeeds();
		List<ForumThread> threadsParsedInLastHour
			= requestThreadsParsedInLastHour();
		return processRssByListOfFeeds(feeds, threadsParsedInLastHour);
	}

	private List<Feed> requestAllFeeds()
			throws RedirectionException, NotAuthorizedException{
		RestServiceFeed serviceFeed = RestTarget.getServiceFeedStatic();
		/* La siguiente orden hace una peticion REST a la aplicacion web.
		Si la peticion no pasa el FilterREST de la aplicacion web,
		este lanzara RedirectionException o NotAuthorizedException,
		en cuyo caso se desea que este programa termine */
		return serviceFeed.getAllFeeds();
	}
	
	private List<ForumThread> requestThreadsParsedInLastHour()
			throws RedirectionException, NotAuthorizedException{
		RestServiceForumThread serviceThread =
				RestTarget.getServiceForumThreadStatic();
		/* La siguiente orden hace una peticion REST a la aplicacion web.
		Si la peticion no pasa el FilterREST de la aplicacion web,
		este lanzara RedirectionException o NotAuthorizedException,
		en cuyo caso se desea que este programa termine */
		return serviceThread.getAllForumThreadGUIDsInLastHour();
	}
	
	private List<ForumThread> processRssByListOfFeeds(
			List<Feed> allFeeds, List<ForumThread> threadsParsedInLastJob)
			throws Exception{
		List<ForumThread> allThreads = new ArrayList<ForumThread>();
		//List<ForumThread> threadsOfFeed = new ArrayList<ForumThread>();
		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Parser parser = new Parser(threadsParsedInLastJob);
		for( Feed feed : allFeeds ){
			int numAllParsedThreads = allThreads.size();
			URL urlFeed = new URL(feed.getUrl());
			Document doc = builder.parse(urlFeed.openStream());
			NodeList newsOfFeed = doc.getElementsByTagName("item");
			parser.setFeed(feed);
			//parser.setThreadsParsedInCurrentJob(allThreads);
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
