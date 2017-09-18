package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceForumThread;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceForumThread}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceThread extends ServiceForumThread{
	
}
