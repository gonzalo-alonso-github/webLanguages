package com.loqua.business.services;

import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.model.User;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link ChangeEmail} y {@link ChangePassword}
 * @author Gonzalo
 */
public interface ServiceUserAccessDataChange {
	
	// // // // // // // // // // // //
	// METODOS PARA EL CAMBIO DE EMAIL
	// // // // // // // // // // // //
	
	/**
	 * Consulta el cambio de email, realizado por un usuario,
	 * segun el atributo 'urlConfirm'
	 * @param urlConfirm atributo homonimo del ChangeEmail que se consulta
	 * @return objeto ChangeEmail cuyo atributo 'urlConfirm' coincide
	 * con el parametro recibido, o null si no existe
	 */
	ChangeEmail getEmailChangeByUrlConfirm(String urlConfirm);
	
	/**
	 * Actualiza el objeto ChangeEmail dado
	 * @param changeEmail objeto ChangeEmail que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	void updateEmailChange(ChangeEmail changeEmail) 
			throws EntityNotFoundException;
	
	/**
	 * Envia un correo electronico, a la direccion de email actual del usuario
	 * dado, para que confirme su cambio de email en la aplicacion
	 * @param user usuario que actualiza su email
	 * @param newEmail nueva direccion de email que desea utilizar el usuario
	 * @param content cuerpo del correo que se envia
	 * @param subject asunto del correo que se envia
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de cambios de email permitidos en distintos lapsos de tiempo
	 * @return
	 * Si la accion se produce sin ningun error, retorna la cadena 'noError'.
	 * <br/>
	 * Si se alcanza el limite de cambios de email permitidos
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitEmailChangesAtLastHour'</li>
	 * <li>'limitEmailChangesAtLastDay'</li>
	 * <li>'limitEmailChangesAtLastWeek'</li>
	 * <li>'limitEmailChangesAtLastMonth'</li>
	 * <li>'limitEmailChangesAtLastYear'</li></ul>
	 * @throws EntityAlreadyFoundException
	 */
	String sendEmailForEditEmail(User user, String newEmail, String content,
			String subject, Map<String, Integer> mapActionsLimits)
			throws EntityAlreadyFoundException;

	/**
	 * Envia un correo electronico, a la direccion de email actualizada
	 * del usuario dado, para que confirme su cambio de email en la aplicacion
	 * @param changeEmail objeto ChangeEmail que representa el cambio de email
	 * que se desea confirmar
	 * @param content cuerpo del correo que se envia
	 * @param subject asunto del correo que se envia
	 * @throws EntityNotFoundException
	 */
	void sendEmailForEditEmailSecondStep(ChangeEmail changeEmail,
			String content, String subject)
			throws EntityNotFoundException;
	
	// // // // // // // // // // // // // //
	// METODOS PARA EL CAMBIO DE CONTRASENA
	// // // // // // // // // // // // // //
	
	/**
	 * Consulta el cambio de contrase&ntilde;a, realizado por un usuario,
	 * segun el atributo 'urlConfirm'
	 * @param urlConfirm atributo homonimo del ChangePassword que se consulta
	 * @param typeChangePassword filtro de busqueda para el atributo homonimo
	 * de ChangePassword, que es un Enumeration que puede ser 'RESTORE' o 'EDIT'
	 * @return objeto ChangePassword cuyos atributos
	 * coinciden con los parametros recibidos, o null si no existe
	 */
	ChangePassword getPasswordChangeByUrlConfirm(
			String urlConfirm, String typeChangePassword);
	
	/**
	 * Actualiza el objeto ChangePassword dado
	 * @param objectChangePassword objeto ChangePassword que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	void updatePasswordChange(ChangePassword objectChangePassword)
			throws EntityNotFoundException;
	
	/**
	 * Envia un correo electronico a la direccion de email del usuario dado,
	 * para que confirme su restauracion de contrase&ntilde;a en la aplicacion
	 * @param user usuario que actualiza su contrase&ntilde;a
	 * @param content cuerpo del correo que se envia
	 * @param subject asunto del correo que se envia
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de restauraciones de contrase&ntilde;a permitidas en distintos
	 * lapsos de tiempo
	 * @return
	 * Si la accion se produce sin ningun error, retorna la cadena 'noError'.
	 * <br/>
	 * Si se alcanza el limite de restauraciones de contrase&ntilde;a permitidas
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitPasswordRestoresAtLastHour'</li>
	 * <li>'limitPasswordRestoresAtLastDay'</li>
	 * <li>'limitPasswordRestoresAtLastWeek'</li>
	 * <li>'limitPasswordRestoresAtLastMonth'</li>
	 * <li>'limitPasswordRestoresAtLastYear'</li></ul>
	 * @throws EntityAlreadyFoundException
	 */
	String sendEmailForPasswordRestore(User user, List<String> content,
			String subject, Map<String, Integer> mapActionsLimits) 
			throws EntityAlreadyFoundException;

	/**
	 * Envia un correo electronico a la direccion de email del usuario dado,
	 * para que confirme su edicion de contrase&ntilde;a en la aplicacion
	 * @param user usuario que actualiza su contrase&ntilde;a
	 * @param newPass la nueva contrase&ntilde;a que desea utilizar el usuario
	 * @param contentSchema cuerpo del correo electronico que se envia
	 * @param subject asunto del correo electronico que se envia
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de ediciones de contrase&ntilde;a permitidas en distintos
	 * lapsos de tiempo
	 * @return
	 * Si la accion se produce sin ningun error, retorna la cadena 'noError'.
	 * <br/>
	 * Si se alcanza el limite de ediciones de contrase&ntilde;a permitidas
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitPasswordEditsAtLastHour'</li>
	 * <li>'limitPasswordEditsAtLastDay'</li>
	 * <li>'limitPasswordEditsAtLastWeek'</li>
	 * <li>'limitPasswordEditsAtLastMonth'</li>
	 * <li>'limitPasswordEditsAtLastYear'</li></ul>
	 * @throws EntityAlreadyFoundException
	 */
	String sendEmailForEditPassword(User user,
			String newPass, List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits)
			throws EntityAlreadyFoundException;
}