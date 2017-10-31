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
 * UIComponent sobre el que se aplica tiene una longitud de caracteres que
 * no sobrepasa un limite dado. <br> Este validador verifica que dicho valor
 * no sea null, pero no comprueba si dicho valor no esta vacio;
 * de ello se encargaria la propiedad 'required' del UIComponent.
 * @author Gonzalo
 */
@FacesValidator("validatorLength")
public class ValidatorLength implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if( arg2 == null ){
			return;
		}
		String value = arg2.toString();
		String maxLength = (String) arg1.getAttributes().get("inputMaxLength");
		//Comprobar longitud:
		if( value.length()>Integer.parseInt(maxLength) ){
			throw new ValidatorException(new FacesMessage(inputLabel + ": " +
					BeanSettingsSession.getTranslationStatic("errorLength") +
					" (" + maxLength + ")"));
		}
	}
}
