package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceCorrection;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceCorrection}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceCorrection extends ServiceCorrection{
	
}
