package com.loqua.presentation.jsfHandler;

import java.util.List;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;

/**
 * El ViewHandler se encarga de alterar la generacion del contenido de
 * ciertos elementos de la vista y se ejecuta automaticamente
 * en las fases 1 y 6 de JSF ("Render Response" y "Restore View").
 * En este caso se encarga unicamente de permitir que los enlaces de los
 * 'commandLink' y 'commandButton' puedan incluir un ancla; por ejemplo: <br>
 * forum.xhtml?faces-redirect=true&amp;includeViewParams=true#thread46 <br>
 * Este ViewHandlerCustom debe estar registrado en el faces-config.xhtml.
 * @author Gonzalo
 */
public class ViewHandlerCustom extends ViewHandlerWrapper {
	
	/** Valor que se utiliza para nombrar la variable guardada por el componente
	 * {@link NavigationHandlerCustom} en el FacesContext durante la peticion,
	 * y que debe ser leida por ViewHandlerCustom 
	 * (en el metodo {@link #getRedirectURL}) */
	public static final String REDIRECT_FRAGMENT_ATTRIBUTE =
			ViewHandlerCustom.class.getSimpleName() + ".redirect.fragment";
	
	/** Manejador de la generacion de las vistas xhtml */
	private ViewHandler wrapped;
	
	/** Constructor que inicializa el objeto ViewHandler de la clase.
	 * @param wrapped objeto ViewHandler para inicializar el atributo
	 * {@link #wrapped}
	 */
	public ViewHandlerCustom(ViewHandler wrapped){
		super();
		this.wrapped = wrapped;
	}
	
	@Override
	public ViewHandler getWrapped() {
		return wrapped;
	}
	
	/**
	 * Busca en el FacesContext si existe la variable que habria sido creada
	 * por el {@link NavigationHandlerCustom}, cuyo valor es el ancla
	 * que deberia incluirse en la URL de la vista que se va a renderizar.
	 * Si dicha variable existe, entonces concatena su valor a la URL.
	 * @param context instancia del FacesContext que procesa a la peticion
	 * @param viewId el identificador de vista de la pagina de destino
	 * @param parameters una asignacion de nombres de parametros
	 * para uno o varios valores
	 * @param includeViewParams indica si los parametros de la vista
	 * (la 'query string') deberian ser codificados en la URL
	 */
	@Override
	public String getRedirectURL( final FacesContext context,
			final String viewId, final Map<String, List<String>> parameters,
			final boolean includeViewParams ){
        final String redirectURL = super.getRedirectURL(context, viewId,
        		parameters, includeViewParams);
        final Object fragment = context.getAttributes().get(
        		REDIRECT_FRAGMENT_ATTRIBUTE);
        return fragment == null ? redirectURL : redirectURL + fragment;
    }
}

