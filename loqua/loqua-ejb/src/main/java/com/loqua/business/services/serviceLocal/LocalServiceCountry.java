package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceCountry;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceCountry}
 * @author Gonzalo
 */
@Local
public interface LocalServiceCountry extends ServiceCountry{
	
}
