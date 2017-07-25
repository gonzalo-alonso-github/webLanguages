package com.loqua.business.services.impl.transactionScript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loqua.model.Credentials;
import com.loqua.persistence.CredentialsJPA;

public class TransactionCredentials {
	
	private static final CredentialsJPA credentialsJPA = new CredentialsJPA();
	
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
