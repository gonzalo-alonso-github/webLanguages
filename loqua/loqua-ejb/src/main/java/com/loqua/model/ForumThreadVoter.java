package com.loqua.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.loqua.model.compoundkeys.ForumThreadVoterKey;

/**
 * Representa la votacion de un hilo del foro por parte de un usuario.
 * Es una clase asociativa entre {@link ForumThread} y {@link User}
 * @author Gonzalo
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "forumThreadVoter")
@Entity
@Table(name="ForumThreadVoter")
@IdClass(ForumThreadVoterKey.class)
public class ForumThreadVoter implements Serializable {

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Hilo del foro que ha recibido el voto */
	@Id @GeneratedValue @ManyToOne private ForumThread forumThread;
	
	/** Usuario que vota el hilo del foro */
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public ForumThreadVoter(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al ForumThreadVoter
	 * @param forumThread objeto ForumThread asociado al ForumThreadVoter
	 */
	public ForumThreadVoter(User user, ForumThread forumThread){
		this.user = user;
		user._getForumThreadVoters().add( this );
		
		this.forumThread = forumThread;
		forumThread._getForumThreadVoters().add( this );
	}

	/** Desasigna de esta entidad a las entidades asociadas */
	public void unlink(){
		user._getForumThreadVoters().remove( this );
		forumThread._getForumThreadVoters().remove( this );
		
		user = null;
		forumThread = null;
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
	 *  1 User <--> * ForumThreadVoters <--> 1 ForumThread 
	 */
	public User getUser() {
		return user;
	}
	void _setUser( User u ) {
		user = u;
	}
	public void setUser( User u ) {
		user = u;
	}
	
	@XmlTransient
	public ForumThread getForumThread() {
		return forumThread;
	}
	void _setForumThread( ForumThread t ) {
		forumThread = t;
	}
	public void setForumThread( ForumThread t ) {
		forumThread = t;
	}
}
