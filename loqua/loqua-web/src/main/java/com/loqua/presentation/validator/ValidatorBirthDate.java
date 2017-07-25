package com.loqua.presentation.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.presentation.bean.BeanSettingsSession;

@FacesValidator("validatorBirthDate")
public class ValidatorBirthDate implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic("errorBirthDateIsRequired")));
		}
		Date birthDate = (Date) arg2;
		// Comprobar mayoria de edad:
		if( ! validateFullAge(birthDate) ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic("errorBirthDateNotFullAge")));
		}
	}
	
	private boolean validateFullAge(Date birthDate) {
		Date eighteenYearsAgo = new Date();
		eighteenYearsAgo.setTime( (new Date()).getTime()-567648000000l );
		if( ! birthDate.before(eighteenYearsAgo) ){
			return false;
		}
		return true;
	}
}
