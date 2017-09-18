package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceForumPost;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceForumPost}
 * @author Gonzalo
 */
@Local
public interface LocalServiceForumPost extends ServiceForumPost{
	
}
