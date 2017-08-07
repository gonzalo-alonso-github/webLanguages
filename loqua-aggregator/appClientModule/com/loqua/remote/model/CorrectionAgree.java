package com.loqua.remote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.remote.model.compoundkeys.CorrectionAgreeKey;

@SuppressWarnings("serial")
@XmlRootElement(name = "correctionAgree")
@Entity
@Table(name="CorrectionAgree")
@IdClass(CorrectionAgreeKey.class)
public class CorrectionAgree implements Serializable {

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@Id @GeneratedValue @ManyToOne private ForumPost correction;
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public CorrectionAgree(){}
	
	public CorrectionAgree(User u, Correction c){
		user = u;
		u._getCorrectionAgreements().add( this );
		
		correction = c;
		c._getCorrectionAgreements().add( this );
	}
	
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
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/** Relacion entre entidades:<br>
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
