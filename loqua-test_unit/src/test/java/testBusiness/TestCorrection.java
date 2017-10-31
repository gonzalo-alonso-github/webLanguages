package testBusiness;

//import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.loqua.business.services.ServiceCorrection;
import com.loqua.business.services.ServiceForumPost;
import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.User;

import logging.LoquaLogger;

public class TestCorrection {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static Comment comment;
	
	private static Correction correction;
	private static ServiceCorrection serviceCorr;
	private static List<Correction> corrList;
	
	private static ServiceForumPost serviceForumPost;
	private static User user;
	private static ServiceUser serviceUser;
	
	@BeforeClass
	public static void setUpClass(){
		correction = new Correction();
		serviceCorr=new LocatorRemoteEjbServices().getServiceCorrection();
		corrList = new ArrayList<Correction>();
		
		serviceForumPost=new LocatorRemoteEjbServices().getServiceForumPost();
		
		serviceUser=new LocatorRemoteEjbServices().getServiceUser();
	}
	
	@Test
	public void testSendCorrection(){
		try {
			long commId = 1L;
			long userId = 1L;
			corrList = serviceCorr.getNotApprovedCorrectionsByComment(commId);
			int numCorrBeforeCreate = corrList.size();
			comment = (Comment) serviceForumPost.getForumPostById(commId);
			user = serviceUser.getUserById(userId);
			
			// Crear correccion1
			correction = generateCorrection();
			correction = serviceCorr.sendCorrection(correction);
			corrList = serviceCorr.getNotApprovedCorrectionsByComment(commId);
			int numCorrAfterCreate = corrList.size();
			assertTrue( numCorrAfterCreate == numCorrBeforeCreate+1 );
			
			// Recomendar correcion1
			int numAgreesBeforeCreate = serviceCorr.getNumCorrectionAgrees(
					correction.getId());
			serviceCorr.recommendCorrection(userId, correction);
			int numAgreesAfterCreate = serviceCorr.getNumCorrectionAgrees(
					correction.getId());
			assertTrue( numAgreesAfterCreate == numAgreesBeforeCreate+1 );
			
			// Eliminar recomendacion
			serviceCorr.deleteAgreementForTest(userId, correction);
			int numAgreesAfterDelete = serviceCorr.getNumCorrectionAgrees(
					correction.getId());
			assertTrue( numAgreesAfterDelete == numAgreesAfterCreate-1 );
			
			// Crear correccion2 (sobre mismo comentario que la primera)
			Correction correction2 = generateCorrection();
			correction2 = serviceCorr.sendCorrection(correction2);
			corrList = serviceCorr.getNotApprovedCorrectionsByComment(commId);
			numCorrAfterCreate = corrList.size();
			assertTrue( numCorrAfterCreate == numCorrBeforeCreate+2 );
			
			// Aprobar correccion2 (test: no hay otra correccion aprobada)
			int userPointsBeforeApproval = user.getUserInfo().getPoints();
			serviceCorr.acceptCorrection(correction2);
			correction2 = serviceCorr.getApprovedCorrectionByComment(commId);
			assertTrue( !(correction2==null) && correction2.getApproved() );
			user = serviceUser.getUserById(userId);
			int userPointsAfterApproval = user.getUserInfo().getPoints();
			assertTrue( userPointsAfterApproval==userPointsBeforeApproval+25 );
			// el numero de correcciones no aprobadas se decrementa:
			numCorrAfterCreate--;
			
			// Aprobar correccion1 (test: ya hay otra correccion aprobada)
			serviceCorr.acceptCorrection(correction);
			correction = serviceCorr.getApprovedCorrectionByComment(commId);
			assertTrue( !(correction==null) && correction.getApproved() );
			
			// Eliminar correccion2
			serviceCorr.deleteCorrection(correction2);
			corrList = serviceCorr.getNotApprovedCorrectionsByComment(commId);
			int numCorrAfterDelete = corrList.size();
			assertTrue( numCorrAfterDelete == numCorrAfterCreate-1 );
			
			// Eliminar correccion1
			serviceCorr.deleteCorrection(correction);
			correction = serviceCorr.getApprovedCorrectionByComment(commId);
			assertTrue( correction==null );
			user = serviceUser.getUserById(userId);
			int userPointsAfterDelete = user.getUserInfo().getPoints();
			assertTrue( userPointsAfterDelete==userPointsBeforeApproval );
		} catch (Exception e) {
			e.printStackTrace();
    		log.error("Unexpected Exception at 'testSendCorrection()'");
		}
	}
	
	private Correction generateCorrection() throws Exception{
		Correction correction = new Correction();
		correction.setTextThis("text").setTextHtmlThis("<p>text</p>")
			.setApprovedThis(false).setCommentThis(comment)
			.setUserThis(user).setDateThis(new Date())
			.setForumThreadThis(comment.getForumThread())
			/*.setPostType("TypeCorrection")*/;
		return correction;
	}
	
	@After
	public void tearDown(){
		corrList.clear();
	}
}
