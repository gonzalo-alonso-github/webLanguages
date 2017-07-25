package com.loqua.business.services.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import org.mindrot.jbcrypt.BCrypt;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionUserAccessDataChange;
import com.loqua.business.services.serviceLocal.LocalServiceUserAccessDataChange;
import com.loqua.business.services.serviceRemote.RemoteServiceUserAccessDataChange;
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.model.User;

@Stateless
@WebService(name="ServiceUserAccessDataChange")
public class EjbServiceUserAccessDataChange
		implements LocalServiceUserAccessDataChange,
		RemoteServiceUserAccessDataChange {

	private static final TransactionUserAccessDataChange transactionDataChange = 
			new TransactionUserAccessDataChange();
	
	// // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME LA EDICION DE SU EMAIL
	// // // // // // // // // // // // // // // // // // // // //
	
	@Override
	public ChangeEmail getEmailChangeByUrlConfirm(String urlConfirm){
		/*
		ChangeEmail result = new ChangeEmail();
		try{
			result = transactionDataChange
					.getEmailChangeByUrlConfirm(urlConfirm);
		}catch( EntityNotFoundException e ){
			result = null;
		}
		return result;
		*/
		return transactionDataChange.getEmailChangeByUrlConfirm(urlConfirm);
	}
	
	@Override
	public void updateEmailChange(ChangeEmail changeEmail) 
			throws EntityNotFoundException {
		transactionDataChange.updateEmailChange(changeEmail);
	}
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU EMAIL
	// // // // // // // // // // // // // // // //
	
	// http://www.codecodex.com/wiki/Generate_a_random_password_or_random_string
	// 128 bits son considerados suficientes para una codificacion segura,
	// pero en base32 cada digito codifica 5 bits
	// asi que utilizamos el siguiente multiplo de 5: 130
	// Por tanto: con 26 caracteres (son 130/5) tenemos una cadena segura
	@Override
	public String sendEmailForEditEmail(User updatedUser, String newEmail,
			List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits)
			throws EntityAlreadyFoundException {
		String result = validateNumLastEmailChanges(
				updatedUser.getId(), mapActionsLimits);
		if( ! result.equals("noError") ){
			return result;
		}
		// la urlConfirm tiene 26 caracteres (26*5=130)
		String urlConfirm=new BigInteger(130, new SecureRandom()).toString(32);
		String content = contentSchema.get(0) + urlConfirm;
		ManagementEmail.sendEmail(updatedUser, content, subject);
		// Guardamos un nuevo objeto en la tabla ChangeEmail:
		generateObjectChangeEmail(updatedUser, urlConfirm, newEmail);
		return result;
	}
	
	private String validateNumLastEmailChanges(
			Long userID, Map<String, Integer> mapActionsLimits){
		String result = "noError";
		MapEntityCounterByDate numOccurrences = 
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
	
	private MapEntityCounterByDate getNumLastEmailChangesByUser(Long userID){
		/*
		MapEntityCounterByDate result = null;
		try{
			result = transactionDataChange.getNumLastEmailChangesByUser(userID);
		}catch( EntityNotFoundException e ){
			result = new MapEntityCounterByDate();
		}
		return result;
		*/
		return transactionDataChange.getNumLastEmailChangesByUser(userID);
	}
	
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
	
	private Long createEmailChange(ChangeEmail objectChangeEmail) 
			throws EntityAlreadyFoundException {
		return transactionDataChange.createEmailChange(objectChangeEmail);
	}
	
	// http://www.codecodex.com/wiki/Generate_a_random_password_or_random_string
	// 128 bits son considerados suficientes para una codificacion segura,
	// pero en base32 cada digito codifica 5 bits
	// asi que utilizamos el siguiente multiplo de 5: 130
	// Por tanto: con 26 caracteres (son 130/5) tenemos una cadena segura
	@Override
	public void sendEmailForEditEmailSecondStep(ChangeEmail changeEmail,
			List<String> contentSchema, String subject)
			throws EntityNotFoundException {
		// la urlConfirm tiene 26 caracteres (6*5=130)
		String urlConfirm=new BigInteger(130, new SecureRandom()).toString(32);
		String content = contentSchema.get(0) + urlConfirm;
		
		User userToUpdate = changeEmail.getUser();
		userToUpdate.setEmail(changeEmail.getNewEmail());
		ManagementEmail.sendEmail(userToUpdate, content, subject);
		
		// Editamos el nuevo objeto de la tabla ChangeEmail:
		changeEmail.setUrlConfirm(urlConfirm);
		updateEmailChange(changeEmail);
	}
	
	// // // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME RESTAURACION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // // // // // //
	
	@Override
	public ChangePassword getPasswordChangeByUrlConfirm(
			String urlConfirm, String typeChangePassword){
		return transactionDataChange.getPasswordChangeByUrlConfirm(
				urlConfirm, typeChangePassword);
	}
	
	@Override
	public void updatePasswordChange(ChangePassword objectPasswordRestore)
			throws EntityNotFoundException {
		transactionDataChange.updatePasswordChange(objectPasswordRestore);
	}
	
	// // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO RESTAURE SU CONTRASENA
	// // // // // // // // // // // // // // // // // // //
	
	// http://www.codecodex.com/wiki/Generate_a_random_password_or_random_string
	// 128 bits son considerados suficientes para una codificacion segura,
	// pero en base32 cada digito codifica 5 bits
	// asi que utilizamos el siguiente multiplo de 5: 130
	// Por tanto: con 26 caracteres (son 130/5) tenemos una cadena segura
	@Override
	public String sendEmailForPasswordRestore(User userReceiver,
			List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits) 
			throws EntityAlreadyFoundException {
		String result = validateNumLastPasswordRestores(
				userReceiver.getId(), mapActionsLimits);
		if( ! result.equals("noError") ){
			return result;
		}
		mailAndCreateRandomPasswordChange(userReceiver, contentSchema, subject,
				ChangePassword.RESTORE);
		return result;
	}
	
	private String validateNumLastPasswordRestores(
			Long userID, Map<String, Integer> mapActionsLimits){
		String result = "noError";
		MapEntityCounterByDate numOccurrences =
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

	private MapEntityCounterByDate getNumLastPasswordChangesByUser(
			Long userID, String typeChangePassword){
		/*
		MapEntityCounterByDate result = null;
		try{
			result = transactionDataChange.getNumLastPasswordChangesByUser(
					userID, typeChangePassword);
		}catch( EntityNotFoundException e ){
			result = new MapEntityCounterByDate();
		}
		return result;
		*/
		return transactionDataChange.getNumLastPasswordChangesByUser(
				userID, typeChangePassword);
	}
	
	private void mailAndCreateRandomPasswordChange(User userReceiver,
			List<String> contentSchema,String subject,String typeChangePassword)
			throws EntityAlreadyFoundException {
		SecureRandom random = new SecureRandom();
		// la urlConfirm tiene 26 caracteres (6*5=130)
		String urlConfirm = new BigInteger(130, random).toString(32);
		// la password no tiene que ser tan larga: bastan 8 caracteres (8*5=45)
		String newPassword = new BigInteger(45, random).toString(32);
		String content = contentSchema.get(0) + urlConfirm
				+ contentSchema.get(1) + newPassword;
		ManagementEmail.sendEmail(userReceiver, content, subject);
		
		// Guardamos un nuevo objeto en la tabla ChangePassword:
		generateObjectPasswordChange(userReceiver, urlConfirm, newPassword,
				typeChangePassword);
	}
	
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
	
	private Long createPasswordChange(ChangePassword objectPasswordRestore) 
			throws EntityAlreadyFoundException {
		return transactionDataChange.createPasswordChange(
				objectPasswordRestore);
	}
	
	private String hashPassword(String password){
		String salt = BCrypt.gensalt(12);
		String hashedPassword = BCrypt.hashpw(password, salt);
		return hashedPassword;
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU CONTRASENA
	// // // // // // // // // // // // // // // // // //
	
	// http://www.codecodex.com/wiki/Generate_a_random_password_or_random_string
	// 128 bits son considerados suficientes para una codificacion segura,
	// pero en base32 cada digito codifica 5 bits
	// asi que utilizamos el siguiente multiplo de 5: 130
	// Por tanto: con 26 caracteres (son 130/5) tenemos una cadena segura
	@Override
	public String sendEmailForEditPassword(User userReceiver, String newPass,
			List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits) 
			throws EntityAlreadyFoundException {
		String result = validateNumLastPasswordEditions(
				userReceiver.getId(), mapActionsLimits);
		if( ! result.equals("noError") ){
			return result;
		}
		mailAndCreatePasswordChange(userReceiver,newPass,contentSchema,subject,
				ChangePassword.EDIT);
		return result;
	}
	
	private String validateNumLastPasswordEditions(
			Long userID, Map<String, Integer> mapActionsLimits){
		String result = "noError";
		MapEntityCounterByDate numOccurrences =
				getNumLastPasswordChangesByUser(userID, ChangePassword.RESTORE);
		if( numOccurrences.getOccurrencesLastHour() >=
				mapActionsLimits.get("limitPasswordChangesAtLastHour") ){
			return "limitPasswordChangesAtLastHour";
		}
		if( numOccurrences.getOccurrencesLastDay() >=
				mapActionsLimits.get("limitPasswordChangesAtLastDay") ){
			return "limitPasswordChangesAtLastDay";
		}
		if( numOccurrences.getOccurrencesLastWeek() >=
				mapActionsLimits.get("limitPasswordChangesAtLastWeek") ){
			return "limitPasswordChangesAtLastWeek";
		}
		if( numOccurrences.getOccurrencesLastMonth() >=
				mapActionsLimits.get("limitPasswordChangesAtLastMonth") ){
			return "limitPasswordChangesAtLastMonth";
		}
		if( numOccurrences.getOccurrencesLastYear() >=
				mapActionsLimits.get("limitPasswordChangesAtLastYear") ){
			return "limitPasswordChangesAtLastYear";
		}
		return result;
	}
	
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
}
