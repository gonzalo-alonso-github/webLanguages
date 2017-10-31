package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Event;
import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link Publication} y {@link PublicationReceiver}
 * @author Gonzalo
 */
public interface ServicePublication {

	/**
	 * Consulta publicaciones segun su atributo 'id'
	 * @param publicationID atributo 'id' de la Publication que se consulta
	 * @return Publication cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	Publication getPublicationByID(Long publicationID) 
			throws EntityNotFoundException;
	
	/**
	 * Consulta eventos de publicaciones segun su atributo 'id'
	 * @param eventID atributo 'id' del Event que se consulta
	 * @return Event cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	Event getEventById(Long eventID) throws EntityNotFoundException;
	
	/**
	 * Elimina del sistema dado todas las publicaciones
	 * enviadas y recibidas por un usuario. <br>
	 * No es necesario llamar a un metodo que se encargue
	 * especificamente de eliminar los objetos PublicationReceiver, ya que estos
	 * se eliminan automaticamente al haber borrado sus Publication asociados
	 * @param user User asociado a las Publication que se eliminan
	 * @param user User al que pertenecen las Publication que se eliminan
	 * @throws EntityNotFoundException
	 */
	void deletePublicationsByUser(User user) throws EntityNotFoundException;
	
	/**
	 * Halla las notificaciones recibidas por un usuario ordenadas por fecha
	 * descendientemente (las mas nuevas primero)
	 * @param userReceiverId atributo 'id' del User
	 * asociado con las Publication que se consultan
	 * @param limitNumPubs limite maximo de Publication devueltas
	 * @return lista de Publication, pertenecientes al User dado, cuyo
	 * Event asociado tiene su atributo 'showAsNotification' igual a 'true',
	 * aplicando el limite maximo de elementos indicado
	 */
	List<Publication> getLastNotificationsByUser(
			Long userReceiverId, Integer limitNumPubs);
	
	/**
	 * Halla las publicaciones recibidas por un usuario ordenadas por fecha
	 * descendientemente (las mas nuevas primero)
	 * @param userId atributo 'id' del User
	 * asociado con las Publication que se consultan
	 * @param offset offset de las Publication devueltas
	 * @param limitNumPubs limite maximo de Publication devueltas
	 * @return lista de Publication pertenecientes al User dado,
	 * cuyo atributo 'selfGenerated' es 'false'
	 * o bien cuyo Event asociado tiene su atributo 'showAsPublication' igual a
	 * 'true', aplicando el offset dado y el limite maximo de elementos
	 * indicado
	 */
	List<Publication> getPublicationsByUser(
			Long userId, Integer offset, Integer limitNumPubs);
	
	/**
	 * Halla el numero total de publicaciones que pertenecen al usuario dado
	 * @param userId atributo 'id' del User que se consulta
	 * @return cantidad de Publication pertenecientes al User dado,
	 * cuyo atributo 'selfGenerated' es 'false' o bien cuyo Event asociado
	 * tiene su atributo 'showAsPublication' igual a 'true'
	 * @throws EntityNotFoundException
	 */
	Integer getNumPublicationsByUser(Long userId)
			throws EntityNotFoundException;

	/**
	 * Halla las publicaciones recibidas por un usuario y sus contactos,
	 * ordenadas por fecha descendientemente (las mas nuevas primero)
	 * @param userId atributo 'id' del User que se consulta
	 * @param offset offset de las Publication devueltas
	 * @param limitNumPubs limite maximo de Publication devueltas
	 * @return lista de Publication, que pertenecen al User dado
	 * o a los Contact de este, cuyo atributo 'selfGenerated' es 'false'
	 * o bien cuyo Event asociado tiene su atributo 'showAsPublication' igual a
	 * 'true', aplicando el offset dado y el limite maximo de elementos
	 * indicado
	 */
	List<Publication> getPublicationsByUserAndContacts(
			Long userId, Integer offset, Integer limitNumPubs);
	
	/**
	 * Halla el numero total de publicaciones recibidas por un usuario
	 * y sus contactos
	 * @param userId atributo 'id' del User que se consulta
	 * @return cantidad de Publication, pertenecientes al User dado
	 * o a los Contact de este, cuyo atributo 'selfGenerated' es 'false'
	 * o bien cuyo Event asociado tiene su atributo 'showAsPublication'
	 * igual a 'true'
	 * @throws EntityNotFoundException
	 */
	Integer getNumPublicationsByUserAndContacts(Long userId)
			throws EntityNotFoundException;
	
	/**
	 * Halla las notificaciones no leidas de un usuario
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se consultan
	 * @return cantidad de Publication, pertenecientes al User dado,
	 * cuyo atributo 'readPub' es 'false' y cuyo Event asociado
	 * tiene su atributo 'showAsNotification' igual a 'true'
	 * @throws EntityNotFoundException
	 */
	Integer getNumUnreadNotificationsByUser(Long userId)
			throws EntityNotFoundException;
	
	/**
	 * Agrega una publicacion a la base de datos 
	 * @param publication objeto Publication que se desea guardar
	 * @throws EntityAlreadyFoundException
	 */
	void createPublication(Publication publication)
			throws EntityAlreadyFoundException;

	/**
	 * Actualiza la publicacion dada
	 * @param publication Publication que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	void updatePublication(Publication publication)
			throws EntityNotFoundException;
	
	/**
	 * Actualiza todos los objetos Publication,
	 * cuyo atributo 'user' coincide con el parametro recibido,
	 * estableciendo su atribuo 'read' igual a 'true'
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se desean actualizar
	 * @throws EntityNotFoundException
	 */
	void setNotificationsToRead(Long userId) throws EntityNotFoundException;

	/**
	 * Elimina el objeto Publication indicado
	 * @param publication Publication que se desea eliminar
	 * @throws EntityNotFoundException
	 */
	void deletePublication(Publication publication)
			throws EntityNotFoundException;
}