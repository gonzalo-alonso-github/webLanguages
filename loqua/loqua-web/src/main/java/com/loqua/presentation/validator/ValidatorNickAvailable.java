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
 * UIComponent sobre el que se aplica
 * no coincide con el pseudonimo (o 'nick') de ningun usuario no eliminado.
 * @author Gonzalo
 */
@FacesValidator("validatorNickAvailable")
public class ValidatorNickAvailable implements Validator {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String inputLabel = (String) arg1.getAttributes().get("inputLabel");
		if (arg2 == null) {
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorNickRequired")));
		}
		String nick = arg2.toString();
		
		// Comprobar que no este vacio
		if (nick == null || nick.isEmpty()) {
			throw new ValidatorException(new FacesMessage(inputLabel + ": "
					+ BeanSettingsSession.getTranslationStatic(
					"errorNickRequired")));
		}
		
		//Comprobar longitud nick:
		if( ! validateLength(nick) ){
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorNickLength")));
		}
		
		// Comprobar que el nick no esta usado por usuarios no eliminados:
		if( ! verifyNotNickExistsForNotRemovedUser(nick) ){
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorAlreadyFoundNick")));
		}
	}
	
	/**
	 * Comprueba si el pseudonimo (o 'nick') indicado supera el
	 * limite de longitud de unos 20 caracteres.
	 * @param nick pseudonimo (o 'nick') que se comprueba
	 * @return
	 * 'true' si la longitud de la cadena recibida no supera
	 * los 20 caracteres <br>
	 * 'false' si la longitud de la cadena recibida sobrepasa
	 * los 20 caracteres
	 */
	private boolean validateLength(String nick) {
		if( nick.length()>20 ){
			return false;
		}
		return true;
	}
	
	/**
	 * Comprueba si el pseudonimo (o 'nick') indicado esta disponible,
	 * o por el contrario esta siendo utilizado por algun usuario no eliminado
	 * @param nick pseudonimo (o 'nick') que se comprueba
	 * @return
	 * 'true' si el pseudonimo no esta siendo utilizado por ningun usuario<br>
	 * 'false' si el pseudonimo ya esta siendo utilizado por otro usuario
	 */
	private boolean verifyNotNickExistsForNotRemovedUser(String nick) {
		// Comprobar que el email no esta siendo usado
		// por algun usuario no eliminado:
		User user = null;
		try{
			user = Factories.getService().getServiceUser()
					.getUserNotRemovedByNick(nick);
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'verifyNotNickExistsForNotRemovedUser()'");
			throw new ValidatorException(new FacesMessage(BeanSettingsSession
					.getTranslationStatic("errorUnexpected")));
		}
		if( user==null ){
			return true;
		}
		return false;
	}
}
