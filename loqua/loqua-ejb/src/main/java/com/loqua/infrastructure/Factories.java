package com.loqua.infrastructure;

import com.loqua.business.factory.ServicesFactory;

/**
 * Da acceso al ServicesFactory despues de instanciarlo a partir de la
 * implementacion concreta indicada en el fichero 'factories.properties'
 * @author Gonzalo
 */
public class Factories {
	
	private static String CONFIG_FILE = "/factories.properties";
	
	private static ServicesFactory service = (ServicesFactory)
			FactoriesHelper.createFactory(CONFIG_FILE, "SERVICES_FACTORY");
	
	public static ServicesFactory getService(){
		return service;
	}
}