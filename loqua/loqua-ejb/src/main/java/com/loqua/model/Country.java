package com.loqua.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Representa un pais que puede ser el lugar de origen o de ubicacion
 * de un usuario
 * @author Gonzalo
 */
@XmlRootElement(name = "country")
@Entity
@Table(name="Country")
public class Country implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	
	/** Nombre del pais */
	private String name;
	
	/** Codigo ISO 3166 (de dos letras) del pais. No es necesario
	 * que coincida con algun valor de {@link Language#locale} */
	private String codeIso3166;
	
	/** Icono de la bandera del pais */
	private byte[] flagIcon;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Lista de lenguajes del pais */
	@OneToMany(mappedBy="country")
	private Set<CountryLanguage> languages = new HashSet<CountryLanguage>();
	
	/** Lista usuarios originarios del pais */
	@OneToMany(mappedBy="countryOrigin")
	private Set<UserInfoPrivacity> usersOriginary =
		new HashSet<UserInfoPrivacity>();
	
	/** Lista usuarios localizados en el pais */
	@OneToMany(mappedBy="countryLocation")
	private Set<UserInfoPrivacity> usersLocated =
		new HashSet<UserInfoPrivacity>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Country(){}
	
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
	 *  1 Country <--> * CountryLanguage <--> 1 Language
	 */
	@XmlTransient
	public Set<CountryLanguage> getCountryLanguages() {
		return Collections.unmodifiableSet(languages);
	}
	Set<CountryLanguage> _getCountryLanguages() {
		return languages;
	}
	
	/* Relacion entre entidades:
	 *  1 Country <--> * UserInfoPrivacity (users cuyo origen es este country)
	 */
	@XmlTransient
	public Set<UserInfoPrivacity> getUsersOriginary() {
		return Collections.unmodifiableSet(usersOriginary);
	}
	Set<UserInfoPrivacity> _getUserOriginary() {
		return usersOriginary;
	}
	
	/* Relacion entre entidades:
	 *  1 Country <--> * UserInfoPrivacity (users cuya ubicacion es este country)
	 */
	@XmlTransient
	public Set<UserInfoPrivacity> getUsersLocated() {
		return Collections.unmodifiableSet(usersLocated);
	}
	Set<UserInfoPrivacity> _getUsersLocated() {
		return usersLocated;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public String getCodeIso3166() {
		return codeIso3166;
	}
	public void setCodeIso3166(String codeIso3166) {
		this.codeIso3166 = codeIso3166;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlElement
	public byte[] getFlagIcon() {
		return flagIcon;
	}
	public void setFlagIcon(byte[] flagIcon) {
		this.flagIcon = flagIcon;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/* Relacion entre entidades:
	 *  1 Country <--> * CountryLanguages <--> Language
	 */
	
	/** Agrega un lenguaje a la lista de ellos que posee el pais
	 * @param countryLanguage objeto CountryLanguage que se agrega 
	 */
	public void addCountryLanguage(CountryLanguage countryLanguage){
		languages.add(countryLanguage);
		countryLanguage._setCountry(this);
	}
	/** Elimina un lenguaje de la lista de ellos que posee el pais
	 * @param countryLanguage objeto CountryLanguage que se elimina
	 */
	public void removeCountryLanguage(CountryLanguage countryLanguage){
		languages.remove(countryLanguage);
		countryLanguage._setCountry(null);
	}
	
	/* Relacion entre entidades:
	 *  1 Country <--> * UserInfoPrivacity (users cuyo origen es este country)
	 */
	
	/** Agrega un usuario a la lista de usuarios originarios del pais
	 * @param userInfoPrivacity objeto UserInfoPrivacity que se agrega
	 */
	public void addUserOriginary(UserInfoPrivacity userInfoPrivacity){
		usersOriginary.add(userInfoPrivacity);
		userInfoPrivacity._setCountryOrigin(this);
	}
	/** Elimina un usuario de la lista de usuarios originarios del pais
	 * @param userInfoPrivacity objeto UserInfoPrivacity que se elimina
	 */
	public void removeUserOriginary(UserInfoPrivacity userInfoPrivacity){
		usersOriginary.remove(userInfoPrivacity);
		userInfoPrivacity._setCountryOrigin(this);
	}
	
	/* Relacion entre entidades:
	 *  1 Country <--> * UserInfoPrivacity (users cuya ubicacion es este country)
	 */
	
	/** Agrega un usuario a la lista de usuarios localizados en el pais
	 * @param userInfoPrivacity objeto UserInfoPrivacity que se agrega
	 */
	public void addUserLocated(UserInfoPrivacity userInfoPrivacity){
		usersLocated.add(userInfoPrivacity);
		userInfoPrivacity._setCountryLocation(this);
	}
	/** Elimina un usuario de la lista de usuarios localizados en el pais
	 * @param userInfoPrivacity objeto UserInfoPrivacity que se elimina
	 */
	public void removeUserLocated(UserInfoPrivacity userInfoPrivacity){
		usersLocated.remove(userInfoPrivacity);
		userInfoPrivacity._setCountryLocation(this);
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
		Country other = (Country) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	// // // // // // //
	// TO STRING
	// // // // // // //
	@Override
	public String toString() {
		return "Country [name=" + name 
				+ "codeIso3166=" + codeIso3166 + "]";
	}
}
