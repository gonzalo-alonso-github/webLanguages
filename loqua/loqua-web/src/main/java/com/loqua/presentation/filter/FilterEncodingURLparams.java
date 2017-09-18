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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Define el filtro, que se aplica sobre todas las paginas del sitio web,
 * y que comprueba si la URL ha sido codificada (por ejemplo, que el caracter
 * '#', utilizado para indicar un ancla en la URL, sea codificado a '%23').
 * En tal caso la descodifica de nuevo, para evitar que los demas filtros
 * tengan problemas a la hora de trabajar con la 'query string' de la URL.<br/>
 * Puesto que se definen varios filtros sobre las mismas paginas, es coveniente
 * indicar, en el fichero web.xml, el orden en que se aplican.
 * @author Gonzalo
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/pages/*" })
public class FilterEncodingURLparams implements Filter {

	/** Se utliza para acceder a los parametros de inicializacion
	 * definidos en las anotaciones de esta clase */
	FilterConfig config = null;

	boolean urlWasEncoded = false;
	
	/** Constructor sin parametros de la clase */
	public FilterEncodingURLparams() {}

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
		
		String redirectPage =  null;
		String requestedPage = req.getRequestURI().toString();
		/* Si automaticamente se ha agregado a la URL el parametro 'sessionId',
		 * antes de decodificar la 'query string' se eliminara dicho parametro
		 * de la URL, y despues se redireccionara */
		if( existsSessionIdInURL(req) ){
			//requestedPage=requestedPage.substring(0,requestedPage.indexOf(";"));
			redirectPage = requestedPage;
		}
		String queryStringDecoded = getQueryStringDecoded(req);
		if( queryStringDecoded!=null && urlWasEncoded ){
			// La condicion 'urlWasEncoded' es para que no se meta en un bucle
			redirectPage = requestedPage + "?" + queryStringDecoded;
		}
		if( redirectPage!=null ){
			res.sendRedirect(redirectPage);
			return;
		}
		chain.doFilter(req, response);
	}
	
	/**
	 * Comprueba si durante el ciclo de JSF se ha agregado a la URL
	 * el parametro 'sessionId'.
	 * @param req la peticion HTTP
	 * @return
	 * 'true' si la URL presenta el parametro 'sessionId' <br/>
	 * 'false' si la URL no tiene el parametro 'sessionId'
	 */
	private boolean existsSessionIdInURL(HttpServletRequest req) {
		HttpSession session = req.getSession();
        if( session.isNew() ) {
        	/* este 'if' hace que el redireccionamiento
        	no se repita aqui otra vez */
        	if( !(req.isRequestedSessionIdFromCookie()
        			&& req.isRequestedSessionIdFromURL()) ){
        		// Sera necesario redireccionar. Se devuelve true:
        		return true;
        	}
        }
        return false;
    }
	
	/**
	 * Decodifica los parametros y el ancla de la 'query string', si existen.
	 * @param req la peticion HTTP
	 * @return la 'query string', una vez decodificada
	 */
	private String getQueryStringDecoded(HttpServletRequest req) {
		// Por defecto las URL son codificadas de tal modo que, por ejemplo,
		// el caracter '#' se convierte en '%23'. Aqui se revierte el proceso:
		String queryString = req.getQueryString();
		String queryStringDecoded = queryString;
		if(queryString==null || queryString.isEmpty()) return null;
		//try {
			/*
			queryStringDecoded=java.net.URLDecoder.decode(queryString,"UTF-8");
			urlWasEncoded = queryString.compareTo(queryStringDecoded)==0 ?
					false:true;
			*/
			// Las dos lineas anteriores serian una solucion reutilizable
			// en cualquier proyecto. Pero para este caso es mas rapido esto:
			queryStringDecoded = queryString.replace("%23", "#");
			urlWasEncoded = queryString.indexOf("%23")==-1 ? false : true;
		//} catch (UnsupportedEncodingException e) {}
		return queryStringDecoded;
	}
	
	/*
	 * Convierte el texto dado en una simple cadena de caracteres, evitando que
	 * contenga caracteres especiales propios de una expresion regular.
	 * Este metodo es adecuado si, por ejemplo, se desea usar "replaceFirst"
	 * para reemplazar una cadena original que contenga tales caracteres. 
	 * Ejemplo:<br/><i>sampleURL.replaceFirst(escapeRegExp("?param=value"),"")</i>.
	 * (Por el contrario, no es necesario hacer eso con el metodo "replace").
	 * @param regExp cadena de texto que puede contener caracteres propios de
	 * una expresion regular
	 * @return cadena de texto 'escapando' los caracteres especiales propios de
	 * una expresion regular
	private String escapeRegExp(String regExp){
		Pattern SPECIAL_REGEX_CHARS=Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
		return SPECIAL_REGEX_CHARS.matcher(regExp).replaceAll("\\\\$0");
	}
	*/
}
