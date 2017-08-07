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

@XmlRootElement(name = "language")
@Entity
@Table(name="Language")
public class Language implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String name;
	
	private String locale;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@OneToMany(mappedBy="language")
	private Set<Feed> feeds = new HashSet<Feed>();
	
	@OneToMany(mappedBy="language")
	private Set<CountryLanguage> countries = new HashSet<CountryLanguage>();
	
	@OneToMany(mappedBy="language"/*, fetch = FetchType.EAGER*/)
	private Set<UserNativeLanguage> userNativeLanguages = 
			new HashSet<UserNativeLanguage>();
	
	@OneToMany(mappedBy="language"/*, fetch = FetchType.EAGER*/)
	private Set<UserPracticingLanguage> userPracticingLanguages = 
			new HashSet<UserPracticingLanguage>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Language(){}
	
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
	 *  1 Language <--> * Feeds
	 */
	@XmlTransient
	public Set<Feed> getFeeds() {
		return Collections.unmodifiableSet(feeds);
	}
	Set<Feed> _getFeeds() {
		return feeds;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Language <--> * CountryLanguage <--> 1 Country
	 */
	@XmlTransient
	public Set<CountryLanguage> getCountryLanguages() {
		return Collections.unmodifiableSet(countries);
	}
	Set<CountryLanguage> _getCountryLanguages() {
		return countries;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Language <--> * UserNativeLanguage <--> 1 UserInfoPrivacity
	 */
	@XmlTransient
	public Set<UserNativeLanguage> getUserNativeLanguages() {
		return Collections.unmodifiableSet(userNativeLanguages);
	}
	Set<UserNativeLanguage> _getUserNativeLanguages() {
		return userNativeLanguages;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Language <--> * UserPracticingLanguage <--> 1 UserInfoPrivacity
	 */
	@XmlTransient
	public Set<UserPracticingLanguage> getUserPracticingLanguages() {
		return Collections.unmodifiableSet(userPracticingLanguages);
	}
	Set<UserPracticingLanguage> _getUserPracticingLanguages() {
		return userPracticingLanguages;
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
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/** Relacion entre entidades:<br>
	 *  1 Language <--> * Feeds
	 */
	public void addFeed(Feed f){
		feeds.add(f);
		f._setLanguage(this);
	}
	public void removeFeed(Feed f){
		feeds.remove(f);
		f._setLanguage(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Language <--> * CountryLanguages <--> 1 Country
	 */
	public void addCountryLanguage(CountryLanguage c){
		countries.add(c);
		c._setLanguage(this);
	}
	public void removeCountryLanguage(CountryLanguage c){
		countries.remove(c);
		c._setLanguage(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Language <--> * UserNativeLanguages <--> 1 UserInfoPrivacity
	 */
	public void addUserNativeLanguage(UserNativeLanguage u){
		userNativeLanguages.add(u);
		u._setLanguage(this);
	}
	public void removeUserNativeLanguage(UserNativeLanguage u){
		userNativeLanguages.remove(u);
		u._setLanguage(null);
	}

	/** Relacion entre entidades:<br>
	 *  1 Language <--> * UserPracticingLanguages <--> 1 UserInfoPrivacity
	 */
	public void addUserPracticingLanguage(UserPracticingLanguage u){
		userPracticingLanguages.add(u);
		u._setLanguage(this);
	}
	public void removeUserPracticingLanguage(UserPracticingLanguage u){
		userPracticingLanguages.remove(u);
		u._setLanguage(null);
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
		Language other = (Language) obj;
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
		return "Language [name=" + name + "]";
	}
}
