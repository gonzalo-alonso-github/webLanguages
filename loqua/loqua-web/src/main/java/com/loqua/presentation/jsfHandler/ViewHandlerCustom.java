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
 * 'commandLink' y 'commandButton' puedan agregar un ancla; por ejemplo:
 * 'forum.xhtml?faces-redirect=true&includeViewParams=true#thread46
 * Este ViewHandlerCustom debe estar registrado en el faces-config.xhtml.
 * @author Gonzalo
 */
public class ViewHandlerCustom extends ViewHandlerWrapper {
	
	private ViewHandler wrapped;
	public static final String REDIRECT_FRAGMENT_ATTRIBUTE =
			ViewHandlerCustom.class.getSimpleName() + ".redirect.fragment";
	
	public ViewHandlerCustom(ViewHandler wrapped){
		super();
		this.wrapped = wrapped;
	}
	
	/*
	@Override
	public void initView(FacesContext context) throws FacesException{
		//acciones que se realizan al renderizar cualquier vista o recargarla
		//(Se ejecuta antes de cada 'restoreView' y de cada 'renderView')
		//(A su vez, los Filtros se ejecutarian antes de cada 'ViewHandler').
		super.initView(context);
	}
	
	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId)
			throws FacesException, NullPointerException {
		//acciones que se realizan al re-renderizar la misma pagina
		//(mediante Ajax). Si se ejecuta 'renderView' no se ejecuta este metodo
		return super.restoreView(context, viewId);
	}
	
	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender)
			throws FacesException, IOException {
		//acciones que se realizan al renderizar cualquier pagina
		//(salvo Ajax). Si se ejecuta 'restoreView' no se ejecuta este metodo
		super.renderView(context, viewToRender);
	}
	*/
	
	@Override
	public ViewHandler getWrapped() {
		return wrapped;
	}
	
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

