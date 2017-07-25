package com.loqua.presentation.bean.applicationBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PreDestroy;

/**
 * Administra la configuracion de propiedades de la aplicacion,
 * que son iguales para todas las sesiones de usuario,
 * relativas a los idiomas en los que puede visualizarse el sitio web.
 * @author Gonzalo
 */
public class BeanSettingsLocale implements Serializable {
	
	/**
	 * Numero de version de la clase serializable.
	 * @see Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Mapa de pares clave-valor &lt;String, String&gt; donde cada elemento
	 * representa un lenguaje especificado en el fichero 'locales.properties'.
	 */
	private static Map<String, String> mapLanguages;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/**
	 * Construccion del bean.
	 */
	public BeanSettingsLocale() {
		loadMapLanguagesFromProperties();
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
	 * Llena el Map de lenguages de la clase a partir de los datos presentes
	 * en el fichero 'locales.properties'. 
	 * @throws IOException
	 */
	private void loadMapLanguagesFromProperties() {
		mapLanguages = new HashMap<String, String>();
		String FILE_LOCATION = "/locales.properties";
		Properties properties = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream(FILE_LOCATION);
			properties.load(in);
			in.close();
			Set<Object> keys = properties.keySet();

			for (Object k : keys) {
				String key = (String) k;
				String value = properties.getProperty(key);
				if (value == null) {
					// throw new RuntimeException("Property not found");
					// TODO
				}
				mapLanguages.put(key, value);
			}
		} catch( Exception e) {
			//throw new RuntimeException("Propeties file can not be loaded", e);
			// TODO
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/**
	 * Obtiene el Map de propiedades que almacena los idiomas de las vistas
	 * del sitio web
	 * @return
	 * Mapa de pares clave-valor &lt;String, String&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'locales.properties'.
	 */
	public static Map<String, String> getMapLanguages(){
		return Collections.unmodifiableMap(mapLanguages);
	}
}