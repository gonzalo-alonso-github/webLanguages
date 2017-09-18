package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceUser;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceUser}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceUser extends ServiceUser{
	
}
