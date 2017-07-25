package com.loqua.business.services.impl;

import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.services.impl.transactionScript.TransactionCredentials;
import com.loqua.business.services.serviceLocal.LocalServiceCredentials;
import com.loqua.business.services.serviceRemote.RemoteServiceCredentials;

@Stateless
@WebService(name="ServiceCredentials")
public class EjbServiceCredentials
		implements LocalServiceCredentials, RemoteServiceCredentials {

	private static final TransactionCredentials tsCredentials = 
			new TransactionCredentials();
	
	@Override
	public Map<String, String[]> getAllCredentials() {
		return tsCredentials.getAllCredentials();
	}
}
