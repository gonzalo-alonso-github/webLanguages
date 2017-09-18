package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceUserAccessDataChange;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceUserAccessDataChange}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceUserAccessDataChange 
	extends ServiceUserAccessDataChange{	
}
