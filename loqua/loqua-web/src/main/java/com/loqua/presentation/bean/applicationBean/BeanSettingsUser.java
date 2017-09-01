package com.loqua.presentation.bean.applicationBean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PreDestroy;

import com.loqua.model.PrivacityData;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Administra la configuracion de propiedades de la aplicacion,
 * que son iguales para todas las sesiones de usuario,
 * que afectan en general a la interaccion de los mismos con las interfaces
 * @author Gonzalo
 */
public class BeanSettingsUser implements Serializable {
	
	/**
	 * Numero de version de la clase serializable.
	 * @see Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/**
	 * Limite maximo del peso de la imagen de perfil de usuario por defecto
	 */
	private static final int DEFAULT_AVATAR_SIZE_LIMIT_KB = 512;
	
	/**
	 * Extensiones de imagen de perfil permitidas por defecto
	 */
	private static final List<String> DEFAULT_AVATAR_EXTENSIONS = 
			Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/bmp");
	
	/**
	 * Limite maximo del ancho de la imagen de perfil de usuario por defecto
	 */
	private static final int DEFAULT_AVATAR_WIDTH = 500;
	
	/**
	 * Limite maximo de la altura de la imagen de perfil de usuario por defecto
	 */
	private static final int DEFAULT_AVATAR_HEIGHT = 500;
	
	/**
	 * Mapa de pares clave-valor &lt;String, String&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'users.properties'.
	 */
	private static Map<String, String> mapUserProperties;

	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/**
	 * Construccion del bean.
	 */
	public BeanSettingsUser() {
		loadMapUserProperties();
	}

