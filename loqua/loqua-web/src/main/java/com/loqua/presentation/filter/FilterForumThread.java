package com.loqua.presentation.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.loqua.model.ForumThread;
import com.loqua.model.User;
import com.loqua.presentation.bean.BeanForumThread;
import com.loqua.presentation.bean.applicationBean.BeanSettingsForumPage;
import com.loqua.presentation.util.MapQueryString;
import com.loqua.presentation.util.VerifierAjaxRequest;

/**
 * Define el filtro, que se aplica sobre la pagina de 'forum_thread.xhtml',
 * y que comprueba si son correctos los parametros enviados en la URL
 * (la 'query string'). <br>
 * El ciclo de JSF es interceptado por el Filtro antes de que el navegador
 * muestre la pagina sobre la que este se aplica, y se ejecuta inmediatamene
 * despues de los manejadores de navegacion (NavigationHandler) y de vista
 * (ViewHandler). <br>
 * Puesto que se definen varios filtros sobre esta misma pagina, es coveniente
 * indicar, en el fichero web.xml, el orden en que se aplican.
 * @author Gonzalo
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/pages/anonymousUser/forum_thread.xhtml",
				"/pages/registeredUser/forum_thread.xhtml",
				"/pages/admin_user/forum_thread.xhtml"},
		initParams = { 
			@WebInitParam(
				name="errorAnonymous",
				value="/pages/anonymousUser/errorPageNotFound.xhtml"),
			@WebInitParam(
				name="errorRegistered",
				value="/pages/registeredUser/errorPageNotFound.xhtml"),
			@WebInitParam(
				name="errorAdmin",
				value="/pages/admin_user/errorPageNotFound.xhtml")
		})


public class FilterForumThread implements Filter {

	/** Se utliza para acceder a los parametros de inicializacion
	 * definidos en las anotaciones de esta clase */
	FilterConfig config = null;
	
	Long requestedThreadId = 0L;
	ForumThread requestedThread;
	int requestedPage = 0;
	MapQueryString queryStringMap;

	/** Constructor sin parametros de la clase */
	public FilterForumThread() {}

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
		HttpSession session = req.getSession();
		if( req.getSession(false)==null ){
			session = req.getSession(true);
		}
		String errorPage = config.getInitParameter("errorAnonymous");
		
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
		} else if( loggedUser.getRole().equals(User.ADMINISTRATOR)==true ){
			// Si el usuario autenticado es administrador entonces,
			// en caso de redirigirlo, seria a una pagina de administrador,
			// asi que inicializamos la pagina adecuada:
			errorPage = config.getInitParameter("errorAdmin");
		}
		if( VerifierAjaxRequest.isAJAXRequest(req) ){
			chain.doFilter(request, response);
			return;
		}
		queryStringMap = new MapQueryString(req);
		if( queryStringMap.loadedQueryStringMap()==false
				|| verifyParameters(req)==false ){
			// A diferencia del 'FilterForum',
			// si la URL no tiene queryString o sus parametros no son correctos,
			// redirecciona (no se pasa el filtro):
			res.sendRedirect(req.getContextPath() + errorPage);
		    return;
		}
		incrementForumThreadVisits(session);
		chain.doFilter(request, response);
	}
	
	/**
	 * Comprueba que la url de la peticion a la pagina 'forum_thread.xhtml'
	 * contiene los parametros correctos ('thread' y 'page'). <ul>
	 * <li>Parametro 'thread': 'hilo' del foro que se va a consultar
	 * en la siguiente vista (forum_thread.xhtml).</li>
	 * <li>Parametro 'page': pagina del 'hilo' que se va a consultar.</li></ul>
	 * @param req la peticion HTTP
	 * @return
	 * 'true' si los parametros de la url son correctos <br>
	 * 'false' si los parametros de la url no son correctos
	 */
	private boolean verifyParameters(HttpServletRequest req){
		try{
			String requestedThreadParam = queryStringMap.get("thread");
			if( requestedThreadParam==null ){ return false; }
			requestedThreadId = Long.parseLong( requestedThreadParam );
			requestedThread = 
					BeanForumThread.getThreadByIdStatic( requestedThreadId );
			if( requestedThread==null ){ return false; }
			else{
				return verifyRequestedPage(requestedThread, req);
			}
		} catch( NumberFormatException nfe ){
			return false;
		}
	}

	/**
	 * Comprueba que la url de la peticion a la pagina 'forum_thread.xhtml'
	 * contiene el parametro 'page' y es correcto (pagina que se va a consultar
	 * en la lista de comentarios de la noticia).
	 * @param thread hilo del foro al que se accede en la peticion
	 * @param req la peticion HTTP
	 * @return
	 * 'true' si el parametro 'page' de la url es correcto <br>
	 * 'false' si el parametro 'page' de la url no es correcto
	 */
	private boolean verifyRequestedPage(
			ForumThread thread, HttpServletRequest req){
		try{
			String requestedPageParam = queryStringMap.get("page");
			if( requestedPageParam==null ){ return false; }
			requestedPage = Integer.parseInt( requestedPageParam );
			// la pagina 0 del 'hilo' es la misma que la 1 (se permite el valor 0)
			// pero obviamente si ese valor es < 0, no pasa el filtro:
			if( requestedPage<0 ){ return false; }
			
			int numComments = thread.getForumThreadInfo().getCountComments();
			int numPages = BeanSettingsForumPage.getNumPagesOfThreadStatic(
					numComments);
			// si se pretende entrar en una pagina que no existe, no pasa filtro:
			if( requestedPage > numPages ){ return false; }
			return true;
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
	/**
	 * Incrementa el numero de visitas de una noticia del foro.
	 * No se incrementa si, durante una misma sesion, se realizan varias
	 * visitas al hilo. Para comprobarlo utiliza la variable 'VISITED_THREADS'
	 * almacenada en el contexto de sesion.
	 * @param session la sesion del usuario
	 */
	private void incrementForumThreadVisits(HttpSession session) {
		// Los valores 'requestedThread' y 'requestedThreadId'
		// ya se han inicializado con el metodo "verifyParameters(...)"
		
		// Se guarda la lista de noticias visitadas en el contexto de SESSION
		// NOTA: si se quisiera guardar en contexto de APPLICATION,
		// entonces en lugar de 'session' usariamos 'req.getServletContext()'
		@SuppressWarnings("unchecked")
		List<Long> visitedForumThreads = 
				(List<Long>) session.getAttribute("VISITED_THREADS");
		if( visitedForumThreads == null ){
			visitedForumThreads = new ArrayList<Long>();
		}
		if( visitedForumThreads.contains(requestedThreadId) ){
			// Si en esta sesion ya se ha visitado la noticia, no hacer nada
			return;
		}else{
			// Si en esta sesion es la primera vez que se visita la noticia
			// se incrementa el numero de visitas de la noticia:
			BeanForumThread.incrementCountVisitsStatic(requestedThread);
			// se guarda en dicho List de session el id del Thread visitado:
			visitedForumThreads.add( requestedThreadId );
		}
		session.setAttribute("VISITED_THREADS", visitedForumThreads);
	}
}
