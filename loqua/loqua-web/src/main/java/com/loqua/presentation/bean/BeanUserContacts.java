package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.User;
import com.loqua.presentation.bean.requestBean.BeanActionResult;

public class BeanUserContacts implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private List<Contact> contactsOfUser;
	private List<ContactRequest> pendingContactRequests;
	private User user;
	private User userContact;

	private BeanActionResult actionSendContactRequest;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
	}
	
	private void initBeanLogin() {
		// Buscamos el BeanLogin en la sesion.
		beanLogin = null;
		beanLogin = (BeanLogin)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanLogin");
		// si no existe lo creamos e inicializamos:
		if( beanLogin == null ){
			beanLogin = new BeanLogin();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanLogin", beanLogin);
		}
	}

	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Genera una peticion de contacto cuyo autor es el usuario dado 'userSender'
	 * y cuyo receptor es el usuario 'userReceiver'
	 * @param userSender usuario que envia la peticion de contacto
	 * @param userReceiver usuario que recibe la peticion de contacto
	 * @param beanActionResult bean que se encarga de imprimir en la vista el
	 * resultado de la accion, exitoso o no.
	 */
	public void sendContactRequest(User userSender, User userReceiver,
			BeanActionResult beanActionResult){
		actionSendContactRequest = beanActionResult;
		actionSendContactRequest.setFinish(false);
		actionSendContactRequest.setSuccess(false);
		try {
			Factories.getService().getServiceContact()
				.createContactRequest(userSender, userReceiver);
			actionSendContactRequest.setSuccess(true);
		} catch (Exception e) {
			// TODO Log
			//actionSendContactRequest.setMsgActionResult("errorSendContactRequest");
		}
		actionSendContactRequest.setFinish(true);
	}
	
	/**
	 * Elimina a los dos usuarios dados de sus respectivas listas de contactos
	 * @param user el primero de los usuarios que conforman la relacion de
	 * contactos
	 * @param userContact el segundo de los usuarios que conforman la relacion
	 * de contactos
	 * @param beanActionResult bean que se encarga de imprimir en la vista el
	 * resultado de la accion, exitoso o no.
	 */
	public void deleteContact(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try {
			Factories.getService().getServiceContact()
				.deleteReciprocalContact(user.getId(), userContact.getId());
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			// TODO Log
			//actionDeletePub.setMsgActionResult("errorContactDelete");
		}
		// resetear la lista de contactos del usuario:
		resetContacts(user.getId());
		beanActionResult.setFinish(true);
	}
	
	public void acceptRequest(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try {
			Factories.getService().getServiceContact()
				.acceptRequest(user.getId(), userContact.getId());
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// resetear la lista de contactos del usuario:
		resetContacts(user.getId());
		// resetear la lista de solicitudes de contacto del usuario:
		resetPendingContactRequests(user.getId());
		beanActionResult.setFinish(true);
	}
	
	public void rejectRequest(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try {
			Factories.getService().getServiceContact()
				.deleteRequest(user.getId(), userContact.getId());
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			// TODO Log
		}
		// resetear la lista de solicitudes de contacto del usuario:
		resetPendingContactRequests(user.getId());
		beanActionResult.setFinish(true);
	}
	
	public boolean isContactOfLoggedUser(User userContact){
		// Si el usuario dado es contacto del usuario logueado, devuelve true
		if( beanLogin.getLoggedUser()==null ) return false;
		List<Contact> userContacts = Factories.getService().getServiceContact()
				.getContactsByUser( beanLogin.getLoggedUser().getId() );
		for( Contact c : userContacts ){
			if( c.getUserContact().equals(userContact) ){ return true; }
		}
		return false;
	}
	public static boolean areContacts(User user, User userContact){
		// Este metodo se usa desde el BenUserData y el BeanPublication
		// Cuando declaro 'static' un metodo de un bean,
		// suele ser para poder acceder a el desde otro bean sin instanciar aquel
		if( user==null || userContact==null ) return false;
		List<Contact> userContacts = Factories.getService().getServiceContact()
				.getContactsByUser( user.getId() );
		for( Contact c : userContacts ){
			if( c.getUserContact().equals(userContact) ){ return true; }
		}
		return false;
	}
	
	public boolean contactRequestAlreadySent(User userReceiver){
		// Si el usuario dado ya ha recibido la peticion de contacto
		// por parte del usuario logueado, devuelve true
		List<ContactRequest> userContactRequest = Factories.getService()
				.getServiceContact().getContactRequestByUser(
						beanLogin.getLoggedUser());
		for( ContactRequest c : userContactRequest ){
			if( c.getUserReceiver().equals(userReceiver) ){ return true; }
		}
		return false;
	}
	
	public List<Contact> getContactsByUser(Long userId){
		if(contactsOfUser!=null && !contactsOfUser.isEmpty()){
			return contactsOfUser;
		}
		try {
			contactsOfUser = Factories.getService().getServiceContact()
					.getContactsByUser(userId);
		} catch (Exception e) {
			// TODO
		}
		return contactsOfUser;
	}
	
	public void resetContacts(Long userId){
		try {
			contactsOfUser = Factories.getService().getServiceContact()
					.getContactsByUser(userId);
		} catch (Exception e) {
			// TODO Log
		}
	}
	
	public List<ContactRequest> getContactRequestsReceivedByUser(Long userId){
		if(pendingContactRequests!=null && !pendingContactRequests.isEmpty()){
			return pendingContactRequests;
		}
		try {
			pendingContactRequests = Factories.getService().getServiceContact()
				.getContactRequestsReceivedByUser(userId);
		} catch (Exception e) {
			// TODO Log
		}
		return pendingContactRequests;
	}
	
	public void resetPendingContactRequests(Long userId){
		try {
			pendingContactRequests = Factories.getService().getServiceContact()
				.getContactRequestsReceivedByUser(userId);
		} catch (Exception e) {
			// TODO Log
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public User getUserContact() {
		return userContact;
	}
	public void setUserContact(User userContact) {
		this.userContact = userContact;
	}
	
	public void setBothUsers(User user, User userContact){
		setUser(user);
		setUserContact(userContact);
	}
}
