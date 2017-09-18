package com.loqua.business.services;

import java.util.Map;

import com.loqua.model.Credentials;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de la entidad {@link Credentials}
 * @author Gonzalo
 */
public interface ServiceCredentials {
	
	/**
	 * Consulta todas las credenciales guardadas
	 * @return Map&lt;String, String[]&gt;, donde la clave es el nombre
	 * del servicio ('generic_email', 'azure_blob', 'translator', 'rest'),
	 * y donde el valor es el array de credenciales correspondiente
	 * (cuyas contrase&ntilde;as permanecen cifradas con hash,
	 * que es tal como estan en la base de datos)
	 */
	public Map<String, String[]> getAllCredentials();
	
}