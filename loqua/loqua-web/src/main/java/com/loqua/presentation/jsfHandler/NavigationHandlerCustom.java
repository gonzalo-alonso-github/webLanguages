package com.loqua.presentation.jsfHandler;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ConfigurableNavigationHandlerWrapper;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

/**
 * El NavigationHandler se encarga de elegir la vista que debe ser presentada
 * segun la cadena de texto devuelta por la aplicacion.
 * En este caso solo se encarga de agregar explicitamente a la URL requerida
 * el ancla especificado (en caso de existir), para evitar que este sea omitido
 * por defecto al pinchar en los controles JSF 'commandLink' y 'commandButton'
 * presentes en las vistas xhtml.
 * Este NavigationHandlerCustom debe estar registrado en el faces-config.xhtml.
 * @author Gonzalo
 */
public class NavigationHandlerCustom /* extends ConfigurableNavigationHandler */
		extends ConfigurableNavigationHandlerWrapper {
	
	/** Manejador de la navegacion de la vista */
	private NavigationHandler wrapped;

	/** Constructor que inicializa el objeto NavigationHandler de la clase.
	 * @param wrapped objeto NavigationHandler para inicializar el atributo
	 * {@link #wrapped}
	 */
	public NavigationHandlerCustom(NavigationHandler wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public void handleNavigation(final FacesContext context, final String from,
			final String outcome) {
		super.handleNavigation(context, from, storeFragment(context, outcome));
	}

	@Override
	public void handleNavigation(final FacesContext context,
			final String fromAction, final String outcome,
			final String toFlowDocumentId) {
		super.handleNavigation(context, fromAction,
				storeFragment(context, outcome), toFlowDocumentId);
	}

	/**
	 * A partir de la URL resultante del enlace seguido
	 * por el usuario (es decir, a partir del 'action' de un 'commandLink' o
	 * 'commandButton'), obtiene el ancla si existe, y lo guarda
	 * en el Map del FacesContext. El manejador {@link ViewHandlerCustom}
	 * sera quien se encargue de redirigir la vista agregandole a la URL
	 * el ancla guardado en el FacesContext.
	 * @param context instancia del FacesContext que procesa a la peticion
	 * @param outcome cadena resultante despues de que el usuario haya pinchado
	 * en un enlace 'commandLink' o 'commandButton', que indica
	 * la siguiente vista que debe ser presentada
	 * @return cadena de texto que indica la siguiente vista que debe ser
	 * presentada, es decir, el mismo parametro 'outcome' recibido
	 */
	private static String storeFragment(final FacesContext context,
			final String outcome) {
		if (outcome != null) {
			// ('hash' se denomina al simbolo '#')
			final int hash = outcome.lastIndexOf('#');
			if (hash >= 0 && hash + 1 < outcome.length()
					&& outcome.charAt(hash + 1) != '{') {
				context.getAttributes().put(
						ViewHandlerCustom.REDIRECT_FRAGMENT_ATTRIBUTE,
						outcome.substring(hash));
				return outcome.substring(0, hash);
			}
		}
		return outcome;
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return (wrapped instanceof ConfigurableNavigationHandler)
				? (ConfigurableNavigationHandler) wrapped : null;
	}
}
