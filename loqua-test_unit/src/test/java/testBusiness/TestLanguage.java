package testBusiness;

import static org.junit.Assert.assertEquals;
//import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.model.Language;
import com.loqua.model.User;

import logging.LoquaLogger;

public class TestLanguage {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	//private static Language language;
	private static ServiceLanguage serviceLang;
	
	private static User user;
	private static ServiceUser serviceUser;
	
	@BeforeClass
	public static void setUpClass(){
		//language = new Language();
		serviceLang = new LocatorRemoteEjbServices().getServiceLanguage();
		
		user = new User();
		serviceUser = new LocatorRemoteEjbServices().getServiceUser();
	}
	
	@Test
	public void testCreateUserNativeLLanguage(){
		try {
			long userId = 1L;
			long langId = 3L;
			user = serviceUser.getUserById(userId);
			List<Long> originalNativeLangsIDs = getNativeLangsIDs(userId);
			List<Long> editedNativeLangsIDs = new ArrayList<Long>();
			editedNativeLangsIDs.add(langId);
			
			// Actualizar los lenguajes maternos del usuario
			serviceLang.updateNativeLanguages(
					user, originalNativeLangsIDs, editedNativeLangsIDs);
			List<Language> langList=serviceLang.getNativeLanguagesByUser(userId);
			assertTrue( langList!=null
					&& !langList.isEmpty()
					&& langList.size()==1
					&& langList.get(0).getId().equals(langId));
			
			// Revertir a la situacion inicial
			serviceLang.updateNativeLanguages(
					user, editedNativeLangsIDs, originalNativeLangsIDs);
			List<Long> nativeLangsAfterRevert = getNativeLangsIDs(userId);
			assertEquals( nativeLangsAfterRevert, originalNativeLangsIDs );
		} catch (Exception e) {
			e.printStackTrace();
    		log.error("Unexpected Exception at "
    				+ "'testCreateUserNativeLLanguage()'");
		}
	}
	
	private List<Long> getNativeLangsIDs(Long userId) {
		List<Long> result = new ArrayList<Long>();
		List<Language> originalNativeLangs= 
				serviceLang.getNativeLanguagesByUser(userId);
		for(Language lang : originalNativeLangs){
			result.add( lang.getId() );
		}
		return result;
	}
}
