package com.loqua.presentation.bean.applicationBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.loqua.business.services.impl.ManagementBlobs;
import com.loqua.model.Country;
import com.loqua.model.User;
import com.loqua.presentation.bean.BeanUserView;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Maneja la imagen de perfil de usuario, y tambien los iconos de pais de origen
 * y de ubicacion del usuario, permitiendo leer dichas imagenes
 * desde los componentes Omnifaces de la vista.
 * El componente graphicImage de Omnifaces solo puede acceder a beans
 * que tengan la anotacion @ApplicationScoped
 * @author Gonzalo
 */
@Named
@ApplicationScoped
public class BeanUserImages implements Serializable {
	
	/**
	 * Numero de version de la clase serializable.
	 * @see Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/**
	 * Construccion del bean
	 */
	public BeanUserImages() {}
	
	/**
	 * Destruccion del bean
	 */
	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // // // // // //
	// METODOS PARA OBTENER LA IMAGEN DE PERFIL DE USUARIO
	// // // // // // // // // // // // // // // // // // //
	
	public byte[] getLoggedUserImage() {
    	// si hicieramos User loggedUser = beanLogin.getLoggedUser();
    	// obtendriamos igualmente el usuario 'logueado', pero necesitariamos
    	// inyectar en esta clase el beanLogin, lo cual no debe hacerse
    	// porque este es un bean de Application (por requisito de OmniFaces).
    	// De hacer eso el beanLogin persistiria durante todo el ambito de aplicacion
    	Map<String, Object> session = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		User loggedUser = BeanUtils.getLoggedUser();
		if( loggedUser.getUserInfoPrivacity().getImage()==null ){
			byte[] loggedUserImage = getUserImage(loggedUser);
			// Tambien guarda la imagen en la entidad User mapeada en memoria:
			loggedUser.getUserInfoPrivacity().setImage(loggedUserImage);
			// Y actualiza el User editado en sesion:
			session.put("LOGGED_USER",loggedUser);
		}
    	return loggedUser.getUserInfoPrivacity().getImage();
    }
	
    public byte[] getUserImage(User user) {
    	// Este metodo es publico porque tambien se invoca desde los .xhtml
    	byte[] userImage = avoidGettingImageFromDB(user);
    	if( userImage!=null ){ return userImage; }
		try{
			String containerName = "data-user-image";
			List<String> extensions=BeanSettingsUser.getProfileImageExtensions();
			String imageCode = getUserImageCode(user.getId());
			userImage = ManagementBlobs.getImageFromAzureStorage(
					imageCode, containerName, extensions);
			if( userImage==null ){
				return getDefaultUserImage(user);
			}
			return userImage;
		}catch( Exception e ){
			new BeanUserImages().log.error("Unexpected Exception at "
					+ "'getUserImage()' ");
			return getDefaultUserImage(user);
		}
	}

	private byte[] avoidGettingImageFromDB(User user) {
		if( user==null ) return getDefaultUserImage(user);
    	/*
    	// Si la entidad User mapeada en memoria ya tiene guardada la imagen,
    	// devuelve esta en lugar de hacer un acceso a base de datos...
    	// Lo dejo comentado, porque el mapeo ORM no es del todo fiable.
    	// Si se descomenta, al cambiar de avatar no se actualizan bien
    	// los avatares de la lista de publicaciones
		if( user.getUserInfoPrivacity().getImage()!=null ){
    		return user.getUserInfoPrivacity().getImage();
    	}
    	*/
		// Si, por privacidad, la imagen no es visible al visitante,
		// se devuelve la imagen por defecto:
    	User loggedUser = BeanUtils.getLoggedUser();
    	if( BeanUserView.shouldBeShownByPrivacityStatic(loggedUser, user,
    			user.getPrivacityData().getImage()) == false ){
    		return getDefaultUserImage(user);
    	}
    	return null;
	}
    
    private static String getUserImageCode(Long userID) {
		String result = "";
		int numZerosToAdd = 7 - userID.toString().length();
		for(int i=1; i<=numZerosToAdd; i++){
			result+="0";
		}
		result += userID.toString();
		return result;
	}

	private byte[] getDefaultUserImage(User user) {
		if( user.getUserInfoPrivacity().getGender()==true ){
			return getDefaultWomanImage();
		}else{
			return getDefaultManImage();
		}
	}
	
	private byte[] getDefaultWomanImage() {
		String fileLocation = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath("/")
				+ "/resources/img/user_woman.jpg";
		return getDefaultImage(fileLocation);
	}
	
	private byte[] getDefaultManImage() {
		String fileLocation = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath("/")
				+ "/resources/img/user_man.jpg";
		return getDefaultImage(fileLocation);
	}
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA ACTUALIZAR LA IMAGEN DE PERFIL DE USUARIO
	// // // // // // // // // // // // // // // // // // // //
	
