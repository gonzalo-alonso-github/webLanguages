package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceComment;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceComment}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceComment extends ServiceComment{
	
}
