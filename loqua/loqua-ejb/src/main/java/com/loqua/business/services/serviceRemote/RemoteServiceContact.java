package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceContact;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceContact}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceContact extends ServiceContact{
	
}
