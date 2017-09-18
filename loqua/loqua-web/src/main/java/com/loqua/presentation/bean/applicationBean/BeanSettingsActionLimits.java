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
 * relativas a limitar la cantidad de acciones que pueden llevarse a cabo
 * por los usuarios del sitio web.
 * @author Gonzalo
 */
public class BeanSettingsActionLimits implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/**
	 * Mapa de pares clave-valor &lt;String, Integer&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'numActionsAtPeriod.properties'.
	 */
	private static Map<String, Integer> mapActionLimitsProperties;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el atributo
	 * {@link #mapActionLimitsProperties} */
	public BeanSettingsActionLimits() {
		loadMapActionLimitsProperties();
	}

	/** Destructor del bean */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Llena la lista de propiedades utilizadas en las comprobaciones
	 * de la cantidad de acciones realizadas por los usuarios,
	 * a partir de los datos del fichero 'numActionsAtPeriod.properties'.
	 */
	private void loadMapActionLimitsProperties() {
		mapActionLimitsProperties = new HashMap<String, Integer>();
		String FILE_LOCATION = "/numActionsAtPeriod.properties";
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
					mapActionLimitsProperties.put(key, value);
				}catch (NumberFormatException e) {
					// No introduce esta propiedad en el Map y continua el bucle
					log.error("NumberFormatException at"
							+ "'loadMapActionLimitsProperties()'");
				}
			}
		} catch( Exception e) {
			//throw new RuntimeException("Properties file cannot be loaded", e);
			String msg = "Properties file cannot be loaded";
			log.error("Unexpected Exception at"
					+ "'loadMapActionLimitsProperties()': " + msg);
		}
	}
	
	/**
	 * Comprueba que la propiedad indicada del fichero
	 * 'numActionsAtPeriod.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto: 0.
	 * @param action nombre de la propiedad que describe una accion que,
	 * en el fichero 'numActionsAtPeriod.properties',
	 * indica el limite de veces que puede ser realizada
	 * @return
	 * numero maximo de noticias mostradas en cada pagina del foro.
	 */
	public int getLimitTimesForAction(String action) {
		Integer result = 0;
		if( mapActionLimitsProperties != null
				&& ! mapActionLimitsProperties.isEmpty() 
				&& mapActionLimitsProperties.get(action)!=null ){
			result = mapActionLimitsProperties.get(action);
		}
		return result;
	}
	
	/**
	 * Obtiene el Map de propiedades utilizadas en las comprobaciones
	 * de la cantidad de acciones realizadas por los usuarios
	 * @return
	 * Mapa de pares clave-valor &lt;String, Integer&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'numActionsAtPeriod.properties'.
	 */
	public Map<String, Integer> getMapActionLimitsProperties(){
		return Collections.unmodifiableMap(mapActionLimitsProperties);
	}
}