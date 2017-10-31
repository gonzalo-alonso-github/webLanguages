package testBusiness;

//import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.loqua.business.services.ServiceComment;
import com.loqua.business.services.ServiceForumThread;
import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.model.Comment;
import com.loqua.model.ForumThread;
import com.loqua.model.User;

import logging.LoquaLogger;

public class TestComment {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static ForumThread forumThread;
	private static ServiceForumThread serviceThread;
	
	private static Comment comment;
	private static ServiceComment serviceComment;
	
	private static User user;
	private static ServiceUser serviceUser;
	
	@BeforeClass
	public static void setUpClass(){
		
		forumThread = new ForumThread();
		serviceThread = new LocatorRemoteEjbServices().getServiceThread();
		
		comment = new Comment();
		serviceComment = new LocatorRemoteEjbServices().getServiceComment();
		
		user = new User();
		serviceUser = new LocatorRemoteEjbServices().getServiceUser();
	}
	
	@Test
	public void testSendComment(){
		try {
			long threadId = 1L;
			long userId = 1L;
			forumThread = serviceThread.getThreadById(threadId);
			user = serviceUser.getUserById(userId);
			int numCommentsBeforeCreate =
					serviceComment.getNumCommentsByThread(threadId);
			
			// Crear comentario
			comment = generateComment();
			int userPointsBeforeCreate = user.getUserInfo().getPoints();
			comment = serviceComment.sendComment(comment);
			int numCommentsAfterCreate =
					serviceComment.getNumCommentsByThread(threadId);
			assertTrue( numCommentsAfterCreate == numCommentsBeforeCreate+1 );
			user = serviceUser.getUserById(userId);
			int userPointsAfterCreate = user.getUserInfo().getPoints();
			assertTrue( userPointsAfterCreate == userPointsBeforeCreate+1 );
			
			// Eliminar comentario
			serviceComment.deleteComment(comment);
			int numCommentsAfterDelete =
					serviceComment.getNumCommentsByThread(threadId);
			assertTrue( numCommentsAfterDelete == numCommentsAfterCreate-1 );
			user = serviceUser.getUserById(userId);
			int userPointsAfterDelete = user.getUserInfo().getPoints();
			assertTrue( userPointsAfterDelete == userPointsAfterCreate-1 );
		} catch (Exception e) {
			e.printStackTrace();
    		log.error("Unexpected Exception at 'sendComment()'");
		}
	}
	
	private Comment generateComment() throws Exception{
		Comment comment = new Comment();
		comment.setTextThis("text").setTextHtmlThis("<p>text</p>")
			.setUserThis(user).setForumThreadThis(forumThread);
		return comment;
	}
}
