package com.loqua.persistence.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Permite acceder a ficheros de consultas escritas en el lenguaje nativo
 * de la base de datos, en lugar de JPQL
 * @author Gonzalo
 *
 */
public class NotJPA {
	
	public final static String CONFIG_FILE_NAME =
			"/META-INF/mysql.properties";
	Properties properties = null;
	private static NotJPA instance = new NotJPA();
	
	
	private NotJPA() {
		this.properties = new Properties();
		try {
			properties.load( NotJPA.class.getClassLoader()
					.getResourceAsStream(CONFIG_FILE_NAME) );
		} catch (IOException ex) {}
	}
	
	/**
    * @return instancia unica de la propia clase, patron Singleton
    */
	public static NotJPA getInstance() {
		return instance;
	}
	
	/**
	* @param key: nombre de la sentencia SQL solicitada
	* @return retorna la sentencia MySQL, solicitada al fichero
	*/
	public String getString(String key) {
		return this.properties.getProperty(key);
	}
}