package com.loqua.presentation.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.presentation.bean.BeanSettingsSession;

/**
 * Define un validador encargado de comprobar si el valor del componente
 * UIComponent sobre el que se aplica es igual al email introducido previamente
 * en otro UIComponent.
 * @author Gonzalo
 */
@FacesValidator("validatorEmailConfirmation")
public class ValidatorEmailConfirmation implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		HtmlInputText inputEmailToConfirm = (HtmlInputText) arg1.getAttributes()
				.get("emailToConfirm");
		
		if( inputEmailToConfirm == null || 
				inputEmailToConfirm.getValue() == null ){
			throw new ValidatorException(new FacesMessage(inputLabel + ": "
					+ BeanSettingsSession.getTranslationStatic(
							"errorEmailRequired")));
		}
		String emailToConfirm = (inputEmailToConfirm).getValue().toString();
		
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": "
					+ BeanSettingsSession.getTranslationStatic(
							"errorEmailConfirmRequired")));
		}
		String emailConfirmation = arg2.toString();
		
		// Comprobar que no este vacio
		if (emailConfirmation == null || emailConfirmation.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel
					+ ": " + BeanSettingsSession.getTranslationStatic(
							"errorEmailConfirmRequired")));
		}
		
		emailConfirmation = emailConfirmation.replaceAll(" +$", "");
		emailConfirmation = emailConfirmation.replaceAll("^ +", "");
		
		//Comprobar que la confirmacion de email coincide con el original:
		if( ! emailConfirmation.equalsIgnoreCase( emailToConfirm ) ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic(
							"errorConfirmEmail")));
		}
	}
}
