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

/**
 * Representa un lenguaje, ya sea el idioma utilizado en una noticia,
 * ya sea el idioma nativo o practicado por los usuarios
 * @author Gonzalo
 */
@XmlRootElement(name = "language")
@Entity
@Table(name="Language")
public class Language implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String name;
	
	/** Codigo de dos letras de la configuracion regional
	 * asociada al lenguaje. No es necesario que coincida con algun valor de
	 * {@link Country#codeIso3166} */
	private String locale;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Lista de fuentes que emiten sus noticias en este lenguaje */
	@OneToMany(mappedBy="language")
	private Set<Feed> feeds = new HashSet<Feed>();
	
	/** Lista de paises donde se utiliza este lenguaje a nivel nativo */
	@OneToMany(mappedBy="language")
	private Set<CountryLanguage> countries = new HashSet<CountryLanguage>();
	
	/** Lista de usuarios que utilizan este lenguaje a nivel nativo */
	@OneToMany(mappedBy="language"/*, fetch = FetchType.EAGER*/)
	private Set<UserNativeLanguage> userNativeLanguages = 
			new HashSet<UserNativeLanguage>();
	
	/** Lista de usuarios que utilizan este lenguaje a nivel de practicante */
	@OneToMany(mappedBy="language"/*, fetch = FetchType.EAGER*/)
	private Set<UserPracticingLanguage> userPracticingLanguages = 
			new HashSet<UserPracticingLanguage>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Language(){}
	
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
	 *  1 Language <--> * Feeds
	 */
	@XmlTransient
	public Set<Feed> getFeeds() {
		return Collections.unmodifiableSet(feeds);
	}
	Set<Feed> _getFeeds() {
		return feeds;
	}
	
	/* Relacion entre entidades:
	 *  1 Language <--> * CountryLanguage <--> 1 Country
	 */
	@XmlTransient
	public Set<CountryLanguage> getCountryLanguages() {
		return Collections.unmodifiableSet(countries);
	}
	Set<CountryLanguage> _getCountryLanguages() {
		return countries;
	}
	
	/* Relacion entre entidades:
	 *  1 Language <--> * UserNativeLanguage <--> 1 UserInfoPrivacity
	 */
	@XmlTransient
	public Set<UserNativeLanguage> getUserNativeLanguages() {
		return Collections.unmodifiableSet(userNativeLanguages);
	}
	Set<UserNativeLanguage> _getUserNativeLanguages() {
		return userNativeLanguages;
	}
	
	/* Relacion entre entidades:
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
	
	/* Relacion entre entidades:
	 *  1 Language <--> * Feeds
	 */
	
	/** Agrega una fuente de noticias a la lista de ellas
	 * que posee el lenguaje
	 * @param feed objeto Feed que se agrega
	 */
	public void addFeed(Feed feed){
		feeds.add(feed);
		feed._setLanguage(this);
	}
	/** Elimina una fuente de noticias de la lista de ellas
	 * que posee el lenguaje
	 * @param feed objeto Feed que se elimina
	 */
	public void removeFeed(Feed feed){
		feeds.remove(feed);
		feed._setLanguage(null);
	}
	
	/* Relacion entre entidades:
	 *  1 Language <--> * CountryLanguages <--> 1 Country
	 */
	/** Agrega un pais a la lista de ellos que posee el lenguaje
	 * @param countryLanguage objeto CountryLanguage que se agrega
	 */
	public void addCountryLanguage(CountryLanguage countryLanguage){
		countries.add(countryLanguage);
		countryLanguage._setLanguage(this);
	}
	/** Elimina un pais de la lista de ellos que posee el lenguaje
	 * @param countryLanguage objeto CountryLanguage que se elimina
	 */
	public void removeCountryLanguage(CountryLanguage countryLanguage){
		countries.remove(countryLanguage);
		countryLanguage._setLanguage(null);
	}
	
	/* Relacion entre entidades:
	 *  1 Language <--> * UserNativeLanguages <--> 1 UserInfoPrivacity
	 */
	/** Agrega un usuario a la lista de ellos que utilizan este lenguaje
	 * a nivel nativo 
	 * @param userNativeLanguage objeto UserNativeLanguage que se agrega
	 */
	public void addUserNativeLanguage(UserNativeLanguage userNativeLanguage){
		userNativeLanguages.add(userNativeLanguage);
		userNativeLanguage._setLanguage(this);
	}
	/** Elimina un usuario de la lista de ellos que utilizan este lenguaje
	 * a nivel nativo 
	 * @param userNativeLanguage objeto UserNativeLanguage que se elimina
	 */
	public void removeUserNativeLanguage(UserNativeLanguage userNativeLanguage){
		userNativeLanguages.remove(userNativeLanguage);
		userNativeLanguage._setLanguage(null);
	}

	/* Relacion entre entidades:
	 *  1 Language <--> * UserPracticingLanguages <--> 1 UserInfoPrivacity
	 */
	/** Agrega un usuario a la lista de ellos que utilizan este lenguaje
	 * a nivel practicante
	 * @param userPracticingLanguage objeto UserPracticingLanguage que se agrega
	 */
	public void addUserPracticingLanguage(
			UserPracticingLanguage userPracticingLanguage){
		userPracticingLanguages.add(userPracticingLanguage);
		userPracticingLanguage._setLanguage(this);
	}
	/** Elimina un usuario de la lista de ellos que utilizan este lenguaje
	 * a nivel practicante
	 * @param userPracticingLanguage objeto UserPracticingLanguage
	 * que se elimina
	 */
	public void removeUserPracticingLanguage(
			UserPracticingLanguage userPracticingLanguage){
		userPracticingLanguages.remove(userPracticingLanguage);
		userPracticingLanguage._setLanguage(null);
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
