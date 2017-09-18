package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceMessage;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceMessage}
 * @author Gonzalo
 */
@Local
public interface LocalServiceMessage extends ServiceMessage{
	
}
