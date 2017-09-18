package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServicePublication;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServicePublication}
 * @author Gonzalo
 */
@Remote
public interface RemoteServicePublication extends ServicePublication{
	
}
