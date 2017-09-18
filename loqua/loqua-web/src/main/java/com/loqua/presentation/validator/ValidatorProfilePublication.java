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
 * UIComponent sobre el que se aplica no esta vacio, y ademas
 * tiene una longitud de caracteres que no sobrepasa un limite dado
 * @author Gonzalo
 */
@FacesValidator("validatorProfilePublication")
public class ValidatorProfilePublication implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		// Comprobar que no este vacio
		if (arg2 == null || arg2.toString().isEmpty()) {
			throw new ValidatorException(new FacesMessage(""));
		}
		String value = arg2.toString();
		String maxLength = (String) arg1.getAttributes().get("inputMaxLength");
		//Comprobar longitud:
		if( value.length()>Integer.parseInt(maxLength) ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic("errorLength") +
					" (" + maxLength + ")"));
		}
	}
}
