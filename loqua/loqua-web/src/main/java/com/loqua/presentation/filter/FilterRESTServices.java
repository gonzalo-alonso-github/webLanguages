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
 * Define el filtro que se aplica sobre los recursos REST ubicados en
 * el directorio '/rest' y comprueba si las peticiones HTTP recibidas
 * contienen la cabecera con la credencial correcta para acceder a dichos
 * recursos.
 * @author Gonzalo
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

	/** Se utliza para acceder a los parametros de inicializacion
	 * definidos en las anotaciones de esta clase */
	FilterConfig config = null;

	/*private static String[] credentials = 
			MapCredentials.getInstance().getDecrypted("rest");*/
	private static String[] credentials = 
			MapCredentials.getInstance().get("rest");
	
	/** Constructor sin parametros de la clase */
	public FilterRESTServices() {}

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
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String credential = null;
		String base64Credentials = req.getHeader("NotStandard-Filter");
		if( base64Credentials!=null && !base64Credentials.isEmpty() ){
			credential = new String(Base64.getDecoder()
					.decode(base64Credentials), Charset.forName("UTF-8"));
		}
		if( credential==null || credential.equals(credentials[0])==false ){
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
