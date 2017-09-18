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
 * Maneja la imagen de perfil de usuario, y tambien los iconos de
 * pais de origen y de ubicacion del usuario, permitiendo leer dichas imagenes
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
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. */
	public BeanUserImages() {}
	
	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // // // // // //
	// METODOS PARA OBTENER LA IMAGEN DE PERFIL DE USUARIO
	// // // // // // // // // // // // // // // // // // //
	
	/**
     * Halla la imagen del perfil propia del usuario logueado.
     * @return la imagen hallada
     */
	public byte[] getLoggedUserImage() {
    	// si hicieramos User loggedUser = beanLogin.getLoggedUser();
    	// obtendriamos igualmente el usuario 'logueado', pero necesitariamos
    	// inyectar en esta clase el beanLogin, lo cual no debe hacerse
    	// porque este es un bean de Application (por requisito de OmniFaces).
    	// De hacer eso el beanLogin persistiria
		// durante todo el ambito de aplicacion
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
	
	/**
     * Halla la imagen del perfil del usuario dado.
     * @param user usuario cuya imagen se consulta
     * @return la imagen hallada
     */
    public byte[] getUserImage(User user) {
    	// Este metodo es publico porque tambien se invoca desde los .xhtml
    	
    	byte[] userImage = avoidGettingImageFromDB(user);
    	if( userImage!=null ){ return userImage; }
		try{
			String containerName = "data-user-image";
			List<String> extensions =
					BeanSettingsUser.getProfileImageExtensions();
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

    /**
     * Comprueba si la imagen del perfil del usuario dado,
     * segun su nivel de privacidad, no es visible para el usuario
     * que la consulta. En tal caso no hay que hacer una acceso a base de datos
     * (repositorio remoto de Azure)
     * para hallar la imagen, sino que se devuelve la imagen por defecto.<br/>
     * En caso contrario, no quedaria mas remedio que hallar la imagen adecuada
     * accediendo a la base de datos; entonces de ello se encargara otro metodo
     * y aqui se devuelve null.
     * @param user usuario cuya imagen se consulta
     * @return
     * <ul><li>imagen por defecto, si su nivel de privacidad establecido
     * no permite su visibilidad por parte del usuario que la consulta.
     * </li><li>valor 'null', si su nivel de privacidad establecido si permite
     * su visibilidad por parte del usuario que la consulta,
     * y por tanto hubiera que mostrarla, teniendo entonces que descargarla
     * de la base de datos
     * </li></ul>
     */
	private byte[] avoidGettingImageFromDB(User user) {
		if( user==null ) return getDefaultUserImage(user);
		// Si, por privacidad, la imagen no es visible al visitante,
		// se devuelve la imagen por defecto:
    	User loggedUser = BeanUtils.getLoggedUser();
    	if( BeanUserView.shouldBeShownByPrivacityStatic(loggedUser, user,
    			user.getPrivacityData().getImage()) == false ){
    		return getDefaultUserImage(user);
    	}
    	return null;
	}
    
	/**
	 * Halla el identificador (el nombre) de la imagen de perfil del usuario
	 * dado. Se calcula mediante el atributo 'id' del User indicado.
	 * @param userID usuario de quien se consulta el nombre de imagen
	 * @return el nombre, sin la extension, de la imagen de perfil del usuario
	 */
    private static String getUserImageCode(Long userID) {
		String result = "";
		int numZerosToAdd = 7 - userID.toString().length();
		for(int i=1; i<=numZerosToAdd; i++){
			result+="0";
		}
		result += userID.toString();
		return result;
	}

    /** Obtiene del directorio de la aplicacion (no de la base de datos)
	 * la imagen por defecto adecuada para el usuario dado.
	 * @param user usuario para quien se devueve la imagen
	 * @return la imagen hallada
	 */
	private byte[] getDefaultUserImage(User user) {
		if( user.getUserInfoPrivacity().getGender()==true ){
			return getDefaultWomanImage();
		}else{
			return getDefaultManImage();
		}
	}
	
	/**
	 * Obtiene del directorio de la aplicacion (no del repositorio remoto)
	 * la imagen por defecto de los usuarios de sexo femenino.
	 * @return la imagen hallada
	 */
	private byte[] getDefaultWomanImage() {
		String fileLocation = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath("/")
				+ "/resources/img/user_woman.jpg";
		return getDefaultImage(fileLocation);
	}
	
	/**
	 * Obtiene del directorio de la aplicacion (no del repositorio remoto)
	 * la imagen por defecto de los usuarios de sexo masculino.
	 * @return la imagen hallada
	 */
	private byte[] getDefaultManImage() {
		String fileLocation = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath("/")
				+ "/resources/img/user_man.jpg";
		return getDefaultImage(fileLocation);
	}
	
	// // // // // // // // // // // // // // // // // // // //
	// METODOS PARA ACTUALIZAR LA IMAGEN DE PERFIL DE USUARIO
	// // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Actualiza la imagen de perfil de usuario en el repositorio remoto
	 * de Azure
	 * @param userID atributo 'id' del User cuya imagen se actualiza
	 * @param imagePart imagen que sobreescribe a la imagen actual del usuario
	 * @return
	 * <ul><li>la nueva imagen actualizada (es decir, el parametro 'imagePart'
	 * convertido al tipo byte[]), si no se produce ninguna excepcion
     * </li><li>valor 'null', si se produce cualquier excepcion
     * </li></ul>
	 */
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
	
	/**
	 * Halla el icono de la bandera del pais de origen del usuario logueado.
	 * @return la imagen hallada
	 */
	public byte[] getLoggedUserOriginImage() {
		return getUserOriginImage( BeanUtils.getLoggedUser() );
    }
	
	/**
	 * Halla el icono de la bandera del pais de ubicacion del usuario logueado.
	 * @return la imagen hallada
	 */
	public byte[] getLoggedUserLocationImage() {
		return getUserLocationImage( BeanUtils.getLoggedUser() );
    }
	
	/**
	 * Halla el icono de la bandera del pais de origen del usuario dado.
	 * Si el usuario no tiene establecido un pais de origen, se devuelve
	 * el icono por defecto, que es una bandera blanca.
	 * @param user usuario cuya imagen se consulta
	 * @return la imagen hallada
	 */
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
	
	/**
	 * Halla el icono de la bandera del pais de ubicacion del usuario dado.
	 * Si el usuario no tiene establecido un pais de origen, se devuelve
	 * el icono por defecto, que es una bandera blanca.
	 * @param user usuario cuya imagen se consulta
	 * @return la imagen hallada
	 */
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
	
	/**
	 * Halla el icono de la bandera correspondiente a un pais,
	 * guardado en el repositorio remoto de Azure.
	 * @param country objeto Country que representa al pais cuya bandera se
	 * consulta
	 * @return la imagen hallada
	 */
	private byte[] getCountryImage(Country country) {
		if( country==null ) return null;
		try{
			String containerName = "data-country-flagicon";
			List<String> extensions =
					BeanSettingsUser.getProfileImageExtensions();
			return ManagementBlobs.getImageFromAzureStorage(
					country.getCodeIso3166(), containerName, "gif", extensions);
		}catch( Exception e ){
			new BeanUserImages().log.error("Unexpected Exception at "
					+ "'getCountryImage()' ");
			return null;
		}
	}
	
	/**
	 * Obtiene del directorio de la aplicacion (no del repositorio remoto)
	 * el icono por defecto de un pais, que es una bandera blanca.
	 * @return la imagen hallada
	 */
	private byte[] getDefaultFlag() {
		String fileLocation = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath("/")
				+ "/resources/img/unknown_flag.gif";
		return getDefaultImage(fileLocation);
	}
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA OBTENER LOS ICONOS DEL GENERO
	// // // // // // // // // // // // // // // //
	
	/**
	 * Halla el icono del genero del usuario logueado.
	 * @return la imagen hallada
	 */
	public byte[] getLoggedUserGenderImage() {
		return getUserGenderImage( BeanUtils.getLoggedUser() );
    }
	
	/**
	 * Halla el icono del genero del usuario dado.
	 * @param user usuario cuyo icono se consulta
	 * @return la imagen hallada
	 */
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
	
	/**
	 * Lee un fichero de imagen ubicado en un directorio del sistema
	 * (previsiblemente, en el directorio del proyecto).
	 * @param fileLocation directorio donde esta ubicado el fichero de imagen
	 * @return la imagen hallada
	 */
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
	
	/**
	 * Convierte un fichero de tipo Part a tipo byte[].
	 * @param imagePart fichero que se desea convertir
	 * @return la imagen que se recibe, convertida al tipo byte[]
	 */
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