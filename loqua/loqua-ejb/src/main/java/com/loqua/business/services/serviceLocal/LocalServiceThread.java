package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceForumThread;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceForumThread}
 * @author Gonzalo
 */
@Local
public interface LocalServiceThread extends ServiceForumThread{
	
}