	/**
	 * Destruccion del bean
	 */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Llena la lista de propiedades,
	 * a partir de los datos del fichero 'users.properties'.
	 */
	private void loadMapUserProperties() {
		mapUserProperties = new HashMap<String, String>();
		String FILE_LOCATION = "/users.properties";
		Properties properties = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream(FILE_LOCATION);
			properties.load(in);
			in.close();
			Set<Object> keys = properties.keySet();

			for (Object k : keys) {
				String key = (String) k;
				String value = properties.getProperty(key);
				mapUserProperties.put(key, value);
			}
		} catch( Exception e) {
			//throw new RuntimeException("Propeties file can not be loaded", e);
			String msg = "Properties file cannot be loaded";
			log.info("Unexpected Exception at "
					+ "'loadMapUserProperties()' " + msg);
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/**
	 * Lee de la clase PrivacityData el listado de niveles de privacidad de los
	 * datos de los usuarios, o de las acciones que llevan a cabo.
	 * La lista devuelta puede ser utilizada en las vistas .xhtml para
	 * presentar componentes selectOneMenu con los que editar la privacidad
	 * de algun dato personal, publicacion, o participacion en el foro.
	 * 
	 * @return
	 * lista de los niveles de privacidad definidos en la clase PrivacityData
	 * (PUBLIC, CONTACTS y PRIVATE)
	 */
	public List<String> getListPrivacityLevels() {
		List<String> result = PrivacityData.getPrivacityLevels();
		return result;
	}
	
	/**
	 * Comprueba que la propiedad 'profileImageLimitKB'
	 * del fichero 'users.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto: DEFAULT_AVATAR_SIZE_LIMIT_KB.
	 * @return
	 * limite maximo del peso de la imagen de perfil de usuario
	 */
	public static Integer getProfileImageLimitKB() {
		Integer result = DEFAULT_AVATAR_SIZE_LIMIT_KB;
		if( mapUserProperties != null
				&& ! mapUserProperties.isEmpty() 
				&& mapUserProperties.get("profileImageLimitKB")!=null ){
			try{
			result = Integer.parseInt(mapUserProperties.get("profileImageLimitKB"));
			}catch( NumberFormatException e ){
				new BeanSettingsUser().log.error("NumberFormatException at "
						+ "'getProfileImageLimitKB()' ");
				return DEFAULT_AVATAR_SIZE_LIMIT_KB;
			}
			if( result<512 || result>2048 ) result = DEFAULT_AVATAR_SIZE_LIMIT_KB;
		}
		return result;
	}
	
	/**
	 * Comprueba que la propiedad 'profileImageMaxWidthPx'
	 * del fichero 'users.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto: DEFAULT_AVATAR_WIDTH.
	 * @return
	 * limite maximo de la anchura de la imagen de perfil de usuario
	 */
	public static Integer getProfileImageMaxWidth() {
		Integer result = DEFAULT_AVATAR_WIDTH;
		if( mapUserProperties != null
				&& ! mapUserProperties.isEmpty() 
				&& mapUserProperties.get("profileImageMaxWidthPx")!=null ){
			try{
			result = Integer.parseInt(
					mapUserProperties.get("profileImageMaxWidthPx"));
			}catch( NumberFormatException e ){
				new BeanSettingsUser().log.error("NumberFormatException at "
						+ "'getProfileImageMaxWidth()' ");
				return DEFAULT_AVATAR_WIDTH;
			}
			if( result<1 || result>500 ) result = DEFAULT_AVATAR_WIDTH;
		}
		return result;
	}
	
	/**
	 * Comprueba que la propiedad 'profileImageMaxHeightPx'
	 * del fichero 'users.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto: DEFAULT_AVATAR_WIDTH.
	 * @return
	 * limite maximo de la altura de la imagen de perfil de usuario
	 */
	public static Integer getProfileImageMaxHeight() {
		Integer result = DEFAULT_AVATAR_HEIGHT;
		if( mapUserProperties != null
				&& ! mapUserProperties.isEmpty() 
				&& mapUserProperties.get("profileImageMaxHeightPx")!=null ){
			try{
			result = Integer.parseInt(
					mapUserProperties.get("profileImageMaxHeightPx"));
			}catch( NumberFormatException e ){
				new BeanSettingsUser().log.error("NumberFormatException at "
						+ "'getProfileImageMaxHeight()' ");
				return DEFAULT_AVATAR_HEIGHT;
			}
			if( result<1 || result>500 ) result = DEFAULT_AVATAR_HEIGHT;
		}
		return result;
	}

	/**
	 * Comprueba que la propiedad 'profileImageExtensions'
	 * del fichero 'users.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto: DEFAULT_AVATAR_EXTENSIONS.
	 * @return
	 * extensiones de imagen permitidas por defecto
	 */
	public static List<String> getProfileImageExtensions() {
		List<String> result = DEFAULT_AVATAR_EXTENSIONS;
		if( mapUserProperties != null
				&& ! mapUserProperties.isEmpty() 
				&& mapUserProperties.get("profileImageExtensions")!=null ){
			String extensions = mapUserProperties.get("profileImageExtensions");
			result = Arrays.asList( extensions.split(";") );
			for( String extension : result ){
				if( !extension.equals("image/jpeg")
						&& !extension.equals("image/jpg")
						&& !extension.equals("image/png")
						&& !extension.equals("image/bmp") ){
					return DEFAULT_AVATAR_EXTENSIONS;
				}
			}
		}
		return result;
	}
	
	/**
	 * Convierte la propiedad 'profileImageExtensions'
	 * del fichero 'users.properties' en una cadena de texto mas apta
	 * para ser mostrada en las vistas.
	 */
	public static String getProfileImageExtensionsAsString() {
		String result = "";
		List<String> extensions = getProfileImageExtensions();
		for( String extension : extensions ){
			// Elimina el prefijo 'image/' del valor en el fichero .properties
			// y agrega la cadena al resultado devuelto
			result += extension.substring(6) + ", ";
		}
		// Elimina la ultima coma
		result = result.replaceFirst(", $", "");
		return result;
	}
	
	/**
	 * Obtiene el Map de propiedades utilizadas en las vistas del foro
	 * @return
	 * Mapa de pares clave-valor &lt;String, String&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'user.properties'.
	 */
	public Map<String, String> getMapUserProperties(){
		return Collections.unmodifiableMap(mapUserProperties);
	}
}