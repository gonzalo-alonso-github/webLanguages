package testBusiness;

//import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.loqua.business.services.ServiceFeed;
import com.loqua.business.services.ServiceForumThread;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.model.Feed;
import com.loqua.model.ForumThread;

import logging.LoquaLogger;

public class TestForumThread {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static Feed feed;
	private static ServiceFeed serviceFeed;
	
	private static ForumThread forumThread;
	private static ServiceForumThread serviceThread;
	private static List<ForumThread> forumThreadsList;
	
	@BeforeClass
	public static void setUpClass(){
		feed = new Feed();
		serviceFeed = new LocatorRemoteEjbServices().getServiceFeed();
		
		forumThread = new ForumThread();
		serviceThread = new LocatorRemoteEjbServices().getServiceThread();
		forumThreadsList = new ArrayList<ForumThread>();
	}
	
	@Test
	public void testVoteThread(){
    	try {
    		long userId = 1L;
    		long threadId = 1L;
    		forumThread = serviceThread.getThreadById(threadId);
    		
    		// Votar un hilo del foro:
    		forumThread = serviceThread.voteThread(userId, forumThread);
    		int numVotes = forumThread.getForumThreadInfo().getCountVotes();
			assertTrue(numVotes==1);
			
			// Deshacer el voto:
			serviceThread.deleteThreadVoterForTest(userId, forumThread);
    		forumThread = serviceThread.getThreadById(1L);
    		numVotes = forumThread.getForumThreadInfo().getCountVotes();
			assertTrue(numVotes==0);
    	} catch (Exception e) {
    		e.printStackTrace();
    		log.error("Unexpected Exception at 'testVoteThread()'");
		}
	}
	
	@Test
	public void testCreateForumThread(){
		try {
			forumThreadsList = serviceThread.getAllForumThreadsInLastHour();
			int numThreadsBeforeCreate = forumThreadsList.size();
			
			// Crear hilo del foro
			forumThread = generateForumThread();
			forumThread = serviceThread.restCreateForumThread(forumThread);
			forumThreadsList = serviceThread.getAllForumThreadsInLastHour();
			int numThreadsAfterCreate = forumThreadsList.size();
			assertTrue( numThreadsAfterCreate == numThreadsBeforeCreate+1 );
			
			// Eliminar hilo del foro
			serviceThread.deleteForumThreadForTest(forumThread.getId());
			forumThreadsList = serviceThread.getAllForumThreadsInLastHour();
			int numThreadsAfterDelete = forumThreadsList.size();
			assertTrue( numThreadsAfterDelete == numThreadsAfterCreate-1 );
		} catch (Exception e) {
			e.printStackTrace();
    		log.error("Unexpected Exception at 'createForumThread()'");
		}
	}
	
	private ForumThread generateForumThread() throws Exception {
		feed = serviceFeed.getFeedByID(101L);
		ForumThread thread = new ForumThread();
		thread.setGuidThis("guid").setTitleThis("title").setUrlThis("url")
			.setContentThis("content")
			.setFeedThis(feed).setFeedNameThis(feed.getName())
			.setDateThis(new Date()).setDateLastCommentThis(new Date())
			.setDateAggregatedThis(new Date());
		return thread;
	}
	
	@After
	public void tearDown(){
		forumThreadsList.clear();
	}
}
