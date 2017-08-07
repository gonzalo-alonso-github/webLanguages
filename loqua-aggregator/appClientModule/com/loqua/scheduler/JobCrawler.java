package com.loqua.scheduler;

import java.util.List;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.RedirectionException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.loqua.crawler.Gatherer;
import com.loqua.remote.RestTarget;
import com.loqua.remote.model.ForumThread;
import com.loqua.remote.services.RestServiceForumThread;

public class JobCrawler implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try{
			Gatherer gatherer = new Gatherer();
			List<ForumThread> forumThreads = gatherer.downloadAllNews();
			createForumThreads(forumThreads);
		}catch( Exception e ){
			throw new JobExecutionException(e);
		}
	}

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
