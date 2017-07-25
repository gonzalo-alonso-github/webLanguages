package com.loqua.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fhorumThreadInfo")
@Entity
@Table(name="ForumThreadInfo")
public class ForumThreadInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private int countVisits;
	private int countComments;
	private int countVotes;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@OneToOne(cascade = CascadeType.ALL) @JoinColumn(name="forumThread_id")
	private ForumThread forumThread;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public ForumThreadInfo(){
		forumThread = new ForumThread();
		forumThread.setForumThreadInfo(this);
	}
	
	public ForumThreadInfo( ForumThread thread ){
		this.forumThread = thread;
		thread.setForumThreadInfo(this);
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
	
	/**
	 * Relacion entre entidades:<br>
	 *  1 ThreadInfo <--> 1 Thread
	 */
	/*@XmlTransient*/@XmlElement
	public ForumThread getForumThread() {
		return forumThread;
	}
	ForumThread _getForumThread() {
		return forumThread;
	}
	public void setForumThread(ForumThread trhead) {
		this.forumThread = trhead;
	}
	
	// // // //
	// METODOS
	// // // //	
	
	public void incrementCountVisits() {
		countVisits += 1;
	}
	
	public void incrementCountComments() {
		countComments += 1;
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
	public int getCountVisits() {
		return countVisits;
	}
	public void setCountVisits(int countVisits) {
		this.countVisits = countVisits;
	}

	@XmlElement
	public int getCountComments() {
		return countComments;
	}
	public void setCountComments(int countComments) {
		this.countComments = countComments;
	}

	@XmlElement
	public int getCountVotes() {
		return countVotes;
	}
	public void setCountVotes(int countVotes) {
		this.countVotes = countVotes;
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
		ForumThreadInfo other = (ForumThreadInfo) obj;
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
		return "ForumThreadInfo [countVisits=" + countVisits
				+ ", countComments=" + countComments
				+ ", countVotes=" + countVotes + "]";
	}
}
