package com.loqua.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "feed")
@Entity
@Table(name="Feed")
public class Feed implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String name;
	private String url;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@ManyToOne @JoinColumn(name="language_id")
	private Language language;
	
	@ManyToOne @JoinColumn(name="feedCategory_id")
	private FeedCategory feedCategory;
	
	@OneToMany(mappedBy="feed")
	private Set<ForumThread> threads = new HashSet<ForumThread>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Feed(){}
	
	public Feed(Language language, FeedCategory feedCategory){
		this.language = language;
		this.feedCategory = feedCategory;
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
	 *  * Feeds <--> 1 Language
	 */
	@XmlTransient
	public Language getLanguage() {
		return language;
	}
	void _setLanguage(Language l) {
		language = l;
	}
	
	/** Relacion entre entidades:<br>
	 *  * Feeds <--> 1 FeedCategory
	 */
	@XmlTransient
	public FeedCategory getFeedCategory() {
		return feedCategory;
	}
	void _setFeedCategory(FeedCategory l) {
		feedCategory = l;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Feed <--> * Threads
	 */
	@XmlTransient
	public Set<ForumThread> getThreads() {
		return Collections.unmodifiableSet(threads);
	}
	Set<ForumThread> _getThreads() {
		return threads;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	/** Relacion entre entidades:<br>
	 *  1 Feed <--> * Threads
	 */
	public void addThread(ForumThread n){
		threads.add(n);
		n._setFeed(this);
	}
	public void removeThread(ForumThread n){
		threads.remove(n);
		n._setFeed(null);
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
		Feed other = (Feed) obj;
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
		return "Feed [name=" + name + ", url=" + url + "]";
	}
}
