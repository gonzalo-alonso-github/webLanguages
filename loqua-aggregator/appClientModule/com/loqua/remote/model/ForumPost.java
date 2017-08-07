package com.loqua.remote.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "forumPost")
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="postType")
@Table(name="ForumPost")
public class ForumPost implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) protected Long id;
	protected Date date;
	//@Transient protected String postType;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	protected User user;

	@ManyToOne
	@JoinColumn(name="forumThread_id", referencedColumnName="id")
	protected ForumThread forumThread;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public ForumPost(){}
		
	public ForumPost(User user, ForumThread thread){
		this.user = user;
		this.forumThread = thread;
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
	 *  * ForumPosts <--> 1 User
	 */
	@XmlTransient
	public User getUser() {
		return user;
	}
	void _setUser(User u) {
		user = u;
	}
	public void setUser(User u) {
		user = u;
	}
	public ForumPost setUserThis(User u) {
		user = u;
		return this;
	}
	
	/** Relacion entre entidades:<br>
	 *  * ForumPosts <--> 1 Thread
	 */
	@XmlTransient
	public ForumThread getForumThread() {
		return forumThread;
	}
	void _setForumThread(ForumThread thread) {
		this.forumThread = thread;
	}
	public void setForumThread(ForumThread thread) {
		this.forumThread = thread;
	}
	public ForumPost setForumThreadThis(ForumThread thread) {
		this.forumThread = thread;
		return this;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/* A la hora de acceder a una propiedad de una clase o de un bean,
	JSF requiere que exista un getter y un setter de dicha propiedad,
	y ademas los setter deben devolver obligatoriamente 'void'.
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	@XmlElement
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ForumPost setDateThis(Date date) {
		this.date = date;
		return this;
	}
	/*
	@XmlElement
	@Transient
	public String getPostType() {
		return postType;
	}
	@Transient
	public void setPostType(String postType) {
		this.postType = postType;
	}
	@Transient
	public ForumPost setPostTypeThis(String postType) {
		this.postType = postType;
		return this;
	}
	*/
	@Transient
	public String getDiscriminatorValue() {
	    return this.getClass().getAnnotation(DiscriminatorValue.class).value();
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ForumPost setIdThis(Long id) {
		this.id = id;
		return this;
	}
	
	@XmlElement
	public Comment getAsCommnent() {
		Comment result = null;
		if( getDiscriminatorValue().equals("TypeComment") ){
			result = ((Comment)this);
		}else if(getDiscriminatorValue().equals("TypeCorrection")){
			result = ((Correction)this).getComment();
		}
		return result;
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
		ForumPost other = (ForumPost) obj;
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
		return "ForumPost [date= " + date.toString()  + ""
				+ ", getDiscriminatorValue()= " + getDiscriminatorValue() + "]";
	}
}
