package com.loqua.remote.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.remote.model.types.TypePrivacity;

@XmlRootElement(name = "privacityData")
@Entity
@Table(name="PrivacityData")
public class PrivacityData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Transient
	private static List<String> privacityLevels = 
			Stream.of(TypePrivacity.values())
			.map(Enum::name).collect(Collectors.toList());
	@Transient
	public static final String PRIVATE = "PRIVATE";
	@Transient
	public static final String CONTACTS = "CONTACTS";
	@Transient
	public static final String PUBLIC = "PUBLIC";
	
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	@Enumerated(EnumType.STRING)
	private TypePrivacity gender;
	@Enumerated(EnumType.STRING)
	private TypePrivacity image;
	@Enumerated(EnumType.STRING)
	private TypePrivacity name;
	@Enumerated(EnumType.STRING)
	private TypePrivacity surname;
	@Enumerated(EnumType.STRING)
	private TypePrivacity countryOrigin;
	@Enumerated(EnumType.STRING)
	private TypePrivacity countryLocation;
	@Enumerated(EnumType.STRING)
	private TypePrivacity publications;
	@Enumerated(EnumType.STRING)
	private TypePrivacity contactsList;
	@Enumerated(EnumType.STRING)
	private TypePrivacity receivingMessages;
	private boolean appearingInSearcher;
	private boolean receivingCorrectionRequests;

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public PrivacityData(){
		loadDefaultData();
	}
	
	public PrivacityData(User user){
		this.user = user;
		user.setPrivacityData(this);
		
		loadDefaultData();
	}
	
	private void loadDefaultData() {
		gender = Enum.valueOf(TypePrivacity.class, PUBLIC);
		image = Enum.valueOf(TypePrivacity.class, PUBLIC);
		name = Enum.valueOf(TypePrivacity.class, PUBLIC);
		surname = Enum.valueOf(TypePrivacity.class, PUBLIC);
		countryOrigin = Enum.valueOf(TypePrivacity.class, PUBLIC);
		countryLocation = Enum.valueOf(TypePrivacity.class, PUBLIC);
		publications = Enum.valueOf(TypePrivacity.class, PUBLIC);
		contactsList = Enum.valueOf(TypePrivacity.class, PUBLIC);
		receivingMessages = Enum.valueOf(TypePrivacity.class, PUBLIC);
		appearingInSearcher = true;
		receivingCorrectionRequests = true;
	}
	
	public void unlink(){
		user.setPrivacityData( null );
		user = null;
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
	
	/**
	 * Relacion entre entidades:<br>
	 *  1 PrivacityData <--> 1 User
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
	public TypePrivacity getGender() {
		return gender;
	}
	public void setGender(TypePrivacity gender) {
		this.gender = gender;
	}

	@XmlElement
	public TypePrivacity getImage() {
		return image;
	}
	public void setImage(TypePrivacity image) {
		this.image = image;
	}

	@XmlElement
	public TypePrivacity getName() {
		return name;
	}
	public void setName(TypePrivacity name) {
		this.name = name;
	}

	@XmlElement
	public TypePrivacity getSurname() {
		return surname;
	}
	public void setSurname(TypePrivacity surname) {
		this.surname = surname;
	}

	@XmlElement
	public TypePrivacity getCountryOrigin() {
		return countryOrigin;
	}
	public void setCountryOrigin(TypePrivacity countryOrigin) {
		this.countryOrigin = countryOrigin;
	}

	@XmlElement
	public TypePrivacity getCountryLocation() {
		return countryLocation;
	}
	public void setCountryLocation(TypePrivacity countryLocation) {
		this.countryLocation = countryLocation;
	}

	@XmlElement
	public TypePrivacity getPublications() {
		return publications;
	}
	public void setPublications(TypePrivacity publications) {
		this.publications = publications;
	}

	@XmlElement
	public TypePrivacity getContactsList() {
		return contactsList;
	}
	public void setContactsList(TypePrivacity contactsList) {
		this.contactsList = contactsList;
	}
	
	@XmlElement
	public TypePrivacity getReceivingMessages() {
		return receivingMessages;
	}
	public void setReceivingMessages(TypePrivacity receivingMessages) {
		this.receivingMessages = receivingMessages;
	}
	
	@XmlElement
	public boolean getAppearingInSearcher() {
		return appearingInSearcher;
	}
	public void setAppearingInSearcher(boolean appearingInSearcher) {
		this.appearingInSearcher = appearingInSearcher;
	}
	
	@XmlElement
	public boolean getReceivingCorrectionRequests() {
		return receivingCorrectionRequests;
	}
	public void setReceivingCorrectionRequests(boolean receivingCorrections) {
		this.receivingCorrectionRequests = receivingCorrections;
	}
	
	@XmlElement//@XmlTransient
	public static List<String> getPrivacityLevels() {
		return Collections.unmodifiableList(privacityLevels);
	}
	List<String> _getPrivacityLevels() {
		return privacityLevels;
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
		PrivacityData other = (PrivacityData) obj;
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
		return "PrivacityData [gender=" + gender + ", image=" + image
				+ ", name=" + name + ", surname=" + surname
				+ ", countryOrigin=" + countryOrigin
				+ ", countryLocation=" + countryLocation
				+ ", publications=" + publications
				+ ", contactsList=" + contactsList
				+ ", receivingMessages=" + receivingMessages
				+ ", appearingInSearcher=" + appearingInSearcher
				+ ", receivingCorrectionRequests=" + receivingCorrectionRequests
				+ "]";
	}
}
