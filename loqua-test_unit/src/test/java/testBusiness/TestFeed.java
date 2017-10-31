package testBusiness;

import static org.junit.Assert.assertEquals;
//import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.loqua.business.services.ServiceFeed;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.model.Feed;

import logging.LoquaLogger;

public class TestFeed {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static Feed feed;
	private static ServiceFeed serviceFeed;
	private static List<Feed> feedsList;
	
	@BeforeClass
	public static void setUpClass(){
		feed = new Feed();
		serviceFeed = new LocatorRemoteEjbServices().getServiceFeed();
		feedsList = new ArrayList<Feed>();
	}
	
	@Test
	public void testGetFeedById(){
    	try {
    		long feedId = 101L;
    		feed = serviceFeed.getFeedByID(feedId);
    		assertTrue(!(feed==null));
    	    assertEquals(feed.getId().longValue(), feedId);
    	} catch (Exception e) {
    		e.printStackTrace();
    		log.error("Unexpected Exception at 'testGetFeedById()'");
		}
	}
	
	@Test
	public void testGetAllFeeds(){
    	try {
			feedsList = serviceFeed.restGetAllFeeds();
			assertTrue(!(feedsList.size()==0));
			
			long feedId = 101L;
			feed = serviceFeed.getFeedByID(feedId);
			assertTrue(feedsList.contains(feed));
    	} catch (Exception e) {
    		e.printStackTrace();
    		log.error("Unexpected Exception at 'testGetAllFeeds()'");
		}
	}
	
	@After
	public void tearDown(){
		feedsList.clear();
	}
}
