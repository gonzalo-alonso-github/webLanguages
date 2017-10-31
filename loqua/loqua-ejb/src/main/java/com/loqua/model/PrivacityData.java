package com.loqua.model;

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

import com.loqua.model.types.TypePrivacity;

/**
 * Representa la informacion sobre los niveles de privacidad por defecto
 * de la informacion que el usuario genera en la aplicacion
 * @author Gonzalo
 */
@XmlRootElement(name = "privacityData")
@Entity
@Table(name="PrivacityData")
public class PrivacityData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Lista de los niveles de privacidad admitidos en el Enumerado
	 * {@link TypePrivacity}*/
	@Transient
	private static List<String> privacityLevels = 
			Stream.of(TypePrivacity.values())
			.map(Enum::name).collect(Collectors.toList());
	
	/** Constante con el valor 'PRIVATE' */
	@Transient
	public static final String PRIVATE = "PRIVATE";
	
	/** Constante con el valor 'CONTACTS' */
	@Transient
	public static final String CONTACTS = "CONTACTS";
	
	/** Constante con el valor 'PUBLIC' */
	@Transient
	public static final String PUBLIC = "PUBLIC";
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	
	/** Nivel de privacidad del genero del usuario */
	@Enumerated(EnumType.STRING)
	private TypePrivacity gender
	
	/** Nivel de privacidad de la imagen del perfil del usuario */;
	@Enumerated(EnumType.STRING)
	private TypePrivacity image;
	
	/** Nivel de privacidad del nombre del usuario */;
	@Enumerated(EnumType.STRING)
	private TypePrivacity name;
	
	/** Nivel de privacidad del apellido del usuario */;
	@Enumerated(EnumType.STRING)
	private TypePrivacity surname;
	
	/** Nivel de privacidad del pais de origen del usuario */;
	@Enumerated(EnumType.STRING)
	private TypePrivacity countryOrigin;
	
	/** Nivel de privacidad del pais de ubicacion del usuario */;
	@Enumerated(EnumType.STRING)
	private TypePrivacity countryLocation;
	
	/** Nivel de privacidad de las publicaciones generadas o provocadas
	 * por del usuario */;
	@Enumerated(EnumType.STRING)
	private TypePrivacity publications;
	
	/** Nivel de privacidad de la lista de contactos del usuario */
	@Enumerated(EnumType.STRING)
	private TypePrivacity contactsList;
	
	/** Nivel de permiso de envio de mensajes al usuario:
	 * <ul><li>'PRIVATE': solo los administradores tienen permiso</li>
	 * <li>'CONTACTS': solo los administradores y los contactos del usuario
	 * tienen permiso</li>
	 * <li>'PUBLIC': todos los usuarios registrados tienen permiso</li></ul>
	 */
	@Enumerated(EnumType.STRING)
	private TypePrivacity receivingMessages;
	
	/** Indica si el usuario puede ser encontrado en el buscador de usuarios
	 * por parte de otros usuarios registrados */
	private boolean appearingInSearcher;
	
	/** Indica si los demas usuarios registrados tienen permiso para
	 * enviar solicitudes de correccion al usuario */
	private boolean receivingCorrectionRequests;

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Usuario al que se refiere la informacion */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public PrivacityData(){
		loadDefaultData();
	}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al PrivacityData
	 */
	public PrivacityData(User user){
		this.user = user;
		user.setPrivacityData(this);
		
		loadDefaultData();
	}
	
	/** Carga los niveles de privacidad por defecto de los datos del usuario */
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

	/** Desasigna de esta entidad a las entidades asociadas */
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
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/*
	 * Relacion entre entidades:
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
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
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
