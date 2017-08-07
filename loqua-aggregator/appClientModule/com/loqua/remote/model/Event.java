package com.loqua.remote.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "event")
@Entity
@Table(name="Event")
//@IdClass(EventKey.class)
public class Event implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long type;
	private boolean isAchievement;
	private String content;
	private boolean editablePrivacity;
	private boolean showAsPublication;
	private boolean showAsNotification;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //�
	
	@OneToMany(mappedBy="event")
	private Set<Publication> publications = new HashSet<Publication>();
	
	@OneToMany(mappedBy="event")
	private Set<Achievement> achievements = new HashSet<Achievement>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Event(){}
	
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
	 *  1 Event <--> * Publications
	 */
	@XmlTransient
	public Set<Publication> getPublications() {
		return Collections.unmodifiableSet(publications);
	}
	Set<Publication> _getPublications() {
		return publications;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Event <--> * Achievements
	 */
	@XmlTransient
	public Set<Achievement> getAchievements() {
		return Collections.unmodifiableSet(achievements);
	}
	Set<Achievement> _getAchievements() {
		return achievements;
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
	public boolean getIsAchievement() {
		return isAchievement;
	}
	public void setIsAchievement(boolean isAchievement) {
		this.isAchievement = isAchievement;
	}

	@XmlElement
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@XmlElement
	public boolean getEditablePrivacity() {
		return editablePrivacity;
	}
	public void setEditablePrivacity(boolean editablePrivacity) {
		this.editablePrivacity = editablePrivacity;
	}
	
	@XmlElement
	public boolean getShowAsPublication() {
		return showAsPublication;
	}
	public void setShowAsPublication(boolean showAsPublication) {
		this.showAsPublication = showAsPublication;
	}
	
	@XmlElement
	public boolean getShowAsNotification() {
		return showAsNotification;
	}
	public void setShowAsNotification(boolean showAsNotification) {
		this.showAsNotification = showAsNotification;
	}
	
	@XmlElement
	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}

	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/** Relacion entre entidades:<br>
	 *  1 Event <--> * Publications
	 */
	public void addPublication(Publication p){
		publications.add(p);
		p._setEvent(this);
	}
	public void removePublication(Publication p){
		publications.remove(p);
		p._setEvent(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Event <--> * Achievements
	 */
	public void addAchievement(Achievement a){
		achievements.add(a);
		a._setEvent(this);
	}
	public void removeAchievement(Achievement a){
		achievements.remove(a);
		a._setEvent(null);
	}
	
	// // // // // // // //
	// HASH CODE & EQUALS
	// // // // // // // //
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Event other = (Event) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	// // // // // // //
	// TO STRING
	// // // // // // //
	@Override
	public String toString() {
		return "Event [isAchievement=" + isAchievement
				+ ", content=" + content + "]"
				+ ", editablePrivacity=" + editablePrivacity + "]"
				+ ", showAsPublication=" + showAsPublication + "]"
				+ ", showAsNotification=" + showAsNotification + "]";
	}
}
