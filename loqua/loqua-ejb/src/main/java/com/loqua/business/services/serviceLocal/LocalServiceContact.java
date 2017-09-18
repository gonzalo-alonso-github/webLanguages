package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceContact;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceContact}
 * @author Gonzalo
 */
@Local
public interface LocalServiceContact extends ServiceContact{
	
}
