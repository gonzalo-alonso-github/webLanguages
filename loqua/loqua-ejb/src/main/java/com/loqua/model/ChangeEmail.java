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

@XmlRootElement(name = "changeEmail")
@Entity
@Table(name="ChangeEmail")
public class ChangeEmail implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String previousEmail;
	private String newEmail;
	private Date date;
	private String urlConfirm;
	private boolean confirmedPreviousEmail;
	private boolean confirmedNewEmail;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public ChangeEmail(){}
	
	public ChangeEmail(User user){
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
	 *  * EmailChange <--> 1 User
	 */
	@XmlTransient
	public User getUser() {
		return user;
	}
	void _setUser(User u) {
		user = u;
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
	public String getPreviousEmail() {
		return previousEmail;
	}
	public void setPreviousEmail(String previousEmail) {
		this.previousEmail = previousEmail;
	}
	
	@XmlElement
	public String getNewEmail() {
		return newEmail;
	}
	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}
	
	@XmlElement
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@XmlElement
	public String getUrlConfirm() {
		return urlConfirm;
	}
	public void setUrlConfirm(String urlConfirm) {
		this.urlConfirm = urlConfirm;
	}
	
	@XmlElement
	public boolean getConfirmedPreviousEmail() {
		return confirmedPreviousEmail;
	}
	public void setConfirmedPreviousEmail(boolean confirmedPreviousEmail) {
		this.confirmedPreviousEmail = confirmedPreviousEmail;
	}
	
	@XmlElement
	public boolean getConfirmedNewEmail() {
		return confirmedNewEmail;
	}
	public void setConfirmedNewEmail(boolean confirmedNewEmail) {
		this.confirmedNewEmail = confirmedNewEmail;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
		ChangeEmail other = (ChangeEmail) obj;
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
		return "EmailChange [previousEmail=" + previousEmail + ""
				+ " newEmail=" + newEmail  + "]"
				+ " dateEmailChange=" + date.toString()  + "]"
				+ " urlConfirm=" + urlConfirm  + "]"
				+ " confirmedPreviousEmail=" + confirmedPreviousEmail  + "]"
				+ " confirmedNewEmail=" + confirmedNewEmail  + "]";
	}
}
