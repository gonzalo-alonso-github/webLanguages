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
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar las operaciones
 * relativas al funcionamiento de los contactos de usuario. Incluye los metodos
 * de visualizacion de listas de contactos y de solicitudes de contacto,
 * el envio, aceptacion o rechazo de solicitudes y laeliminacion de contactos.
 * @author Gonzalo
 */
public class BeanUserContacts implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Lista de contactos del usuario que se consulta. Se permite consultar
	 * no solo la lista de contactos propia, sino tambien la de otros usuarios
	 * (considerando su nivel de privacidad).
	 */
	private List<Contact> contactsOfUser;
	
	/** Lista de solicitudes de contacto pendientes recibidas por el usuario
	 * que se consulta. Cada usuario solo puede ver su propia lista de
	 * solicitudes pendientes, salvo los administradores, que pueden ver
	 * la de otros usuarios. */
	private List<ContactRequest> pendingContactRequests;
	
	/** Representa el usuario que posee al contacto {@link #userContact}.<br/>
	 * Ambos atributos son utilizados para obtener la relacion de contactos
	 * entre dos usuarios, necesaria en los metodos de eliminacion de contactos,
	 * y de aceptacion o rechazo de solicitudes de contacto. */
	private User user;
	/** Representa el contacto del usuario {@link #user}.<br/>
	 * Ambos atributos son utilizados para obtener la relacion de contactos
	 * entre dos usuarios, necesaria en los metodos de eliminacion de contactos,
	 * y de aceptacion o rechazo de solicitudes de contacto. */
	private User userContact;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el bean inyectado {@link BeanLogin} */
	@PostConstruct
	public void init() {
		initBeanLogin();
	}
	
	/** Inicializa el objeto {@link BeanLogin} inyectado */
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

	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Genera una peticion de contacto cuyo autor es el usuario dado
	 * 'userSender' y cuyo receptor es el usuario 'userReceiver'
	 * @param userSender usuario que envia la peticion de contacto
	 * @param userReceiver usuario que recibe la peticion de contacto
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	public void sendContactRequest(User userSender, User userReceiver,
			BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try {
			Factories.getService().getServiceContact()
				.createContactRequest(userSender, userReceiver);
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			/* beanActionResult
				.setMsgActionResult("errorSendContactRequest"); */
			log.error("Unexpected Exception at 'sendContactRequest()'");
		}
		beanActionResult.setFinish(true);
	}
	
	/**
	 * Elimina a los dos usuarios dados de sus respectivas listas de contactos
	 * @param user el primero de los usuarios que conforman la relacion de
	 * contactos
	 * @param userContact el segundo de los usuarios que conforman la relacion
	 * de contactos
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	public void deleteContact(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try {
			Factories.getService().getServiceContact()
				.deleteReciprocalContact(user.getId(), userContact.getId());
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			// actionDeletePub.setMsgActionResult("errorContactDelete");
			log.error("Unexpected Exception at 'deleteContact()'");
		}
		// resetear la lista de contactos del usuario:
		resetContacts(user.getId());
		beanActionResult.setFinish(true);
	}
	
	/**
	 * Acepta una solicitud de contacto recibida por el usuario
	 * indicado por el usuario {@link BeanUserContacts#user} y enviada por
	 * el usuario {@link BeanUserContacts#userContact}
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
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
	
	/**
	 * Rechaza una solicitud de contacto recibida por el usuario
	 * indicado por el usuario {@link BeanUserContacts#user} y enviada por
	 * el usuario {@link BeanUserContacts#userContact}
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 */
	public void rejectRequest(BeanActionResult beanActionResult){
		beanActionResult.setFinish(false);
		beanActionResult.setSuccess(false);
		try {
			Factories.getService().getServiceContact()
				.deleteRequest(user.getId(), userContact.getId());
			beanActionResult.setSuccess(true);
		} catch (Exception e) {
			log.error("Unexpected Exception at 'rejectRequest()'");
		}
		// resetear la lista de solicitudes de contacto del usuario:
		resetPendingContactRequests(user.getId());
		beanActionResult.setFinish(true);
	}
	
	/**
	 * Comprueba si el usuario logueado es contacto del usuario indicado.
	 * @param userContact contacto que se comprueba
	 * @return
	 * 'true' si el usuario dado es logueado del usuario indicado
	 * 'false' si el usuario dado no es logueado del usuario indicado
	 */
	public boolean isContactOfLoggedUser(User userContact){
		if( beanLogin.getLoggedUser()==null ) return false;
		List<Contact> userContacts = Factories.getService().getServiceContact()
				.getContactsByUser( beanLogin.getLoggedUser().getId() );
		for( Contact c : userContacts ){
			if( c.getUserContact().equals(userContact) ){ return true; }
		}
		return false;
	}
	
	/**
	 * Comprueba si el usuario se&ntilde;alado por el parametro 'user'
	 * es contacto del usuario indicado por el parametro 'userContact'.
	 * @param user usuario que se comprueba si posee al contato dado
	 * @param userContact contacto que se comprueba
	 * @return
	 * 'true' si el usuario indicado por 'user' es contacto del usuario
	 * indicado por 'userContact'
	 * 'false' si 'user' no es contacto de 'userContact'
	 */
	public static boolean areContacts(User user, User userContact){
		// Este metodo se usa desde el BenUserData y el BeanPublication
		if( user==null || userContact==null ) return false;
		List<Contact> userContacts = Factories.getService().getServiceContact()
				.getContactsByUser( user.getId() );
		for( Contact c : userContacts ){
			if( c.getUserContact().equals(userContact) ){ return true; }
		}
		return false;
	}
	
	/**
	 * Comprueba si el usuario dado ya ha recibido una peticion de contacto
	 * por parte del usuario logueado
	 * @param userReceiver usuario que se comprueba
	 * @return
	 * 'true' si el usuario dado ya ha recibido la peticion
	 * 'false' si el usuario dado no ha recibido la peticion
	 */
	public boolean contactRequestAlreadySent(User userReceiver){
		List<ContactRequest> userContactRequest = Factories.getService()
				.getServiceContact().getContactRequestByUser(
						beanLogin.getLoggedUser());
		for( ContactRequest c : userContactRequest ){
			if( c.getUserReceiver().equals(userReceiver) ){ return true; }
		}
		return false;
	}
	
	/**
	 * Inicializa el atributo {@link #contactsOfUser}, consultando la lista
	 * de contactos del usuario dado.
	 * @param userId identificador del usuario cuya lista de contactos se
	 * consulta
	 * @return la lista {@link #contactsOfUser}, una vez inicializada
	 */
	public List<Contact> getContactsByUser(Long userId){
		if(contactsOfUser!=null && !contactsOfUser.isEmpty()){
			return contactsOfUser;
		}
		try {
			contactsOfUser = Factories.getService().getServiceContact()
					.getContactsByUser(userId);
		} catch (Exception e) {
			log.error("Unexpected Exception at 'getContactsByUser()'");
		}
		return contactsOfUser;
	}
	
	/**
	 * Sobreescribe la propiedad {@link #contactsOfUser},
	 * inicializandola sin tener en cuenta su informacion actual.
	 * @param userId identificador del usuario cuya lista de contactos se
	 * sobreescribe
	 */
	public void resetContacts(Long userId){
		try {
			contactsOfUser = Factories.getService().getServiceContact()
					.getContactsByUser(userId);
		} catch (Exception e) {
			log.error("Unexpected Exception at 'resetContacts()'");
		}
	}
	
	/**
	 * Inicializa el atributo {@link #contactsOfUser}, consultando la lista
	 * de solicitudes de contacto recibidas.
	 * @param userId identificador del usuario cuya lista de solicitudes
	 * de contacto recibidas se consulta
	 * @return la lista {@link #pendingContactRequests}, una vez inicializada
	 */
	public List<ContactRequest> getContactRequestsReceivedByUser(Long userId){
		if(pendingContactRequests!=null && !pendingContactRequests.isEmpty()){
			return pendingContactRequests;
		}
		try {
			pendingContactRequests = Factories.getService().getServiceContact()
				.getContactRequestsReceivedByUser(userId);
		} catch (Exception e) {
			log.error("Unexpected Exception at "
					+ "'getContactRequestsReceivedByUser()'");
		}
		return pendingContactRequests;
	}
	
	/**
	 * Sobreescribe la propiedad {@link #pendingContactRequests},
	 * inicializandola sin tener en cuenta su informacion actual.
	 * @param userId identificador del usuario cuya lista de solicitudes
	 * de contacto recibidas se sobreescribe
	 */
	public void resetPendingContactRequests(Long userId){
		try {
			pendingContactRequests = Factories.getService().getServiceContact()
				.getContactRequestsReceivedByUser(userId);
		} catch (Exception e) {
			log.error("Unexpected Exception at "
					+ "'resetPendingContactRequests()'");
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
