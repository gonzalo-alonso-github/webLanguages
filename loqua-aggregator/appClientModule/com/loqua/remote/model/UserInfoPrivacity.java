package com.loqua.remote.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Representa la informacion sobre los datos estrictamente personales
 * de un usuario. <br/><br/>
 * En lugar de albergar esta informacion en {@link User} esta entidad
 * corresponde a la tabla UserInfoPrivacity, que almacena datos
 * que no son de acceso ni referentes al estado del usuario
 * @author Gonzalo
 */
@XmlRootElement(name = "userInfoPrivacity")
@Entity
@Table(name="UserInfoPrivacity")
public class UserInfoPrivacity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private boolean gender;
	/** Imagen del perfil de usuario */
	private byte[] image;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Usuario al que se refiere la informacion */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	/** Pais de origen del usuario */
	@ManyToOne @JoinColumn(name="countryOrigin_id")
	private Country countryOrigin;
	
	/** Pais de ubicacion del usuario */
	@ManyToOne @JoinColumn(name="countryLocation_id")
	private Country countryLocation;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public UserInfoPrivacity(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al UserInfoPrivacity
	 * @param cOrigin objeto Country que indica el pais de origen
	 * asociado al UserInfoPrivacity
	 * @param cLocation objeto Country que indica el pais de ubicacion
	 * asociado al UserInfoPrivacity
	 */
	public UserInfoPrivacity(User user,
			Country cOrigin, Country cLocation){
		this.user = user;
		user.setUserInfoPrivacity(this);
		
		this.countryOrigin = cOrigin;
		this.countryLocation = cLocation;
	}

	/** Desasigna de esta entidad a las entidades asociadas */
	public void unlink(){
		user.setUserInfoPrivacity( null );
		user = null;
		
		countryOrigin = null;
		countryLocation = null;
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
	
	/**
	 * Relacion entre entidades:<br>
	 *  1 UserInfoPrivacity <--> 1 User
	 */
	/*@XmlTransient*/@XmlElement
	public User getUser() {
		return user;
	}
	User _getUser() {
		return user;
	}
	public void setUser(User u) {
		user = u;
	}
	
	/* Relacion entre entidades:
	 *  * UserInfoPrivacity <--> 1 Country (country de origen del user)
	 */
	@XmlTransient
	public Country getCountryOrigin() {
		return countryOrigin;
	}
	public void setCountryOrigin(Country c) {
		countryOrigin = c;
	}
	void _setCountryOrigin(Country c) {
		countryOrigin = c;
	}
	
	/* Relacion entre entidades:
	 *  * UserInfoPrivacity <--> 1 Country (country de residencia del user)
	 */
	@XmlTransient
	public Country getCountryLocation() {
		return countryLocation;
	}
	public void setCountryLocation(Country c) {
		countryLocation = c;
	}
	void _setCountryLocation(Country c) {
		countryLocation = c;
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
	public boolean getGender() {
		return gender;
	}
	public void setGender(boolean gender) {
		this.gender = gender;
	}

	@XmlElement
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	/*
	@XmlElement
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	@XmlElement
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	*/
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
		UserInfoPrivacity other = (UserInfoPrivacity) obj;
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
		return "UserInfoPrivacity ["
				+ "gender=" + gender
				+ ", countryOrigin=" + countryOrigin.toString()
				+ ", countryLocation=" + countryLocation.toString()+ "]";
	}
}
