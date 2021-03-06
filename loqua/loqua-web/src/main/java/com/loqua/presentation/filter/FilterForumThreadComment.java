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

import com.loqua.model.Comment;
import com.loqua.model.ForumThread;
import com.loqua.model.User;
import com.loqua.presentation.bean.BeanForumThread;
import com.loqua.presentation.util.MapQueryString;
import com.loqua.presentation.util.VerifierAjaxRequest;

/**
 * Define el filtro, que se aplica sobre la pagina de
 * 'forum_thread_comment.xhtml',
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
		urlPatterns = { "/pages/registeredUser/forum_thread_comment.xhtml",
				"/pages/admin_user/forum_thread_comment.xhtml"},
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


public class FilterForumThreadComment implements Filter {

	/** Se utliza para acceder a los parametros de inicializacion
	 * definidos en las anotaciones de esta clase */
	FilterConfig config = null;

	// La noticia a la que pertenece el comentario para enviar/citar/editar:
	Long requestedThreadId = 0L;
	ForumThread requestedThread;
	
	// El comentario que se va a citar/editar
	Long requestedCommentId = 0L;
	Comment requestedComment;
	
	MapQueryString queryStringMap;
	
	/** Constructor sin parametros de la clase */
	public FilterForumThreadComment() {}

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
				|| verifyParameters(req, loggedUser)==false ){
			// A diferencia del 'FilterForum',
			// si la URL no tiene queryString o sus parametros no son correctos,
			// redirecciona (no se pasa el filtro):
			res.sendRedirect(req.getContextPath() + errorPage);
		    return;
		}
		chain.doFilter(request, response);
	}
	
	/**
	 * Comprueba que la URL de la peticion a la pagina
	 * 'forum_thread_comment.xhtml' contiene los parametros correctos
	 * ('thread', 'action' y 'comment'), descritos a continuacion.
	 * <ul><li>Parametro 'thread': 'hilo' del foro al cual pertenece el post
	 * que se va a crear o editar en la siguiente vista
	 * (forum_thread_comment.xhtml).
	 * </li><li>Parametro 'action': accion que se va a realizar en la pagina
	 * 'forum_thread_comment.xhtml':<br>
	 * - Si action=1 la vista mostrara lo necesario para crear un comentario
	 * <br>
	 * - Si action=2 la vista mostrara lo necesario para editar un comentario
	 * <br>
	 * - Si action=3 la vista mostrara lo necesario para citar un comentario
	 * </li><li>Parametro 'comment': commentario del foro que se va a
	 * editar/citar/corregir en la pagina 'forum_thread_comment.xhtml'.
	 * </li></ul>
	 * @param req la peticion HTTP
	 * @param loggedUser el usuario que accede a la pagina
	 * @return
	 * 'true' si los parametros de la url son correctos <br>
	 * 'false' si los parametros de la url no son correctos
	 */
	private boolean verifyParameters(HttpServletRequest req, User loggedUser){
		try{
			String requestedActionParam = queryStringMap.get("action");
			if( requestedActionParam==null ){ return false; }
			int action = Integer.parseInt( requestedActionParam );
			if( action==1 ){
				// si action=1: crear un comentario.
				/* Para ello no es necesario el parametro 'comment'
				(de hecho, el agregar a la url un parametro 'comment'
				con cualquier valor aleatorio no provoca efecto alguno)
				pero es necesario el parametro 'thread', que se verifica aqui: */
				return verifyRequestedThread(req);
			}else if( action==2 ){
				// action=2: editar un comentario
				// Para ello son necesarios los parametros 'thread' y 'comment',
				// y comprobar que solo el admin puede editar comentarios ajenos
				return verifyRequestedThread(req) && verifyRequestedComment(req)
						&& verifyIfEdittingForeignComment(req, loggedUser);
			}else if( action==3 ){
				// action=3: citar un comentario
				// Para ello son necesarios los parametros 'thread' y 'comment':
				return verifyRequestedThread(req) && verifyRequestedComment(req);
			}else{
				// Si el parametro action tiene un valor distinto de entre 1 a 3
				return false;
			}
		} catch( NumberFormatException nfe ){
			return false;
		}
	}

	/**
	 * Comprueba que la url de la peticion a la pagina
	 * 'forum_thread_comment.xhtml' contiene el parametro 'thread'
	 * y es correcto (indica la noticia, tambien llamada 'hilo',
	 * a la que pertenece el comentario que se va a crear/citar/editar).
	 * @param req la peticion HTTP
	 * @return
	 * 'true' si el parametro 'thread' de la url es correcto <br>
	 * 'false' si el parametro 'thread' de la url no es correcto
	 */
	private boolean verifyRequestedThread(HttpServletRequest req){
		try{
			String requestedThreadParam = queryStringMap.get("thread");
			if( requestedThreadParam==null ){ return false; }
			requestedThreadId = Long.parseLong( requestedThreadParam );
			requestedThread = 
					BeanForumThread.getThreadByIdStatic( requestedThreadId );
			if( requestedThread==null ){ return false; }
			else{ return true; }
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
	/**
	 * Comprueba que la url de la peticion a la pagina
	 * 'forum_thread_comment.xhtml' contiene el parametro 'comment'
	 * y es correcto (indica el comentario que se va a citar/editar).
	 * @param req la peticion HTTP
	 * @return
	 * 'true' si el parametro 'comment' de la url es correcto <br>
	 * 'false' si el parametro 'comment' de la url no es correcto
	 */
	private boolean verifyRequestedComment(HttpServletRequest req){
		try{
		// Esta llamada a "verifyRequestedThread" no es necesaria,
		// porque aqui solo sirve para inicializar "requestedThreadId",
		// cosa que ya se ha hecho anteriormente a este metodo,
		// pero es prudente dejarlo asi ante futuros cambios en el codigo:
		if( ! verifyRequestedThread(req) ){ return false; }
		
		String requestedCommentParam = queryStringMap.get("comment");
		if( requestedCommentParam==null ){ return false; }
		requestedCommentId = Long.parseLong( requestedCommentParam );
		requestedComment = (Comment) BeanForumThread
				.getPostByIdStatic( requestedCommentId );
		if( requestedComment==null ){ return false; }
		
		// NOTA: los valores Long se comparan con "equals", no con "==" ni "!=" 
		if( ! requestedComment.getForumThread().getId()
				.equals(requestedThreadId) ){ 
			return false;
		}else{ return true; }
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
	/**
	 * Comprueba si el usuario tiene permiso para
	 * editar el comentario recibido en la 'query string' de la URL.
	 * <ul><li>Si los parametros 'action' y 'comment' de la URL indican
	 * que el usuario quiere editar un comentario ajeno,
	 * solo se permite realizar la accion si es un administrador.
	 * </li><li>Si por el contrario el parametro 'comment' indica que es un
	 * comentario propio, siempre se permite la accion
	 * </li></ul>
	 * @param req la peticion HTTP
	 * @param loggedUser el usuario que accede a la pagina
	 * @return
	 * 'true' si el usuario dado tiene permiso para editar el comentario <br>
	 * 'false' si el usuario dado no tiene permiso para editar el comentario
	 */
	private boolean verifyIfEdittingForeignComment(HttpServletRequest req,
			User loggedUser) {
		// Si el usuario autenticado es administrador,
		// si se permite editar comentario ajeno
		if(loggedUser.getRole().equals(User.ADMINISTRATOR)==true){return true;}
		
		// Esta llamada a "verifyRequestedComment" no es necesaria,
		// porque aqui solo sirve para inicializar "requestedComment",
		// cosa que ya se ha hecho anteriormente a este metodo,
		// pero es prudente dejarlo asi ante futuros cambios en el codigo:
		if( ! verifyRequestedComment(req) ){ return false; }
		
		// NOTA: los valores Long se comparan con "equals", no con "==" ni "!=" 
		if( ! requestedComment.getUser().getId().equals(loggedUser.getId()) ){
			// no se permite editar comentarios ajenos:
			return false;
		}
		return true;
	}
}
