package com.loqua.business.services.impl.transactionScript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loqua.model.Credentials;
import com.loqua.persistence.CredentialsJPA;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de la entidad
 * {@link Credentials}. <br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionCredentials {
	
	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a la entidad {@link Credentials} */
	private static final CredentialsJPA credentialsJPA = new CredentialsJPA();
	
	
	/**
	 * Consulta todas las credenciales guardadas
	 * @return Map&lt;String, String[]&gt;, donde la clave es el nombre
	 * del servicio ('generic_email', 'azure_blob', 'translator', 'rest'),
	 * y donde el valor es el array de credenciales correspondiente
	 * (cuyas contrase&ntilde;as permanecen cifradas con hash,
	 * que es tal como estan en la base de datos)
	 */
	public Map<String, String[]> getAllCredentials(){
		Map<String, String[]> result = new HashMap<String, String[]>();
		List<Credentials> credentials = null;
		credentials = credentialsJPA.getAllCredentials();
		if( credentials==null || credentials.isEmpty() ){
			return new HashMap<String, String[]>();
		}
		for( Credentials credential : credentials ){
			String[] values =
				{credential.getCredentialKey1(),credential.getCredentialKey2()};
			result.put(credential.getCredentialType(), values);
		}
		return result;
	}
}
