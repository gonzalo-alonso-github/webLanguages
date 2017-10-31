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
 * Representa un suceso significativo (un 'evento') alcanzado o recibido
 * por un usuario en la aplicacion. Los objetos Event ya estan predefinidos
 * (se obtienen de la base de datos) y nunca se van a crear
 * nuevos objetos Event en tiempo de ejecucion.<br>
 * Ejemplos de eventos:
 * <ul><li>El usuario entra en el top-X de la clasificacion de puntos</li>
 * <li>El usuario alcanza una cantidad X de comentarios
 * publicados en el foro</li>
 * <li>El usuario recibe una sugerencia de correccion en el comentario X</li>
 * </ul>
 * @author Gonzalo
 */
@XmlRootElement(name = "event")
@Entity
@Table(name="Event")
public class Event implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long type;
	
	/** Si un usuario provoca un 'evento' cuyo atributo 'isAchievement'
	 * es igual a 'true', se generara un objeto {@link Achievement}. Ese tipo
	 * de 'eventos' se muestran como publicaciones (en el perfil del usuario)
	 * y tambien como notificaciones (en el menu horizontal superior y tambien
	 * en la pagina de notificaciones)
	 */
	private boolean isAchievement;
	/** Palabra clave que, en los ficheros .properties de internacionalizacion
	 * del proyecto 'loqua-web', indica el texto de plantilla que muestran
	 * las publicaciones asociadas al 'evento' */
	private String content;
	/** Indica si es editable la privacidad de las publicaciones
	 * generadas por este 'evento'*/
	private boolean editablePrivacity;
	/** Si es 'true', indica que la publicacion asociada al 'evento'
	 * se deberia mostrar en su pagina de perfil. <br>
	 * Si es 'false', no se deberia mostrar en dicha vista
	 */
	private boolean showAsPublication;
	/** Si es 'true', indica que la publicacion asociada al 'evento'
	 * se deberia mostrar en el menu horizontal superior de la pagina,
	 * o en la pagina de notificaciones. <br>
	 * Si es 'false', no se deberia mostrar en dichas vistas
	 */
	private boolean showAsNotification;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Si un usuario provoca un 'evento', siempre se generara un objeto
	 * {@link Publication} asociado a este.
	 * La relacion entre ambas entidades es que los 'eventos'
	 * dan lugar a publicaciones
	 */
	@OneToMany(mappedBy="event")
	private Set<Publication> publications = new HashSet<Publication>();
	
	/** Si un usuario provoca un 'evento' cuyo atributo 'isAchievement' es igual
	 * a 'true', se generara un objeto {@link Achievement} asociado a este.
	 * La relacion entre ambas entidades es que los 'eventos'
	 * pueden dar lugar a logros
	 */
	@OneToMany(mappedBy="event")
	private Set<Achievement> achievements = new HashSet<Achievement>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Event(){}
	
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
	 *  1 Event <--> * Publications
	 */
	@XmlTransient
	public Set<Publication> getPublications() {
		return Collections.unmodifiableSet(publications);
	}
	Set<Publication> _getPublications() {
		return publications;
	}
	
	/* Relacion entre entidades:
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
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
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
	
	/* Relacion entre entidades:
	 *  1 Event <--> * Publications
	 */
	
	/** Agrega una publicacion a la lista de ellas que posee el evento
	 * @param publication objeto Publication que se agrega
	 */
	public void addPublication(Publication publication){
		publications.add(publication);
		publication._setEvent(this);
	}
	/** Elimina una publicacion de la lista de ellas que posee el evento
	 * @param publication objeto Publication que se elimina
	 */
	public void removePublication(Publication publication){
		publications.remove(publication);
		publication._setEvent(null);
	}
	
	/* Relacion entre entidades:
	 *  1 Event <--> * Achievements
	 */
	/** Agrega un logro a la lista de ellos que posee el evento
	 * @param achievement objeto Achievement que se agrega
	 */
	public void addAchievement(Achievement achievement){
		achievements.add(achievement);
		achievement._setEvent(this);
	}
	/** Elimina un logro de la lista de ellos que posee el evento
	 * @param achievement objeto Achievement que se elimina
	 */
	public void removeAchievement(Achievement achievement){
		achievements.remove(achievement);
		achievement._setEvent(null);
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
