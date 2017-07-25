package com.loqua.infrastructure;

import com.loqua.business.factory.ServicesFactory;

public class Factories {
	
	private static String CONFIG_FILE = "/factories.properties";
	
	private static ServicesFactory service = (ServicesFactory)
			FactoriesHelper.createFactory(CONFIG_FILE, "SERVICES_FACTORY");
	
	public static ServicesFactory getService(){
		return service;
	}
}