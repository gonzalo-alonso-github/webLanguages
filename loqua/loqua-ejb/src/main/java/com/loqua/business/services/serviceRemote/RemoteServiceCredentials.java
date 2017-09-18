package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceCredentials;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceCredentials}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceCredentials extends ServiceCredentials{
	
}
