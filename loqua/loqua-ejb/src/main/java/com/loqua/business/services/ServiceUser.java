package com.loqua.business.services;

import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.utils.security.MapOccurrCounterByDate;
import com.loqua.model.PrivacityData;
import com.loqua.model.User;
import com.loqua.model.UserInfo;
import com.loqua.model.UserInfoPrivacity;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
 * y {@link PrivacityData}
 * @author Gonzalo
 */
public interface ServiceUser {
	
	/**
	 * Consulta usuarios segun su atributo 'id'
	 * @param userID atributo 'id' del User que se consulta
	 * @return objeto User cuyo atributo 'id' coincide
	 * con el parametro dado
	 * @throws EntityNotFoundException
	 */
	User getUserById(Long userID) throws EntityNotFoundException;
	
	/**
	 * Consulta usuarios segun su atributo 'email'
	 * @param email atributo homonimo del User que se consulta
	 * @return objeto User cuyo atributo 'email' coincide
	 * con el parametro dado
	 */
	User getUserNotRemovedByEmail(String email);
	
	/**
	 * Consulta los usuarios, segun su atributo 'nick', que no esten en estado
	 * eliminado
	 * @param nick atributo homonimo del User que se consulta
	 * @return objeto User cuyo atributo 'nick' coincide con el
	 * parametro recibido y cuyo atributo 'removed' es 'false'
	 */
	User getUserNotRemovedByNick(String nick);
	
	/**
	 * Consulta usuarios, segun su atributo 'email' y su 'password',
	 * que no esten en estado eliminado
	 * @param email atributo homonimo del User que se consulta
	 * @param password atributo homonimo del User que se consulta
	 * @return objeto User cuyo atributo 'email' coincide con el parametro
	 * dado, y cuyo atributo 'password', una vez descifrado,
	 * coincide con el parametro indicado, y cuyo atributo 'removed'
	 * es 'false' o bien tenga 'role' igual a 'ADMINISTRATOR'
	 */
	public User getUserToLogin(String email, String password);	
	
	/**
	 * Halla el numero de intentos de inicio de sesion fallidos por un
	 * usuario, o null si no existe
	 * @param email atributo homonimo del User que se consulta
	 * @return atributo 'loginFails' del User cuyo 'email' coincide
	 * con el parametro dado
	 */
	Integer getNumLoginFails(String email);
	
	/**
	 * Incrementa el numero de intentos de inicio de sesion fallidos
	 * de un usuario
	 * @param email atributo homonimo del User que se consulta
	 * @throws EntityNotFoundException
	 */
	void incrementLoginFails(String email) throws EntityNotFoundException;
	
	/**
	 * Pone a cero el numero de intentos de inicio de sesion fallidos
	 * de un usuario
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	void resetLoginFails(User userToUpdate) throws EntityNotFoundException;

	/**
	 * Agrega un nuevo usuario al sistema.
	 * @param userToCreate User que se desea agregar
	 * @return el usuario agregado
	 * @throws EntityAlreadyFoundException
	 */
	User createUser(User userToCreate) throws EntityAlreadyFoundException;
	
	/**
	 * Actualiza en el sistema el objeto User dado, ademas
	 * de su UserInfo, UserInfoPrivacity y PrivacityData asociados
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	void updateAllDataByUser(User userToUpdate)
			throws EntityNotFoundException;
	
	/**
	 * Actualiza en el sistema el objeto User dado
	 * @param userToUpdate User que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	void updateDataByUser(User userToUpdate) throws EntityNotFoundException;

	/**
	 * Halla el atributo 'locale' del User indicado, esto es, la 'configuracion
	 * regional' establecida por defecto para el usuario
	 * @param userToUpdate User que se consulta
	 * @return atributo 'locale' del User que se recibe por parametro
	 * @throws EntityNotFoundException
	 */
	String getLocaleByUser(User userToUpdate) throws EntityNotFoundException;

