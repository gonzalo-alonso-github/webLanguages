package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceCorrection;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceCorrection}
 * @author Gonzalo
 */
@Local
public interface LocalServiceCorrection extends ServiceCorrection{
	
}
