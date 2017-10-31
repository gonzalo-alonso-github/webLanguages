package testBusiness;

//import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.loqua.business.services.ServiceContact;
import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.User;

import logging.LoquaLogger;

public class TestContacts {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	//private static Contact Contact;
	private static ServiceContact serviceContact;
	private static List<Contact> contactList;
	private static List<ContactRequest> requestList;
	
	private static User user;
	private static User userContact;
	private static ServiceUser serviceUser;
	
	@BeforeClass
	public static void setUpClass(){
		//Contact = new Contact();
		serviceContact = new LocatorRemoteEjbServices().getServiceContact();
		contactList = new ArrayList<Contact>();
		requestList = new ArrayList<ContactRequest>();
		
		user = new User();
		userContact = new User();
		serviceUser = new LocatorRemoteEjbServices().getServiceUser();
	}
	
	@Test
	public void testCreateContact(){
		try {
			long userId = 1L;
			long userContactId = 7L;
			user = serviceUser.getUserById(userId);
			userContact = serviceUser.getUserById(userContactId);
			
			// Crear solicitud de contacto
			requestList = serviceContact.getContactRequestsReceivedByUser(
					userContactId);
			int numRequestsBeforeCreate = requestList.size();
			serviceContact.createContactRequest(user, userContact);
			requestList = serviceContact.getContactRequestsReceivedByUser(
					userContactId);
			int numRequestsAfterCreate = requestList.size();
			assertTrue( numRequestsAfterCreate==numRequestsBeforeCreate+1 );
			
			// Crear relacion de contactos
			// (no se crea si previamente no existe una solicitud)
			contactList = serviceContact.getContactsByUser(userId);
			int numContactsBeforeCreate = contactList.size();
			serviceContact.acceptRequest(userId, userContactId);
			contactList = serviceContact.getContactsByUser(userId);
			int numContactsAfterCreate = contactList.size();
			assertTrue( numContactsAfterCreate==numContactsBeforeCreate+1 );
			
			// Deshacer la relacion de contactos creada
			serviceContact.deleteReciprocalContact(userId, userContactId);
			contactList = serviceContact.getContactsByUser(userId);
			int numContactsAfterRevert = contactList.size();
			assertTrue( numContactsAfterRevert==numContactsBeforeCreate );
		} catch (Exception e) {
			e.printStackTrace();
    		log.error("Unexpected Exception at 'testCreateContact()'");
		}
	}

	@After
	public void tearDown(){
		contactList.clear();
		requestList.clear();
	}
}
