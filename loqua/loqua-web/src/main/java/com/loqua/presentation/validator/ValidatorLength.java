package com.loqua.presentation.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.presentation.bean.BeanSettingsSession;

@FacesValidator("validatorLength")
public class ValidatorLength implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if( arg2 == null ){
			/*
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic("errorIsRequired")));
			*/
			return;
		}
		String name = arg2.toString();
		/*
		// Comprobar que no este vacio
		if (name == null || name.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic("errorIsRequired")));
		}
		*/
		String maxLength = (String) arg1.getAttributes().get("inputMaxLength");
		//Comprobar longitud nombre:
		if( name.length()>Integer.parseInt(maxLength) ){
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic("errorLength") +
					" (" + maxLength + ")"));
		}
	}
}
