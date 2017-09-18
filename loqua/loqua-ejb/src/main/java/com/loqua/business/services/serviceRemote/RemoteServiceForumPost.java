package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceForumPost;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceForumPost}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceForumPost extends ServiceForumPost{
	
}
