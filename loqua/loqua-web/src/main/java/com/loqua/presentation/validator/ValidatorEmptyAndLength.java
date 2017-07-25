package com.loqua.presentation.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.presentation.bean.BeanSettingsSession;

@FacesValidator("validatorEmptyAndLength")
public class ValidatorEmptyAndLength implements Validator {

	// NOTA: Este validator no se usa nunca, se podria eliminar esta clase.
	// En lugar de este, en los inputs siempre se usa el "ValidatorLength"
	// y, si es necesario, la propiedad "required"
	
	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic("errorIsRequired")));
		}
		String name = arg2.toString();
		
		// Comprobar que no este vacio
		if (name == null || name.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic("errorIsRequired")));
		}
		
		String maxLength = (String) arg1.getAttributes().get("inputMaxLength");
		//Comprobar longitud nombre:
		if( name.length()>Integer.parseInt(maxLength) ){
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic("errorLength") +
					" (" + maxLength + ")"));
		}
	}
}
