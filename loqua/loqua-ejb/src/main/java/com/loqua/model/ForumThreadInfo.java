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
import javax.xml.bind.annotation.XmlTransient;

/**
 * Representa la informacion sobre la relevancia y la puntuacion de un hilo
 * del foro. <br/><br/>
 * En lugar de albergar esta informacion en {@link ForumThread} esta entidad
 * corresponde a la tabla ForumThreadInfo, que debido al previsible
 * alto ritmo de operaciones que van a soportar sus campos, esta separada
 * de la tabla ForumThread
 * @author Gonzalo
 */
@XmlRootElement(name = "forumThreadInfo")
@Entity
@Table(name="ForumThreadInfo")
public class ForumThreadInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	
	/** Cantidad de visitas que ha recibido el hilo del foro */
	private int countVisits;
	/** Cantidad de comentarios que ha recibido el hilo del foro */
	private int countComments;
	/** Cantidad de puntos (o 'votos') que ha recibido el hilo del foro */
	private int countVotes;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Hilo del foro al que pertenece la informacion de esta entidad */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="forumThread_id", referencedColumnName="id")
	private ForumThread forumThread;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public ForumThreadInfo(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param thread objeto ForumThread asociado al ForumThreadInfo
	 */
	public ForumThreadInfo( ForumThread thread ){
		this.forumThread = thread;
		thread.setForumThreadInfo(this);
	}

	/** Desasigna de esta entidad a las entidades asociadas */
	public void unlink(){
		forumThread.setForumThreadInfo( null );
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
	
	/**
	 * Relacion entre entidades:<br>
	 *  1 ThreadInfo <--> 1 Thread
	 */
	@XmlTransient
	public ForumThread getForumThread() {
		return forumThread;
	}
	ForumThread _setForumThread() {
		return forumThread;
	}
	public void setForumThread(ForumThread trhead) {
		this.forumThread = trhead;
	}
	
	// // // //
	// METODOS
	// // // //	
	
	/** Incrementa el numero de visitas del hilo */
	public void incrementCountVisits() {
		countVisits += 1;
	}
	
	/** Incrementa el numero de comentarios del hilo */
	public void incrementCountComments() {
		countComments += 1;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/* A la hora de acceder a una propiedad de una clase o de un bean,
	JSF requiere que exista un getter y un setter de dicha propiedad,
	y ademas los setter deben devolver obligatoriamente 'void'.
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
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
