package com.loqua.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.loqua.model.types.TypeUserRole;

/**
 * Representa la informacion sobre los datos de acceso a la aplicacion
 * y del estado de un usuario
 * @author Gonzalo
 */
@XmlRootElement(name = "user")
@Entity
@Table(name="User")
public class User implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Constante con el valor 'ADMINISTRATOR' */
	@Transient
	public static final String ADMINISTRATOR = "ADMINISTRATOR";
	
	/** Constante con el valor 'USER' */
	@Transient
	public static final String USER = "USER";
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	/** Direccion de email del usuario */
	private String email;
	/** Fecha de registro del usuario */
	private Date dateRegistered;
	/** Fecha de eliminacion de la cuenta del usuario */
	private Date dateRemoved;
	/** Pseud&oacute;nimo (o 'nick') del usuario */
	private String nick;
	/** Contrase&ntilde;a del usuario */
	private String password;
	/** Cantidad de intentos de inicio de sesion fallidos del usuario */
	private int loginFails;
	/** Indica el tipo del usuario. Es un Enumerado que
	 * admite los siguientes valores:
	 * <ul>
	 * <li>'ADMINISTRATOR': es un usuario administrador,
	 * con permisos adicionales respecto al usuario comun</li>
	 * <li>'USER': es un usuario comun, sin permisos especiales</li></ul> */
	@Enumerated(EnumType.STRING)
	private TypeUserRole role;
	/** Indica si el usuario esta en estado activo */
	private boolean active;
	/** Indica si el usuario esta en estado bloqueado */
	private boolean locked;
	/** Indica si el usuario esta en estado eliminado */
	private boolean removed;
	/** Codigo de dos letras de la configuracion regional
	 * asociada al usuario. No es necesario que coincida con algun valor de
	 * {@link Country#codeIso3166}. Este atributo permite, por ejemplo,
	 * decidir en que idioma se envian los correos electronicos al usuario */
	private String locale;
	/** Cadena aleatoria (de al menos 26 caracteres) que permite identificar
	 * al usuario que accede a la URL de confirmacion de su registro
	 * en la aplicacion, o de restauracion de contrase&ntilde;a,
	 * o de eliminacion de su cuenta */
	private String urlConfirm;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Informacion sobre la puntuacion del usuario */
	@OneToOne(mappedBy = "user")
	private UserInfo userInfo;
	
	/** Informacion sobre los datos estrictamente personales del usuario */
	@OneToOne(mappedBy = "user")
	private UserInfoPrivacity userInfoPrivacity;
	
	/** Informacion sobre los niveles de privacidad de los datos del usuario */
	@OneToOne(mappedBy = "user")
	private PrivacityData privacityData;
	
	/** Lista de contactos del usuario */
	@OneToMany(mappedBy="user")
	private Set<Contact> contacts = new HashSet<Contact>();
	
	/** Lista de solicitudes de contacto enviadas por el usuario */
	@OneToMany(mappedBy="userSender")
	private Set<ContactRequest> contactRequestSent = 
		new HashSet<ContactRequest>();
	/** Lista de solicitudes de contacto recibidas por el usuario */
	@OneToMany(mappedBy="userReceiver")
	private Set<ContactRequest> contactRequestReceived = 
		new HashSet<ContactRequest>();
	
	/** Lista de participaciones en el foro realizadas por el usuario */
	@OneToMany(mappedBy="user")
	private Set<ForumPost> phorumPosts = new HashSet<ForumPost>();
	
	/** Lista de lenguajes utilizados por el usuario a nivel nativo */
	@OneToMany(mappedBy="user"/*, fetch = FetchType.EAGER*/)
	private Set<UserNativeLanguage> userNativeLanguages = 
		new HashSet<UserNativeLanguage>();
	
	/** Lista de lenguajes utilizados por el usuario a nivel de practicante */
	@OneToMany(mappedBy="user"/*, fetch = FetchType.EAGER*/)
	private Set<UserPracticingLanguage> userPracticingLanguages = 
		new HashSet<UserPracticingLanguage>();
	
	/** Lista de mensajes enviados por el usuario */
	@OneToMany(mappedBy="user")
	private Set<Message> messages = new HashSet<Message>();
	
	/** Lista de mensajes recibidos por el usuario */
	@OneToMany(mappedBy="user")
	private Set<MessageReceiver> messageReceivers = 
		new HashSet<MessageReceiver>();
	
	/** Lista de publicaciones generadas o provocadas por el usuario */
	@OneToMany(mappedBy="user")
	private Set<Publication> publications = new HashSet<Publication>();
	
	/** Lista de publicaciones recibidas por el usuario */
	@OneToMany(mappedBy="user")
	private Set<PublicationReceiver> publicationReceivers = 
		new HashSet<PublicationReceiver>();
	
	/** Lista de logros alcanzados por el usuario */
	@OneToMany(mappedBy="user")
	private Set<Achievement> achievements = new HashSet<Achievement>();
	
	/** Lista de recomendaciones (de correcciones) emitidas por el usuario */
	@OneToMany(mappedBy="user")
	private Set<CorrectionAgree> correctionAgreements = 
		new HashSet<CorrectionAgree>();
	
	/** Lista de desaprobaciones (de correcciones) emitidas por el usuario */
	@OneToMany(mappedBy="user")
	private Set<CorrectionDisagree> correctionDisagreements = 
		new HashSet<CorrectionDisagree>();
	
	/** Lista de votaciones a comentarios enviadas por el usuario */
	@OneToMany(mappedBy="user")
	private Set<CommentVoter> commentVoters = new HashSet<CommentVoter>();
	
	/** Lista de votaciones a hilos del foro enviadas por el usuario */
	@OneToMany(mappedBy="user")
	private Set<ForumThreadVoter> phorumThreadVoters =
		new HashSet<ForumThreadVoter>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public User(){
		userInfoPrivacity = new UserInfoPrivacity();
		userInfoPrivacity.setUser(this);
		
		userInfo = new UserInfo();
		userInfo.setUser(this);
		
		privacityData = new PrivacityData();
		privacityData.setUser(this);
	}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param userInfo objeto UserInfo asociado al User
	 * @param userInfoPrivacity objeto UserInfoPrivacity asociado al User
	 * @param privacityData objeto PrivacityData asociado al User
	 */
	public User( UserInfo userInfo, UserInfoPrivacity userInfoPrivacity,
			PrivacityData privacityData ){
		this.userInfo = userInfo;
		userInfo.setUser(this);
		
		this.userInfoPrivacity = userInfoPrivacity;
		userInfoPrivacity.setUser(this);
		
		this.privacityData = privacityData;
		privacityData.setUser(this);
	}
	
	/**
	 * Constructor que inicializa todos los atributos de la clase
	 * asignandoles el msimo valor que los del objeto dado.
	 * El efecto buscado es que el usuario creado mediante este constructor
	 * sea una copia identica al usuario que se da como parametro.
	 * @param user objeto User que se desea clonar
	 */
	public User( User user ){
		// Con el fin de evitar escribir muchas lineas para inicializar
		// los muchos atributos de la clase,
		// se utiliza reflection para hacerlo de forma automatica.
		try {
			Field[] fields = this.getClass().getDeclaredFields();
			for( Field field : fields ) {
				if( ! Modifier.isFinal(field.getModifiers() )){
					field.set(this, field.get(user));
				}
	        }
		} catch (IllegalArgumentException | IllegalAccessException e) {}
	}
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (GETTERS & SETTERS)
	// // // // // // // // // // // // // //
	
	/* A la hora de acceder a una propiedad de una clase o de un bean,
	JSF requiere que exista un getter y un setter de dicha propiedad,
	y ademas los setter deben devolver obligatoriamente 'void'.
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/* Relacion entre entidades:
	 *  1 User <--> 1 UserInfo
	 */
	/*@XmlElement*/@XmlTransient
	public UserInfo getUserInfo() {
		return userInfo;
	}
	UserInfo _getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo ui) {
		userInfo = ui;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> 1 UserInfoPrivacity
	 */
	/*@XmlElement*/@XmlTransient
	public UserInfoPrivacity getUserInfoPrivacity() {
		return userInfoPrivacity;
	}
	UserInfoPrivacity _getUserInfoPrivacity() {
		return userInfoPrivacity;
	}
	public void setUserInfoPrivacity(UserInfoPrivacity uip) {
		userInfoPrivacity = uip;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> 1 PrivacityData
	 */
	/*@XmlElement*/@XmlTransient
	public PrivacityData getPrivacityData() {
		return privacityData;
	}
	PrivacityData _getPrivacityData() {
		return privacityData;
	}
	public void setPrivacityData(PrivacityData pd) {
		privacityData = pd;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * Contact
	 */
	@XmlTransient
	public Set<Contact> getContacts() {
		return Collections.unmodifiableSet(contacts);
	}
	Set<Contact> _getContacts() {
		return contacts;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * ContactRequest (contactRequest enviados por el usuario)
	 */
	@XmlTransient
	public Set<ContactRequest> getContactRequestSent() {
		return Collections.unmodifiableSet(contactRequestSent);
	}
	Set<ContactRequest> _getContactRequestSent() {
		return contactRequestSent;
	}
	
	
	/* Relacion entre entidades:
	 *  1 User <--> * ContactRequest (contactRequest recibidos por el usuario)
	 */
	@XmlTransient
	public Set<ContactRequest> getContactRequestReceived() {
		return Collections.unmodifiableSet(contactRequestReceived);
	}
	Set<ContactRequest> _getContactRequestReceived() {
		return contactRequestReceived;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * PhorumPost
	 */
	@XmlTransient
	public Set<ForumPost> getPhorumPosts() {
		return Collections.unmodifiableSet(phorumPosts);
	}
	Set<ForumPost> _getPhorumPosts() {
		return phorumPosts;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * UserNativeLanguage <--> 1 Language
	 */
	@XmlTransient
	public Set<UserNativeLanguage> getUserNativeLanguages() {
		return Collections.unmodifiableSet(userNativeLanguages);
	}
	Set<UserNativeLanguage> _getUserNativeLanguages() {
		return userNativeLanguages;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * UserPracticingLanguage <--> 1 Language
	 */
	@XmlTransient
	public Set<UserPracticingLanguage> getUserPracticingLanguages() {
		return Collections.unmodifiableSet(userPracticingLanguages);
	}
	Set<UserPracticingLanguage> _getUserPracticingLanguages() {
		return userPracticingLanguages;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * Messages
	 */
	@XmlTransient
	public Set<Message> getMessages() {
		return Collections.unmodifiableSet(messages);
	}
	Set<Message> _getMessages() {
		return messages;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * MessageReceivers <--> 1 Message
	 */
	@XmlTransient
	public Set<MessageReceiver> getMessageReceivers() {
		return Collections.unmodifiableSet(messageReceivers);
	}
	Set<MessageReceiver> _getMessageReceivers() {
		return messageReceivers;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * Publications
	 */
	@XmlTransient
	public Set<Publication> getPublications() {
		return Collections.unmodifiableSet(publications);
	}
	Set<Publication> _getPublications() {
		return publications;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * PublicationReceivers <--> 1 Publication
	 */
	@XmlTransient
	public Set<PublicationReceiver> getPublicationReceivers() {
		return Collections.unmodifiableSet(publicationReceivers);
	}
	Set<PublicationReceiver> _getPublicationReceivers() {
		return publicationReceivers;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * Achievements
	 */
	@XmlTransient
	public Set<Achievement> getAchievements() {
		return Collections.unmodifiableSet(achievements);
	}
	Set<Achievement> _getAchievements() {
		return achievements;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * CorrectionAgrees <--> 1 Correction
	 */
	@XmlTransient
	public Set<CorrectionAgree> getCorrectionAgreements() {
		return Collections.unmodifiableSet(correctionAgreements);
	}
	Set<CorrectionAgree> _getCorrectionAgreements() {
		return correctionAgreements;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * CorrectioDisaAgrees <--> 1 Correction
	 */
	@XmlTransient
	public Set<CorrectionDisagree> getCorrectionDisagreements() {
		return Collections.unmodifiableSet(correctionDisagreements);
	}
	Set<CorrectionDisagree> _getCorrectionDisagreements() {
		return correctionDisagreements;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * CommentVoters <--> 1 Comment
	 */
	@XmlTransient
	public Set<CommentVoter> getCommentVoters() {
		return Collections.unmodifiableSet(commentVoters);
	}
	Set<CommentVoter> _getCommentVoters() {
		return commentVoters;
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * ThreadVoter <--> 1 Thread
	 */
	@XmlTransient
	public Set<ForumThreadVoter> getPhorumThreadVoters() {
		return Collections.unmodifiableSet(phorumThreadVoters);
	}
	Set<ForumThreadVoter> _getForumThreadVoters() {
		return phorumThreadVoters;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/* A la hora de acceder a una propiedad de una clase o de un bean,
	JSF requiere que exista un getter y un setter de dicha propiedad,
	y ademas los setter deben devolver obligatoriamente 'void'.
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	@XmlElement
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}

	@XmlElement
	public String getPassword() {
		return password;
	}
	
	/**
	 * Cifra la clave de usuario dada y la actualiza.
	 * @param password
	 * la clave de usuario, todav�a sin cifrar 
	 */
	public void setPassword(String password) {
		//String salt = BCrypt.gensalt(12);
		//String hashedPassword = BCrypt.hashpw(password, salt);
		this.password = password;
	}

	@XmlElement
	public int getLoginFails() {
		return loginFails;
	}
	public void setLoginFails(int loginFails) {
		this.loginFails = loginFails;
	}
	
	@XmlElement
	public String getRole() {
		return role.toString();
	}
	public void setRole(String role) {
		this.role = Enum.valueOf(TypeUserRole.class, role);
	}
	
	@XmlElement
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	@XmlElement
	public boolean getLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@XmlElement
	public boolean getRemoved() {
		return removed;
	}
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	
	@XmlElement
	public Date getDateRemoved() {
		return dateRemoved;
	}
	public void setDateRemoved(Date dateRemoved) {
		this.dateRemoved = dateRemoved;
	}
	
	@XmlElement
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement
	public Date getDateRegistered() {
		return dateRegistered;
	}
	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}
	
	@XmlElement
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	@XmlElement
	public String getUrlConfirm() {
		return urlConfirm;
	}
	public void setUrlConfirm(String urlConfirm) {
		this.urlConfirm = urlConfirm;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	// // // //
	// METODOS
	// // // //
	
	/* Clona este objeto User generando y devolviendo una
	 * nueva instancia identica
	public User cloneUser(){
		// Como alternativa a este metodo se usa el constructor
		// que recibe un User
		// requiere 'implements Cloneable'
		User result = null;
		try {
			result = (User) super.clone();
		} catch (CloneNotSupportedException e) {}
		return result;
	}
	*/
	
	/** Elimina los datos del User reseteandolos a su valor por defecto,
	 * cambia el estado de 'removed' igual a 'true',
	 * y establece la fecha actual como fecha de eliminacion
	 * de la cuenta de usuario */
	public void removeUserData() {
		locale = null;
		dateRemoved = new Date();
		password = null;
		loginFails = 0;
		active = true;
		locked = false;
		removed = true;
		urlConfirm = null;
		// Si se elimina un Administrador, ademas de "resetear" esos datos
		// tambien se le podria reducir el "role":
		// role = TypeUserRole.USER;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/* Relacion entre entidades:
	 *  1 User <--> * Contacts
	 */
	/** Agrega un contacto a la lista de ellos que posee el usuario
	 * @param contact objeto Contact que se agrega
	 */
	public void addContact(Contact contact){
		contacts.add(contact);
		contact._setUser(this);
	}
	/** Elimina un contacto a la lista de ellos que posee el usuario
	 * @param contact objeto Contact que se elimina
	 */
	public void removeContact(Contact contact){
		contacts.remove(contact);
		contact._setUser(null);
	}
	/** Elimina todos los contactos de la lista de ellos que posee el usuario */
	public void removeAllContacts(){
		for(Contact c : Collections.unmodifiableSet(contacts)){
			c.unlink();
		}
		contacts.clear();
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * ContactRequest (contactRequest enviados por el usuario)
	 */
	/** Agrega una solicitud de contacto a la lista de solicitudes enviadas
	 * por el usuario
	 * @param contactRequest objeto ContactRequest que se agrega
	 */
	public void addContactRequestSent(ContactRequest contactRequest){
		contactRequestSent.add(contactRequest);
		contactRequest._setUserSender(this);
	}
	/** Ellimina una solicitud de contacto de la lista de solicitudes enviadas
	 * por el usuario
	 * @param contactRequest objeto ContactRequest que se elimina
	 */
	public void removeContactRequestSent(ContactRequest contactRequest){
		contactRequestSent.remove(contactRequest);
		contactRequest._setUserSender(null);
	}
	/** Elimina todas las solicitudes de contacto de la lista de solicitudes
	 * enviadas que posee el usuario */
	public void removeAllSentContactRequests(){
		for(ContactRequest cr : 
				Collections.unmodifiableSet(contactRequestSent)){
			cr.unlink();
		}
		contactRequestSent.clear();
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * ContactRequest (contactRequest recibidos por el usuario)
	 */
	/** Agrega una solicitud de contacto a la lista de solicitudes recibidas
	 * por el usuario
	 * @param contactRequest objeto ContactRequest que se agrega
	 */
	public void addContactRequestReceived(ContactRequest contactRequest){
		contactRequestSent.add(contactRequest);
		contactRequest._setUserReceiver(this);
	}
	/** Elimina una solicitud de contacto a la lista de solicitudes recibidas
	 * por el usuario
	 * @param contactRequest objeto ContactRequest que se elimina
	 */
	public void removeContactRequestReceived(ContactRequest contactRequest){
		contactRequestSent.remove(contactRequest);
		contactRequest._setUserReceiver(null);
	}
	/** Elimina todas las solicitudes de contacto de la lista de recibidas
	 * enviadas que posee el usuario */
	public void removeAllReceivedContactRequests(){
		for(ContactRequest cr : 
				Collections.unmodifiableSet(contactRequestReceived)){
			cr.unlink();
		}
		contactRequestReceived.clear();
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * PhorumPost
	 */
	/** Agrega una participacion del foro (comentario o correccion)
	 * a la lista de ellas que posee el usuario
	 * @param forumPost objeto ForumPost que se agrega
	 */
	public void addPhorumPost(ForumPost forumPost){
		phorumPosts.add(forumPost);
		forumPost._setUser(this);
	}
	/** Elimina una participacion del foro (comentario o correccion)
	 * de la lista de ellas que posee el usuario
	 * @param forumPost objeto ForumPost que se elimina
	 */
	public void removePhorumPost(ForumPost forumPost){
		phorumPosts.remove(forumPost);
		forumPost._setUser(null);
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * Messages
	 */
	/** Agrega un mensaje a la lista de mensajes enviados por el usuario
	 * @param message objeto Message que se agrega
	 */
	public void addMessage(Message message){
		messages.add(message);
		message._setUser(this);
	}
	/** Elimina un mensaje a la lista de mensajes enviados por el usuario
	 * @param message objeto Message que se agrega
	 */
	public void removeMessage(Message message){
		messages.remove(message);
		message._setUser(null);
	}
	/** Elimina todos los mensajes de la lista de mensajes enviados
	 * por el usuario
	 */
	public void removeAllMessages(){
		for(Message m : Collections.unmodifiableSet(messages)){
			m._setUser(null);
		}
		messages.clear();
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * Publications
	 */
	/** Agrega una publicacion a la lista de publicaciones
	 * generadas o provocadas por el usuario
	 * @param publication objeto Publication que se agrega
	 */
	public void addPublication(Publication publication){
		publications.add(publication);
		publication._setUser(this);
	}
	/** Elimina una publicacion de la lista de publicaciones
	 * generadas o provocadas por el usuario
	 * @param publication objeto Publication que se elimina
	 */
	public void removePublication(Publication publication){
		publications.remove(publication);
		publication._setUser(null);
	}
	/** Elimina todas las publicaciones de la lista de ellas
	 * que posee el usuario */
	public void removeAllPublications(){
		for(Publication p : Collections.unmodifiableSet(publications)){
			p._setUser(null);
		}
		publications.clear();
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * UserNativeLanguages <--> 1 Language
	 */
	/** Agrega un lenguaje a la lista de lenguajes nativos del usuario
	 * @param userNativeLanguage objeto UserNativeLanguage que se agrega
	 */
	public void addUserNativeLanguage(UserNativeLanguage userNativeLanguage){
		userNativeLanguages.add(userNativeLanguage);
		userNativeLanguage._setUser(this);
	}
	/** Elimina un lenguaje de la lista de lenguajes nativos del usuario
	 * @param userNativeLanguage objeto UserNativeLanguage que se elimina
	 */
	public void removeUserNativeLanguage(UserNativeLanguage userNativeLanguage){
		userNativeLanguages.remove(userNativeLanguage);
		userNativeLanguage._setUser(null);
	}
	/** Elimina todos los lenguajes nativos que posee el usuario */
	public void removeAllUserNativeLanguages(){
		for(UserNativeLanguage unl : 
				Collections.unmodifiableSet(userNativeLanguages)){
			unl.unlink();
		}
		userNativeLanguages.clear();
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * UserPracticingLanguages <--> 1 Language
	 */
	/** Agrega un lenguaje a la lista de lenguajes practicados por el usuario
	 * @param userPracticingLanguage objeto UserPracticingLanguage que se agrega
	 */
	public void addUserPracticingLanguage(UserPracticingLanguage
			userPracticingLanguage){
		userPracticingLanguages.add(userPracticingLanguage);
		userPracticingLanguage._setUser(this);
	}
	/** Elimina un lenguaje de la lista de lenguajes practicados por el usuario
	 * @param userPracticingLanguage objeto UserPracticingLanguage
	 * que se elimina
	 */
	public void removeUserPracticingLanguage(
			UserPracticingLanguage userPracticingLanguage){
		userPracticingLanguages.remove(userPracticingLanguage);
		userPracticingLanguage._setUser(null);
	}
	/** Elimina todos los lenguajes practicados que posee el usuario */
	public void removeAllUserPracticedLanguages(){
		for(UserPracticingLanguage upl : 
				Collections.unmodifiableSet(userPracticingLanguages)){
			upl.unlink();
		}
		userPracticingLanguages.clear();
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * Achievements
	 */
	/** Agrega un logro a la lista de ellos que posee el usuario
	 * @param achievement objeto Achievement que se agrega
	 */
	public void addAchievement(Achievement achievement){
		achievements.add(achievement);
		achievement._setUser(this);
	}
	/** Elimina un logro de la lista de ellos que posee el usuario
	 * @param achievement objeto Achievement que se elimina
	 */
	public void removeAchievement(Achievement achievement){
		achievements.remove(achievement);
		achievement._setUser(null);
	}
	/** Elimina todos los logros que posee el usuario */
	public void removeAllAchievements(){
		for(Achievement a : Collections.unmodifiableSet(achievements)){
			a._setUser(null);
		}
		achievements.clear();
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * MessageReceivers <--> 1 Message
	 */
	/** Agrega un mensaje a la lista de mensajes recibidos por el usuario
	 * @param messageReceiver objeto MessageReceiver que se agrega
	 */
	public void addMessageReceiver(MessageReceiver messageReceiver){
		messageReceivers.add(messageReceiver);
		messageReceiver._setUser(this);
	}
	/** Elimina un mensaje de la lista de mensajes recibidos por el usuario
	 * @param messageReceiver objeto MessageReceiver que se elimina
	 */
	public void removeMessageReceiver(MessageReceiver messageReceiver){
		messageReceivers.remove(messageReceiver);
		messageReceiver._setUser(null);
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * PublicationReceivers <--> 1 Publication
	 */
	/** Agrega una publicacion a la lista de publicaciones
	 * que posee el usuario
	 * @param publicationReceiver objeto PublicationReceiver que se agrega
	 */
	public void addPublicationReceiver(
			PublicationReceiver publicationReceiver){
		publicationReceivers.add(publicationReceiver);
		publicationReceiver._setUser(this);
	}
	/** Elimina una publicacion de la lista de publicaciones
	 * que posee el usuario
	 * @param publicationReceiver objeto PublicationReceiver que se elimina
	 */
	public void removePublicationReceiver(
			PublicationReceiver publicationReceiver){
		publicationReceivers.remove(publicationReceiver);
		publicationReceiver._setUser(null);
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * CorrectionAgrees <--> 1 Correction
	 */
	/** Agrega una recomendacion de una correccion a la lista de ellas
	 * que posee el usuario
	 * @param correctionAgree objeto CorrectionAgree que se agrega
	 */
	public void addCorrectionAgree(CorrectionAgree correctionAgree){
		correctionAgreements.add(correctionAgree);
		correctionAgree._setUser(this);
	}
	/** Elimina una recomendacion de una correccion de la lista de ellas
	 * que posee el usuario
	 * @param correctionAgree objeto CorrectionAgree que se elimina
	 */
	public void removeCorrectionAgree(CorrectionAgree correctionAgree){
		correctionAgreements.remove(correctionAgree);
		correctionAgree._setUser(null);
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * CorrectionDisagrees <--> 1 Correction
	 */
	/** Agrega una desaprobacion de una correccion a la lista de ellas
	 * que posee el usuario
	 * @param correctionDisagree objeto CorrectionDisagree que se agrega
	 */
	public void addCorrectionDisagree(CorrectionDisagree correctionDisagree){
		correctionDisagreements.add(correctionDisagree);
		correctionDisagree._setUser(this);
	}
	/** Elimina una desaprobacion de una correccion de la lista de ellas
	 * que posee el usuario
	 * @param correctionAgree objeto CorrectionAgree que se elimina
	 */
	public void removeCorrectionDisagree(CorrectionDisagree correctionDisagree){
		correctionDisagreements.remove(correctionDisagree);
		correctionDisagree._setUser(null);
	}
	
	/** Agrega una votacion de un comentario a la lista de ellas
	 * realizadas por el usuario
	 * @param commentVoter objeto CommentVoter que se agrega
	 */
	public void addCommentVoter(CommentVoter commentVoter){
		commentVoters.add(commentVoter);
		commentVoter._setUser(this);
	}
	/** Elimina una votacion de un comentario de la lista de ellas
	 * realizadas por el usuario
	 * @param commentVoter objeto CommentVoter que se elimina
	 */
	public void removeCommentVoter(CommentVoter commentVoter){
		commentVoters.remove(commentVoter);
		commentVoter._setUser(null);
	}
	
	/* Relacion entre entidades:
	 *  1 User <--> * ThreadVoters <--> 1 Thread
	 */
	/** Agrega una votacion de un hilo a la lista de ellas
	 * realizadas por el usuario
	 * @param forumThreadVoter objeto ForumThreadVoter que se agrega
	 */
	public void addPhorumThreadVoter(ForumThreadVoter forumThreadVoter){
		phorumThreadVoters.add(forumThreadVoter);
		forumThreadVoter._setUser(this);
	}
	/** Elimina una votacion de un hilo de la lista de ellas
	 * realizadas por el usuario
	 * @param forumThreadVoter objeto ForumThreadVoter que se elimina
	 */
	public void removePhorumThreadVoter(ForumThreadVoter forumThreadVoter){
		phorumThreadVoters.remove(forumThreadVoter);
		forumThreadVoter._setUser(null);
	}
	
	// // // // // // // //
	// HASH CODE & EQUALS
	// // // // // // // //
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	// // // // // // //
	// TO STRING
	// // // // // // //
	@Override
	public String toString() {
		return "User [email=" + email
				+ ", dateRegistered=" + dateRegistered.toString()
				+ ", nick=" + nick
				+ ", loginFails=" + loginFails
				+ ", role=" + role.toString()
				+ ", active=" + active
				+ ", locked=" + locked
				+ ", removed=" + removed
				+ "]";
	}
}
