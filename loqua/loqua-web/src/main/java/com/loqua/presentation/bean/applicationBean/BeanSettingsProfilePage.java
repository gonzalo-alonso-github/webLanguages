package com.loqua.presentation.bean.applicationBean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PreDestroy;

import com.loqua.presentation.logging.LoquaLogger;

/**
 * Administra la configuracion de propiedades de la aplicacion,
 * que son iguales para todas las sesiones de usuario,
 * relativas al uso y visualizacion de las paginas de perfiles de usuario.
 * @author Gonzalo
 */
public class BeanSettingsProfilePage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Numero maximo de publicaciones mostradas en cada pagina del perfil */
	private static final int DEFAULT_NUM_PUBS_PER_PAGE = 15;
	
	/** Numero maximo de notificaciones mostradas */
	private static final int DEFAULT_NUM_NOTIFICATIONS = 30;
	
	/** Mapa de pares clave-valor &lt;String, Integer&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'profile.properties'
	 */
	private Map<String, Integer> mapProfileProperties;

	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el atributo
	 * {@link #mapProfileProperties} */
	public BeanSettingsProfilePage() {
		loadMapProfileProperties();
	}

	/** Destructor del bean */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Llena la lista de propiedades utilizadas en las vistas relativas
	 * a los perfiles de usuario,
	 * a partir de los datos del fichero 'profile.properties'.
	 */
	private void loadMapProfileProperties() {
		mapProfileProperties = new HashMap<String, Integer>();
		String FILE_LOCATION = "/profile.properties";
		Properties properties = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream(FILE_LOCATION);
			properties.load(in);
			in.close();
			Set<Object> keys = properties.keySet();

			for (Object k : keys) {
				String key = (String) k;
				Integer value = 0;
				try{
					value = Integer.parseInt(properties.getProperty(key));
					mapProfileProperties.put(key, value);
				}catch (NumberFormatException e) {
					// No introduce esta propiedad en el Map y continua el bucle
					log.error("NumberFormatException at "
							+ "'loadMapProfileProperties()'");
				}
			}
		} catch( Exception e) {
			//throw new RuntimeException("Propeties file can not be loaded", e);
			String msg = "Properties file cannot be loaded";
			log.error("Unexpected Exception at"
					+ "'loadMapProfileProperties()': " + msg);
		}
	}
	
	/**
	 * Comprueba que la propiedad 'numberOfPublicationsPerPage'
	 * del fichero 'profile.properties' esta inicializada
	 * con un valor aceptable, y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto:
	 * {@link #DEFAULT_NUM_PUBS_PER_PAGE}.
	 * @return
	 * numero maximo de publicaciones que se cargan de cada vez
	 * en las paginas del perfil.
	 */
	public int getNumPublicationsPerPage() {
		Integer result = DEFAULT_NUM_PUBS_PER_PAGE;
		if( mapProfileProperties != null
				&& ! mapProfileProperties.isEmpty() 
				&& mapProfileProperties.get("numberOfPublicationsPerPage")!=null){
			result = mapProfileProperties.get("numberOfPublicationsPerPage");
			if( result<5 || result>40 ) result = DEFAULT_NUM_PUBS_PER_PAGE;
		}
		return result;
	}
	
	/**
	 * Comprueba que la propiedad 'numberOfNotifications'
	 * del fichero 'profile.properties' esta inicializada
	 * con un valor aceptable, y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto:
	 * {@link #DEFAULT_NUM_NOTIFICATIONS}.
	 * @return
	 * numero maximo de notificaciones mostradas.
	 */
	public int getNumNotifications() {
		Integer result = DEFAULT_NUM_PUBS_PER_PAGE;
		if( mapProfileProperties != null
				&& ! mapProfileProperties.isEmpty() 
				&& mapProfileProperties.get("numberOfNotifications")!=null){
			result = mapProfileProperties.get("numberOfNotifications");
			if( result<5 || result>100 ) result = DEFAULT_NUM_NOTIFICATIONS;
		}
		return result;
	}
	
	/**
	 * Obtiene el Map de propiedades utilizadas en las vistas relativas
	 * a los perfiles de usuario.
	 * @return
	 * Mapa de pares clave-valor &lt;String, Integer&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'profile.properties'.
	 */
	public Map<String, Integer> getMapProfileProperties(){
		return Collections.unmodifiableMap(mapProfileProperties);
	}
}