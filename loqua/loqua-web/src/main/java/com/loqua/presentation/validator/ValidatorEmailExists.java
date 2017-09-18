package com.loqua.presentation.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.bean.BeanSettingsSession;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Define un validador encargado de comprobar si el valor del componente
 * UIComponent sobre el que se aplica corresponde a un formato de email,
 * y que existe algun usuario no eliminado con dicho email.
 * @author Gonzalo
 */
@FacesValidator("validatorEmailExists")
public class ValidatorEmailExists implements Validator {
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Usuario cuyo email coincide con la direccion de email
	 * indicada en el UIComponent. Es util para que este valor sea accesible
	 * desde los beans (ej. 'beanRestorePassword')
	 */
	private static User userExists = null;
	
	/** Formato de email al que debe ajustarse el valor del UIComponent
	 * para que sea validado */
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic("errorEmailRequired")));
		}
		String email = arg2.toString();
		
		// Comprobar que no este vacio
		if (email == null || email.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel
					+ ": " + BeanSettingsSession.getTranslationStatic(
							"errorEmailRequired")));
		}
		
		//Comprobar longitud email:
		if( ! validateLength(email) ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic(
							"errorEmailLength")));
		}
		// Comprobar formato de email:
		if( ! validateRegex(email) ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic(
							"errorEmailExpression")));
		}
		// Comprobar que el email si pertenece a un usuario no eliminado:
		if( ! verifyEmailExistsForNotRemovedUser(email) ){
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic(
							"errorNotFoundEmail")));
		}
	}

	/**
	 * Comprueba si una direccion dada de email supera el limite de longitud
	 * de unos 40 caracteres.
	 * @param email direccion de email que se comprueba
	 * @return
	 * 'true' si la longitud de la cadena recibida no supera
	 * los 40 caracteres <br/>
	 * 'false' si la longitud de la cadena recibida sobrepasa
	 * los 40 caracteres
	 */
	private boolean validateLength(String email) {
		if( email.length()>40 ){
			return false;
		}
		return true;
	}
	
	/**
	 * Comprueba si una direccion dada de email cumple el formato indicado
	 * en el atributo {@link #EMAIL_PATTERN}.
	 * @param email direccion de email que se comprueba
	 * @return
	 * 'true' si la cadena recibida cumple el formato de email <br/>
	 * 'false' si la cadena recibida no cumple el formato de email
	 */
	private boolean validateRegex(String email) {
		if( ! email.matches(EMAIL_PATTERN) ){
			return false;
		}
		return true;
	}
	
	/**
	 * Comprueba si existe algun usuario no eliminado cuyo email coincide
	 * con la direccion dada de email.
	 * @param email direccion de email que se comprueba
	 * @return
	 * 'true' si el email ya esta siendo utilizado por otro usuario <br/>
	 * 'false' si el email no esta siendo utilizado por ningun usuario
	 */
	private boolean verifyEmailExistsForNotRemovedUser(String email) {
		// Comprobar que el email esta siendo usado
		// por algun usuario no eliminado:
		try{
			userExists = Factories.getService().getServiceUser()
					.getUserNotRemovedByEmail(email);
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'verifyEmailExistsForNotRemovedUser()'");
			throw new ValidatorException(new FacesMessage(
					BeanSettingsSession.getTranslationStatic(
							"errorUnexpected")));
		}
		if( userExists==null ){
			return false;
		}
		return true;
	}
	
	/**
	 * Devuelve el usuario cuyo email coincide con la direccion de email
	 * indicada en el UIComponent (sera null si no se encuentra dicho usuario).
	 */
	public static User getUser(){
		return userExists;
	}
	
}
