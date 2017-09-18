package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceLanguage;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceLanguage}
 * @author Gonzalo
 */
@Local
public interface LocalServiceLanguage extends ServiceLanguage{
	
}
