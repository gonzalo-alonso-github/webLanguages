package com.loqua.business.services.impl.transactionScript;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceContact;
import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.ServiceMessage;
import com.loqua.business.services.ServicePublication;
import com.loqua.business.services.impl.ManagementEmail;
import com.loqua.business.services.impl.MapEntityCounterByDate;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.model.PrivacityData;
import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;
import com.loqua.model.UserInfo;
import com.loqua.model.UserInfoPrivacity;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.UserJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
 * y {@link PrivacityData}.<br/>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionUser {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
	 * y {@link PrivacityData} */
	private static final UserJPA userJPA = new UserJPA();
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Publication} y {@link PublicationReceiver},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionPublication transactionPub = 
			new TransactionPublication();
	
	
	/**
	 * Consulta usuarios segun su atributo 'id'
	 * @param userID atributo 'id' del User que se consulta
	 * @return objeto User cuyo atributo 'id' coincide
	 * con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public User getUserById(Long userID)throws EntityNotFoundException {
		User result = new User();
		try{
			result = userJPA.getUserById(userID);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Consulta usuarios segun su atributo 'email'
	 * @param email atributo homonimo del User que se consulta
	 * @return objeto User cuyo atributo 'email' coincide
	 * con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public User getUserNotRemovedByEmail(String email)
			throws EntityNotFoundException  {
		User result = new User();
		try {
			result = userJPA.getUserNotRemovedByEmail(email);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Consulta los usuarios, segun su atributo 'nick', que no esten en estado
	 * eliminado
	 * @param nick atributo homonimo del User que se consulta
	 * @return objeto User cuyo atributo 'nick' coincide con el
	 * parametro recibido y cuyo atributo 'removed' es 'false'
	 * @throws EntityNotFoundException
	 */
	public User getUserNotRemovedByNick(String nick)
			throws EntityNotFoundException {
		User result = new User();
		try{
			result = userJPA.getUserNotRemovedByNick(nick);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Consulta usuarios, segun su atributo 'email' y su 'password',
	 * que no esten en estado eliminado
	 * @param email atributo homonimo del User que se consulta
	 * @param password atributo homonimo del User que se consulta
	 * @return objeto User cuyo atributo 'email' coincide con el parametro
	 * dado, y cuyo atributo 'password', una vez descifrado,
	 * coincide con el parametro indicado, y cuyo atributo 'removed'
	 * es 'false' o bien tenga 'role' igual a 'ADMINISTRATOR'
	 * @throws EntityNotFoundException
	 */
	public User getUserToLogin(String email, String password) 
			throws EntityNotFoundException {
		User result = new User();
		try{
			result = userJPA.getUserToLogin(email, password);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Halla el numero de intentos de inicio de sesion fallidos por un
	 * usuario, o null si no existe
	 * @param email atributo homonimo del User que se consulta
	 * @return atributo 'loginFails' del User cuyo 'email' coincide
	 * con el parametro dado
	 */
	public Integer getNumLoginFails(String email) {
		Integer result;
		try {
			result = userJPA.getNumLoginFails(email);
		} catch (EntityNotPersistedException ex) {
			result = null;
		}
		return result;
	}
	
	/**
	 * Incrementa el numero de intentos de inicio de sesion fallidos
	 * de un usuario
	 * @param email atributo homonimo del User que se consulta
	 * @throws EntityNotFoundException
	 */
	public void incrementLoginFails(String email) 
			throws EntityNotFoundException {
		User userToUpdate = new User();
		userToUpdate = getUserNotRemovedByEmail(email);
		userToUpdate.setLoginFails( userToUpdate.getLoginFails()+1 );
		updateAllDataByUser(userToUpdate);
	}
	
	/**
	 * Pone a cero el numero de intentos de inicio de sesion fallidos
	 * de un usuario
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void resetLoginFails(User userToUpdate) 
			throws EntityNotFoundException {
		userToUpdate.setLoginFails( 0 );
		updateAllDataByUser(userToUpdate);
	}
	
	/**
	 * Incrementa el numero de votos totales, mensuales y anuales que un
	 * usuario a recibido en sus comentarios
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void incrementCommentVotes(User userToUpdate) 
			throws EntityNotFoundException {
		UserInfo userInfoToUpdate = userToUpdate.getUserInfo();
		userInfoToUpdate.incrementVotesComments();
		userToUpdate.setUserInfo(userInfoToUpdate);
		updateAllDataByUser(userToUpdate);
	}
	
	/**
	 * Incrementa el numero de comentarios totales, mensuales y anuales
	 * enviados por un usuario en el foro
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void incrementCountComments(User userToUpdate) 
			throws EntityNotFoundException {
		UserInfo userInfoToUpdate = userToUpdate.getUserInfo();
		userInfoToUpdate.incrementPointsBySentComment();
		userToUpdate.setUserInfo(userInfoToUpdate);
		updateAllDataByUser(userToUpdate);
	}
	
	/**
	 * Agrega un nuevo usuario al sistema
	 * @param userToCreate User que se desea agregar
	 * @throws EntityAlreadyFoundException
	 */
	public void createUser(User userToCreate) throws EntityAlreadyFoundException {
		try {
			userJPA.create(userToCreate);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	/**
	 * Actualiza en el sistema el objeto User dado, ademas
	 * de su UserInfo, UserInfoPrivacity y PrivacityData asociados
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updateAllDataByUser(User userToUpdate)
			throws EntityNotFoundException {
		try {
			userJPA.updateAllDataByUser(userToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Actualiza en el sistema el objeto User dado
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updateDataByUser(User userToUpdate)
			throws EntityNotFoundException {
		try {
			userJPA.updateDataByUser(userToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	/**
	 * Halla el atributo 'locale' del User indicado, esto es, la 'configuracion
	 * regional' establecida por defecto para el usuario
	 * @param userToUpdate User que se consulta
	 * @return atributo 'locale' del User que se recibe por parametro
	 * @throws EntityNotFoundException
	 */
	public String getLocaleByUser(User userToUpdate) 
			throws EntityNotFoundException {
		String result = null;
		try {
			result = userJPA.getLocaleByUser(userToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}

	/**
	 * Halla los usuarios que mas puntos han obtenido en el foro
	 * en el ultimo mes
	 * @return lista de los User cuyo UserInfo asociado tiene
	 * los mayores valores del atributo 'pointsMonth'
	 */
	public List<User> getMostValuedUsersOfTheMonthFromDB() {
		return userJPA.getMostValuedUsersOfTheMonth();
	}
	
	/**
	 * Halla los usuarios que mas participaciones (comentarios y
	 * correcciones aceptadas) han publicado en el foro en el ultimo mes
	 * @return lista de los User cuyo UserInfo asociado tiene
	 * los mayores valores del atributo
	 * 'countCommentsMonth' sumado a 'countCorrectionsMonth'
	 */
	public List<User> getMostActiveUsersOfTheMonthFromDB() {
		return userJPA.getMostActiveUsersOfTheMonth();
	}
	
	/**
	 * Hallar la lista clasificatoria de los 5 usuarios mas proximos
	 * al usuario dado, incluyendo este
	 * @param user User cuya clasificacion se desea consultar
	 * @return lista de User mas proximos al usuario dado
	 * en la clasificacion de puntos
	 */
	public Map<Integer, User> getSmallClasificationByUser(User user){
		Map<Integer, User> result = null;
		int lastPosition = getNumRegisteredUsersAndAdminFromDB();
		try{
			result = userJPA.getSmallClasificationByUser(user, lastPosition);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = new HashMap<Integer, User>();
		}
		return result;
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO SE REGISTRE EN LOQUA
	// // // // // // // // // // // // // // // // // //
	
	/**
	 * Envia al usuario dado un correo electronico con un enlace para que pueda
	 * confirmar su registro en la aplicacion
	 * @param user User que recibe el correo que se envia
	 * @param content cuerpo del correo electronico que se envia
	 * @param subject asunto del correo electronico que se envia
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de registros de usuarios permitidos en distintos
	 * lapsos de tiempo
	 * @return
	 * Si la accion se produce sin ningun error, retorna la cadena 'noError'.
	 * <br/>
	 * Si se alcanza el limite de registros de usuarios permitidos
	 * en cierto lapso de tiempo, se devuelve la cadena 'limitTooRegistrations'
	 */
	public String sendEmailForRegister(User user,
			String content, String subject,
			Map<String, Integer> mapActionsLimits){
		String result = validateNumLastRegistrations(mapActionsLimits);
		if( ! result.equals("noError") ){
			return result;
		}
		content += user.getUrlConfirm();
		ManagementEmail.sendEmail(user, content, subject);
		return result;
	}
	
	/**
	 * Comprueba si la cantidad de registros de usuarios en la aplicacion
	 * en distintos lapsos de tiempo supera el numero maximo indicado
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de registros de usuarios permitidos en distintos
	 * lapsos de tiempo
	 * @return
	 * Si no se alcanza el limite de registros de usuarios permitidos
	 * en cierto lapso de tiempo, retorna la cadena 'noError'.
	 * <br/>
	 * Si se alcanza el limite de registros de usuarios permitidos
	 * en cierto lapso de tiempo, se devuelve la cadena 'limitTooRegistrations'
	 */
	private String validateNumLastRegistrations(
			Map<String, Integer> mapActionsLimits){
		String result = "noError";
		MapEntityCounterByDate numOccurrences =
				getNumLastRegistrationsFromDB();
		if( numOccurrences.getOccurrencesLastMinute() >= 
				mapActionsLimits.get("limitRegistrationsAtLastMinute") 
			|| numOccurrences.getOccurrencesLastFiveMinutes() >=
				mapActionsLimits.get("limitRegistrationsAtLastFiveMinutes") 
			|| numOccurrences.getOccurrencesLastQuarter() >=
				mapActionsLimits.get("limitRegistrationsAtLastQuarter") 
			|| numOccurrences.getOccurrencesLastHour() >=
				mapActionsLimits.get("limitRegistrationsAtLastHour") 
			|| numOccurrences.getOccurrencesLastDay() >=
				mapActionsLimits.get("limitRegistrationsAtLastDay") 
			|| numOccurrences.getOccurrencesLastWeek() >=
				mapActionsLimits.get("limitRegistrationsAtLastWeek") 
			|| numOccurrences.getOccurrencesLastMonth() >=
				mapActionsLimits.get("limitRegistrationsAtLastMonth") ){
			return "limitTooRegistrations";
		}
		return result;
	}
	
	/**
	 * Halla el numero de registros de usuarios que se han producido
	 * en distintos lapsos de tiempo
	 * @return instancia de MapEntityCounterByDate que almacena la cantidad
	 * de registros de usuarios a lo largo de varios lapsos de tiempo
	 * (el Map clasifica los siguientes lapsos: por minuto, por cinco minutos,
	 * por cuarto de hora, por hora, por dia, por semana y por mes)
	 */
	public MapEntityCounterByDate getNumLastRegistrationsFromDB(){
		MapEntityCounterByDate result = new MapEntityCounterByDate();
		try {
			result = userJPA.getNumLastRegistrations();
		} catch (EntityNotPersistedException ex) {
			result = new MapEntityCounterByDate();
		}
		return result;
	}
	
	/**
	 * Consulta usuarios segun su atributo 'urlConfirm'
	 * @param urlConfirm tributo homonimo del User que se consulta
	 * @return @return objeto User cuyo atributo 'urlConfirm' coincide
	 * con el parametro dado
	 */
	public User getUserByUrlConfirm(String urlConfirm) 
			/*throws EntityNotFoundException*/ {
		User result = new User();
		try {
			result = userJPA.getUserByUrlConfirm(urlConfirm);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = null;
		}
		return result;
	}
	
	/**
	 * Halla el numero de usuarios disponibles en estado no eliminado o
	 * de tipo administrador
	 * @return cantidad de User cuyo atributo 'removed' es 'false'
	 * o cuyo atributo 'role' es 'ADMINISTRATOR'
	 */
	public int getNumRegisteredUsersAndAdminFromDB() {
		int result;
		try {
			result = userJPA.getNumRegisteredUsersAndAdmin();
		} catch (EntityNotPersistedException ex) {
			result = 0;
		}
		return result;
	}
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO ELIMINE SU CUENTA DE USUARIO
	// // // // // // // // // // // // // // // // // // // //	
	
	/**
	 * Envia al usuario dado un correo electronico con un enlace para que pueda
	 * confirmar la eliminacion de su cuenta de usuario en la aplicacion
	 * @param user User que recibe el correo que se envia
	 * @param content cuerpo del correo electronico que se envia
	 * @param subject asunto del correo electronico que se envia
	 * @throws EntityNotFoundException
	 */
	public void sendEmailForRemoveUser(User user, 
			String content, String subject) throws EntityNotFoundException {
		// 128 bits son considerados suficientes para una codificacion segura,
		// pero en base32 cada digito codifica 5 bits
		// asi que utilizamos el siguiente multiplo de 5: 130
		// Por tanto: la urlConfirm tiene 26 caracteres (26*5=130),
		// suficientes para asegurarse de que es aleatoria
		String urlConfirm=new BigInteger(130, new SecureRandom()).toString(32);
		content += urlConfirm;
		ManagementEmail.sendEmail(user, content, subject);
		
		user.setUrlConfirm(urlConfirm);
		updateAllDataByUser(user);
	}
	
	/**
	 * Elimina del sistema los datos privados de un usuario.<br/>
	 * No se eliminan las participaciones del usuario en el foro
	 * ni los 'votos' que haya dado a las participaciones de otros. <br/>
	 * Tampoco se elimina como destinatario de sus mensajes recibidos,
	 * aunque si se elimina como destinatario de las publicaciones recibidas,
	 * y tambien se eliminan los mensajes que el envio
	 * y las publicaciones/notificaciones creadas/logradas por el
	 * @param user User que se desea eliminar
	 * @throws EntityNotFoundException
	 */
	public void deleteUserAccount(User user) throws EntityNotFoundException {
		try {
			userJPA.deleteUserAccount(user);
			user.getUserInfo().unlink();
			user.getUserInfoPrivacity().unlink();
			user.getPrivacityData().unlink();
			deleteAllContacts(user);
			deleteAllContactRequests(user);
			deleteAllUserLanguages(user);
			// no se eliminan las participaciones del usuario en el foro
			// ni los 'votos' que haya dado a las participaciones de otros.
			// Tampoco se elimina como destinatario de sus mensajes recibidos,
			// aunque si se elimina como destinatario de las publicaciones recibidas,
			// y tammbien eliminan los mensajes que el envio
			// y las publicaciones/notificaciones creadas/logradas por el
			deleteSentMessagesByUser(user);
			deletePublicationsByUser(user);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Elimina los objetos Contact cuyo atributo 'user' o
	 * cuyo atributo 'userContact' sea igual al User dado
	 * (es decir: elimina todas las relaciones de contacto de un usuario)
	 * @param user User asociado a los Contact que se desean eliminar
	 * @throws EntityNotFoundException
	 */
	private void deleteAllContacts(User user) throws EntityNotFoundException {
		ServiceContact serviceContact =
				new LocatorLocalEjbServices().getServiceContact();
		serviceContact.deleteAllReciprocalContactsByUser(user);
	}
	
	/**
	 * Elimina de la base de datos los objetos ContactRequest cuyo atributo
	 * 'userSender' o cuyo atributo 'userReceiver' sea igual al User dado
	 * (es decir: elimina las peticiones de contacto enviadas o recibidas 
	 * por un usuario)
	 * @param user User asociado a los ContactRequest que se desean eliminar
	 * @throws EntityNotFoundException
	 */
	private void deleteAllContactRequests(User user)
			throws EntityNotFoundException {
		ServiceContact serviceContact =
				new LocatorLocalEjbServices().getServiceContact();
		serviceContact.deleteAllContactRequestsByUser(user);
	}
	
	/**
	 * Elimina todos los objetos UserNativeLanguage y UserPracticingLanguage
	 * cuyo atributo 'user' coincide con el parametro recibido
	 * (es decir: elimina el conjunto de todos los Language maternos de User
	 * y de todos los Language practicados por User )
	 * @param user User asociado a los UserNativeLanguage
	 * y UserPracticingLanguage que se eliminan
	 * @throws EntityNotFoundException
	 */
	private void deleteAllUserLanguages(User user) 
			throws EntityNotFoundException {
		ServiceLanguage serviceLanguage =
				new LocatorLocalEjbServices().getServiceLanguage();
		serviceLanguage.deleteNativeLanguagesByUser(user);
		serviceLanguage.deletePracticedLanguagesByUser(user);
	}
	
	/**
	 * Elimina todos los objetos Message enviados por un usuario
	 * @param user User asociado a los Message que se eliminan
	 * @throws EntityNotFoundException
	 */
	private void deleteSentMessagesByUser(User user) 
			throws EntityNotFoundException {
		ServiceMessage serviceMessage =
				new LocatorLocalEjbServices().getServiceMessage();
		serviceMessage.deleteSentMessagesByUser(user);
	}
	
	/**
	 * Elimina todos los objetos Publication enviados y recibidos
	 * por un usuario. <br/> No es necesario llamar a un metodo que se encargue
	 * especificamente de eliminar los objetos PublicationReceiver, ya que estos
	 * se eliminan automaticamente al haber borrado sus Publication asociados
	 * @param user User asociado a las Publication que se eliminan
	 * @throws EntityNotFoundException
	 */
	private void deletePublicationsByUser(User user) 
			throws EntityNotFoundException {
		ServicePublication servicePublication =
				new LocatorLocalEjbServices().getServicePublication();
		servicePublication.deletePublicationsByUser(user);
	}

	/**
	 * Comprueba si es necesaria la generacion de alguna Publication cuando
	 * el usuario alcanza cierta posicion en la clasificacion de puntos. <br/>
	 * Las Publication se generaran si el usuario asciende al
	 * top de los primeros 100 usuarios, o 50, o 25, o 20, o 15, o 10, o 5
	 * o superiores
	 * @param user User que, al haber alcanzado cierto nivel
	 * en la clasificacion, provoca la generacion de las Publication
	 * @throws EntityNotFoundException
	 * @throws EntityAlreadyFoundException
	 */
	public void generatePublicationForTopUsers(User user)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		// Este metodo es utilizado desde otros transaction scripts
		// al igual que "TransactionPublication.generatePublication()"
		TypePrivacity privacity = user.getPrivacityData().getPublications();
		Integer clasificationOfUser = getSingleClasificationByUser(user);
		Integer topReached = reachedTopClasification(clasificationOfUser);
		if( topReached != -1 ){
			// si el autor entra en el top-100/50/25/20/15/10/5/(...)/1:
			// comprueba si el logro ha sido alcanzado otras veces anteriores
			if( transactionPub.achievementTopUsersAlreadyPassedByUser(
					topReached.longValue(), user.getId()) ){
				// en cuyo caso no genera la publicacion:
				return;
			}// else
			// si el logro aun no habia sido alcanzado, genera la publicacion:
			transactionPub.generatePublication(
					privacity, topReached.longValue(), 4L, user);
		}
	}
	
	/**
	 * Halla la posicion de un usuario en la
	 * clasificacion mensual de puntos de usuarios
	 * @param user User cuya clasificacion se desea consultar
	 * @return clasificacion del User dado segun el atributo 'points'
	 * de su UserInfo asociado
	 */
	private int getSingleClasificationByUser(User user){
		int result;
		try{
			result = userJPA.getSingleClasificationByUser(user);
		} catch (EntityNotPersistedException ex) {
			result = 0;
		}
		return result;
	}
	
	/**
	 * Comprueba si el numero recibido (posicion clasificatioria de un usuario)
	 * ha alcanzado una cantidad considerada como logro; es decir, si coincide
	 * con uno de los numeros de la serie 100, 50, 25, 20, 15, 10, 5, (...), 1
	 * @param clasification posicion de un usuario en la
	 * clasificacion mensual de puntos
	 * @return
	 * true: si el numero dado esta en la serie
	 * 100, 50, 25, 20, 15, 10, 5, (...), 1
	 * <br/>
	 * false: si el numero dado no esta la serie
	 * 100, 50, 25, 20, 15, 10, 5, (...), 1
	 */
	private Integer reachedTopClasification(int clasification) {
		// existen los top-100/50/25/20/15/10/5/(...)/1
		// si el valor de clasification es menor o igual que uno de ellos,
		// se devuelve dicho top
		if( clasification>100 ){ return -1; }
		for(int topUsers=1; topUsers<=5; topUsers++)
			if( clasification==topUsers ){ return topUsers; }
		for(int topUsers=10; topUsers<=25; topUsers+=5)
			if( clasification<=topUsers ){ return topUsers; }
		if( clasification<=50 ){ return 50; }
		else if( clasification<=100 ){ return 100; }
		return -1;
	}
}
