package com.loqua.remote.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
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

@XmlRootElement(name = "message")
@Entity
@Table(name="Message")
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String content;
	private Date date;
	private boolean read;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	@OneToMany(mappedBy="message")
	private Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Message(){}
	
	public Message(User user){
		this.user = user;
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
	 *  * Messages <--> 1 User
	 */
	@XmlTransient
	public User getUser() {
		return user;
	}
	void _setUser(User u) {
		user = u;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Message <--> * MessageReceiver <--> 1 User
	 */
	@XmlTransient
	public Set<MessageReceiver> getMessageReceivers() {
		return Collections.unmodifiableSet(receivers);
	}
	Set<MessageReceiver> _getMessageReceivers() {
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
	
	@XmlElement
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlElement
	public boolean getRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/** Relacion entre entidades:<br>
	 *  1 Message <--> * MessageReceiver <--> User
	 */
	public void addMessageReceiver(MessageReceiver r){
		receivers.add(r);
		r._setMessage(this);
	}
	public void removeMessageReceiver(MessageReceiver r){
		receivers.remove(r);
		r._setMessage(null);
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
		Message other = (Message) obj;
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
		return "Message [content=" + content + ""
				+ " date=" + date.toString() 
				+ " read=" + read + "]";
	}
}
