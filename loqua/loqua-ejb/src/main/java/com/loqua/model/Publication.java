package com.loqua.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.loqua.model.types.TypePrivacity;

@XmlRootElement(name = "publication")
@Entity
@Table(name="Publication")
public class Publication implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String content;
	private Date datePub;
	private boolean selfGenerated;
	@Enumerated(EnumType.STRING)
	private TypePrivacity privacity;
	private Long eventValue;
	private boolean readPub;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name="event_type", referencedColumnName="type")
	private Event event;
	
	@OneToMany(mappedBy="publication")
	private Set<PublicationReceiver> receivers = 
			new HashSet<PublicationReceiver>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Publication(){}
	
	public Publication(User user, Event event){
		this.user = user;
		this.event = event;
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
	
	/** Relacion entre entidades:<br>
	 *  * Publications <--> 1 User
	 */
	@XmlTransient
	public User getUser() {
		return user;
	}
	void _setUser(User u) {
		user = u;
	}
	public void setUser(User u) {
		user = u;
	}
	public Publication setUserThis(User u) {
		user = u;
		return this;
	}
	
	/** Relacion entre entidades:<br>
	 *  * Publications <--> 1 Event
	 */
	@XmlTransient
	public Event getEvent() {
		return event;
	}
	void _setEvent(Event e) {
		event = e;
	}
	public void setEvent(Event e) {
		event = e;
	}
	public Publication setEventThis(Event e) {
		event = e;
		return this;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Publication <--> * PublicationReceiver <--> 1 User
	 */
	@XmlTransient
	public Set<PublicationReceiver> getPublicationReceivers() {
		return Collections.unmodifiableSet(receivers);
	}
	Set<PublicationReceiver> _getPublicationReceivers() {
		return receivers;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Publication setContentThis(String content) {
		this.content = content;
		return this;
	}
	
	@XmlElement
	public Long getEventValue() {
		return eventValue;
	}
	public void setEventValue(Long eventValue) {
		this.eventValue = eventValue;
	}
	public Publication setEventValueThis(Long eventValue) {
		this.eventValue = eventValue;
		return this;
	}
	
	@XmlElement
	public Date getDatePub() {
		return datePub;
	}
	public void setDatePub(Date date) {
		this.datePub = date;
	}
	public Publication setDatePubThis(Date date) {
		this.datePub = date;
		return this;
	}
	
	@XmlElement
	public boolean getSelfGenerated() {
		return selfGenerated;
	}
	public void setSelfGenerated(boolean selfGenerated) {
		this.selfGenerated = selfGenerated;
	}
	public Publication setSelfGeneratedThis(boolean selfGenerated) {
		this.selfGenerated = selfGenerated;
		return this;
	}
	
	@XmlElement
	public TypePrivacity getPrivacity() {
		return privacity;
	}
	public void setPrivacity(TypePrivacity privacity) {
		this.privacity = privacity;
	}
	public Publication setPrivacityThis(TypePrivacity privacity) {
		this.privacity = privacity;
		return this;
	}
	
	@XmlElement
	public boolean getReadPub() {
		return readPub;
	}
	public void setReadPub(boolean read) {
		this.readPub = read;
	}
	public Publication setReadPubThis(boolean read) {
		this.readPub = read;
		return this;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Publication setIdThis(Long id) {
		this.id = id;
		return this;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/** Relacion entre entidades:<br>
	 *  1 Publication <--> * PublicationReceiver <--> User
	 */
	public void addPublicationReceiver(PublicationReceiver r){
		receivers.add(r);
		r._setPublication(this);
	}
	public void removePublication(PublicationReceiver r){
		receivers.remove(r);
		r._setPublication(null);
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
		Publication other = (Publication) obj;
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
		return "Publication [content=" + content
				+ " datePub=" + datePub.toString()
				+ " selfGenerated=" + selfGenerated
				+ " privacity=" + privacity
				+ " readPub=" + readPub + "]";
	}
}
