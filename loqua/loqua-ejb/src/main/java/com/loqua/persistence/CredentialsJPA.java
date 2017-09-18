package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import com.loqua.model.Credentials;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Credentials}
 * @author Gonzalo
 */
public class CredentialsJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Credentials.getAllCredentials'
	 * @return  lista de todos los Credentials de la base de datos
	 */
	@SuppressWarnings("unchecked")
	public List<Credentials> getAllCredentials(){
		List<Credentials> result = new ArrayList<Credentials>();
		try{
			result = (List<Credentials>) JPA.getManager()
					.createNamedQuery("Credentials.getAllCredentials")
					.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
}
