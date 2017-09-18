package com.loqua.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Representa una correccion de un comentario publicada por un usuario
 * en el foro.
 * Es una clase heredada de {@link ForumPost}, al igual que {@link Correction}
 * @author Gonzalo
 */
@XmlRootElement(name = "correction")
@Entity
@DiscriminatorValue("TypeCorrection")
@Table(name="Correction")
public class Correction extends ForumPost implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Texto plano de la correccion */
	private String text;
	/** Texto HTML de la correccion */
	private String textHtml;
	/** Indica si la correccion ha quedado aprobada por el usuario que
	 * la recibio */
	private boolean approved;
	/** Fecha en la que la correccion ha quedado aprobada por el usuario que
	 * la recibio */
	private Date dateApproved;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Comentario que recibe la correccion */
	@ManyToOne
	@JoinColumn(name="comment_id", referencedColumnName="id")
	private Comment comment;
	
	/** Lista de recomendaciones que ha recibido la correccion */
	@OneToMany(mappedBy="correction")
	private Set<CorrectionAgree> correctionAgreements =
			new HashSet<CorrectionAgree>();
	
	/** Lista de desaprobaciones que ha recibido la correccion */
	@OneToMany(mappedBy="correction")
	private Set<CorrectionDisagree> correctionDisagreements = 
			new HashSet<CorrectionDisagree>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Correction(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param comment Comentario que recibe la correccion
	 */
	public Correction(Comment comment){
		this.comment = comment;
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
	 *  * Corrections <--> 1 Comment
	 */
	@XmlTransient
	public Comment getComment() {
		return comment;
	}
	void _setComment(Comment c) {
		comment = c;
	}
	public void setComment(Comment c){
		comment = c;
	}
	public Correction setCommentThis(Comment c){
		comment = c;
		return this;
	}
	
	/* Relacion entre entidades:
	 *  1 Correction <--> * CorrectionAgrees <--> 1 User
	 */
	@XmlTransient
	public Set<CorrectionAgree> getCorrectionAgreements() {
		return Collections.unmodifiableSet(correctionAgreements);
	}
	Set<CorrectionAgree> _getCorrectionAgreements() {
		return correctionAgreements;
	}
	
	/* Relacion entre entidades:
	 *  1 Correction <--> * CorrectionDisagrees <--> 1 User
	 */
	@XmlTransient
	public Set<CorrectionDisagree> getCorrectionDisagreements() {
		return Collections.unmodifiableSet(correctionDisagreements);
	}
	Set<CorrectionDisagree> _getCorrectionDisagreements() {
		return correctionDisagreements;
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
	public void setText(String text) {
		this.text = text;
	}
	public Correction setTextThis(String text) {
		this.text = text;
		return this;
	}
	
	@XmlElement
	public String getTextHtml() {
		return textHtml;
	}
	public void setTextHtml(String textHtml) {
		this.textHtml = textHtml;
	}
	public Correction setTextHtmlThis(String textHtml) {
		this.textHtml = textHtml;
		return this;
	}
	
	@XmlElement
	public boolean getApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	public Correction setApprovedThis(boolean approved) {
		this.approved = approved;
		return this;
	}
	
	@XmlElement
	public Date getDateApproved() {
		return dateApproved;
	}
	public void setDateApproved(Date date) {
		this.dateApproved = date;
	}
	public Correction setDateApprovedThis(Date date) {
		this.dateApproved = date;
		return this;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Correction setIdThis(Long id) {
		this.id = id;
		return this;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/* Relacion entre entidades:
	 *  1 Correction <--> * CorrectionAgrees <--> 1 User
	 */
	
	/** Agrega una recomendacion de correccion a la lista de ellas
	 * que posee la correccion 
	 * @param correctionAgree objeto CorrectionAgree que se agrega
	 */
	public void addCorrectionAgree(CorrectionAgree correctionAgree){
		correctionAgreements.add(correctionAgree);
		correctionAgree._setCorrection(this);
	}
	/** Elimina una recomendacion de correccion de la lista de ellas
	 * que posee la correccion 
	 * @param correctionAgree objeto CorrectionAgree que se elimina
	 */
	public void removeCorrectionAgree(CorrectionAgree correctionAgree){
		correctionAgreements.remove(correctionAgree);
		correctionAgree._setCorrection(null);
	}
	
	/* Relacion entre entidades:
	 *  1 Correction <--> * CorrectionDisagrees <--> 1 User
	 */
	
	/** Agrega una desaprobacion a la lista de ellas 
	 * que posee la correccion
	 * @param correctionDisagree objeto CorrectionDisagree que se agrega
	 */
	public void addCorrectionDisagree(CorrectionDisagree correctionDisagree){
		correctionDisagreements.add(correctionDisagree);
		correctionDisagree._setCorrection(this);
	}
	/** Elimina una desaprobacion de la lista de ellas 
	 * que posee la correccion
	 * @param correctionDisagree objeto CorrectionDisagree que se agrega
	 */
	public void removeCorrectionDisagree(CorrectionDisagree correctionDisagree){
		correctionDisagreements.remove(correctionDisagree);
		correctionDisagree._setCorrection(null);
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
		Correction other = (Correction) obj;
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
		return "Correction [text= " + text  + ""
				+ ", approved= " + approved
				+ ", dateApproved= " + dateApproved + "]";
	}
}
