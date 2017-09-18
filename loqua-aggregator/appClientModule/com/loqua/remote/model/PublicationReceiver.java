package com.loqua.remote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.remote.model.compoundkeys.PublicationReceiverKey;

/**
 * Representa la recepcion de una publicacion por parte de un usuario.
 * Es una clase asociativa entre {@link Publication} y {@link User}. <br/><br/>
 * Si bien los 'eventos' siempre generan publicaciones, cada una de estas
 * tiene como minimo un PublicationReceiver referenciando al mismo usuario
 * que la ha provocado. Ademas de este, y si el atributo
 * {@link Publication.privacity} es 'PUBLIC o 'CONTACTS', se genera
 * tambien un PublicationReceiver por cada contacto del usuario
 * @author Gonzalo
 */
@XmlRootElement(name = "publicationReceiver")
@Entity
@Table(name="PublicationReceiver")
@IdClass(PublicationReceiverKey.class)
public class PublicationReceiver implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Publicacion recibida */
	@Id @GeneratedValue @ManyToOne private Publication publication;
	
	/** Usuario que recibe la publicacion */
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public PublicationReceiver(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param pub objeto Publication asociado al PublicationReceiver
	 * @param user objeto User asociado al PublicationReceiver
	 */
	public PublicationReceiver(Publication pub, User user){
		this.publication = pub;
		this.user = user;
		pub._getPublicationReceivers().add( this );
		user._getPublicationReceivers().add( this );
	}

	/** Desasigna de esta entidad a las entidades asociadas */
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
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/* Relacion entre entidades:
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
