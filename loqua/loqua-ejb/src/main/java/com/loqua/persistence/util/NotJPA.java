package com.loqua.persistence.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Clase de utilidad que permite acceder las consultas escritas en
 * el lenguaje nativo de la base de datos (MySQL)
 * @author Gonzalo
 *
 */
public class NotJPA {
	
	/** Indica el directorio del fichero 'mysql.properties' */
	public final static String CONFIG_FILE_NAME = "/META-INF/mysql.properties";
	/** Almacena las consultas MySQL del fichero 'mysql.properties' */
	Properties properties = null;
	/** Instancia de la clase, para implementar el patron Singleton */
	private static NotJPA instance = new NotJPA();
	
	/**
	 * Constructor por defecto. Inicializa la variable 'properties'
	 * que almacena las consultas MySQL del fichero 'mysql.properties'
	 */
	private NotJPA() {
		this.properties = new Properties();
		try {
			properties.load( NotJPA.class.getClassLoader()
					.getResourceAsStream(CONFIG_FILE_NAME) );
		} catch (IOException ex) {}
	}
	
	/**
	 * Implementa el patron Singleton para hallar la misma instancia de esta
	 * clase desde cualquier parte del codigo y desde cualquier hilo de
	 * ejecucion
     * @return unica instancia de la propia clase
     */
	public static NotJPA getInstance() {
		return instance;
	}
	
	/**
	 * Halla en la variable 'properties' la sentencia MySQL dada
	 * @param key: nombre de la sentencia MySQL solicitada
	 * @return retorna la sentencia MySQL solicitada
	 */
	public String getString(String key) {
		return this.properties.getProperty(key);
	}
}