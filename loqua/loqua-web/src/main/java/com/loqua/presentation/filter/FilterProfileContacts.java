package com.loqua.presentation.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.loqua.model.User;
import com.loqua.presentation.bean.BeanUserView;
import com.loqua.presentation.util.MapQueryString;
import com.loqua.presentation.util.VerifierAjaxRequest;

/**
 * Este filtro se aplica a la pagina de 'profile_contacts_user.xhtml'
 * (tanto para usuarios anonimos como para registrados y administradores).
 * Por tanto existen dos filtros para dicha pagina: el 'FilterAuthorization...'
 * y este. Y el orden en que se aplican esta definido en el fichero de despliegue
 * web.xml.
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/pages/registeredUser/profile_contacts_user.xhtml",
				"/pages/admin_user/profile_contacts_user.xhtml"},
		initParams = { 
			@WebInitParam(
				name="errorAnonymous",
				value="/pages/anonymousUser/errorPageNotFound.xhtml"),
			@WebInitParam(
				name="errorRegistered",
				value="/pages/registeredUser/errorPageNotFound.xhtml"),
			@WebInitParam(
				name="errorAdmin",
				value="/pages/admin_user/errorPageNotFound.xhtml"),
			@WebInitParam(
				name="userOwnPage",
				value="/pages/registeredUser/profile_contacts_me.xhtml"),
			@WebInitParam(
				name="adminOwnPage",
				value="/pages/admin_user/profile_contacts_me.xhtml")
		})


public class FilterProfileContacts implements Filter {

	// Necesitamos acceder a los parametros de inicializacion en
	// el metodo doFilter, asi que necesitamos la variable
	// config que se inicializara en init()
	FilterConfig config = null;
	
	Long requestedUserId = 0L;
	User requestedUser;
	
	MapQueryString queryStringMap;

	/**
	 * Default constructor.
	 */
	public FilterProfileContacts() {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// Iniciamos la variable de instancia config
		config = fConfig;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// Si el metodo termina despues de hacer 'chain.doFilter',
		// se permite el acceso a la pagina requerida (no se redirige a otra)
		// Si el metodo termina despues de hacer 'res.sendRedirect',
		// no se permite el acceso a la pagina requerida (se redirige a otra)
		
		// Si no es peticion HTTP no se filtra
		if (!(request instanceof HttpServletRequest)){
			chain.doFilter(request, response);
			return;
		}
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		if( req.getSession(false)==null ){
			session = req.getSession(true);
		}
		String errorPage = config.getInitParameter("errorAnonymous");
		String ownPage = "";
		
		// Si el usuario esta autenticado, el beanLogin deberia haber escrito
		// en el Map de la sesion la variable "LOGGED_USER" de tipo User
		User loggedUser = (User) session.getAttribute("LOGGED_USER");
		
		if( loggedUser == null ){
			// Si el usuario no esta autenticado es un usuario anonimo
			// Por tanto en caso de redirigirlo, seria a una pagina de anonimo,
			// asi que inicializamos la pagina adecuada:
			errorPage = config.getInitParameter("errorAnonymous");
		} else if( loggedUser.getRole().equals(User.USER)==true ){
			// Si el usuario autenticado no es administrador entonces,
			// en caso de redirigirlo, seria a una pagina de usuario registrado,
			// asi que inicializamos la pagina adecuada:
			errorPage = config.getInitParameter("errorRegistered");
			ownPage = config.getInitParameter("userOwnPage");
		} else if( loggedUser.getRole().equals(User.ADMINISTRATOR)==true ){
			// Si el usuario autenticado es administrador entonces,
			// en caso de redirigirlo, seria a una pagina de administrador,
			// asi que inicializamos la pagina adecuada:
			errorPage = config.getInitParameter("errorAdmin");
			ownPage = config.getInitParameter("adminOwnPage");
		}
		if( VerifierAjaxRequest.isAJAXRequest(req) ){
			chain.doFilter(request, response);
			return;
		}
		queryStringMap = new MapQueryString(req);
		if( queryStringMap.loadedQueryStringMap()==false
				|| verifyParameters(req, loggedUser)==false ){
			// Si la URL no tiene queryString o sus parametros no son correctos,
			// redirecciona (a pagina de error):
			res.sendRedirect(req.getContextPath() + errorPage);
		    return;
		}
		// "requestedUser" ha sido inicializada en "verifyParameters()"
		if( requestedUser.getId().equals(loggedUser.getId())  ){
			// Si el parametro 'user' es el id del propio usuario logueado,
			// redirecciona (a pagina de 'profile_contacts_me'):
			res.sendRedirect(req.getContextPath() + ownPage);
		    return;
		}
		chain.doFilter(request, response);
	}
	
	/**
	 * Comprueba que la url de la peticion a la pagina
	 * 'profile_contacs_user.xhtml' contiene el parametro 'user'
	 * (usuario que se va a consultar en la vista).
	 * @return 'true' si el parametro de la url es correcto
	 */
	private boolean verifyParameters(HttpServletRequest req, User loggedUser){
		try{
			String requestedUserParam = queryStringMap.get("user");
			if( requestedUserParam==null ){ return false; }
			requestedUserId = Long.parseLong( requestedUserParam );
			requestedUser = BeanUserView.getUserByIdStatic( requestedUserId );
			if( requestedUser==null ){ return false; }
			else{
				if( requestedUser.getRemoved() ){ return false; }
				else{ return true; }
			}
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
}
