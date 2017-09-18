package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceCountry;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceCountry}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceCountry extends ServiceCountry{
	
}
