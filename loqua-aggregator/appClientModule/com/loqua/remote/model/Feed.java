package com.loqua.remote.model;

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

/**
 * Representa una fuente de noticias de la cual se obtienen los hilos del foro.
 * Los objetos Feed ya estan predefinidos (se obtienen de
 * la base de datos) y nunca se van a crear nuevos objetos Feed en tiempo de
 * ejecucion.
 * @author Gonzalo
 */
@XmlRootElement(name = "feed")
@Entity
@Table(name="Feed")
public class Feed implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String name;
	
	/** Direccion URL de la fuente de noticias, desde la cual el proyecto
	 * 'loqua-aggregator' descargara los datos a partir de los cuales
	 * se generaran los hilos del foro (objetos {@link ForumThread}) */
	private String url;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Lenguaje en el cual la fuente de noticias publica su informacion */
	@ManyToOne @JoinColumn(name="language_id")
	private Language language;
	
	/** Categoria (o seccion) a la que pertenecen las noticias de la fuente */
	@ManyToOne @JoinColumn(name="feedCategory_id")
	private FeedCategory feedCategory;
	
	/** Lista de hilos del foro que han sido generados a partir de
	 * las noticias de esta fuente */
	@OneToMany(mappedBy="feed")
	private Set<ForumThread> threads = new HashSet<ForumThread>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Feed(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param language objeto Language asociado al Feed
	 * @param feedCategory objeto FeedCategory asociado al Feed
	 */
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
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/* Relacion entre entidades:
	 *  * Feeds <--> 1 Language
	 */
	@XmlTransient
	public Language getLanguage() {
		return language;
	}
	void _setLanguage(Language l) {
		language = l;
	}
	
	/* Relacion entre entidades:
	 *  * Feeds <--> 1 FeedCategory
	 */
	@XmlTransient
	public FeedCategory getFeedCategory() {
		return feedCategory;
	}
	void _setFeedCategory(FeedCategory l) {
		feedCategory = l;
	}
	
	/* Relacion entre entidades:
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
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
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
	
	/* Relacion entre entidades:
	 *  1 Feed <--> * Threads
	 */
	/** Agrega un hilo a la lista de ellos que posee la fuente de noticias
	 * @param forumThread objeto ForumThread que se agrega
	 */
	public void addThread(ForumThread forumThread){
		threads.add(forumThread);
		forumThread._setFeed(this);
	}
	/** Elimina un hilo de la lista de ellos que posee la fuente de noticias
	 * @param forumThread objeto ForumThread que se elimina
	 */
	public void removeThread(ForumThread forumThread){
		threads.remove(forumThread);
		forumThread._setFeed(null);
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
