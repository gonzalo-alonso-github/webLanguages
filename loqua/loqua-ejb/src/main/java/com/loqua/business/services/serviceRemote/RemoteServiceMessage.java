package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceMessage;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceMessage}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceMessage extends ServiceMessage{
	
}
