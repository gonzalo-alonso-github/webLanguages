package com.loqua.business.services.impl.transactionScript;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.utils.externalAccounts.ManagementEmail;
import com.loqua.business.services.impl.utils.security.MapOccurrCounterByDate;
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.model.User;
import com.loqua.persistence.ChangeEmailJPA;
import com.loqua.persistence.ChangePasswordJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link ChangeEmail}, {@link ChangePassword}.<br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionUserAccessDataChange {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidad {@link ChangeEmail} */
	private static final ChangeEmailJPA ChangeEmailJPA = new ChangeEmailJPA();
	
	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidad {@link ChangePassword}
	 */
	private static final ChangePasswordJPA changePasswordJPA = 
			new ChangePasswordJPA();

	// // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME LA EDICION DE SU EMAIL
	// // // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Consulta el cambio de email, realizado por un usuario,
	 * segun el atributo 'urlConfirm'
	 * @param urlConfirm atributo homonimo del ChangeEmail que se consulta
	 * @return objeto ChangeEmail cuyo atributo 'urlConfirm' coincide
	 * con el parametro recibido, o null si no existe
	 */
	public ChangeEmail getEmailChangeByUrlConfirm(String urlConfirm)
			/*throws EntityNotFoundException*/{
		ChangeEmail result = new ChangeEmail();
		try {
			result = ChangeEmailJPA.getEmailChangeByUrlConfirm(urlConfirm);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);ç
			result = null;
		}
		return result;
	}
	
	/**
	 * Actualiza el objeto ChangeEmail dado
	 * @param changeEmail objeto ChangeEmail que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updateEmailChange(ChangeEmail changeEmail)
			throws EntityNotFoundException {
		try {
			ChangeEmailJPA.updateEmailChange(changeEmail);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU EMAIL
	// // // // // // // // // // // // // // // //
	
	/**
	 * Envia un correo electronico, a la direccion de email actual del usuario
	 * dado, para que confirme su cambio de email en la aplicacion
	 * @param updatedUser usuario que actualiza su email
	 * @param newEmail nueva direccion de email que desea utilizar el usuario
	 * @param content cuerpo del correo que se envia
	 * @param subject asunto del correo que se envia
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de cambios de email permitidos en distintos lapsos de tiempo
	 * @return
	 * Si la accion se produce sin ningun error, retorna la cadena 'noError'.
	 * <br>
	 * Si se alcanza el limite de cambios de email permitidos
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitEmailChangesAtLastHour'</li>
	 * <li>'limitEmailChangesAtLastDay'</li>
	 * <li>'limitEmailChangesAtLastWeek'</li>
	 * <li>'limitEmailChangesAtLastMonth'</li>
	 * <li>'limitEmailChangesAtLastYear'</li></ul>
	 * @throws EntityAlreadyFoundException
	 */
	public String sendEmailForEditEmail(User updatedUser, String newEmail,
			String content, String subject,
			Map<String, Integer> mapActionsLimits)
			throws EntityAlreadyFoundException {
		String result = validateNumLastEmailChanges(
				updatedUser.getId(), mapActionsLimits);
		if( ! result.equals("noError") ){
			return result;
		}
		// 128 bits son considerados suficientes para una codificacion segura,
		// pero en base32 cada digito codifica 5 bits
		// asi que utilizamos el siguiente multiplo de 5: 130
		// Por tanto: la urlConfirm tiene 26 caracteres (26*5=130),
		// suficientes para asegurarse de que es aleatoria
		String urlConfirm=new BigInteger(130, new SecureRandom()).toString(32);
		content += urlConfirm;
		ManagementEmail.sendEmail(updatedUser, content, subject);
		// Guardamos un nuevo objeto en la tabla ChangeEmail:
		generateObjectChangeEmail(updatedUser, urlConfirm, newEmail);
		return result;
	}
	
	/**
	 * Envia un correo electronico, a la direccion de email actualizada
	 * del usuario dado, para que confirme su cambio de email en la aplicacion
	 * @param changeEmail objeto ChangeEmail que representa el cambio de email
	 * que se desea confirmar
	 * @param content cuerpo del correo que se envia
	 * @param subject asunto del correo que se envia
	 * @throws EntityNotFoundException
	 */
	public void sendEmailForEditEmailSecondStep(ChangeEmail changeEmail,
			String content, String subject)
			throws EntityNotFoundException {
		// 128 bits son considerados suficientes para una codificacion segura,
		// pero en base32 cada digito codifica 5 bits
		// asi que utilizamos el siguiente multiplo de 5: 130
		// Por tanto: la urlConfirm tiene 26 caracteres (26*5=130),
		// suficientes para asegurarse de que es aleatoria
		String urlConfirm=new BigInteger(130, new SecureRandom()).toString(32);
		content += urlConfirm;
		
		User userToUpdate = changeEmail.getUser();
		userToUpdate.setEmail(changeEmail.getNewEmail());
		ManagementEmail.sendEmail(userToUpdate, content, subject);
		
		// Editamos el nuevo objeto de la tabla ChangeEmail:
		changeEmail.setUrlConfirm(urlConfirm);
		updateEmailChange(changeEmail);
	}
	
	/**
	 * Comprueba si la cantidad de cambios de email del usuario en distintos
	 * lapsos de tiempo supera el numero maximo indicado
	 * @param userID atributo 'id' del User que se consulta
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de cambios de email permitidos en distintos lapsos de tiempo
	 * @return
	 * Si no se alcanza el limite de cambios de email permitidos
	 * en cierto lapso de tiempo, retorna la cadena 'noError'.
	 * <br>
	 * Si se alcanza el limite de cambios de email permitidos
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitEmailChangesAtLastHour'</li>
	 * <li>'limitEmailChangesAtLastDay'</li>
	 * <li>'limitEmailChangesAtLastWeek'</li>
	 * <li>'limitEmailChangesAtLastMonth'</li>
	 * <li>'limitEmailChangesAtLastYear'</li></ul>
	 */
	private String validateNumLastEmailChanges(
			Long userID, Map<String, Integer> mapActionsLimits){
		String result = "noError";
		MapOccurrCounterByDate numOccurrences = 
				getNumLastEmailChangesByUser(userID);
		if( numOccurrences.getOccurrencesLastHour() >=
				mapActionsLimits.get("limitEmailChangesAtLastHour") ){
			return "limitEmailChangesAtLastHour";
		}
		if( numOccurrences.getOccurrencesLastDay() >=
				mapActionsLimits.get("limitEmailChangesAtLastDay") ){
			return "limitEmailChangesAtLastDay";
		}
		if( numOccurrences.getOccurrencesLastWeek() >=
				mapActionsLimits.get("limitEmailChangesAtLastWeek") ){
			return "limitEmailChangesAtLastWeek";
		}
		if( numOccurrences.getOccurrencesLastMonth() >=
				mapActionsLimits.get("limitEmailChangesAtLastMonth") ){
			return "limitEmailChangesAtLastMonth";
		}
		if( numOccurrences.getOccurrencesLastYear() >=
				mapActionsLimits.get("limitEmailChangesAtLastYear") ){
			return "limitEmailChangesAtLastYear";
		}
		return result;
	}
	
	/**
	 * Crea y agrega al sistema un nuevo objeto ChangeEmail a partir
	 * de los parametros recibidos
	 * @param user usuario que realiza el cambio de email
	 * @param urlConfirm atributo homonimo del ChangeEmail que se genera
	 * @param newEmail atributo homonimo del ChangeEmail que se genera
	 * @throws EntityAlreadyFoundException
	 */
	private void generateObjectChangeEmail(
			User user, String urlConfirm, String newEmail)
			throws EntityAlreadyFoundException {
		ChangeEmail objectChangeEmail=new ChangeEmail(user);
		objectChangeEmail.setPreviousEmail(user.getEmail());
		objectChangeEmail.setNewEmail(newEmail);
		objectChangeEmail.setUrlConfirm(urlConfirm);
		objectChangeEmail.setConfirmedPreviousEmail(false);
		objectChangeEmail.setConfirmedNewEmail(false);
		objectChangeEmail.setDate(new Date());
		createEmailChange(objectChangeEmail);
	}
	
	/**
	 * Halla el numero de veces que el usuario ha cambiado su email
	 * en distintos lapsos de tiempo
	 * @param userID atributo 'id' del User que se consulta
	 * @return instancia de MapEntityCounterByDate que almacena la cantidad
	 * de veces que el usuario dado ha agregado elementos ChangeEmail
	 * a base de datos a lo largo de varios lapsos de tiempo (el Map clasifica
	 * los siguientes lapsos: por hora, por dia, por semana, por mes y por
	 * a&ntilde;o)
	 */
	private MapOccurrCounterByDate getNumLastEmailChangesByUser(Long userID)
			/*throws EntityNotFoundException*/ {
		MapOccurrCounterByDate result = new MapOccurrCounterByDate();
		try {
			result = ChangeEmailJPA.getNumLastEmailChangesByUser(userID);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = new MapOccurrCounterByDate();
		}
		return result;
	}

	/**
	 * Agrega al sistema un nuevo objeto ChangeEmail
	 * @param changeEmail objeto ChangeEmail que se desea agregar
	 * @return atributo 'id' del elemento ChangeEmail guardado
	 * @throws EntityAlreadyFoundException
	 */
	private Long createEmailChange(ChangeEmail changeEmail) 
			throws EntityAlreadyFoundException {
		try {
			return ChangeEmailJPA.createEmailChange(changeEmail);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	// // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO RESTAURE SU CONTRASENA
	// // // // // // // // // // // // // // // // // // //
	
	/**
	 * Envia un correo electronico a la direccion de email del usuario dado,
	 * para que confirme su restauracion de contrase&ntilde;a en la aplicacion
	 * @param updatedUser usuario que actualiza su contrase&ntilde;a
	 * @param content cuerpo del correo que se envia
	 * @param subject asunto del correo que se envia
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de restauraciones de contrase&ntilde;a permitidas en distintos
	 * lapsos de tiempo
	 * @return
	 * Si la accion se produce sin ningun error, retorna la cadena 'noError'.
	 * <br>
	 * Si se alcanza el limite de restauraciones de contrase&ntilde;a permitidas
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitPasswordRestoresAtLastHour'</li>
	 * <li>'limitPasswordRestoresAtLastDay'</li>
	 * <li>'limitPasswordRestoresAtLastWeek'</li>
	 * <li>'limitPasswordRestoresAtLastMonth'</li>
	 * <li>'limitPasswordRestoresAtLastYear'</li></ul>
	 * @throws EntityAlreadyFoundException
	 */
	public String sendEmailForPasswordRestore(User updatedUser,
			List<String> content, String subject,
			Map<String, Integer> mapActionsLimits) 
			throws EntityAlreadyFoundException {
		String result = validateNumLastPasswordRestores(
				updatedUser.getId(), mapActionsLimits);
		if( ! result.equals("noError") ){
			return result;
		}
		mailAndCreateRandomPasswordChange(updatedUser, content, subject,
				ChangePassword.RESTORE);
		return result;
	}
	
	/**
	 * Comprueba si la cantidad de cambios de contrase&ntilde;a del usuario
	 * en distintos lapsos de tiempo supera el numero maximo indicado
	 * @param userID atributo 'id' del User que se consulta
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de restauraciones de contrase&ntilde;a permitidas en distintos
	 * lapsos de tiempo
	 * @return
	 * Si no se alcanza el limite de restauraciones de contrase&ntilde;a
	 * permitidas en cierto lapso de tiempo, retorna la cadena 'noError'.
	 * <br>
	 * Si se alcanza el limite de restauraciones de contrase&ntilde;a permitidas
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitPasswordRestoresAtLastHour'</li>
	 * <li>'limitPasswordRestoresAtLastDay'</li>
	 * <li>'limitPasswordRestoresAtLastWeek'</li>
	 * <li>'limitPasswordRestoresAtLastMonth'</li>
	 * <li>'limitPasswordRestoresAtLastYear'</li></ul>
	 */
	private String validateNumLastPasswordRestores(
			Long userID, Map<String, Integer> mapActionsLimits){
		String result = "noError";
		MapOccurrCounterByDate numOccurrences =
				getNumLastPasswordChangesByUser(userID, ChangePassword.RESTORE);
		if( numOccurrences.getOccurrencesLastHour() >=
				mapActionsLimits.get("limitPasswordRestoresAtLastHour") ){
			return "limitPasswordRestoresAtLastHour";
		}
		if( numOccurrences.getOccurrencesLastDay() >=
				mapActionsLimits.get("limitPasswordRestoresAtLastDay") ){
			return "limitPasswordRestoresAtLastDay";
		}
		if( numOccurrences.getOccurrencesLastWeek() >=
				mapActionsLimits.get("limitPasswordRestoresAtLastWeek") ){
			return "limitPasswordRestoresAtLastWeek";
		}
		if( numOccurrences.getOccurrencesLastMonth() >=
				mapActionsLimits.get("limitPasswordRestoresAtLastMonth") ){
			return "limitPasswordRestoresAtLastMonth";
		}
		if( numOccurrences.getOccurrencesLastYear() >=
				mapActionsLimits.get("limitPasswordRestoresAtLastYear") ){
			return "limitPasswordRestoresAtLastYear";
		}
		return result;
	}
	
	/**
	 * Envia un correo al usuario dado con su nueva contrase&ntilde;a generada
	 * aleatoriamente, y agrega al sistema un objeto ChangePassword
	 * @param userReceiver usuario que recibe el correo
	 * @param contentSchema cuerpo del correo electronico que se envia
	 * @param subject asunto del correo electronico que se envia
	 * @param typeChangePassword atributo homonimo del ChangePassword que se
	 * genera, que es un Enumeration que puede ser 'RESTORE' o 'EDIT'
	 * @throws EntityAlreadyFoundException
	 */
	private void mailAndCreateRandomPasswordChange(User userReceiver,
			List<String> contentSchema,String subject,String typeChangePassword)
			throws EntityAlreadyFoundException {
		SecureRandom random = new SecureRandom();
		// 128 bits son considerados suficientes para una codificacion segura,
		// pero en base32 cada digito codifica 5 bits
		// asi que utilizamos el siguiente multiplo de 5: 130
		// Por tanto: la urlConfirm tiene 26 caracteres (26*5=130),
		// suficientes para asegurarse de que es aleatoria
		String urlConfirm = new BigInteger(130, random).toString(32);
		// la password no tiene por que ser larga: bastan 8 caracteres (8*5=45)
		String newPassword = new BigInteger(45, random).toString(32);
		String content = contentSchema.get(0) + urlConfirm
				+ contentSchema.get(1) + newPassword;
		ManagementEmail.sendEmail(userReceiver, content, subject);
		
		// Guardamos un nuevo objeto en la tabla ChangePassword:
		generateObjectPasswordChange(userReceiver, urlConfirm, newPassword,
				typeChangePassword);
	}
	
	/**
	 * Cifra una cadena de texto dada aplicando un hash
	 * @param password cadena de texto que se cifra
	 * @return la cadena de texto recibida, una vez aplicado el hash
	 */
	private String hashPassword(String password){
		String salt = BCrypt.gensalt(12);
		String hashedPassword = BCrypt.hashpw(password, salt);
		return hashedPassword;
	}
	
	// // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU CONTRASENA
	// // // // // // // // // // // // // // // // //
	
	/**
	 * Envia un correo electronico a la direccion de email del usuario dado,
	 * para que confirme su edicion de contrase&ntilde;a en la aplicacion
	 * @param updatedUser usuario que actualiza su contrase&ntilde;a
	 * @param newPass la nueva contrase&ntilde;a que desea utilizar el usuario
	 * @param contentSchema cuerpo del correo electronico que se envia
	 * @param subject asunto del correo electronico que se envia
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de ediciones de contrase&ntilde;a permitidas en distintos
	 * lapsos de tiempo
	 * @return
	 * Si la accion se produce sin ningun error, retorna la cadena 'noError'.
	 * <br>
	 * Si se alcanza el limite de ediciones de contrase&ntilde;a permitidas
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitPasswordEditsAtLastHour'</li>
	 * <li>'limitPasswordEditsAtLastDay'</li>
	 * <li>'limitPasswordEditsAtLastWeek'</li>
	 * <li>'limitPasswordEditsAtLastMonth'</li>
	 * <li>'limitPasswordEditsAtLastYear'</li></ul>
	 * @throws EntityAlreadyFoundException
	 */
	public String sendEmailForEditPassword(User updatedUser, String newPass,
			List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits) 
			throws EntityAlreadyFoundException {
		String result = validateNumLastPasswordEdits(
				updatedUser.getId(), mapActionsLimits);
		if( ! result.equals("noError") ){
			return result;
		}
		mailAndCreatePasswordChange(updatedUser,newPass,contentSchema,subject,
				ChangePassword.EDIT);
		return result;
	}
	
	/**
	 * Comprueba si la cantidad de ediciones de contrase&ntilde;a del usuario
	 * en distintos lapsos de tiempo supera el numero maximo indicado
	 * @param userID atributo 'id' del User que se consulta
	 * @param mapActionsLimits Map&lt;String, Integer&gt;, cargado a partir
	 * del fichero 'numActionsAtPeriod.properties', que indica el numero
	 * maximo de ediciones de contrase&ntilde;a permitidas en distintos
	 * lapsos de tiempo
	 * @return
	 * Si no se alcanza el limite de ediciones de contrase&ntilde;a permitidas
	 * en cierto lapso de tiempo, retorna la cadena 'noError'.
	 * <br>
	 * Si se alcanza el limite de ediciones de contrase&ntilde;a permitidas
	 * en cierto lapso de tiempo, se devuelve una cadena mas descriptiva:
	 * <ul><li>'limitPasswordEditsAtLastHour'</li>
	 * <li>'limitPasswordEditsAtLastDay'</li>
	 * <li>'limitPasswordEditsAtLastWeek'</li>
	 * <li>'limitPasswordEditsAtLastMonth'</li>
	 * <li>'limitPasswordEditsAtLastYear'</li></ul>
	 */
	private String validateNumLastPasswordEdits(
			Long userID, Map<String, Integer> mapActionsLimits){
		String result = "noError";
		MapOccurrCounterByDate numOccurrences =
				getNumLastPasswordChangesByUser(userID, ChangePassword.EDIT);
		if( numOccurrences.getOccurrencesLastHour() >=
				mapActionsLimits.get("limitPasswordEditsAtLastHour") ){
			return "limitPasswordEditsAtLastHour";
		}
		if( numOccurrences.getOccurrencesLastDay() >=
				mapActionsLimits.get("limitPasswordEditsAtLastDay") ){
			return "limitPasswordEditsAtLastDay";
		}
		if( numOccurrences.getOccurrencesLastWeek() >=
				mapActionsLimits.get("limitPasswordEditsAtLastWeek") ){
			return "limitPasswordEditsAtLastWeek";
		}
		if( numOccurrences.getOccurrencesLastMonth() >=
				mapActionsLimits.get("limitPasswordEditsAtLastMonth") ){
			return "limitPasswordEditsAtLastMonth";
		}
		if( numOccurrences.getOccurrencesLastYear() >=
				mapActionsLimits.get("limitPasswordEditsAtLastYear") ){
			return "limitPasswordEditsAtLastYear";
		}
		return result;
	}
	
	/**
	 * Envia un correo al usuario dado y agrega al sistema un objeto
	 * ChangePassword
	 * @param userReceiver usuario que recibe el correo
	 * @param newPass la nueva contrase&ntilde;a que desea utilizar el usuario
	 * @param contentSchema cuerpo del correo electronico que se envia
	 * @param subject asunto del correo electronico que se envia
	 * @param typeChangePassword atributo homonimo del ChangePassword que se
	 * genera, que es un Enumeration que puede ser 'RESTORE' o 'EDIT'
	 * @throws EntityAlreadyFoundException
	 */
	private void mailAndCreatePasswordChange(User userReceiver, String newPass,
			List<String> contentSchema,String subject,String typeChangePassword)
			throws EntityAlreadyFoundException {
		SecureRandom random = new SecureRandom();
		// la urlConfirm tiene 26 caracteres (6*5=130)
		String urlConfirm = new BigInteger(130, random).toString(32);
		
		String content = contentSchema.get(0) + urlConfirm
				+ contentSchema.get(1) + newPass;
		ManagementEmail.sendEmail(userReceiver, content, subject);
		
		// Guardamos un nuevo objeto en la tabla ChangePassword:
		userReceiver.setPassword(hashPassword(userReceiver.getPassword()));
		generateObjectPasswordChange(userReceiver, urlConfirm,
				newPass, typeChangePassword);
	}
	
	// // // // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME RESTAURACION/EDITCION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Crea y agrega al sistema un nuevo objeto ChangePassword a partir
	 * de los parametros recibidos
	 * @param user usuario que realiza el cambio de contrase&ntilde;a
	 * @param urlConfirm atributo homonimo del ChangePassword que se genera
	 * @param newPassword atributo homonimo del ChangePassword que se genera
	 * @param typeChangePassword atributo homonimo del ChangePassword
	 * que se genera, que es un Enumeration que puede ser 'RESTORE' o 'EDIT'
	 * @throws EntityAlreadyFoundException
	 */
	private void generateObjectPasswordChange(User user, String urlConfirm,
			String newPassword, String typeChangePassword) 
			throws EntityAlreadyFoundException {
		ChangePassword objectPasswordChange=new ChangePassword(user);
		objectPasswordChange.setPasswordRemoved(user.getPassword());
		objectPasswordChange.setUrlConfirm(urlConfirm);
		objectPasswordChange.setConfirmed(false);
		objectPasswordChange.setDate(new Date());
		objectPasswordChange.setTypeChangePassword(typeChangePassword);
		String hashedNewPassword = hashPassword(newPassword);
		
		objectPasswordChange.setPasswordGenerated(hashedNewPassword);
		createPasswordChange(objectPasswordChange);
	}
	
	/**
	 * Halla el numero de veces que el usuario ha cambiado su contrase&ntilde;a
	 * en distintos lapsos de tiempo
	 * @param userID atributo 'id' del User que se consulta
	 * @param typeChangePassword filtro de busqueda para el atributo homonimo
	 * de ChangePassword, que es un Enumeration que puede ser 'RESTORE' o 'EDIT'
	 * @return instancia de MapEntityCounterByDate que almacena la cantidad
	 * de veces que el usuario dado ha agregado elementos ChangePassword
	 * a base de datos a lo largo de varios lapsos de tiempo (el Map clasifica
	 * los siguientes lapsos: por hora, por dia, por semana, por mes y por
	 * a&ntilde;o)
	 */
	public MapOccurrCounterByDate getNumLastPasswordChangesByUser(
			Long userID, String typeChangePassword) 
			/*throws EntityNotFoundException*/ {
		MapOccurrCounterByDate result = new MapOccurrCounterByDate();
		try {
			result=changePasswordJPA.getNumLastPasswordChangesByUser(
					userID, typeChangePassword);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = new MapOccurrCounterByDate();
		}
		return result;
	}

	/**
	 * Agrega al sistema un nuevo objeto ChangePassword
	 * @param changePassword objeto ChangePassword que se desea agregar
	 * @return atributo 'id' del elemento ChangePassword guardado
	 * @throws EntityAlreadyFoundException
	 */
	public Long createPasswordChange(ChangePassword changePassword) 
			throws EntityAlreadyFoundException {
		try {
			return changePasswordJPA.createPasswordChange(changePassword);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	// // // // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME RESTAURACION/EDICION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Consulta el cambio de contrase&ntilde;a, realizado por un usuario,
	 * segun el atributo 'urlConfirm'
	 * @param urlConfirm atributo homonimo del ChangePassword que se consulta
	 * @param typeChangePassword filtro de busqueda para el atributo homonimo
	 * de ChangePassword, que es un Enumeration que puede ser 'RESTORE' o 'EDIT'
	 * @return objeto ChangePassword cuyos atributos
	 * coinciden con los parametros recibidos, o null si no existe
	 */
	public ChangePassword getPasswordChangeByUrlConfirm(
			String urlConfirm, String typeChangePassword) 
			/*throws EntityNotFoundException */{
		ChangePassword result = new ChangePassword();
		try {
			result = changePasswordJPA.getPasswordChangeByUrlConfirm(
					urlConfirm, typeChangePassword);
		} catch (EntityNotPersistedException ex) {
			// throw new EntityNotFoundException(ex);
			result = null;
		}
		return result;
	}
	
	/**
	 * Actualiza el objeto ChangePassword dado
	 * @param objectChangePassword objeto ChangePassword que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updatePasswordChange(ChangePassword objectChangePassword)
			throws EntityNotFoundException {
		try {
			changePasswordJPA.updatePasswordChange(objectChangePassword);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
}