	/**
	 * Consulta usuarios segun su atributo 'urlConfirm'
	 * @param urlConfirm tributo homonimo del User que se consulta
	 * @return @return objeto User cuyo atributo 'urlConfirm' coincide
	 * con el parametro dado
	 */
	User getUserByUrlConfirm(String urlConfirm);

	/**
	 * Halla los usuarios que mas puntos han obtenido en el foro
	 * en el ultimo mes
	 * @return lista de los User cuyo UserInfo asociado tiene
	 * los mayores valores del atributo 'pointsMonth'
	 */
	List<User> getMostValuedUsersOfTheMonthFromDB();

	/**
	 * Halla los usuarios que mas participaciones (comentarios y
	 * correcciones aceptadas) han publicado en el foro en el ultimo mes
	 * @return lista de los User cuyo UserInfo asociado tiene
	 * los mayores valores del atributo
	 * 'countCommentsMonth' sumado a 'countCorrectionsMonth'
	 */
	List<User> getMostActiveUsersOfTheMonthFromDB();
	
	/**
	 * Halla la lista clasificatoria de los 5 usuarios mas proximos
	 * al usuario dado, incluyendo este
	 * @param user User cuya clasificacion se desea consultar
	 * @return lista de User mas proximos al usuario dado
	 * en la clasificacion de puntos
	 */
	Map<Integer, User> getSmallClasificationByUser(User user);
	
	/**
	 * Halla el numero de usuarios disponibles en estado no eliminado o
	 * de tipo administrador
	 * @return cantidad de User cuyo atributo 'removed' es 'false'
	 * o cuyo atributo 'role' es 'ADMINISTRATOR'
	 */
	int getNumRegisteredUsersAndAdminFromDB();
	
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
	 * <br>
	 * Si se alcanza el limite de registros de usuarios permitidos
	 * en cierto lapso de tiempo, se devuelve la cadena 'limitTooRegistrations'
	 */
	String sendEmailForRegister(User user, String content,
			String subject, Map<String, Integer> mapActionsLimits);

	/**
	 * Halla el numero de registros de usuarios que se han producido
	 * en distintos lapsos de tiempo
	 * @return instancia de MapEntityCounterByDate que almacena la cantidad
	 * de registros de usuarios a lo largo de varios lapsos de tiempo
	 * (el Map clasifica los siguientes lapsos: por minuto, por cinco minutos,
	 * por cuarto de hora, por hora, por dia, por semana y por mes)
	 */
	MapOccurrCounterByDate getNumLastRegistrationsFromDB();

	/**
	 * Envia al usuario dado un correo electronico con un enlace para que pueda
	 * confirmar la eliminacion de su cuenta de usuario en la aplicacion
	 * @param user User que recibe el correo que se envia
	 * @param content cuerpo del correo electronico que se envia
	 * @param subject asunto del correo electronico que se envia
	 * @throws EntityNotFoundException
	 */
	void sendEmailForRemoveUser(User user, String content,
			String subject) throws EntityNotFoundException;
	
	/**
	 * Elimina del sistema los datos privados de un usuario.<br>
	 * No se eliminan las participaciones del usuario en el foro
	 * ni los 'votos' que haya dado a las participaciones de otros. <br>
	 * Tampoco se elimina como destinatario de sus mensajes recibidos,
	 * aunque si se elimina como destinatario de las publicaciones recibidas,
	 * y tambien se eliminan los mensajes que el envio
	 * y las publicaciones/notificaciones creadas/logradas por el
	 * @param user User que se desea eliminar
	 * @return el usuario eliminado
	 * @throws EntityNotFoundException
	 */
	User deleteUserAccount(User user) throws EntityNotFoundException;
	
	/**
	 * Elimina del sistema el usuario indicado (objeto User).
	 * @param user User que se desea eliminar
	 * @throws EntityNotFoundException
	 */
	void deleteUser(User user) throws EntityNotFoundException;
}