package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceUser;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceUser}
 * @author Gonzalo
 */
@Local
public interface LocalServiceUser extends ServiceUser{
	
}
