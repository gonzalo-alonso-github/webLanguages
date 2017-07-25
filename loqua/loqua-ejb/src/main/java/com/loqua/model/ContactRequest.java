package com.loqua.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "contactRequest")
@Entity
@Table(name="ContactRequest")
public class ContactRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private Date dateRequest;
	private boolean rejected;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User userSender;
	
	@ManyToOne
	@JoinColumn(name="userReceiver_id", referencedColumnName="id")
	private User userReceiver;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public ContactRequest(){}
	
	public ContactRequest(User userSender, User userReceiver){
		this.userSender = userSender;
		this.userReceiver = userReceiver;
	}
	
	public void unlink(){
		userSender._getContactRequestSent().remove( this );
		userReceiver._getContactRequestReceived().remove( this );
		
		userSender = null;
		userReceiver = null;
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
	 *  * ContactRequests <--> 1 User () (User que envia los ContactRequest)
	 */
	@XmlTransient
	public User getUserSender() {
		return userSender;
	}
	void _setUserSender(User u) {
		userSender = u;
	}
	public void setUserSender(User u) {
		userSender = u;
	}
	public ContactRequest setUserSenderThis(User u) {
		userSender = u;
		return this;
	}
	
	/** Relacion entre entidades:<br>
	 *  * ContactRequests <--> 1 User () (User que recibe los ContactRequest)
	 */
	@XmlTransient
	public User getUserReceiver() {
		return userReceiver;
	}
	void _setUserReceiver(User u) {
		userReceiver = u;
	}
	public void setUserReceiver(User u) {
		userReceiver = u;
	}
	public ContactRequest setUserReceiverThis(User u) {
		userReceiver = u;
		return this;
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
	public Date getDateRequest() {
		return dateRequest;
	}
	public void setDateRequest(Date dateRequest) {
		this.dateRequest = dateRequest;
	}
	public ContactRequest setDateRequestThis(Date dateRequest) {
		this.dateRequest = dateRequest;
		return this;
	}
	
	@XmlElement
	public boolean getRejected() {
		return rejected;
	}
	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}
	public ContactRequest setRejectedThis(boolean rejected) {
		this.rejected = rejected;
		return this;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ContactRequest setIdThis(Long id) {
		this.id = id;
		return this;
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
		ContactRequest other = (ContactRequest) obj;
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
	public String toString() {
		return "ContactRequest [dateRequest=" + dateRequest.toString() + ""
				+ " userReceiver.email="
				+ userReceiver.getEmail() + ""
				+ " userReceiver.dateRegistered="
				+ userReceiver.getDateRegistered() + "]";
	}
}
