package com.loqua.presentation.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.presentation.bean.BeanSettingsSession;

@FacesValidator("validatorPassword")
public class ValidatorPassword implements Validator {

	// Se almacena el valor enviado en una variable,
	// de forma que asi puede ser leido desde el 'validatorPasswordConfirmation'
	private static String password;
	
	/* La contrase�a debe contener 2 de las siguinetes 4 condiciones:
	mayusculas, minusculas, digitos, y los siguientes caracteres especiales:
	'-', '_', '[', ']', '(', ')', '{', '}', '<', '>'.
	No se admiten otros caracteres */
	private static final String PASSWORD_PATTERN = 
			"("
			+ "((?=.*[A-Z])(?=.*[a-z]))"
			+ "|((?=.*[A-Z])(?=.*\\d))"
			+ "|((?=.*[A-Z])(?=.*[_\\(\\)\\[\\]<>\\{\\}@-]))"
			+ "|((?=.*[a-z])(?=.*\\d))"
			+ "|((?=.*[a-z])(?=.*[_\\(\\)\\[\\]<>\\{\\}@-]))"
			+ "|((?=.*\\d)(?=.*[_\\(\\)\\[\\]<>\\{\\}@-]))"
			+ ")"
			+ "([A-Za-z\\d_\\(\\)\\[\\]<>\\{\\}@-])*";

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(inputLabel
					+ ": " + BeanSettingsSession.getTranslationStatic("errorPasswordRequired")));
		}
		password = arg2.toString();
		
		// Comprobar que no este vacio
		if (password == null || password.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel
					+ ": " + BeanSettingsSession.getTranslationStatic("errorPasswordRequired")));
		}
		
		//Comprobar longitud email:
		if( ! validateLength() ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic("errorPasswordLength")));
		}
		// Comprobar formato de email:
		if( ! validateRegex() ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic("errorPasswordExpression")));
		}
	}

	private boolean validateLength() {
		//Comprobar longitud email:
		if( password.length()<8 || password.length()>15 ){
			return false;
		}
		return true;
	}
	
	private boolean validateRegex() {
		// Comprobar formato de email:
		if( ! password.matches(PASSWORD_PATTERN) ){
			return false;
		}
		return true;
	}
	
	public static String getPassword(){
		return password;
	}
}
