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
import com.loqua.model.Correction;
import com.loqua.model.ForumThread;
import com.loqua.model.User;
import com.loqua.presentation.bean.BeanForumThread;
import com.loqua.presentation.util.MapQueryString;
import com.loqua.presentation.util.VerifierAjaxRequest;

/**
 * Este filtro se aplica a la pagina de 'forum_thread_correction.xhtml'
 * (tanto para usuarios registrados como para administradores).
 * Por tanto existen dos filtros para dicha pagina: el 'FilterAuthorization...'
 * y este. Y el orden en que se aplican esta definido en el fichero de despliegue
 * web.xml.
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/pages/registeredUser/forum_thread_correction.xhtml",
				"/pages/admin_user/forum_thread_correction.xhtml"},
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


public class FilterForumThreadCorrection implements Filter {

	// Necesitamos acceder a los parametros de inicializacion en
	// el metodo doFilter, asi que necesitamos la variable
	// config que se inicializara en init()
	FilterConfig config = null;

	// La noticia a la que pertenece la correccion que se va a enviar/editar:
	Long requestedThreadId = 0L;
	ForumThread requestedThread;
	
	// El comentario que se va a corregir:
	Long requestedCommentId = 0L;
	Comment requestedComment;
	
	// La correccion que se va a editar:
	Long requestedCorrId = 0L;
	Correction requestedCorr;
	
	MapQueryString queryStringMap;
	
	/**
	 * Default constructor.
	 */
	public FilterForumThreadCorrection() {
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
	 * Comprueba que la url de la peticion a la pagina
	 * 'forum_thread_correction.xhtml'
	 * contiene los parametros correctos ('thread', 'action' y 'comment'). <br/>
	 * - Parametro 'thread': 'hilo' del foro al cual pertenece el post que se va
	 * a crear o editar en la siguiente vista (forum_thread_correction.xhtml).<br/>
	 * - Parametro 'action': accion que se va a realizar en la pagina
	 * 'forum_thread_correction.xhtml'.<br/>
	 * -- Si action=1 la vista mostrara lo necesario para crear correccion<br/>
	 * -- Si action=2 la vista mostrara lo necesario para editar correccion<br/>
	 * - Parametro 'comment': commentario del foro que se va a editar/citar/corregir
	 * en la pagina 'forum_thread_comment.xhtml'.
	 * @return 'true' si los parametros de la url son correctos
	 */
	private boolean verifyParameters(HttpServletRequest req, User loggedUser){
		try{
			String requestedActionParam = queryStringMap.get("action");
			if( requestedActionParam==null ){ return false; }
			int action = Integer.parseInt( requestedActionParam );
			if( action==1 ){
				// si action=1: crear una correccion.
				/* Para ello no es necesario el parametro 'correccion'
				(de hecho, el agregar a la url un parametro 'correccion'
				con cualquier valor aleatorio no provoca efecto alguno)
				pero son necesarios 'thread y 'comment',
				y comprobar que solo el admin puede corregir comments ajenos: */
				return verifyRequestedThread(req) 
						&& verifyRequestedComment(req) 
						&& verifyIfCorrectingForeignComm(req, loggedUser);
			}else if( action==2 ){
				// si action=2: editar una correccion
				/* Para ello no es necesario el parametro 'comment'.
				Pero, si se agrega, debe ser el comment asociado a la correction
				Y son necesarios los parametros 'thread y 'comment',
				y comprobar que solo el admin puede editar corrections ajenas */
				return verifyOptionallyCommentOfCorrection(req)
						&& verifyRequestedThread(req) 
						&& verifyRequestedCorrection(req)
						&& verifyIfEdittingForeignCorr(req, loggedUser);
			}else{
				// Si el parametro action tiene un valor distinto de entre 1 a 2
				return false;
			}
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
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
		if(! requestedComment.getForumThread().getId()
				.equals(requestedThreadId) ){ 
			return false;
		}else{ return true; }
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
	private boolean verifyRequestedCorrection(HttpServletRequest req){
		try{
		// Esta llamada a "verifyRequestedThread" no es necesaria,
		// porque aqui solo sirve para inicializar "requestedThreadId",
		// cosa que ya se ha hecho anteriormente a este metodo,
		// pero es prudente dejarlo asi ante futuros cambios en el codigo:
		if( ! verifyRequestedThread(req) ){ return false; }
		
		String requestedCorrectionParam = queryStringMap.get("correction");
		if( requestedCorrectionParam==null ){ return false; }
		requestedCorrId = Long.parseLong( requestedCorrectionParam );
		requestedCorr = (Correction) BeanForumThread
				.getPostByIdStatic( requestedCorrId );
		if( requestedCorr==null ){ return false; }
		
		// NOTA: los valores Long se comparan con "equals", no con "==" ni "!=" 
		if(!requestedCorr.getForumThread().getId().equals(requestedThreadId)){ 
			return false;
		}else{ return true; }
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
	private boolean verifyIfCorrectingForeignComm(HttpServletRequest req,
			User loggedUser) {
		// Si el usuario autenticado es administrador,
		// si se permite corregir un comentario propio (no confundir con editar)
		if(loggedUser.getRole().equals(User.ADMINISTRATOR)==true){return true;}
		
		// Esta llamada a "verifyRequestedComment" no es necesaria,
		// porque aqui solo sirve para inicializar "requestedComment",
		// cosa que ya se ha hecho anteriormente a este metodo,
		// pero es prudente dejarlo asi ante futuros cambios en el codigo:
		if( ! verifyRequestedComment(req) ){ return false; }
		
		// NOTA: los valores Long se comparan con "equals", no con "==" ni "!=" 
		if( requestedComment.getUser().getId().equals(loggedUser.getId()) ){
			// no se permite corregir un comentario propio
			return false;
		}
		return true;
	}
	
	private boolean verifyIfEdittingForeignCorr(HttpServletRequest req,
			User loggedUser) {
		// Si el usuario autenticado es administrador,
		// si se permite editar correccion ajena
		if(loggedUser.getRole().equals(User.ADMINISTRATOR)==true){return true;}
		
		// Esta llamada a "verifyRequestedCorrection" no es necesaria,
		// porque aqui solo sirve para inicializar "requestedCorr",
		// cosa que ya se ha hecho anteriormente a este metodo,
		// pero es prudente dejarlo asi ante futuros cambios en el codigo:
		if( ! verifyRequestedCorrection(req) ){ return false; }
		
		// NOTA: los valores Long se comparan con "equals", no con "==" ni "!=" 
		if( ! requestedCorr.getUser().getId().equals(loggedUser.getId()) ){
			// no se permite editar correccion ajena:
			return false;
		}
		return true;
	}
	
	private boolean verifyOptionallyCommentOfCorrection(HttpServletRequest req){
		// Esta llamada a "verifyRequestedCorrection" no es necesaria,
		// porque aqui solo sirve para inicializar "requestedCorr",
		// cosa que ya se ha hecho anteriormente a este metodo,
		// pero es prudente dejarlo asi ante futuros cambios en el codigo:
		if( ! verifyRequestedCorrection(req) ){ return false; }
		
		// Aqui el parametro comment es opcional; se permite null:
		if( req.getParameter("comment")==null ){ return true; }
		// Pero si existe, el comment debe pertenecer a la correction dada:
		requestedCommentId = Long.parseLong( req.getParameter("comment") );
		// NOTA: los valores Long se comparan con "equals", no con "==" ni "!=" 
		if( requestedCorr.getComment().getId().equals(requestedCommentId) ){
			return false;
		}
		return true;
	}
}
