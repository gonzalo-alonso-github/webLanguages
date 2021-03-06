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

/**
 * Representa una relacion de contacto entre dos usuarios. <br><br>
 * La relacion de contacto entre dos usuarios es reciproca,
 * a imitacion de como sucede en Facebook o Linkedin (donde un usuario es
 * contacto de otro y viceversa), y a diferencia de Twitter (donde un usuario
 * 'sigue' a otros). 
 * @author Gonzalo
 */
@XmlRootElement(name = "contact")
@Entity
@Table(name="Contact")
public class Contact implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private Date dateJoin;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Usuario al que pertenece el contacto */
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	/** Usuario al que referencia el contacto */
	@ManyToOne
	@JoinColumn(name="userContact_id", referencedColumnName="id")
	private User userContact;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Contact(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User al que pertenece el Contact
	 * @param contactOfUser objeto User al que referencia el Contact
	 */
	public Contact(User user, User contactOfUser){
		this.user = user;
		this.userContact = contactOfUser;
	}

	/** Desasigna de esta entidad a las entidades asociadas */
	public void unlink(){
		user._getContacts().remove( this );
		userContact._getContacts().remove( this );
		
		user = null;
		userContact = null;
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
	 *  * Contact <--> 1 User
	 *  (donde User = primer usuario en relacion de contacto)
	 */
	@XmlTransient
	public User getUser() {
		return user;
	}
	public void setUser(User u) {
		user = u;
	}
	void _setUser(User u) {
		user = u;
	}
	public Contact setUserThis(User u) {
		user = u;
		return this;
	}
	
	/* Relacion entre entidades:
	 *  * Contact <--> 1 User
	 *  (donde User = el otro usuario en relacion de contacto)
	 */
	@XmlTransient
	public User getUserContact() {
		return userContact;
	}
	public void setUserContact(User u) {
		userContact = u;
	}
	void _setUserContact(User u) {
		userContact = u;
	}
	public Contact setUserContactThis(User u) {
		userContact = u;
		return this;
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
	public Date getDateJoin() {
		return dateJoin;
	}
	public void setDateJoin(Date dateJoin) {
		this.dateJoin = dateJoin;
	}
	public Contact setDateJoinThis(Date dateJoin) {
		this.dateJoin = dateJoin;
		return this;
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
		Contact other = (Contact) obj;
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
		return "Contact [dateJoin=" + dateJoin.toString() + ""
				+ " userContact.email=" + userContact.getEmail() + ""
				+ " userContact.dateRegistered="
				+ userContact.getDateRegistered() + "]";
	}
}
