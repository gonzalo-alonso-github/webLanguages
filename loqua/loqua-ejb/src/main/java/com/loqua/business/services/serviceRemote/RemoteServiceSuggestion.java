package com.loqua.business.services.serviceRemote;

import javax.ejb.Remote;

import com.loqua.business.services.ServiceSuggestion;

/**
 * Define la interfaz, utilizada desde aplicaciones cliente, que hereda a
 * {@link ServiceSuggestion}
 * @author Gonzalo
 */
@Remote
public interface RemoteServiceSuggestion
		extends ServiceSuggestion{
}
