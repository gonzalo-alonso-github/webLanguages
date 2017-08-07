package com.loqua.remote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "userNativeLanguage")
@Entity
@Table(name="UserNativeLanguage")
//@IdClass(UserNativeLanguageKey.class)
public class UserNativeLanguage implements Serializable {

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	
	//@Id @GeneratedValue @ManyToOne private Language language;
	//@Id @GeneratedValue @ManyToOne private User user;
	@ManyToOne @JoinColumn(name="language_id")
	private Language language;
	@ManyToOne @JoinColumn(name="user_id")
	private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public UserNativeLanguage(){}
	
	public UserNativeLanguage(User uip, Language l){
		user = uip;
		language = l;
		uip.addUserNativeLanguage( this );
		l.addUserNativeLanguage( this );
	}
	
	public void unlink(){
		user.removeUserNativeLanguage( this );
		language.removeUserNativeLanguage( this );
		
		user = null;
		language = null;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (GETTERS & SETTERS)
	// // // // // // // // // // // // // //
	
	/** Relaci�n entre entidades:<br>
	 *  1 User <--> * UserNativeLanguage <--> 1 Language 
	 */
	public User getUser() {
		return user;
	}
	void _setUser( User uip ) {
		user = uip;
	}
	public void setUser( User uip ) {
		user = uip;
	}
	public Language getLanguage() {
		return language;
	}
	void _setLanguage( Language lang ) {
		language = lang;
	}
	public void setLanguage( Language lang ) {
		language = lang;
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
		UserNativeLanguage other = (UserNativeLanguage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
