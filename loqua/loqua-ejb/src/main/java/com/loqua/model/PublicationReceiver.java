package com.loqua.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.model.compoundkeys.PublicationReceiverKey;

@SuppressWarnings("serial")
@XmlRootElement(name = "publicationReceiver")
@Entity
@Table(name="PublicationReceiver")
@IdClass(PublicationReceiverKey.class)
public class PublicationReceiver implements Serializable{

	//private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@Id @GeneratedValue @ManyToOne private Publication publication;
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public PublicationReceiver(){}
	
	public PublicationReceiver(Publication n, User u){
		publication = n;
		user = u;
		n._getPublicationReceivers().add( this );
		u._getPublicationReceivers().add( this );
	}
	
	public void unlink(){
		publication._getPublicationReceivers().remove( this );
		user._getPublicationReceivers().remove( this );
		
		publication = null;
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
	
	/** Relacion entre entidades:<br>
	 *  1 Publication <--> * PublicationReceiver <--> 1 User 
	 */
	public Publication getPublication() {
		return publication;
	}
	void _setPublication( Publication p ) {
		publication = p;
	}
	public void setPublication( Publication p ) {
		publication = p;
	}
	
	public User getUser() {
		return user;
	}
	void _setUser( User u ) {
		user = u;
	}
	public void setUser(User u) {
		user = u;
	}
}
