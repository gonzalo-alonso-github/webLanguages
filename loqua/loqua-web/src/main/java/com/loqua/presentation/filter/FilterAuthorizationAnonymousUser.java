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

/**
 * Define el filtro que se aplica sobre todas las paginas exclusivas
 * de usuarios no registrados (o 'anonimos').
 * Este filtro impide que ningun usuario de otro tipo acceda a dichas paginas.
 * <br>
 * El ciclo de JSF es interceptado por el Filtro antes de que el navegador
 * muestre la pagina sobre la que este se aplica, y se ejecuta inmediatamene
 * despues de los manejadores de navegacion (NavigationHandler) y de vista
 * (ViewHandler). <br>
 * Puesto que se definen varios filtros sobre las mismas paginas, es coveniente
 * indicar, en el fichero web.xml, el orden en que se aplican.
 * @author Gonzalo
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/pages/anonymousUser/*" },
		initParams = { 
			@WebInitParam(
					name="userIndex",
					value="/pages/registeredUser/profile_index.xhtml"),
			@WebInitParam(
					name="adminIndex",
					value="/pages/admin_user/profile_index.xhtml")
		})
public class FilterAuthorizationAnonymousUser implements Filter {

	/** Se utliza para acceder a los parametros de inicializacion
	 * definidos en las anotaciones de esta clase */
	FilterConfig config = null;

	/** Constructor sin parametros de la clase */
	public FilterAuthorizationAnonymousUser() {}

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
		/* Si el metodo termina despues de hacer 'chain.doFilter',
		se permite el acceso a la pagina requerida (no se redirige a otra)
		Si el metodo termina despues de hacer 'res.sendRedirect',
		no se permite el acceso a la pagina requerida (se redirige a otra) */
		
		// Si no es peticion HTTP no se filtra
		if (!(request instanceof HttpServletRequest)){
			chain.doFilter(request, response);
			return;
		}
		// En el resto de casos se verifica el filtro:
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		if( req.getSession(false)==null ){
			session = req.getSession(true);
		}
		String userIndex = config.getInitParameter("userIndex");
		String adminIndex = config.getInitParameter("adminIndex");
		
		User loggedUser = (User) session.getAttribute("LOGGED_USER");
		if (loggedUser != null) {
			// El usuario anonimo abre dos pestanas del navegador.
			// Inicia sesion en una de ellas; todo funcionara normalmente.
			
			// Despues inicia sesion en la otra pestana:
			// entonces no se le aplicara el filtro, se le permitira la accion
			if (req.getRequestURI().contains(";jsessionid=")) {
			    chain.doFilter(request, response);
			    return;
			}
			
			// O bien despues intenta navegar por la pagina en la primera pestana:
			// entonces si le aplicara el filtro, no se le permitira la accion:
			if( loggedUser.getRole().equals(User.USER) ){
				// Si el usuario ya esta logueado con rol USER, redirecciona:
				res.sendRedirect(req.getContextPath() + userIndex);
				return;
			}
			if( loggedUser.getRole().equals(User.ADMINISTRATOR) ){
				// Si el usuario ya esta logueado con rol ADMIN, redirecciona:
				res.sendRedirect(req.getContextPath() + adminIndex);
				return;
			}
		}
		chain.doFilter(request, response);
	}
}
