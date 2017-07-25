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
 * por defecto al pinchar en los 'commandLink' y 'commandButton'.
 * Este NavigationHandlerCustom debe estar registrado en el faces-config.xhtml.
 * @author Gonzalo
 */
public class NavigationHandlerCustom /* extends ConfigurableNavigationHandler */
		extends ConfigurableNavigationHandlerWrapper {

	private NavigationHandler wrapped;

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
