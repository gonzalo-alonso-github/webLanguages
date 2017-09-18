package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceComment;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceComment}
 * @author Gonzalo
 */
@Local
public interface LocalServiceComment extends ServiceComment{
	
}
