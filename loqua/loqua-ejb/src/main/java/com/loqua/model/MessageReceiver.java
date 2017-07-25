package com.loqua.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.model.compoundkeys.MessageReceiverKey;

@SuppressWarnings("serial")
@XmlRootElement(name = "messageReceiver")
@Entity
@Table(name="MessageReceiver")
@IdClass(MessageReceiverKey.class)
public class MessageReceiver implements Serializable{

	//private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@Id @GeneratedValue @ManyToOne private Message message;
	@Id @GeneratedValue @ManyToOne private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public MessageReceiver(){}
	
	public MessageReceiver(Message msg, User u){
		message = msg;
		user = u;
		msg._getMessageReceivers().add( this );
		u._getMessageReceivers().add( this );
	}
	
	public void unlink(){
		message._getMessageReceivers().remove( this );
		user._getMessageReceivers().remove( this );
		
		message = null;
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
	 *  1 Message <--> * MessageReceiver <--> 1 User 
	 */
	public Message getMessage() {
		return message;
	}
	void _setMessage( Message msg ) {
		message = msg;
	}
	public User getUser() {
		return user;
	}
	void _setUser( User u ) {
		user = u;
	}
}
