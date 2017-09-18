package com.loqua.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.model.compoundkeys.CorrectionAgreeKey;

/**
 * Representa la recomendacion de una correccion por parte de un usuario.
 * Es una clase asociativa entre {@link Correction} y {@link User}
 * @author Gonzalo
 */
@XmlRootElement(name = "correctionAgree")
@Entity
@Table(name="CorrectionAgree")
@IdClass(CorrectionAgreeKey.class)
public class CorrectionAgree implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Correccion recomendada */
	@Id @GeneratedValue @ManyToOne private ForumPost correction;
	
	/** Usuario que envia la recomendacion de la correccion */
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public CorrectionAgree(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al CorrectionAgree
	 * @param correction objeto Correction asociado al CorrectionAgree
	 */
	public CorrectionAgree(User user, Correction correction){
		this.user = user;
		user._getCorrectionAgreements().add( this );
		
		this.correction = correction;
		correction._getCorrectionAgreements().add( this );
	}

	/** Desasigna de esta entidad a las entidades asociadas */
	public void unlink(){
		user._getCorrectionAgreements().remove( this );
		((Correction)correction)._getCorrectionAgreements().remove( this );
		
		user = null;
		correction = null;
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
	 *  1 User <--> * CorrectionAgrees <--> 1 Correction 
	 */
	public User getUser() {
		return user;
	}
	void _setUser( User u ) {
		user = u;
	}
	public void setUser(User u) {
		user = u;
	}
	public ForumPost getCorrection() {
		return correction;
	}
	void _setCorrection( Correction c ) {
		correction = c;
	}
	public void setCorrection(Correction c) {
		correction = c;
	}
}
