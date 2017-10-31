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

/**
 * Representa la votacion de un comentario por parte de un usuario.
 * Es una clase asociativa entre {@link Comment} y {@link User}
 * @author Gonzalo
 */
@XmlRootElement(name = "commentVoter")
@Entity
@Table(name="CommentVoter")
@IdClass(CommentVoterKey.class)
public class CommentVoter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Comentario votado por el usuario */
	@Id @GeneratedValue @ManyToOne private ForumPost comment;
	
	/** Usuario que vota el comentario */
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public CommentVoter(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al CommentVoter
	 * @param comment objeto Comment asociado al CommentVoter
	 */
	public CommentVoter(User user, Comment comment){
		this.user = user;
		user._getCommentVoters().add( this );
		
		this.comment = comment;
		comment._getCommentVoters().add( this );
	}
	
	/** Desasigna de esta entidad a las entidades asociadas */
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
