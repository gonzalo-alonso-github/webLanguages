package com.loqua.business.services.impl.transactionScript;

import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Achievement;
import com.loqua.model.Event;
import com.loqua.model.ForumPost;
import com.loqua.model.ForumThread;
import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.PublicationJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link Publication} y {@link PublicationReceiver}.<br/>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionPublication {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link Publication} y {@link PublicationReceiver} */
	private static final PublicationJPA publicationJPA = new PublicationJPA();
	
	/**
	 * Consulta publicaciones segun su atributo 'id'
	 * @param publicationID atributo 'id' de la Publication que se consulta
	 * @return Publication cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public Publication getPublicationByID(Long publicationID)
			throws EntityNotFoundException {
		Publication result = new Publication();
		try {
			result = publicationJPA.getPublicationById(publicationID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Consulta eventos de publicaciones segun su atributo 'id'
	 * @param eventID atributo 'id' del Event que se consulta
	 * @return Event cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public Event getEventById(Long eventID)
			throws EntityNotFoundException {
		Event result = new Event();
		try {
			result = publicationJPA.getEventById(eventID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}

	/**
	 * Elimina del sistema dado todas las publicaciones
	 * enviadas y recibidas por un usuario. <br/>
	 * No es necesario llamar a un metodo que se encargue
	 * especificamente de eliminar los objetos PublicationReceiver, ya que estos
	 * se eliminan automaticamente al haber borrado sus Publication asociados
	 * @param user User asociado a las Publication que se eliminan
	 * @param user User al que pertenecen las Publication que se eliminan
	 * @throws EntityNotFoundException
	 */
	public void deletePublicationsByUser(User user) 
			throws EntityNotFoundException {
		try {
			publicationJPA.deletePublicationsByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Halla las notificaciones no leidas de un usuario
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se consultan
	 * @return cantidad de Publication, pertenecientes al User dado,
	 * cuyo atributo 'readPub' es 'false' y cuyo Event asociado
	 * tiene su atributo 'showAsNotification' igual a 'true'
	 * @throws EntityNotFoundException
	 */
	public Integer getNumUnreadNotificationsByUser(Long userId) 
			throws EntityNotFoundException {
		Integer result = 0;
		try{
			result = publicationJPA.getNumUnreadNotificationsByUser(userId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Halla las notificaciones recibidas por un usuario ordenadas por fecha
	 * descendientemente (las mas nuevas primero)
	 * @param userReceiverId atributo 'id' del User
	 * asociado con las Publication que se consultan
	 * @param limit limite maximo de Publication devueltas
	 * @return lista de Publication, pertenecientes al User dado, cuyo
	 * Event asociado tiene su atributo 'showAsNotification' igual a 'true',
	 * aplicando el limite maximo de elementos indicado
	 */
	public List<Publication> getLastNotificationsByUser(
			Long userReceiverId, Integer limit) {
		return publicationJPA.getLastNotificationsByUser(
				userReceiverId, limit);
	}

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
	public List<Publication> getPublicationsByUser(
			Long userId, Integer offset, Integer limitNumPubs){
		List<Publication> result = null;
		if(offset==null) offset=0;
		result = publicationJPA.getPublicationsByUser(
				userId, offset, limitNumPubs);
		return result;
	}
	
	/**
	 * Halla el numero total de publicaciones que pertenecen al usuario dado
	 * @param userId atributo 'id' del User que se consulta
	 * @return cantidad de Publication pertenecientes al User dado,
	 * cuyo atributo 'selfGenerated' es 'false' o bien cuyo Event asociado
	 * tiene su atributo 'showAsPublication' igual a 'true'
	 * @throws EntityNotFoundException
	 */
	public Integer getNumPublicationsByUser(Long userId) 
			throws EntityNotFoundException {
		Integer result = 0;
		try{
			result = publicationJPA.getNumPublicationsByUser(userId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
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
	public List<Publication> getPublicationsByUserAndContacts(
			Long userId, Integer offset, Integer limitNumPubs){
		List<Publication> result = null;
		if(offset==null) offset=0;
		result = publicationJPA.getPublicationsByUserAndContacts(
				userId, offset, limitNumPubs);
		return result;
	}
	
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
	public Integer getNumPublicationsByUserAndContacts(Long userId) 
			throws EntityNotFoundException {
		Integer result = 0;
		try{
			result = publicationJPA.getNumPublicationsByUserAndContacts(userId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}

	/**
	 * Agrega una publicacion a la base de datos 
	 * @param publication objeto Publication que se desea guardar
	 * @throws EntityAlreadyFoundException
	 */
	public void createPublication(Publication publication) 
			throws EntityAlreadyFoundException {
		try {
			publicationJPA.createPublication(publication);
			//createPublicationReceivers(publication);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	/*
	 * Genera un objeto PublicationReceiver a partir del
	 * parametro recibido y lo agrega a la base de datos
	 * @param publication Publication asociada al PublicationReceiver
	 * que se genera
	 * @throws EntityAlreadyPersistedException
	private void createPublicationReceivers( Publication pub )
			throws EntityAlreadyPersistedException{
		User userPublication = pub.getUser();
		Event eventPublication = pub.getEvent();
		// El propio autor de la publicacion la recibe como notificacion:
		publicationJPA.createPublicationReceiver(pub, pub.getUser());
		if( pub.getPrivacity()==TypePrivacity.CONTACTS
				|| pub.getPrivacity()==TypePrivacity.PUBLIC ){
			// Si se cumplen todas estas condiciones,
			// cada contacto del usuario tambien recibe una notificacion:
			List<User> usersContacts = transactionUser.getUserContactsByUser(
					userPublication.getId());
			for(User userContact : usersContacts){
				publicationJPA.createPublicationReceiver(pub,userContact);
			}
		}
	}
	*/
	
	/**
	 * Genera una Publication y la agrega a la base de datos. Si es preciso,
	 * crea tambien el objeto Achievement asociado a la Publication
	 * @param privacity tipo de privacidad de la publicacion, un objeto
	 * Enumerator que puede ser 'PRIVATE', 'PUBLIC' o 'CONTACT'
	 * @param eventValue atributo homonimo de la Publication creada
	 * @param eventType atributo 'type' del Event asociado a la Publication
	 * creada
	 * @param user atributo homonimo de la Publication creada, es el usuario
	 * que provoca la creacion de la publicacion
	 * @throws EntityNotFoundException
	 * @throws EntityAlreadyFoundException
	 */
	public void generatePublication(TypePrivacity privacity, Long eventValue,
			Long eventType, User user)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		// Este metodo es utilizado desde otros transaction scripts
		// al igual que "TransactionPublication.generatePublicationForTopUsers()"
		Event event = getEventById(eventType);
		// Crear la Publication:
		Publication pub = new Publication();
		pub.setDatePubThis(new Date()).setSelfGeneratedThis(true)
			.setPrivacityThis(privacity).setReadPubThis(false)
			.setEventValueThis(eventValue)
			.setEventThis(event).setUserThis(user);
		createPublication(pub);
		// Crear el Achievement:
		if( event.getIsAchievement() ){
			Achievement achievement = new Achievement();
			achievement.setDateAchievementThis(new Date())
				.setEventValueThis(eventValue)
				.setEventThis(event).setUserThis(user);
			createAchievement(achievement);
		}
	}
	
	/**
	 * Agrega un logro a la base de datos 
	 * @param achievement objeto Achievement que se desea guardar
	 * @throws EntityAlreadyFoundException
	 */
	private void createAchievement(Achievement achievement) 
			throws EntityAlreadyFoundException {
		try {
			publicationJPA.createAchievement(achievement);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	/**
	 * Comprueba si el usuario dado ya ha alcanzado el logro de igualar o
	 * superar la posicion indicada (parametro 'eventValue')
 	 * en la clasificacion de puntos
	 * @param eventValue indica la posicion, en la clasificacion de puntos,
	 * que se comprueba si ha sido alcanzada por el usuario
	 * @param eventType atributo homonimo del Event del Achievement que se consulta
	 * @param userId atributo 'id' del User que se consulta
	 * @return
	 * true: si el usuario ha alcanzado el logro <br/>
	 * false: si el usuario aun no ha alcanzado el logro
	 */
	public boolean achievementTopUsersAlreadyPassedByUser(
			Long eventValue, Long userId) {
		// Si devuelve false el logro no se ha superado aun
		// Si devuelve true el logro ya se supero y no generara mas Publications
		Achievement achievement = null;
		try {
			achievement = publicationJPA.achievementTopUsersAlreadyPassedByUser(
					eventValue, userId);
		} catch (EntityNotPersistedException ex) {
			return false;
		}
		if( achievement==null ){ return false;}
		return true;
	}
	
	/**
	 * Comprueba si el usuario dado ya ha alcanzado el logro de igualar o
	 * superar la cantidad indicada (parametro 'eventValue')
 	 * de comentarios realizados en el foro
	 * @param eventValue indica en numero total de comentarios
	 * realizados por el usuario
	 * @param userId atributo 'id' del User que se consulta
	 * @return
	 * true: si el usuario ha alcanzado el logro <br/>
	 * false: si el usuario aun no ha alcanzado el logro
	 */
	public boolean achievementNumCommentsAlreadyPassedByUser(
			Long eventValue, Long userId) {
		// Si devuelve false el logro no se ha superado aun
		// Si devuelve true el logro ya se supero y no generara mas Publications
		Achievement achievement = null;
		try {
			achievement=publicationJPA.achievementNumCommentsAlreadyPassedByUser(
					eventValue, userId);
		} catch (EntityNotPersistedException ex) {
			return false;
		}
		if( achievement==null ){ return false;}
		return true;
	}
	
	/**
	 * Comprueba si el usuario dado ya ha alcanzado el logro de igualar o
	 * superar la cantidad indicada (parametro 'eventValue')
 	 * de correcciones aprobadas en el foro
	 * @param eventValue indica en numero total de correcciones aprobadas
	 * realizadas por el usuario
	 * @param userId atributo 'id' del User que se consulta
	 * @return
	 * true: si el usuario ha alcanzado el logro <br/>
	 * false: si el usuario aun no ha alcanzado el logro
	 */
	public boolean achievementNumCorrectionsAlreadyPassedByUser(
			Long eventValue, Long userId) {
		// Si devuelve false el logro no se ha superado aun
		// Si devuelve true el logro ya se supero y no generara mas Publications
		Achievement achievement = null;
		try {
			achievement=publicationJPA.achievementNumCorrsAlreadyPassedByUser(
					eventValue, userId);
		} catch (EntityNotPersistedException ex) {
			return false;
		}
		if( achievement==null ){ return false;}
		return true;
	}

	/**
	 * Actualiza la publicacion dada
	 * @param pub Publication que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updatePublication(Publication pub)
			throws EntityNotFoundException {
		try {
			publicationJPA.updatePublication(pub);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Actualiza todos los objetos Publication,
	 * cuyo atributo 'user' coincide con el parametro recibido,
	 * estableciendo su atribuo 'read' igual a 'true'
	 * @param userId atributo 'id' del User al que pertenecen las Publication
	 * que se desean actualizar
	 * @throws EntityNotFoundException
	 */
	public void setNotificationsToRead(Long userId)
			throws EntityNotFoundException {
		try {
			publicationJPA.setNotificationsToRead(userId);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Elimina el objeto Publication indicado
	 * @param publication Publication que se desea eliminar
	 * @throws EntityNotFoundException
	 */
	public void deletePublication(Publication publication)
			throws EntityNotFoundException {
		try {
			publicationJPA.deletePublication(publication);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	/**
	 * Actualiza las publicaciones que hacen referencia a un comentario o
	 * correccion que ha sido eliminado, para que ahora hagan referencia
	 * al hilo del foro al que pertenecia ese comenario o correccion
	 * @param post ForumPost que, tras haber sido eliminado, provoca la
	 * necesidad de dicha actualizacion
	 * @throws EntityNotFoundException
	 */
	public void editPubsForDeletedPost(ForumPost post)
			throws EntityNotFoundException {
		// Buscar las Publications cuyo eventValue
		// apunte a este post (post = un comentario o correccion):
		List<Publication> pubsForDeletedPost = 
				publicationJPA.getPubsForDeletedPost(post);
		for( Publication pub : pubsForDeletedPost ){
			ForumThread thread = post.getForumThread();
			// Si tambien se ha borrado el hilo donde estaba el post,
			// se elimina la publicacion
			if( thread==null ){ deletePublication(pub); }
			else{
				// En caso contrario se edita la publicacion:
				// eventValue apuntara al hilo y no al Comment eliminado...
				pub.setEventValue(thread.getId());
				// ...y se cambia a otro Event cuyo tipo estara entre 401 y 500
				long currentEventType = pub.getEvent().getType();
				Event event = getEventForDeletedPost(currentEventType);
				pub.setEvent(event);
				// Se envia la orden a bdd
				updatePublication(pub);
			}
		}
	}
	
	/**
	 * Halla el Event apropiado para una Publication
	 * que hacia referencia a un comentario o
	 * correccion que ha sido eliminado, para que ahora haga referencia
	 * al hilo del foro al que pertenecia ese comenario o correccion
	 * @param currentEventType atributo 'type' del anterior Event asociado a 
	 * la Publication
	 * @return nuevo Event que quedara asociado a la Publication
	 * @throws EntityNotFoundException
	 */
	private Event getEventForDeletedPost(long currentEventType) 
			throws EntityNotFoundException {
		// El Event 101 es 'analogo' al Event 401 si se elimina el Comment
		// El Event 102 es 'analogo' al Event 402 si se elimina el Comment
		// El Event 103 es 'analogo' al Event 403 si se elimina el Comment
		// El Event 201 es 'analogo' al Event 451 si se elimina el Correction
		// El Event 202 es 'analogo' al Event 452 si se elimina el Correction
		// El Event 203 es 'analogo' al Event 453 si se elimina el Correction
		// El Event 204 es 'analogo' al Event 454 si se elimina el Correction
		long newEventType = -1;
		if( currentEventType>=101 &&  currentEventType<201 ){
			newEventType = currentEventType+300;
		}else if( currentEventType>=201 &&  currentEventType<301 ){
			newEventType = currentEventType+250;
		}
		return getEventById( newEventType );
	}
}
