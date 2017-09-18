package com.loqua.model;

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

/**
 * Representa una seccion o tema al cual pertenecen las noticias de un
 * {@link Feed}. Los objetos FeedCategory ya estan predefinidos (se obtienen de
 * la base de datos) y nunca se van a crear nuevos objetos FeedCategory
 * en tiempo de ejecucion.
 * @author Gonzalo
 */
@XmlRootElement(name = "feedCategory")
@Entity
@Table(name="FeedCategory")
public class FeedCategory implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String name;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Lista de fuentes que emiten noticias de esta categoria o seccion */
	@OneToMany(mappedBy="feedCategory")
	private Set<Feed> feeds = new HashSet<Feed>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public FeedCategory(){}
	
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
	 *  1 FeedCategory <--> * Feeds
	 */
	@XmlTransient
	public Set<Feed> getFeeds() {
		return Collections.unmodifiableSet(feeds);
	}
	Set<Feed> _getFeeds() {
		return feeds;
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
	 *  1 FeedCategory <--> * Feeds
	 */
	/** Agrega una fuente de noticias a la lista de ellas que posee
	 * la categoria (o seccion) de noticias
	 * @param feed objeto Feed que se agrega
	 */
	public void addFeeds(Feed feed){
		feeds.add(feed);
		feed._setFeedCategory(this);
	}
	/** Elimina una fuente de noticias de la lista de ellas que posee
	 * la categoria (o seccion) de noticias
	 * @param feed objeto Feed que se elimina
	 */
	public void removeFeedCategory(Feed feed){
		feeds.remove(feed);
		feed._setFeedCategory(null);
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
		FeedCategory other = (FeedCategory) obj;
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
		return "FeedCategory [name=" + name + "]";
	}
}