	public static byte[] updateUserImage(Long userID, Part imagePart) {
		byte[] imageBytes = null;
		try{
			String imageCode = getUserImageCode(userID);
			// extension: con 'substring(6)' se elimina el prefijo 'image/'
			String extension = imagePart.getContentType().substring(6);
			imageCode += "." + extension;
			imageBytes = convertImagePartToBytes(imagePart);
			String containerName = "data-user-image";
			ManagementBlobs.updateImageToAzureStorage(
					imageCode, imageBytes, containerName);
		}catch( Exception e ){
			new BeanUserImages().log.error("Unexpected Exception at "
					+ "'updateUserImage()' ");
		}
		return imageBytes;
    }
	
	// // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA OBTENER LOS ICONOS DE PAIS DE ORIGEN Y UBICACION
	// // // // // // // // // // // // // // // // // // // // // //
	
	public byte[] getLoggedUserOriginImage() {
		return getUserOriginImage( BeanUtils.getLoggedUser() );
    }
	
	public byte[] getLoggedUserLocationImage() {
		return getUserLocationImage( BeanUtils.getLoggedUser() );
    }
	
	public byte[] getUserOriginImage(User user) {
		// Este metodo se usa en el snippet "profile_header_userData.xhtml"
		// y en las listas de contactos
		Country country = user.getUserInfoPrivacity().getCountryOrigin();
		byte[] originImage = getCountryImage(country); 
		if( originImage==null ){
			return getDefaultFlag();
		}
    	return originImage;
	}
	
	public byte[] getUserLocationImage(User user) {
		// Este metodo se usa en el snippet "profile_header_userData.xhtml"
		// y en las listas de contactos
		Country country = user.getUserInfoPrivacity().getCountryLocation();
		byte[] locationImage = getCountryImage(country); 
		if( locationImage==null ){
			return getDefaultFlag();
		}
    	return locationImage;
	}
	
	private byte[] getCountryImage(Country country) {
		if( country==null ) return null;
		try{
			String containerName = "data-country-flagicon";
			List<String> extensions=BeanSettingsUser.getProfileImageExtensions();
			return ManagementBlobs.getImageFromAzureStorage(
					country.getCodeIso3166(), containerName, "gif", extensions);
		}catch( Exception e ){
			new BeanUserImages().log.error("Unexpected Exception at "
					+ "'getCountryImage()' ");
			return null;
		}
	}
	
	private byte[] getDefaultFlag() {
		String fileLocation = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath("/")
				+ "/resources/img/unknown_flag.gif";
		return getDefaultImage(fileLocation);
	}
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA OBTENER LOS ICONOS DEL GENERO
	// // // // // // // // // // // // // // // //
	
	public byte[] getLoggedUserGenderImage() {
		return getUserGenderImage( BeanUtils.getLoggedUser() );
    }
	
	public byte[] getUserGenderImage(User user) {
		// Este metodo se usa en el snippet "profile_hader_userData.xhtml"
		boolean gender = user.getUserInfoPrivacity().getGender();
		String fileLocation = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath("/")
				+ "/resources/img/gender_male.png";
		if( gender==true ){
			fileLocation = FacesContext.getCurrentInstance()
					.getExternalContext().getRealPath("/")
					+ "/resources/img/gender_female.png";
		}
		return getDefaultImage(fileLocation);
	}
	
	// // // // // //
	// OTROS METODOS
	// // // // // //
	
	private byte[] getDefaultImage(String fileLocation) {
		byte[] result = null;
		try{
			Path path = Paths.get(fileLocation);
			result = Files.readAllBytes(path);
		}catch( Exception e ){
			new BeanUserImages().log.error("Unexpected Exception at "
					+ "'getDefaultImage()' ");
		}
		return result;
	}
	
	public static byte[] convertImagePartToBytes(Part imagePart) {
		InputStream partInputStream = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try{
			if( imagePart == null ){
				return null;
			}
			partInputStream=imagePart.getInputStream();
			byte[] chunk=new byte[4096];
			int amountRead;
			while( (amountRead=partInputStream.read(chunk)) != -1 ){
				outputStream.write(chunk,0,amountRead);
			}
			if( outputStream.size() != 0 ){
				return outputStream.toByteArray();
			}
		}catch( Exception e ){
			new BeanUserImages().log.error("Unexpected Exception at "
					+ "'convertImagePartToBytes()' ");
		}finally{
			try {
				if( partInputStream!=null ){
					partInputStream.close();
				}
			} catch( IOException e ){}
		}
		return null;
	}
}