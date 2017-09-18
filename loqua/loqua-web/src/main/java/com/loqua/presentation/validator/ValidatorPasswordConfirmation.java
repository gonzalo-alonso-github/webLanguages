package com.loqua.presentation.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.presentation.bean.BeanSettingsSession;

/**
 * Define un validador encargado de comprobar si el valor del componente
 * UIComponent sobre el que se aplica es igual a la contrase&ntilde;a
 * introducida previamente en otro UIComponent.
 * @author Gonzalo
 */
@FacesValidator("validatorPasswordConfirmation")
public class ValidatorPasswordConfirmation implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(inputLabel
					+ ": " + BeanSettingsSession.getTranslationStatic(
							"errorPasswordConfirmRequired")));
		}
		String passwordConfirmation = arg2.toString();
		
		// Comprobar que no este vacio
		if (passwordConfirmation == null || passwordConfirmation.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel
					+ ": " + BeanSettingsSession.getTranslationStatic(
							"errorPasswordConfirmRequired")));
		}
		
		passwordConfirmation = passwordConfirmation.replaceAll(" +$", "");
		passwordConfirmation = passwordConfirmation.replaceAll("^ +", "");
		//BeanRegister.setpassword(password);
		
		//Comprobar que la confirmacion de password coincide con la original:
		if( ! passwordConfirmation.equalsIgnoreCase( ValidatorPassword.getPassword() ) ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic("errorConfirmPassword")));
		}
	}
}
