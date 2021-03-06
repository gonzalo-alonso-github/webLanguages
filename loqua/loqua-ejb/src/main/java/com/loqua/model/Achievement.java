package com.loqua.model;

import java.io.Serializable;
import java.util.Date;
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
 * Representa un logro conseguido por un usuario en la aplicacion.<br>
 * Todos los objetos Achievement tienen asociado uno de los objetos
 * {@link Event} cuyo atributo 'isAchievement' es igual a 'true',
 * para indicar el 'evento' que ha dado lugar al logro.<br><br>
 * Mas detalladamente: un usuario puede provocar un mismo 'evento'
 * varias veces; del mismo modo algunos 'eventos' pueden generar un logro,
 * pero nunca se creara repetidamente un logro que ya haya sido alcanzado.
 * La informacion de la tabla Achievement ayuda a controlar que los logros
 * ya generados por un usuario no se repitan.
 * @author Gonzalo
 */
@XmlRootElement(name = "achievement")
@Entity
@Table(name="Achievement")
public class Achievement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private Date dateAchievement;
	
	/** Valor del Achievement que lo distingue de otros objetos Achievement
	 * generados por el mismo User y por el mismo Event.<br>
	 * Ejemplos:
	 * <ul><li>Si el 'evento' indica que el usuario entra
	 * en el top-X de la clasificacion de puntos, este eventValue es la variable
	 * que indica si ese top-X es el top-1, top-5, top-10...</li>
	 * <li>Si el 'evento' indica que el usuario ha alcanzado una cantidad X
	 * de comentarios publicados en el foro, este eventValue es la variable
	 * que indica si ese valor X es 500, 1000, 5000...</li></ul> */
	private Long eventValue;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Usuario que alcanza el evento que da lugar al logro */
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	/** 'Evento' que da lugar al logro */
	@ManyToOne
	@JoinColumn(name="event_type", referencedColumnName="type")
	private Event event;
	
	/** Lista de receptores de la publicacion */
	@OneToMany(mappedBy="publication")
	private Set<PublicationReceiver> receivers = 
			new HashSet<PublicationReceiver>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Achievement(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al Achievement
	 * @param event objeto Event asociado al Achievement
	 */
	public Achievement(User user, Event event){
		this.user = user;
		this.event = event;
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
	 *  * Achievements <--> 1 User
	 */
	@XmlTransient
	public User getUser() {
		return user;
	}
	void _setUser(User u) {
		user = u;
	}
	public void setUser(User u) {
		user = u;
	}
	public Achievement setUserThis(User u) {
		user = u;
		return this;
	}
	
	/* Relacion entre entidades:<br>
	 *  * Achievements <--> 1 Event
	 */
	@XmlTransient
	public Event getEvent() {
		return event;
	}
	void _setEvent(Event e) {
		event = e;
	}
	public void setEvent(Event e) {
		event = e;
	}
	public Achievement setEventThis(Event e) {
		event = e;
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
	
	@XmlElement
	public Long getEventValue() {
		return eventValue;
	}
	public void setEventValue(Long eventValue) {
		this.eventValue = eventValue;
	}
	public Achievement setEventValueThis(Long eventValue) {
		this.eventValue = eventValue;
		return this;
	}
	
	@XmlElement
	public Date getDateAchievement() {
		return dateAchievement;
	}
	public void setDateAchievement(Date date) {
		this.dateAchievement = date;
	}
	public Achievement setDateAchievementThis(Date date) {
		this.dateAchievement = date;
		return this;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Achievement setIdThis(Long id) {
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
		Achievement other = (Achievement) obj;
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
		return "Achievement [dateAchievement=" + dateAchievement.toString() + "]";
	}
}
