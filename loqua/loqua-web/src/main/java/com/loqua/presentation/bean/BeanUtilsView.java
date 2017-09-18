package com.loqua.presentation.bean;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Bean de ambito de vista que contiene metodos pueden ser accedidos
 * desde cualquier pagina del sitio y manejan informacion
 * de la vista actual a la que accede el usuario.
 * @author Gonzalo
 */
public class BeanUtilsView implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/**Constructor del bean. */
	public BeanUtilsView() {}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Recarga la pagina actual en la que se encuentra el usuario.
	 * Si se llama a este metodo desde un componente de formulario de la vista
	 * (ej: el boton para recomendar/desaconsejar una correccion)
	 * el efecto que produce es equivalente a agregar en dicho componente
	 * la etiqueta &lt;f:ajax render="@all"&gt;
	 * @param anchor 'ancla' en el cual se situara el scroll del navegador tras
	 * recargar la pagina.
	 * @return valor necesario del atributo 'action' de un commandLink
	 * para que redirija a la misma vista actual
	 */
	public String renderViewAgainFromCommandLink(String anchor){
		// Por ejemplo, se utiliza desde la pagina 'forum_thread.xhtml'.
		return renderViewAgainFromCommandLinkStatic(anchor);
	}
	
	/**
	 * Version estatica del metodo {@link #renderViewAgainFromCommandLink}
	 * @param anchor 'ancla' en el cual se situara el scroll del navegador tras
	 * recargar la pagina.
	 * @return valor necesario del atributo 'action' de un commandLink
	 * para que redirija a la misma vista actual
	 * @see #renderViewAgainFromCommandLink
	 */
	public static String renderViewAgainFromCommandLinkStatic(String anchor){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest)
				facesContext.getExternalContext().getRequest();
		String uri = req.getRequestURI().toString();
		// ejem. req.getRequestURI() = loqua-web/pages/admin_user/f_thread.xhtml
		String currentView=uri.substring(uri.lastIndexOf("/")+1, uri.length());
		// ejem. currentView = f_thread.xhtml
		// Se agrega el 'faces-redirect=true' obligado en los commandLink:
		currentView += "?faces-redirect=true&includeViewParams=true";
		// 'includeViewParams=true' para reutilizar los parametros de la vista
		// Hay que incluir en la URL el 'ancla' que se recibe por parametro:
		if(anchor!=null && !anchor.isEmpty()){
			currentView += "#" + anchor;
			// Pero para que se incluya el ancla no basta con esto.
			// Es necesario el ViewHandlerCustom y el NavigationHandlerCustom
		}
		// ExternalContext ec = facesContext.getExternalContext();
		// try { ec.redirect(currentView); } catch (IOException e) {}
		return currentView;
	}
}
