package com.loqua.model;

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

/**
 * Representa la informacion del lenuage practicado por un usuario.
 * Es una clase asociativa entre {@link Language} y {@link User}
 * @author Gonzalo
 */
@XmlRootElement(name = "userPracticingLanguage")
@Entity
@Table(name="UserPracticingLanguage")
//@IdClass(UserPracticingLanguageKey.class)
public class UserPracticingLanguage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	
	//@Id @GeneratedValue @ManyToOne private Language language;
	//@Id @GeneratedValue @ManyToOne private User user;
	@ManyToOne @JoinColumn(name="language_id")
	private Language language;
	
	/** Usuario que practica el lenguaje indicado por {@link #language}*/
	@ManyToOne @JoinColumn(name="user_id")
	private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public UserPracticingLanguage(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al UserNativeLanguage
	 * @param language objeto Language asociado al UserNativeLanguage
	 */
	public UserPracticingLanguage(User user, Language language){
		this.user = user;
		this.language = language;
		user.addUserPracticingLanguage( this );
		language.addUserPracticingLanguage( this );
	}

	/** Desasigna de esta entidad a las entidades asociadas */
	public void unlink(){
		user.removeUserPracticingLanguage( this );
		language.removeUserPracticingLanguage( this );
		
		user = null;
		language = null;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	 *  1 User <--> * UserPracticingLanguage <--> 1 Language 
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
	void _setLanguage( Language lan ) {
		language = lan;
	}
	public void setLanguage( Language lan ) {
		language = lan;
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
		UserPracticingLanguage other = (UserPracticingLanguage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
