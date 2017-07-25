package com.loqua.business.services;

import java.util.List;
import java.util.Map;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.model.User;

public interface ServiceUserAccessDataChange {
	
	// // // // // // // // // // // //
	// METODOS PARA EL CAMBIO DE EMAIL
	// // // // // // // // // // // //
	
	ChangeEmail getEmailChangeByUrlConfirm(String urlConfirm);
	
	void updateEmailChange(ChangeEmail changeEmail) 
			throws EntityNotFoundException;
	
	String sendEmailForEditEmail(User user,String newEmail,List<String> content,
			String subject, Map<String, Integer> mapActionsLimits)
			throws EntityAlreadyFoundException;

	void sendEmailForEditEmailSecondStep(ChangeEmail changeEmail,
			List<String> content, String subject)
			throws EntityNotFoundException;
	
	// // // // // // // // // // // // // //
	// METODOS PARA EL CAMBIO DE CONTRASENA
	// // // // // // // // // // // // // //
	
	ChangePassword getPasswordChangeByUrlConfirm(
			String urlConfirm, String typeChangePassword);
	
	void updatePasswordChange(ChangePassword objectPasswordRestore)
			throws EntityNotFoundException;
	
	String sendEmailForPasswordRestore(User user, List<String> content,
			String subject, Map<String, Integer> mapActionsLimits) 
			throws EntityAlreadyFoundException;

	String sendEmailForEditPassword(User userReceiver,
			String newPass, List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits)
			throws EntityAlreadyFoundException;
}