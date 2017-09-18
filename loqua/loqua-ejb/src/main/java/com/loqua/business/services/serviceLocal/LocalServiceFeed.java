package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceFeed;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceFeed}
 * @author Gonzalo
 */
@Local
public interface LocalServiceFeed extends ServiceFeed{
	
}
