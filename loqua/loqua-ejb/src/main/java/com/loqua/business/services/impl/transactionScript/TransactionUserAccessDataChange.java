package com.loqua.business.services.impl.transactionScript;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.MapEntityCounterByDate;
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.persistence.ChangeEmailJPA;
import com.loqua.persistence.ChangePasswordJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionUserAccessDataChange {

	private static final ChangeEmailJPA ChangeEmailJPA = new ChangeEmailJPA();
	private static final ChangePasswordJPA changePasswordJPA = 
			new ChangePasswordJPA();

	// // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME LA EDICION DE SU EMAIL
	// // // // // // // // // // // // // // // // // // // // //
	
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
	
	public void updateEmailChange(ChangeEmail ChangeEmail)
			throws EntityNotFoundException {
		try {
			ChangeEmailJPA.updateEmailChange(ChangeEmail);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU EMAIL
	// // // // // // // // // // // // // // // //
	
	public MapEntityCounterByDate getNumLastEmailChangesByUser(Long userID)
			/*throws EntityNotFoundException*/ {
		MapEntityCounterByDate result = new MapEntityCounterByDate();
		try {
			result = ChangeEmailJPA.getNumLastEmailChangesByUser(userID);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = new MapEntityCounterByDate();
		}
		return result;
	}

	public Long createEmailChange(ChangeEmail objectChangeEmail) 
			throws EntityAlreadyFoundException {
		try {
			return ChangeEmailJPA.createEmailChange(objectChangeEmail);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	// // // // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME RESTAURACION/EDICION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // // // // // // //
	
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
	
	public void updatePasswordChange(ChangePassword objectChangePassword)
			throws EntityNotFoundException {
		try {
			changePasswordJPA.updatePasswordChange(objectChangePassword);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	// // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO RESTAURE/EDITE SU CONTRASENA
	// // // // // // // // // // // // // // // // // // // // //
	
	public MapEntityCounterByDate getNumLastPasswordChangesByUser(
			Long userID, String typeChangePassword) 
			/*throws EntityNotFoundException*/ {
		MapEntityCounterByDate result = new MapEntityCounterByDate();
		try {
			result=changePasswordJPA.getNumLastPasswordChangesByUser(
					userID, typeChangePassword);
		} catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			result = new MapEntityCounterByDate();
		}
		return result;
	}

	public Long createPasswordChange(ChangePassword objectChangePassword) 
			throws EntityAlreadyFoundException {
		try {
			return changePasswordJPA.createPasswordChange(objectChangePassword);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
}
