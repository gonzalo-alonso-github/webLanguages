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
import com.loqua.presentation.logging.LoquaLogger;

@FacesValidator("validatorEmailAvailable")
public class ValidatorEmailAvailable implements Validator {
	
	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorEmailRequired")));
		}
		String email = arg2.toString();
		
		// Comprobar que no este vacio
		if (email == null || email.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": "
		+ BeanSettingsSession.getTranslationStatic("errorEmailRequired")));
		}
		
		//Comprobar longitud email:
		if( ! validateLength(email) ){
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorEmailLength")));
		}
		// Comprobar formato de email:
		if( ! validateRegex(email) ){
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorEmailExpression")));
		}
		// Comprobar que el email no esta usado por usuarios no eliminados:
		if( ! verifyNotEmailExistsForNotRemovedUser(email) ){
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorAlreadyFoundEmail")));
		}
	}

	private boolean validateLength(String email) {
		//Comprobar longitud email:
		if( email.length()>40 ){
			return false;
		}
		return true;
	}
	
	private boolean validateRegex(String email) {
		// Comprobar formato de email:
		if( ! email.matches(EMAIL_PATTERN) ){
			return false;
		}
		return true;
	}
	
	private boolean verifyNotEmailExistsForNotRemovedUser(String email) {
		// Comprobar que el email no esta siendo usado
		// por algun usuario no eliminado:
		User user = null;
		try{
			user = Factories.getService().getServiceUser()
					.getUserNotRemovedByEmail(email);
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'verifyNotEmailExistsForNotRemovedUser()'");
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorUnexpected")));
		}
		if( user==null ){
			return true;
		}
		return false;
	}
}
