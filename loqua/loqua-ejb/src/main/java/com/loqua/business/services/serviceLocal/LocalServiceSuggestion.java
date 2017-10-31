package com.loqua.business.services.serviceLocal;

import javax.ejb.Local;

import com.loqua.business.services.ServiceSuggestion;

/**
 * Define la interfaz, utilizada desde la misma aplicacion, que hereda a
 * {@link ServiceSuggestion}
 * @author Gonzalo
 */
@Local
public interface LocalServiceSuggestion extends ServiceSuggestion{
}
