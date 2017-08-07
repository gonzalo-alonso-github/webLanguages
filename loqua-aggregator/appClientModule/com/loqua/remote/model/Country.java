package com.loqua.remote.model;

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

@XmlRootElement(name = "country")
@Entity
@Table(name="Country")
public class Country implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String name;
	private String codeIso3166;
	private byte[] flagIcon;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@OneToMany(mappedBy="country")
	private Set<CountryLanguage> languages = new HashSet<CountryLanguage>();
	
	@OneToMany(mappedBy="countryOrigin")
	private Set<UserInfoPrivacity> usersOriginary = new HashSet<UserInfoPrivacity>();
	
	@OneToMany(mappedBy="countryLocation")
	private Set<UserInfoPrivacity> usersLocated = new HashSet<UserInfoPrivacity>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Country(){}
	
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
	 *  1 Country <--> * CountryLanguage <--> 1 Language
	 */
	@XmlTransient
	public Set<CountryLanguage> getCountryLanguages() {
		return Collections.unmodifiableSet(languages);
	}
	Set<CountryLanguage> _getCountryLanguages() {
		return languages;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Country <--> * UserInfoPrivacity (users cuyo origen es este country)
	 */
	@XmlTransient
	public Set<UserInfoPrivacity> getUsersOriginary() {
		return Collections.unmodifiableSet(usersOriginary);
	}
	Set<UserInfoPrivacity> _getUserOriginary() {
		return usersOriginary;
	}
	
	/** Relacion entre entidades:<br>
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
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
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
	
	/** Relacion entre entidades:<br>
	 *  1 Country <--> * CountryLanguages <--> Language
	 */
	public void addCountryLanguage(CountryLanguage c){
		languages.add(c);
		c._setCountry(this);
	}
	public void removeCountryLanguage(CountryLanguage c){
		languages.remove(c);
		c._setCountry(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Country <--> * UserInfoPrivacity (users cuyo origen es este country)
	 */
	public void addUserOriginary(UserInfoPrivacity uip){
		usersOriginary.add(uip);
		uip._setCountryOrigin(this);
	}
	public void removeUserOriginary(UserInfoPrivacity uip){
		usersOriginary.remove(uip);
		uip._setCountryOrigin(this);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Country <--> * UserInfoPrivacity (users cuya ubicacion es este country)
	 */
	public void addUserLocated(UserInfoPrivacity uip){
		usersLocated.add(uip);
		uip._setCountryLocation(this);
	}
	public void removeUserLocated(UserInfoPrivacity uip){
		usersLocated.remove(uip);
		uip._setCountryLocation(this);
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
