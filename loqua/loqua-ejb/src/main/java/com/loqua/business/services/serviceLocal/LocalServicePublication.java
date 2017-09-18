package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServicePublication;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServicePublication}
 * @author Gonzalo
 */
@Local
public interface LocalServicePublication extends ServicePublication{
	
}
