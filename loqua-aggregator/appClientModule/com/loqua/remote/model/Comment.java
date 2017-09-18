package com.loqua.remote.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Representa un comentario publicado por un usuario en el foro.
 * Es una clase heredada de {@link ForumPost}, al igual que {@link Correction}
 * @author Gonzalo
 */
@XmlRootElement(name = "comment")
@Entity
@DiscriminatorValue("TypeComment")
@Table(name="Comment")
public class Comment extends ForumPost implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Texto plano del comentario */
	private String text;
	/** Texto HTML del comentario */
	private String textHtml;
	/** Posicion del comentario entre todos los comentarios
	 * pertenecientes al mismo hilo, ordenados por su fecha */
	private int positionIndex;
	/** Cantidad de puntos ('votos') que ha recibido el comentario */
	private int numVotes;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Lista de correciones del comentario */
	@OneToMany(mappedBy="comment")
	private Set<Correction> corrections = new HashSet<Correction>();
	
	/** Lista de votaciones (o puntuaciones) del comentario */
	@OneToMany(mappedBy="comment")
	private Set<CommentVoter> commentVoters = new HashSet<CommentVoter>();
	
	/** Lista de comentarios citados por este comentario. <br/> En esta version
	 * de la aplicacion solo se permite que cada comentario cite a un solo
	 * comentario, por tanto esta lista tiene a lo sumo un solo elemento.
	 * Sin embargo si se espera permitirlo en proximas versiones, por eso
	 * se implementa como una lista */
	@OneToMany(mappedBy="actorComment")
	private Set<CommentQuoteTo> commentsQuotedByThis =
		new HashSet<CommentQuoteTo>();
	
	/** Lista de comentarios que citan a este comentario */
	@OneToMany(mappedBy="quotedComment")
	private Set<CommentQuoteTo> commentQuotesToThis =
		new HashSet<CommentQuoteTo>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Comment(){}
	
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
	 *  1 Comment <--> * Corrections
	 */
	@XmlTransient
	public Set<Correction> getCorrections() {
		return Collections.unmodifiableSet(corrections);
	}
	Set<Correction> _getCorrections() {
		return corrections;
	}
	
	/* Relacion entre entidades:
	 *  1 Comment <--> * CommentVoters <--> 1 User
	 */
	@XmlTransient
	public Set<CommentVoter> getCommentVoters() {
		return Collections.unmodifiableSet(commentVoters);
	}
	Set<CommentVoter> _getCommentVoters() {
		return commentVoters;
	}
	
	/* Relacion entre entidades:
	 *  1 Comment <--> * CommentsQuoteTo <--> 1 Comment
	 */
	@XmlTransient
	public Set<CommentQuoteTo> getCommentQuotedByThis() {
		return Collections.unmodifiableSet(commentsQuotedByThis);
	}
	Set<CommentQuoteTo> _getCommentQuotedByThis() {
		return commentsQuotedByThis;
	}
	
	/* Relacion entre entidades:
	 *  1 Comment <--> * CommentsQuoteTo <--> 1 Comment
	 */
	@XmlTransient
	public Set<CommentQuoteTo> getCommentQuotesToThis() {
		return Collections.unmodifiableSet(commentQuotesToThis);
	}
	Set<CommentQuoteTo> _getCommentQuotesToThis() {
		return commentQuotesToThis;
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
	public String getText() {
		return text;
	}
	public void setText(String textCurrent) {
		this.text = textCurrent;
	}
	public Comment setTextThis(String textCurrent) {
		this.text = textCurrent;
		return this;
	}
	
	@XmlElement
	public String getTextHtml() {
		return textHtml;
	}
	public void setTextHtml(String code) {
		this.textHtml = code;
	}
	public Comment setTextHtmlThis(String code) {
		this.textHtml = code;
		return this;
	}
	
	/* En la vista de crear/editar comentarios,
	el usuario esablece el texto del comentario mediante un editor de summernote.
	No es muy eficaz: une las palabras cuando hay saltos de linea entre ellas
	(ej: traduce "<p>word1</p><p>word2</p>" por "word1word2"). 
	En este caso se solvento agregando mediante javascript una indicacion "\n"
	de salto de linea al final de cada parrafo (ej: "word1\nword2").
	Y con ese formato queda guardada la propiedad text al crear el Comment en bdd. 
	Por tanto ahora se necesita un getter que sustituya "\\n" por "\n".
	Este getter es usado en las listas de publicaciones y notificaciones
	("profile_list_publiations.xhtml" y "menu_main_registered/_admin.xhtml") */
	@XmlElement
	public String getPlainText() {
		String regExpNewParagraph = "(\\\\n)";
		return text.replaceAll(regExpNewParagraph, "\n");
	}
	public void setPlainText(String plainText) {
		this.text = plainText;
	}
	public Comment setPlainThis(String plainText) {
		this.text = plainText;
		return this;
	}
	
	@XmlElement
	public int getPositionIndex() {
		return positionIndex;
	}
	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}
	public Comment setPositionIndexThis(int positionIndex) {
		this.positionIndex = positionIndex;
		return this;
	}
	
	@XmlElement
	public int getNumVotes() {
		return numVotes;
	}
	public void setNumVotes(int numVotes) {
		this.numVotes = numVotes;
	}
	public Comment setNumVotesThis(int numVotes) {
		this.numVotes = numVotes;
		return this;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Comment setIdThis(Long id) {
		this.id = id;
		return this;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/* Relacion entre entidades:
	 *  1 Comment <--> * Correction
	 */
	/** Agrega una correccion a la lista de ellas que posee el comentario
	 * @param correction objeto Correction que se agrega
	 */
	public void addCorrection(Correction correction){
		corrections.add(correction);
		correction._setComment(this);
	}
	/** Elimina una correccion de la lista de ellas que posee el comentario
	 * @param correction objeto Correction que se elimina
	 */
	public void removeCorrection(Correction correction){
		corrections.remove(correction);
		correction._setComment(null);
	}
	
	/* Relacion entre entidades:
	 *  1 Comment <--> * CommentVoters <--> 1 User
	 */
	/** Agrega una votacion a la lista de ellas que posee el comentario
	 * @param commentVoter objeto CommentVoter que se agrega
	 */
	public void addCommentVoter(CommentVoter commentVoter){
		commentVoters.add(commentVoter);
		commentVoter._setComment(this);
	}
	/** Elimina una votacion de la lista de ellas que posee el comentario
	 * @param commentVoter objeto CommentVoter que se elimina
	 */
	public void removeCommentVoter(CommentVoter commentVoter){
		commentVoters.remove(commentVoter);
		commentVoter._setComment(null);
	}
	
	/* Relacion entre entidades:
	 *  1 Comment <--> * CommentsQuoteTo <--> 1 Comment
	 */
	/** Agrega un comentario a la lista de comentarios
	 * citados por este comentario
	 * @param commentQuoteTo objeto CommentQuoteTo que se agrega
	 */
	public void addCommentQuotedByThis(CommentQuoteTo commentQuoteTo){
		commentsQuotedByThis.add(commentQuoteTo);
		commentQuoteTo._setActorComment(this);
	}
	/** Elimina un comentario de la lista de comentarios
	 * citados por este comentario
	 * @param commentQuoteTo objeto CommentQuoteTo que se elimina
	 */
	public void removeCommentQuotedByThis(CommentQuoteTo commentQuoteTo){
		commentsQuotedByThis.remove(commentQuoteTo);
		commentQuoteTo._setActorComment(null);
	}
	
	/* Relacion entre entidades:
	 *  1 Comment <--> * CommentsQuoteTo <--> 1 Comment
	 */
	/** Agrega un comentario a la lista de comentarios
	 * que citan a este comentario
	 * @param commentQuoteTo objeto CommentQuoteTo que se agrega
	 */
	public void addCommentQuoteToThis(CommentQuoteTo commentQuoteTo){
		commentQuotesToThis.add(commentQuoteTo);
		commentQuoteTo._setQuotedComment(this);
	}
	/** Elimina un comentario de la lista de comentarios
	 * que citan por a este comentario
	 * @param commentQuoteTo objeto CommentQuoteTo que se elimina
	 */
	public void removeCommentQuoteToThis(CommentQuoteTo commentQuoteTo){
		commentQuotesToThis.remove(commentQuoteTo);
		commentQuoteTo._setQuotedComment(null);
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
		Comment other = (Comment) obj;
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
		return "Comment [text= " + text  + ""
				+ ", positionOrder= " + positionIndex + ""
				+ ", numVotes= " + numVotes + "]";
	}
}
