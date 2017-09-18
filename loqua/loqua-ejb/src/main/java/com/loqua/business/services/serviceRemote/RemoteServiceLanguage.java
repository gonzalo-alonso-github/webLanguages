package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceLanguage;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceLanguage}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceLanguage extends ServiceLanguage{
	
}
