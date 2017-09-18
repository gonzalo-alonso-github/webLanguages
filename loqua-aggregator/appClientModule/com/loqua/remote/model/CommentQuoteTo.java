package com.loqua.remote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Representa una respuesta (una 'cita') que un comentario publicado
 * por un usuario realiza sobre otro comentario en el foro
 * @author Gonzalo
 */
@XmlRootElement(name = "commentQuoteTo")
@Entity
@Table(name="CommentQuoteTo")
public class CommentQuoteTo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	
	/** Texto plano original del comentario citado ({@link #quotedComment}).
	 * <br/> Este campo se utiliza para que persista ese dato en caso de que
	 * el comentario citado sea eliminado o editado */
	private String quotedText;
	
	/** Texto HTML original del comentario citado ({@link #quotedComment}).
	 * Este campo se utiliza para que persista ese dato en caso de que
	 * el comentario citado sea eliminado o editado */
	private String quotedTextHtml;
	
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@ManyToOne
	@JoinColumn(name="actorComment_id", referencedColumnName="id")
	private Comment actorComment;
	
	@ManyToOne
	@JoinColumn(name="quotedComment_id", referencedColumnName="id")
	private Comment quotedComment;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public CommentQuoteTo(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param actorComment objeto Comment asociado al CommentQuoteTo
	 * @param quotedComment objeto Comment asociado al CommentQuoteTo
	 */
	public CommentQuoteTo(Comment actorComment, Comment quotedComment){
		this.actorComment = actorComment;
		this.quotedComment = quotedComment;
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
	 *  1 Comment <--> * CommentQuoteTo <--> 1 Comment 
	 */
	@XmlTransient
	public Comment getActorComment() {
		return actorComment;
	}
	void _setActorComment( Comment c ) {
		actorComment = c;
	}
	public void setActorComment( Comment c ) {
		actorComment = c;
	}
	public CommentQuoteTo setActorCommentThis( Comment c ) {
		actorComment = c;
		return this;
	}
	
	@XmlTransient
	public Comment getQuotedComment() {
		return quotedComment;
	}
	void _setQuotedComment( Comment c ) {
		quotedComment = c;
	}
	public void setQuotedComment( Comment c ) {
		quotedComment = c;
	}
	public CommentQuoteTo setQuotedCommentThis( Comment c ) {
		quotedComment = c;
		return this;
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
	
	public String getQuotedText() {
		return quotedText;
	}
	public void setQuotedText(String quotedText) {
		this.quotedText = quotedText;
	}
	public CommentQuoteTo setQuotedTextThis(String quotedText) {
		this.quotedText = quotedText;
		return this;
	}
	
	public String getQuotedTextHtml() {
		return quotedTextHtml;
	}
	public void setQuotedTextHtml(String quotedTextHtml) {
		this.quotedTextHtml = quotedTextHtml;
	}
	public CommentQuoteTo setQuotedTextHtmlThis(String quotedTextHtml) {
		this.quotedTextHtml = quotedTextHtml;
		return this;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CommentQuoteTo setIdThis(Long id) {
		this.id = id;
		return this;
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
		CommentQuoteTo other = (CommentQuoteTo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
