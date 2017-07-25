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

@FacesValidator("validatorImageProfile")
public class ValidatorImageProfie implements Validator {

	// no es necesario llamar al codigo del constructor del BeanSettingsUser
	// porque en el faces-config dicho bean esta configurado con eager="true"
	
	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		String error = "";
		if (arg2 == null) {
			error = BeanSettingsSession.getTranslationStatic("errorImageRequired");
			throw new ValidatorException(new FacesMessage(error));
		}
		Part imagePart = (Part) arg2;
		// Comprobar peso de la imagen:
		Integer weightLimitKB = BeanSettingsUser.getProfileImageLimitKB();
		if( imagePart.getSize() > weightLimitKB*1024 ){
			error = BeanSettingsSession.getTranslationStatic("errorImageWeigth");
			error = error.replaceFirst("\\?1", weightLimitKB.toString());
			throw new ValidatorException(new FacesMessage(error));
		}
		// Comprobar extension de la imagen:
		if( verifyExtension(imagePart) == false ){
			error = BeanSettingsSession.getTranslationStatic("errorImageExtension");
			error = error.replaceFirst("\\?1",
					BeanSettingsUser.getProfileImageExtensionsAsString());
			throw new ValidatorException(new FacesMessage(error));
		}
		// Comprobar tamano (ancho y alto) de la imagen:
		Integer maxWidth = BeanSettingsUser.getProfileImageMaxWidth();
		Integer maxHeight = BeanSettingsUser.getProfileImageMaxHeight();
		if( verifyWidthHeight(imagePart,maxWidth,maxHeight) == false ){
			error = BeanSettingsSession.getTranslationStatic("errorImageWidthHeight");
			error = error.replaceFirst("\\?1", maxWidth.toString());
			error = error.replaceFirst("\\?2", maxHeight.toString());
			throw new ValidatorException(new FacesMessage(error));
		}
	}

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
	
	private boolean verifyWidthHeight(Part imagePart,int maxWidth,int maxHeight){
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
			// TODO Log
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
