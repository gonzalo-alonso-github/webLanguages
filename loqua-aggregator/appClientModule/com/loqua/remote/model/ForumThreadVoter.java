package com.loqua.remote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.loqua.remote.model.compoundkeys.ForumThreadVoterKey;

@SuppressWarnings("serial")
@XmlRootElement(name = "forumThreadVoter")
@Entity
@Table(name="ForumThreadVoter")
@IdClass(ForumThreadVoterKey.class)
public class ForumThreadVoter implements Serializable {

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@Id @GeneratedValue @ManyToOne private ForumThread forumThread;
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public ForumThreadVoter(){}
	
	public ForumThreadVoter(User u, ForumThread t){
		user = u;
		u._getForumThreadVoters().add( this );
		
		forumThread = t;
		t._getForumThreadVoters().add( this );
	}
	
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
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/** Relacion entre entidades:<br>
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
