package com.loqua.remote.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.loqua.remote.model.types.TypePrivacity;

/**
 * Representa una publicacion generada por un usuario. <br/>
 * Todos los objetos Publication cuyo atributo 'selfGenerated'
 * es igual a 'true', tienen asociado un objeto {@link Event}
 * para indicar el 'evento' que ha dado lugar a la publicacion.<br/><br/>
 * Mas detalladamente: un usuario puede provocar un mismo 'evento'
 * varias veces; del mismo modo cada 'evento' genera una publicacion.<br/>
 * Por otro lado existe una manera mas de crear publicaciones: se da
 * cuando el usuario escribe un texto en su pagina de perfil y lo publica
 * (en este caso el atributo 'selfGenerated' de la publicacion creada
 * sera igual a 'false')
 * @author Gonzalo
 */
@XmlRootElement(name = "publication")
@Entity
@Table(name="Publication")
public class Publication implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	
	/** Texto de la publicacion.<br/>Si la publicacion ha sido generada
	 * automaticamente al provocar un evento, este 'content' es null
	 * (y en tal caso el texto de la publicacion estara indicado por
	 * {@link Event#content}).<br/>
	 * Si la publicacion ha sido generada intencionadamente por el usuario
	 * al escribir un texto en su perfil, este 'content' es dicho texto */
	private String content;
	
	/** Fecha de creacion de la publicacion */
	private Date datePub;
	
	/** Si es 'true', indica que la publicacion ha sido generada
	 * automaticamente cuando el usuario ha provocado algun 'evento'.<br/>
	 * Si es 'false', indica que ha sido generada
	 * intencionadamente por el usuario cuando este ha escrito un texto en su
	 * pagina de perfil
	 */
	private boolean selfGenerated;
	
	/** Nivel de privacidad de la publicacion. Normalmente coincidira con
	 * el valor {@link PrivacityData#publications}, que es el nivel por defecto.
	 * Pero si el usuario edita el nivel de privacidad de esta publicacion,
	 * quedara reflejado en este atributo. */
	@Enumerated(EnumType.STRING)
	private TypePrivacity privacity;
	
	/** Valor de la Publication que la distingue de otros objetos Publication
	 * generados por el mismo User y por el mismo Event. <br/>
	 * Si bien el atributo {@link Event#content} referencia el texto
	 * de plantilla que se mostrara en la publicacion (establecido en los
	 * ficheros .properties de internacionalizacion del proyecto 'loqua-web'),
	 * este eventValue indica el valor que completa dicho texto.<br/>
	 * Ejemplos:
	 * <ul><li>Si el 'evento' indica que el usuario entra
	 * en el top-X de la clasificacion de puntos, este eventValue es la variable
	 * que indica si ese top-X es el top-1, top-5, top-10...</li>
	 * <li>Si el 'evento' indica que el usuario ha alcanzado una cantidad X
	 * de comentarios publicados en el foro, este eventValue es la variable
	 * que indica si ese valor X es 500, 1000, 5000...</li></ul> */
	private Long eventValue;
	
	/** Indica si el usuario ya ha leido la publicacion. <br/>
	 * En la practica este atributo solo resulta de utilidad en caso de que
	 * la publicacion se muestre como una notificacion
	 * (dato indicado por {@link Event#showAsNotification}), puesto que
	 * el menu superior de la pagina debe discriminar las notificaciones
	 * leidas y las no leidas para mostrar solo estas ultimas. */
	private boolean readPub;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Usuario que genera la publicacion, o que provoca el evento que da lugar
	 * a la publicacion */
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	/** 'Evento' que da lugar a la publicacion */
	@ManyToOne
	@JoinColumn(name="event_type", referencedColumnName="type")
	private Event event;
	
	/** Lista de usuarios que reciben la publicacion */
	@OneToMany(mappedBy="publication")
	private Set<PublicationReceiver> receivers = 
			new HashSet<PublicationReceiver>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Publication(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param user objeto User asociado al Publication
	 * @param event objeto Event asociado al Publication
	 */
	public Publication(User user, Event event){
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
	 *  * Publications <--> 1 User
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
	public Publication setUserThis(User u) {
		user = u;
		return this;
	}
	
	/* Relacion entre entidades:
	 *  * Publications <--> 1 Event
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
	public Publication setEventThis(Event e) {
		event = e;
		return this;
	}
	
	/* Relacion entre entidades:
	 *  1 Publication <--> * PublicationReceiver <--> 1 User
	 */
	@XmlTransient
	public Set<PublicationReceiver> getPublicationReceivers() {
		return Collections.unmodifiableSet(receivers);
	}
	Set<PublicationReceiver> _getPublicationReceivers() {
		return receivers;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Publication setContentThis(String content) {
		this.content = content;
		return this;
	}
	
	@XmlElement
	public Long getEventValue() {
		return eventValue;
	}
	public void setEventValue(Long eventValue) {
		this.eventValue = eventValue;
	}
	public Publication setEventValueThis(Long eventValue) {
		this.eventValue = eventValue;
		return this;
	}
	
	@XmlElement
	public Date getDatePub() {
		return datePub;
	}
	public void setDatePub(Date date) {
		this.datePub = date;
	}
	public Publication setDatePubThis(Date date) {
		this.datePub = date;
		return this;
	}
	
	@XmlElement
	public boolean getSelfGenerated() {
		return selfGenerated;
	}
	public void setSelfGenerated(boolean selfGenerated) {
		this.selfGenerated = selfGenerated;
	}
	public Publication setSelfGeneratedThis(boolean selfGenerated) {
		this.selfGenerated = selfGenerated;
		return this;
	}
	
	@XmlElement
	public TypePrivacity getPrivacity() {
		return privacity;
	}
	public void setPrivacity(TypePrivacity privacity) {
		this.privacity = privacity;
	}
	public Publication setPrivacityThis(TypePrivacity privacity) {
		this.privacity = privacity;
		return this;
	}
	
	@XmlElement
	public boolean getReadPub() {
		return readPub;
	}
	public void setReadPub(boolean read) {
		this.readPub = read;
	}
	public Publication setReadPubThis(boolean read) {
		this.readPub = read;
		return this;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Publication setIdThis(Long id) {
		this.id = id;
		return this;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	
	/* Relacion entre entidades:
	 *  1 Publication <--> * PublicationReceiver <--> User
	 */
	/** Agrega un usuario a la lista de ellos que posee la Publication
	 * @param publicationReceiver objeto PublicationReceiver que se agrega
	 */
	public void addPublicationReceiver(PublicationReceiver publicationReceiver){
		receivers.add(publicationReceiver);
		publicationReceiver._setPublication(this);
	}
	/** Elimina un usuario de la lista de ellos que posee la Publication
	 * @param publicationReceiver objeto PublicationReceiver que se elimina
	 */
	public void removePublication(PublicationReceiver publicationReceiver){
		receivers.remove(publicationReceiver);
		publicationReceiver._setPublication(null);
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
		Publication other = (Publication) obj;
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
		return "Publication [content=" + content
				+ " datePub=" + datePub.toString()
				+ " selfGenerated=" + selfGenerated
				+ " privacity=" + privacity
				+ " readPub=" + readPub + "]";
	}
}
