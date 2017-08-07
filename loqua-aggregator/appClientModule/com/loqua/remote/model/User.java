package com.loqua.remote.model;

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

import com.loqua.remote.model.types.TypeUserRole;

@XmlRootElement(name = "user")
@Entity
@Table(name="User")
public class User implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Transient
	public static final String ADMINISTRATOR = "ADMINISTRATOR";
	@Transient
	public static final String USER = "USER";
	
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String email;
	private Date dateRegistered;
	private Date dateRemoved;
	private String nick;
	private String password;
	private int loginFails;
	@Enumerated(EnumType.STRING)
	private TypeUserRole role;
	private boolean active;
	private boolean locked;
	private boolean removed;
	private String locale;
	private String urlConfirm;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@OneToOne(mappedBy = "user")
	private UserInfo userInfo;
	
	@OneToOne(mappedBy = "user")
	private UserInfoPrivacity userInfoPrivacity;
	
	@OneToOne(mappedBy = "user")
	private PrivacityData privacityData;
	
	@OneToMany(mappedBy="user")
	private Set<Contact> contacts = new HashSet<Contact>();
	
	@OneToMany(mappedBy="userSender")
	private Set<ContactRequest> contactRequestSent = 
		new HashSet<ContactRequest>();
	@OneToMany(mappedBy="userReceiver")
	private Set<ContactRequest> contactRequestReceived = 
		new HashSet<ContactRequest>();
	
	@OneToMany(mappedBy="user")
	private Set<ForumPost> phorumPosts = new HashSet<ForumPost>();
	
	@OneToMany(mappedBy="user"/*, fetch = FetchType.EAGER*/)
	private Set<UserNativeLanguage> userNativeLanguages = new HashSet<UserNativeLanguage>();
	
	@OneToMany(mappedBy="user"/*, fetch = FetchType.EAGER*/)
	private Set<UserPracticingLanguage> userPracticingLanguages = 
		new HashSet<UserPracticingLanguage>();
	
	@OneToMany(mappedBy="user")
	private Set<Message> messages = new HashSet<Message>();
	
	@OneToMany(mappedBy="user")
	private Set<MessageReceiver> messageReceivers = 
		new HashSet<MessageReceiver>();
	
	@OneToMany(mappedBy="user")
	private Set<Publication> publications = new HashSet<Publication>();
	
	@OneToMany(mappedBy="user")
	private Set<PublicationReceiver> publicationReceivers = 
		new HashSet<PublicationReceiver>();
	
	@OneToMany(mappedBy="user")
	private Set<Achievement> achievements = new HashSet<Achievement>();
	
	@OneToMany(mappedBy="user")
	private Set<CorrectionAgree> correctionAgreements = 
		new HashSet<CorrectionAgree>();
	
	@OneToMany(mappedBy="user")
	private Set<CorrectionDisagree> correctionDisagreements = 
		new HashSet<CorrectionDisagree>();
	
	@OneToMany(mappedBy="user")
	private Set<CommentVoter> commentVoters = new HashSet<CommentVoter>();
	
	@OneToMany(mappedBy="user")
	private Set<ForumThreadVoter> phorumThreadVoters =
		new HashSet<ForumThreadVoter>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public User(){
		userInfoPrivacity = new UserInfoPrivacity();
		userInfoPrivacity.setUser(this);
		
		userInfo = new UserInfo();
		userInfo.setUser(this);
		
		privacityData = new PrivacityData();
		privacityData.setUser(this);
	}
	
	public User( UserInfo userInfo, UserInfoPrivacity userInfoPrivacity,
			PrivacityData pd ){
		this.userInfo = userInfo;
		userInfo.setUser(this);
		
		this.userInfoPrivacity = userInfoPrivacity;
		userInfoPrivacity.setUser(this);
		
		this.privacityData = pd;
		pd.setUser(this);
	}
	
	/**
	 * Este constructor inicializa todos los atributos de la clase
	 * asignandoles el msimo valor que los del objeto dado.
	 * El efecto buscado es que el usuario creado mediante este constructor
	 * sea una copia identica al usuario que se da como parametro.
	 * @param user 
	 */
	public User( User user ){
		// Con el fin de evitar escribir muchas lineas para inicializar
		// los muchos atributos de la clase,
		// se utiliza reflection para tratar de hacerlo de forma automatica.
		try {
			Field[] fields = this.getClass().getDeclaredFields();
			for( Field field : fields ) {
				if( ! Modifier.isFinal(field.getModifiers() )){
					field.set(this, field.get(user));
				}
	        }
		} catch (IllegalArgumentException | IllegalAccessException e) {
			//TODO
			//e.printStackTrace();
		}
	}
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (GETTERS & SETTERS)
	// // // // // // // // // // // // // //
	
	/* A la hora de acceder a una propiedad de una clase o de un bean,
	JSF requiere que exista un getter y un setter de dicha propiedad,
	y ademas los setter deben devolver obligatoriamente 'void'.
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/**
	 * Relacion entre entidades:<br>
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
	
	/**
	 * Relacion entre entidades:<br>
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
	
	/**
	 * Relacion entre entidades:<br>
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
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * Contact
	 */
	@XmlTransient
	public Set<Contact> getContacts() {
		return Collections.unmodifiableSet(contacts);
	}
	Set<Contact> _getContacts() {
		return contacts;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * ContactRequest (contactRequest enviados por el usuario)
	 */
	@XmlTransient
	public Set<ContactRequest> getContactRequestSent() {
		return Collections.unmodifiableSet(contactRequestSent);
	}
	Set<ContactRequest> _getContactRequestSent() {
		return contactRequestSent;
	}
	
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * ContactRequest (contactRequest recibidos por el usuario)
	 */
	@XmlTransient
	public Set<ContactRequest> getContactRequestReceived() {
		return Collections.unmodifiableSet(contactRequestReceived);
	}
	Set<ContactRequest> _getContactRequestReceived() {
		return contactRequestReceived;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * PhorumPost
	 */
	@XmlTransient
	public Set<ForumPost> getPhorumPosts() {
		return Collections.unmodifiableSet(phorumPosts);
	}
	Set<ForumPost> _getPhorumPosts() {
		return phorumPosts;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * UserNativeLanguage <--> 1 Language
	 */
	@XmlTransient
	public Set<UserNativeLanguage> getUserNativeLanguages() {
		return Collections.unmodifiableSet(userNativeLanguages);
	}
	Set<UserNativeLanguage> _getUserNativeLanguages() {
		return userNativeLanguages;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * UserPracticingLanguage <--> 1 Language
	 */
	@XmlTransient
	public Set<UserPracticingLanguage> getUserPracticingLanguages() {
		return Collections.unmodifiableSet(userPracticingLanguages);
	}
	Set<UserPracticingLanguage> _getUserPracticingLanguages() {
		return userPracticingLanguages;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * Messages
	 */
	@XmlTransient
	public Set<Message> getMessages() {
		return Collections.unmodifiableSet(messages);
	}
	Set<Message> _getMessages() {
		return messages;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * MessageReceivers <--> 1 Message
	 */
	@XmlTransient
	public Set<MessageReceiver> getMessageReceivers() {
		return Collections.unmodifiableSet(messageReceivers);
	}
	Set<MessageReceiver> _getMessageReceivers() {
		return messageReceivers;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * Publications
	 */
	@XmlTransient
	public Set<Publication> getPublications() {
		return Collections.unmodifiableSet(publications);
	}
	Set<Publication> _getPublications() {
		return publications;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * PublicationReceivers <--> 1 Publication
	 */
	@XmlTransient
	public Set<PublicationReceiver> getPublicationReceivers() {
		return Collections.unmodifiableSet(publicationReceivers);
	}
	Set<PublicationReceiver> _getPublicationReceivers() {
		return publicationReceivers;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * Achievements
	 */
	@XmlTransient
	public Set<Achievement> getAchievements() {
		return Collections.unmodifiableSet(achievements);
	}
	Set<Achievement> _getAchievements() {
		return achievements;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * CorrectionAgrees <--> 1 Correction
	 */
	@XmlTransient
	public Set<CorrectionAgree> getCorrectionAgreements() {
		return Collections.unmodifiableSet(correctionAgreements);
	}
	Set<CorrectionAgree> _getCorrectionAgreements() {
		return correctionAgreements;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * CorrectioDisaAgrees <--> 1 Correction
	 */
	@XmlTransient
	public Set<CorrectionDisagree> getCorrectionDisagreements() {
		return Collections.unmodifiableSet(correctionDisagreements);
	}
	Set<CorrectionDisagree> _getCorrectionDisagreements() {
		return correctionDisagreements;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * CommentVoters <--> 1 Comment
	 */
	@XmlTransient
	public Set<CommentVoter> getCommentVoters() {
		return Collections.unmodifiableSet(commentVoters);
	}
	Set<CommentVoter> _getCommentVoters() {
		return commentVoters;
	}
	
	/** Relacion entre entidades:<br>
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
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
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
	/*
	public User cloneUser(){
		// requiere 'implements Cloneable'
		User result = null;
		try {
			result = (User) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO
		}
		return result;
	}
	*/
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * Contacts
	 */
	public void addContact(Contact c){
		contacts.add(c);
		c._setUser(this);
	}
	public void removeContact(Contact c){
		contacts.remove(c);
		c._setUser(null);
	}
	public void removeAllContacts(){
		for(Contact c : Collections.unmodifiableSet(contacts)){
			c.unlink();
		}
		contacts.clear();
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * ContactRequest (contactRequest enviados por el usuario)
	 */
	public void addContactRequestSent(ContactRequest c){
		contactRequestSent.add(c);
		c._setUserSender(this);
	}
	public void removeContactRequestSent(ContactRequest c){
		contactRequestSent.remove(c);
		c._setUserSender(null);
	}
	public void removeAllSentContactRequests(){
		for(ContactRequest cr : 
				Collections.unmodifiableSet(contactRequestSent)){
			cr.unlink();
		}
		contactRequestSent.clear();
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * ContactRequest (contactRequest recibidos por el usuario)
	 */
	public void addContactRequestReceived(ContactRequest c){
		contactRequestSent.add(c);
		c._setUserReceiver(this);
	}
	public void removeContactRequestReceived(ContactRequest c){
		contactRequestSent.remove(c);
		c._setUserReceiver(null);
	}
	public void removeAllReceivedContactRequests(){
		for(ContactRequest cr : 
				Collections.unmodifiableSet(contactRequestReceived)){
			cr.unlink();
		}
		contactRequestReceived.clear();
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * PhorumPost
	 */
	public void addPhorumPost(ForumPost p){
		phorumPosts.add(p);
		p._setUser(this);
	}
	public void removePhorumPost(ForumPost p){
		phorumPosts.remove(p);
		p._setUser(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * Messages
	 */
	public void addMessage(Message m){
		messages.add(m);
		m._setUser(this);
	}
	public void removeMessage(Message m){
		messages.remove(m);
		m._setUser(null);
	}
	public void removeAllMessages(){
		for(Message m : Collections.unmodifiableSet(messages)){
			m._setUser(null);
		}
		messages.clear();
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * Publications
	 */
	public void addPublication(Publication p){
		publications.add(p);
		p._setUser(this);
	}
	public void removePublication(Publication p){
		publications.remove(p);
		p._setUser(null);
	}
	public void removeAllPublications(){
		for(Publication p : Collections.unmodifiableSet(publications)){
			p._setUser(null);
		}
		publications.clear();
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * UserNativeLanguages <--> 1 Language
	 */
	public void addUserNativeLanguage(UserNativeLanguage u){
		userNativeLanguages.add(u);
		u._setUser(this);
	}
	public void removeUserNativeLanguage(UserNativeLanguage u){
		userNativeLanguages.remove(u);
		u._setUser(null);
	}
	public void removeAllUserNativeLanguages(){
		for(UserNativeLanguage unl : 
				Collections.unmodifiableSet(userNativeLanguages)){
			unl.unlink();
		}
		userNativeLanguages.clear();
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * Achievements
	 */
	public void addAchievement(Achievement a){
		achievements.add(a);
		a._setUser(this);
	}
	public void removeAchievement(Achievement a){
		achievements.remove(a);
		a._setUser(null);
	}
	public void removeAllAchievements(){
		for(Achievement a : Collections.unmodifiableSet(achievements)){
			a._setUser(null);
		}
		achievements.clear();
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * UserPracticingLanguages <--> 1 Language
	 */
	public void addUserPracticingLanguage(UserPracticingLanguage u){
		userPracticingLanguages.add(u);
		u._setUser(this);
	}
	public void removeUserPracticingLanguage(UserPracticingLanguage u){
		userPracticingLanguages.remove(u);
		u._setUser(null);
	}
	public void removeAllUserPracticedLanguages(){
		for(UserPracticingLanguage upl : 
				Collections.unmodifiableSet(userPracticingLanguages)){
			upl.unlink();
		}
		userPracticingLanguages.clear();
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * MessageReceivers <--> 1 Message
	 */
	public void addMessageReceiver(MessageReceiver msg){
		messageReceivers.add(msg);
		msg._setUser(this);
	}
	public void removeMessageReceiver(MessageReceiver msg){
		messageReceivers.remove(msg);
		msg._setUser(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * PublicationReceivers <--> 1 Publication
	 */
	public void addPublicationReceiver(PublicationReceiver n){
		publicationReceivers.add(n);
		n._setUser(this);
	}
	public void removePublicationReceiver(PublicationReceiver n){
		publicationReceivers.remove(n);
		n._setUser(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * CorrectionAgrees <--> 1 Correction
	 */
	public void addCorrectionAgree(CorrectionAgree c){
		correctionAgreements.add(c);
		c._setUser(this);
	}
	public void removeCorrectionAgree(CorrectionAgree c){
		correctionAgreements.remove(c);
		c._setUser(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * CorrectionDisagrees <--> 1 Correction
	 */
	public void addCorrectionDisagree(CorrectionDisagree c){
		correctionDisagreements.add(c);
		c._setUser(this);
	}
	public void removeCorrectionDisagree(CorrectionDisagree c){
		correctionDisagreements.remove(c);
		c._setUser(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * CommentVoters <--> 1 Comment
	 */
	public void addCommentVoter(CommentVoter cv){
		commentVoters.add(cv);
		cv._setUser(this);
	}
	public void removeCommentVoter(CommentVoter cv){
		commentVoters.remove(cv);
		cv._setUser(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * ThreadVoters <--> 1 Thread
	 */
	public void addPhorumThreadVoter(ForumThreadVoter tv){
		phorumThreadVoters.add(tv);
		tv._setUser(this);
	}
	public void removePhorumThreadVoter(ForumThreadVoter tv){
		phorumThreadVoters.remove(tv);
		tv._setUser(null);
	}
	
	// // // // // // // //
	// HASH CODE & EQUALS
	// // // // // // // //
	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((dateRegistered == null) ? 0 : dateRegistered.hashCode());
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
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (dateRegistered == null) {
			if (other.dateRegistered != null)
				return false;
		} else if (!dateRegistered.equals(other.dateRegistered))
			return false;
		return true;
	}
	*/
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

	public void removeUserData() {
		locale = null;
		dateRemoved = new Date();
		password = null;
		loginFails = 0;
		active = true;
		locked = false;
		removed = true;
		urlConfirm = null;
		// Si queremos borrar un Administrador,
		// ademas de "resetear" esos datos anteriores le reducimos el "role":
		role = TypeUserRole.USER;
	}
}
