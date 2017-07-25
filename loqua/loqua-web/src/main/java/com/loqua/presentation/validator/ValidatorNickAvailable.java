package com.loqua.presentation.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.bean.BeanSettingsSession;

@FacesValidator("validatorNickAvailable")
public class ValidatorNickAvailable implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorNickRequired")));
		}
		String nick = arg2.toString();
		
		// Comprobar que no este vacio
		if (nick == null || nick.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": "
					+ BeanSettingsSession.getTranslationStatic(
					"errorNickRequired")));
		}
		
		//Comprobar longitud nick:
		if( ! validateLength(nick) ){
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorNickLength")));
		}
		
		// Comprobar que el nick no esta usado por usuarios no eliminados:
		if( ! verifyNotNickExistsForNotRemovedUser(nick) ){
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorAlreadyFoundNick")));
		}
	}
	
	private boolean validateLength(String nick) {
		//Comprobar longitud nick:
		if( nick.length()>20 ){
			return false;
		}
		return true;
	}
	
	private boolean verifyNotNickExistsForNotRemovedUser(String nick) {
		// Comprobar que el email no esta siendo usado
		// por algun usuario no eliminado:
		User user = null;
		try{
			user = Factories.getService().getServiceUser()
					.getUserNotRemovedByNick(nick);
		}catch( Exception e ){
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorUnexpected")));
			// TODO log
		}
		if( user==null ){
			return true;
		}
		return false;
	}
}
