package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceUserAccessDataChange;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceUserAccessDataChange}
 * @author Gonzalo
 */
@Local
public interface LocalServiceUserAccessDataChange 
	extends ServiceUserAccessDataChange{
}
