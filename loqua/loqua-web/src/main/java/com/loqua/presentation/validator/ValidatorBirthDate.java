package com.loqua.presentation.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.presentation.bean.BeanSettingsSession;

/**
 * Define un validador encargado de comprobar si el valor del componente
 * UIComponent sobre el que se aplica corresponde a un formato de fecha,
 * donde la antig&uuml;edad de esta es superior a la mayoia de edad predefinida
 * (18 a&ntilde;os).
 * @author Gonzalo
 */
@FacesValidator("validatorBirthDate")
public class ValidatorBirthDate implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic(
							"errorBirthDateIsRequired")));
		}
		Date birthDate = (Date) arg2;
		// Comprobar mayoria de edad:
		if( ! validateFullAge(birthDate) ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic(
							"errorBirthDateNotFullAge")));
		}
	}
	
	/**
	 * Comprueba si la fecha dada tiene una antig&uuml;edad superior a
	 * la mayoia de edad predefinida (18 a&ntilde;os).
	 * @param birthDate fecha que se comprueba
	 * @return
	 * 'true' si la fecha dada tiene una antig&uuml;edad superior a
	 * la mayoia de edad predefinida <br>
	 * 'true' si la fecha dada tiene una antig&uuml;edad inferior a
	 * la mayoia de edad predefinida
	 */
	private boolean validateFullAge(Date birthDate) {
		Date eighteenYearsAgo = new Date();
		eighteenYearsAgo.setTime( (new Date()).getTime()-567648000000l );
		if( ! birthDate.before(eighteenYearsAgo) ){
			return false;
		}
		return true;
	}
}
