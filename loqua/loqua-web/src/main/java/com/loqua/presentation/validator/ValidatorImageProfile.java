package com.loqua.presentation.validator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.imageio.ImageIO;
import javax.servlet.http.Part;

import com.loqua.presentation.bean.BeanSettingsSession;
import com.loqua.presentation.bean.applicationBean.BeanSettingsUser;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Define un validador encargado de comprobar si el valor del componente
 * InputFile de OmniFaces sobre el que se aplica es una imagen que cumple todas
 * las siguientes condiciones:
 * <ul>
 * <li>La imagen no ocupa mas del peso indicado en el fichero
 * 'users.properties'</li>
 * <li>La imagen tiene una extension que coincide con alguna de las indicadas
 * en el fichero 'users.properties'</li>
 * <li>La imagen no supera la anchura ni altura definidas en el fichero
 * 'users.properties'</li>
 * </ul>
 * @author Gonzalo
 */
@FacesValidator("validatorImageProfile")
public class ValidatorImageProfile implements Validator {
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String error = "";
		if (arg2 == null) {
			error = BeanSettingsSession.getTranslationStatic(
					"errorImageRequired");
			throw new ValidatorException(new FacesMessage(error));
		}
		Part imagePart = (Part) arg2;
		// Comprobar peso de la imagen:
		// no es necesario llamar al constructor del BeanSettingsUser
		// porque en faces-config dicho bean tiene la propiedad eager="true"
		Integer weightLimitKB = BeanSettingsUser.getProfileImageLimitKB();
		// Se multiplica por 1024 para pasar de KB a bytes:
		if( imagePart.getSize() > weightLimitKB*1024 ){
			error = BeanSettingsSession.getTranslationStatic(
					"errorImageWeigth");
			error = error.replaceFirst("\\?1", weightLimitKB.toString());
			throw new ValidatorException(new FacesMessage(error));
		}
		// Comprobar extension de la imagen:
		if( verifyExtension(imagePart) == false ){
			error = BeanSettingsSession.getTranslationStatic(
					"errorImageExtension");
			error = error.replaceFirst("\\?1",
					BeanSettingsUser.getProfileImageExtensionsAsString());
			throw new ValidatorException(new FacesMessage(error));
		}
		// Comprobar tamano (ancho y alto) de la imagen:
		Integer maxWidth = BeanSettingsUser.getProfileImageMaxWidth();
		Integer maxHeight = BeanSettingsUser.getProfileImageMaxHeight();
		if( verifyWidthHeight(imagePart,maxWidth,maxHeight) == false ){
			error = BeanSettingsSession.getTranslationStatic(
					"errorImageWidthHeight");
			error = error.replaceFirst("\\?1", maxWidth.toString());
			error = error.replaceFirst("\\?2", maxHeight.toString());
			throw new ValidatorException(new FacesMessage(error));
		}
	}

	/**
	 * Comprueba si la extension de la imagen es aceptada.
	 * @param imagePart objeto Part que contiene la imagen introducida por el
	 * usuario a traves del componente InputFile de OmniFaces
	 * @return
	 * 'true' si la extension coincide con alguna de las indicadas
	 * en el fichero 'users.properties' <br>
	 * 'false' si la extension no coincide con ninguna de las indicadas
	 * en el fichero 'users.properties'
	 */
	private boolean verifyExtension(Part imagePart) {
		boolean validExtension = false;
		List<String> extensions = BeanSettingsUser.getProfileImageExtensions();
		for( String extension: extensions ){
			if( extension.equals(imagePart.getContentType()) ){
				return true;
			}
		}
		return validExtension;
	}
	
	/**
	 * Comprueba si la anchura y la altura de la imagen no superan los
	 * limites predefinidos.
	 * @param imagePart objeto Part que contiene la imagen introducida por el
	 * usuario a traves del componente InputFile de OmniFaces
	 * @param maxWidth limite de anchura, definido en el fichero
	 * 'users.properties'
	 * @param maxHeight limite de altura, definido en el fichero
	 * 'users.properties'
	 * @return
	 * 'true' si la anchura y la altura no superan lo limites definidos
	 * en el fichero 'users.properties' <br>
	 * 'false' si la anchura o la altura sobrepasan lo limites definidos
	 * en el fichero 'users.properties'
	 */
	private boolean verifyWidthHeight(
			Part imagePart,int maxWidth,int maxHeight){
		boolean validSize = false;
		BufferedImage bufferImage = null;
		InputStream inputStreamImage = null;
		try{
			inputStreamImage = imagePart.getInputStream();
			bufferImage = ImageIO.read(inputStreamImage);
			if( bufferImage.getWidth()<=maxWidth
					&& bufferImage.getHeight()<=maxHeight ){
				return true;
			}
		}catch( IOException e ){
			log.error("IOException at 'verifyWidthHeight()'");
		}finally{
			try {
				if( inputStreamImage!=null ){
					inputStreamImage.close();
				}
			} catch( IOException e ){}
		}
		return validSize;
	}
}
