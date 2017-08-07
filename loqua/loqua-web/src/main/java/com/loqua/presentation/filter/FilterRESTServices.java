package com.loqua.presentation.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

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

import com.loqua.business.services.impl.MapCredentials;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/rest/*" },
		initParams = { 
			@WebInitParam(
				name="defaultResponse",
				value="/pages/anonymousUser/errorPageNotFound.xhtml")
		})


public class FilterRESTServices implements Filter {

	// Necesitamos acceder a los parametros de inicializacion en
	// el metodo doFilter, asi que necesitamos la variable
	// config que se inicializara en init()
	FilterConfig config = null;

	/*private static String[] credentials = 
			MapCredentials.getInstance().getDecrypted("rest");*/
	private static String[] credentials = 
			MapCredentials.getInstance().get("rest");
	
	/**
	 * Default constructor.
	 */
	public FilterRESTServices() {
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
		/*
		HttpSession session = req.getSession();
		if( req.getSession(false)==null ){
			session = req.getSession(true);
		}
		*/
		String credential = null;
		String base64Credentials = req.getHeader("NotStandard-Filter");
		if( base64Credentials!=null && !base64Credentials.isEmpty() ){
			// && authorization.startsWith("Basic")
			// credential = base64Credentials.substring("Basic".length()).trim();
			credential = new String(Base64.getDecoder()
					.decode(base64Credentials), Charset.forName("UTF-8"));
		}
		if( credential==null || credential.equals(credentials[0])==false ){
			/* Se dice que el header de la respuesta se debe editar
			despues del doFilter y no antes. Asi que hacemos primero doFilter:
			https://stackoverflow.com/questions/13461500/
			java-filter-failing-to-set-response-headers#answer-13483065 */
			//chain.doFilter(request, response);
			
			/* ...aunque finalmente se decide no hacer aqui doFilter,
			sino redireccionar a pagina de error, por si el 'intruso fallido'
			es un navegador de usuario y no la aplicacion cliente REST */
			String defaultResponse = config.getInitParameter("defaultResponse");
			res.sendRedirect(req.getContextPath() + defaultResponse);
			
			/* Se devuelve el resultado "401 Unauthorized" en la respuesta,
			que como indica en.wikipedia.org/wiki/List_of_HTTP_status_codes
			corresponde a la cabecera 'WWW-Authenticate'.
			en.wikipedia.org/wiki/List_of_HTTP_header_fields */
			res.setHeader("WWW-Authenticate", "Not Basic");
			res.setStatus(401);
			return;
		}
		chain.doFilter(request, response);
	}
}
