package com.loqua.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.model.compoundkeys.CommentVoterKey;

@SuppressWarnings("serial")
@XmlRootElement(name = "commentVoter")
@Entity
@Table(name="CommentVoter")
@IdClass(CommentVoterKey.class)
public class CommentVoter implements Serializable {

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@Id @GeneratedValue @ManyToOne private ForumPost comment;
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public CommentVoter(){}
	
	public CommentVoter(User u, Comment c){
		user = u;
		u._getCommentVoters().add( this );
		
		comment = c;
		c._getCommentVoters().add( this );
	}
	
	public void unlink(){
		user._getCommentVoters().remove( this );
		((Comment)comment)._getCommentVoters().remove( this );
		
		user = null;
		comment = null;
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
	public void setUser( User u ) {
		user = u;
	}
	public CommentVoter setUserThis( User u ) {
		user = u;
		return this;
	}
	
	public ForumPost getComment() {
		return comment;
	}
	void _setComment( ForumPost c ) {
		comment = c;
	}
	public void setComment( ForumPost c ) {
		comment = c;
	}
	public CommentVoter setCommentThis( ForumPost c ) {
		comment = c;
		return this;
	}
}
