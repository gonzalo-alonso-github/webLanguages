package com.loqua.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.loqua.model.types.TypeChangePassword;

/**
 * Representa un cambio de direccion de contrase&ntilde;a
 * de la cuenta de un usuario
 * @author Gonzalo
 */
@XmlRootElement(name = "changePassword")
@Entity
@Table(name="ChangePassword")
public class ChangePassword implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Constante con el valor 'RESTORE' */
	@Transient
	public static final String RESTORE = "RESTORE";
	/** Constante con el valor 'EDIT' */
	@Transient
	public static final String EDIT = "EDIT";
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	
	/** Indica la contrase&ntilde;a que utiliza el usuario, previa a la
	 * confirmacion del cambio */
	private String passwordRemoved;
	
	/** Indica la nueva contrase&ntilde;a que utilizara el usuario una vez que
	 * confirme el cambio. Esta puede ser generada aleatoriamente
	 * (en caso de que se trate de una restauracion de contrase&ntilde;a)
	 * o bien puede ser introducida por el usuario
	 * (en caso de que se trate de una edicion de contrase&ntilde;a) */
	private String passwordGenerated;
	
	/** Cadena aleatoria (de al menos 26 caracteres) que permite identificar
	 * al usuario que accede a la URL de confirmacion de su cambio de
	 * contrase&ntilde;a */
	private String urlConfirm;
	
	/** Indica si el usuario que solicita el cambio de contrase&ntilde;a
	 * ya ha accedido a la URL de confirmacion enviada a su email */
	private boolean confirmed;
	
	/** Fecha en la que el usuario solicita el cambio de contrase&ntilde;a */
	private Date date;
	
	/** Indica el tipo del cambio de contrase&ntilde;a. Es un Enumerado que
	 * admite los siguientes valores:
	 * <ul>
	 * <li>'RESTORE': es una restauracion de contrase&ntilde;a por parte del
	 * usuario</li>
	 * <li>'EDIT': es una edicion de contrase&ntilde;a por parte del usuario
	 * </li></ul> */
	@Enumerated(EnumType.STRING)
	private TypeChangePassword typeChangePassword;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Usuario que da lugar al ChangePassword */
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public ChangePassword(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al ChangePassword
	 */
	public ChangePassword(User user){
		this.user = user;
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
	 *  * Messages <--> 1 User
	 */
	@XmlTransient
	public User getUser() {
		return user;
	}
	void _setUser(User u) {
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
	public String getPasswordRemoved() {
		return passwordGenerated;
	}
	public void setPasswordRemoved(String passwordRemoved) {
		this.passwordRemoved = passwordRemoved;
	}
	
	@XmlElement
	public String getPasswordGenerated() {
		return passwordGenerated;
	}
	public void setPasswordGenerated(String passwordGenerated) {
		this.passwordGenerated = passwordGenerated;
	}
	
	@XmlElement
	public String getUrlConfirm() {
		return urlConfirm;
	}
	public void setUrlConfirm(String urlConfirm) {
		this.urlConfirm = urlConfirm;
	}
	
	@XmlElement
	public boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	@XmlElement
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@XmlElement
	public String getTypeChangePassword() {
		return typeChangePassword.toString();
	}
	public void setTypeChangePassword(String typePasswordChange) {
		this.typeChangePassword =
				Enum.valueOf(TypeChangePassword.class, typePasswordChange);
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
		ChangePassword other = (ChangePassword) obj;
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
		return "PasswordRestore [passwordRemoved=" + passwordRemoved + ""
				+ " passwordGenerated=" + passwordGenerated
				+ " date=" + date.toString()
				+ " urlConfirm=" + urlConfirm 
				+ " confirmed=" + confirmed 
				+ " typeChangePassword=" + typeChangePassword 
				+ "]";
	}
}
