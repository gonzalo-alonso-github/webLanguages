package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceFeed;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceFeed}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceFeed extends ServiceFeed{
	
}
