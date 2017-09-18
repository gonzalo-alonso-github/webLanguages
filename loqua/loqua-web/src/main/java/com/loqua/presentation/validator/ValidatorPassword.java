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
 * UIComponent sobre el que se aplica corresponde al formato de
 * contrase&ntilde;a definido en esta misma clase
 * @author Gonzalo
 */
@FacesValidator("validatorPassword")
public class ValidatorPassword implements Validator {
	
	/** Valor de la contrase&ntilde;a indicada en el UIComponent.
	 * Es util para que este valor sea accesible
	 * desde el {@link ValidatorPasswordConfirmation}
	 */
	private static String password;
	
	/** La contrase&ntilde;a debe contener 2 de las siguinetes 4 condiciones:
	mayusculas, minusculas, digitos, y los siguientes caracteres especiales
	(no se admiten otros caracteres, distintos a los siguientes,
	que no sean digitos o letras del alfabeto ingl&eacute;s):
	<ul>
	<li>Guion (Hyphen or Dash): '-'</li>
	<li>Guion bajo (Underscore): '_'</li>
	<li>Parentesis (Brackets): '(' y ')'</li>
	<li>Corchetes (Square Brackets): '[' y ']'</li>
	<li>Llaves (Curly Brackets) '{' y '}'</li>
	<li>Signos Menor y Mayor (Less and Greater): '<' y '>'</li>
	</ul> */
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

	/**
	 * Comprueba si la contrase&ntilde;a indicada en el UIComponent
	 * tiene una longitud aceptable.
	 * @return
	 * 'true' si la longitud de la cadena recibida esta entre
	 * los 8 y los 15 caracteres (ambos incluidos como valores validos) <br/>
	 * 'false' si la longitud de la cadena recibida
	 * no alcanza los 8 caracteres, o sobrepasa los 15 caracteres
	 */
	private boolean validateLength() {
		if( password.length()<8 || password.length()>15 ){
			return false;
		}
		return true;
	}
	
	/**
	 * Comprueba si la contrase&ntilde;a indicada en el UIComponent
	 * cumple el formato indicado en el atributo {@link #PASSWORD_PATTERN}
	 * @return
	 * 'true' si la contrase&ntilde;a cumple el formato indicado <br/>
	 * 'false' si la contrase&ntilde;a no cumple el formato indicado
	 */
	private boolean validateRegex() {
		// Comprobar formato de email:
		if( ! password.matches(PASSWORD_PATTERN) ){
			return false;
		}
		return true;
	}
	
	/**
	 * Devuelve la contrase&ntilde;a indicada en el UIComponent
	 * (sera cadena vacia si no ha sido inicializada).
	 */
	public static String getPassword(){
		return password;
	}
}
