package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceCredentials;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceCredentials}
 * @author Gonzalo
 */
@Local
public interface LocalServiceCredentials extends ServiceCredentials{
	
}
