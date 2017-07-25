package com.loqua.model;

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

@XmlRootElement(name = "comment")
@Entity
@DiscriminatorValue("TypeComment")
@Table(name="Comment")
public class Comment extends ForumPost implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	private String text;
	private String textHtml;
	private int positionIndex;
	private int numVotes;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@OneToMany(mappedBy="comment")
	private Set<Correction> corrections = new HashSet<Correction>();
	
	@OneToMany(mappedBy="comment")
	private Set<CommentVoter> commentVoters = new HashSet<CommentVoter>();
	
	@OneToMany(mappedBy="actorComment")
	private Set<CommentQuoteTo> commentsQuotedByThis = new HashSet<CommentQuoteTo>();
	
	@OneToMany(mappedBy="quotedComment")
	private Set<CommentQuoteTo> commentQuotesToThis = new HashSet<CommentQuoteTo>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Comment(){}
	
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
	 *  1 Comment <--> * Corrections
	 */
	@XmlTransient
	public Set<Correction> getCorrections() {
		return Collections.unmodifiableSet(corrections);
	}
	Set<Correction> _getCorrections() {
		return corrections;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Comment <--> * CommentVoters <--> 1 User
	 */
	@XmlTransient
	public Set<CommentVoter> getCommentVoters() {
		return Collections.unmodifiableSet(commentVoters);
	}
	Set<CommentVoter> _getCommentVoters() {
		return commentVoters;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Comment <--> * CommentsQuoteTo <--> 1 Comment
	 */
	@XmlTransient
	public Set<CommentQuoteTo> getCommentQuotedByThis() {
		return Collections.unmodifiableSet(commentsQuotedByThis);
	}
	Set<CommentQuoteTo> _getCommentQuotedByThis() {
		return commentsQuotedByThis;
	}
	
	/** Relacion entre entidades:<br>
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
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
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
	
	/** Relacion entre entidades:<br>
	 *  1 Comment <--> * Correction
	 */
	public void addCorrection(Correction c){
		corrections.add(c);
		c._setComment(this);
	}
	public void removeCorrection(Correction c){
		corrections.remove(c);
		c._setComment(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Comment <--> * CommentVoters <--> 1 User
	 */
	public void addCommentVoter(CommentVoter cv){
		commentVoters.add(cv);
		cv._setComment(this);
	}
	public void removeCommentVoter(CommentVoter cv){
		commentVoters.remove(cv);
		cv._setComment(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Comment <--> * CommentsQuoteTo <--> 1 Comment
	 */
	public void addCommentQuotedByThis(CommentQuoteTo cv){
		commentsQuotedByThis.add(cv);
		cv._setActorComment(this);
	}
	public void removeCommentQuotedByThis(CommentQuoteTo cv){
		commentsQuotedByThis.remove(cv);
		cv._setActorComment(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Comment <--> * CommentsQuoteTo <--> 1 Comment
	 */
	public void addCommentQuoteToThis(CommentQuoteTo cv){
		commentQuotesToThis.add(cv);
		cv._setQuotedComment(this);
	}
	public void removeCommentQuoteToThis(CommentQuoteTo cv){
		commentQuotesToThis.remove(cv);
		cv._setQuotedComment(null);
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
