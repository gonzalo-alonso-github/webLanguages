package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import com.loqua.model.Credentials;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class CredentialsJPA {
	
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
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
