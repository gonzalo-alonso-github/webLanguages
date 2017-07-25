package com.loqua.presentation.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/pages/admin_user/*" },
		initParams = { 
			@WebInitParam(
				name="anonymousIndex", value="/pages/anonymousUser/forum.xhtml"),
			@WebInitParam(
				name="userIndex",
				value="/pages/registeredUser/profile_me.xhtml"),
			@WebInitParam(
				name="multisessionWarningPage",
				value="/pages/anonymousUser/multisessionWarning.xhtml"),
			@WebInitParam(
				name="adminIndex",
				value="/pages/admin_user/profile_me.xhtml")
		})


public class FilterAuthorizationAdministratorUser implements Filter {

	// Necesitamos acceder a los parametros de inicializacion en
	// el metodo doFilter, asi que necesitamos la variable
	// config que se inicializara en init()
	FilterConfig config = null;

	/**
	 * Default constructor.
	 */
	public FilterAuthorizationAdministratorUser() {
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
		// En el resto de casos se verifica que se haya hecho login previamente
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		if( req.getSession(false)==null ){
			session = req.getSession(true);
		}
		String anonymousIndex = config.getInitParameter("anonymousIndex");
		String userIndex = config.getInitParameter("userIndex");
		String multisessionWarnPage =
				config.getInitParameter("multisessionWarningPage");
		String adminIndex =
				config.getInitParameter("adminIndex");
		
		// Si el usuario esta autenticado, el beanLogin deberia haber escrito
		// en el Map de la sesion la variable "LOGGED_USER" de tipo User
		User loggedUser = (User) session.getAttribute("LOGGED_USER");
		if (loggedUser == null) {
			// Si el usuario no esta autenticado redirecciona a pagina de inicio
			res.sendRedirect(req.getContextPath() + anonymousIndex);
			// COMENTAR EL SIGUIENTE TRY-CATCH Y SU CONTENIDO (NO HACER INVALIDATE AQUI)
			/*
			try{
				if( req.getSession(false)!=null ) session.invalidate();
			}catch (IllegalStateException e){}
			*/
			return;
		}
		if( loggedUser.getRole().equals(User.USER) ){
			// Si usuario es de rol USER, redirecciona a pagina de inicio
			res.sendRedirect(req.getContextPath() + userIndex);
			/*
			// Y elimina la sesion iniciada:
			try{
				if( req.getSession(false)!=null ) session.invalidate();
			}catch (IllegalStateException e){}
			*/
			return;
		}
		if( isDeactivated(loggedUser, req) ){
			// Si esta desactivado...
			// Elimina al usuario de las listas y mapas:
			deleteUserFromMemory(loggedUser, session.getId(), req);
			// Redirecciona a pagina de inicio:
			res.sendRedirect(req.getContextPath() + anonymousIndex);
			// Y elimina la sesion iniciada:
			try{
				if( req.getSession(false)!=null ) session.invalidate();
			}catch (IllegalStateException e){}
			return;
		}
		if( visitOwnUserProfile(req, loggedUser.getId()) ){
			// Redirecciona a pagina de perfil propio:
			res.sendRedirect(req.getContextPath() + adminIndex);
		    return;
		}
		if( userRepeatedlyLogged(loggedUser, req) ){
			// El usuario, tras loguearse, abre un segundo navegador o dispositivo
			// donde vuelve a loguearse con la misma cuenta.
			// El usuario, con el primer navegador o dispositivo, intenta
			// navegar por la pagina: entonces entrara en este condicional:
			// pues se debera anular esta sesion y dejar solo la mas reciente...
			// Elimina al usuario de las listas y mapas:
			deleteUserFromMemory(loggedUser, session.getId(), req);
			// Redirecciona a pagina de aviso de multisesion:
			res.sendRedirect(req.getContextPath() + multisessionWarnPage);
			// Y elimina la sesion iniciada:
			try{
				if( req.getSession(false)!=null ) session.invalidate();
			}catch (IllegalStateException e){}
			return;
		}
		chain.doFilter(request, response);
	}
	
	private boolean isDeactivated(User loggedUser, HttpServletRequest req){
		// Obtener, del contexto Aplicacion, la lista "DEACTIVATED_USERS":
		@SuppressWarnings("unchecked")
		List<Long> listOfUsersIDs = (List<Long>) req.getServletContext()
				.getAttribute("DEACTIVATED_USERS");
		// Si existe la lista "DEACTIVATED_USERS", buscar alli el usuario:
		if( listOfUsersIDs != null ){
			if( listOfUsersIDs.contains(loggedUser.getId()) ){
				// Si encuentra al usuario en la lista "DEACTIVATED_USERS"
				// no se pasa el filtro. Redirecciona a pagina de inicio
				return true;
			}
		}
		return false;
	}
	
	private boolean visitOwnUserProfile(
			HttpServletRequest req, Long loggedUserId){
		// req.getServletPath() = /pages/admin_user/(...).xhtml
		int beginPageNameInUrl = (req.getServletPath().lastIndexOf("/"))+1;
		String pageName = req.getServletPath().substring( beginPageNameInUrl );
		if( pageName.equals("profile_user.xhtml") ){
			if( req.getParameter("user")!=null ){
				Long paramUser = 0L;
				try{
					paramUser = Long.parseLong(req.getParameter("user"));
				}catch( NumberFormatException e ){
					return false;
				}
				if( paramUser==loggedUserId ){ return true; }
			}
		}
		return false;
	}
	
	private boolean userRepeatedlyLogged(User loggedUser,HttpServletRequest req){
		// Obtener, del contexto Aplicacion, el Map "LOGGED_USERS":
		Map<Long, List<HttpSession>> mapLoggedUsers = getMapLoggedUsers(req);
		if(mapLoggedUsers==null || mapLoggedUsers.isEmpty()){return false;}
		// Si existe el Map "LOGGED_USERS",
		// buscar la lista de sesiones abiertas del usuario logueado
		List<HttpSession> userOpenSessions = 
				mapLoggedUsers.get(loggedUser.getId());
		if(userOpenSessions==null || userOpenSessions.isEmpty()){return false;}
		// Si existe la List de sesiones del usuario,
		// descarta la mas reciente y quedate con las demas: 
		// quedate con las sesiones pendientes de invalidar
		List<HttpSession> userPendingToCloseSessions = 
				new ArrayList<HttpSession>(userOpenSessions);
		userPendingToCloseSessions.remove(userPendingToCloseSessions.size()-1);
		// Si entre ellas encuentra la sesion en cuestion no se pasa el filtro:
		
		// lo logico, en vez de este for, seria hacer el return true si...
		// if( userPendingToCloseSessions.contains(req.getSession(false)) )
		// pero no funciona ese 'contains': siempre devuelve false
		for(HttpSession s : userPendingToCloseSessions){
			if( s.getId().equals(req.getSession(false).getId()) ){
				return true;
			}
		}
		return false;
	}
	
	private void deleteUserFromMemory(User loggedUser, String currentSessionID,
			HttpServletRequest req){
		deleteUserFromLoggedUsersMap(loggedUser, currentSessionID, req);
		deleteUserFromDeactivatedUsersList(loggedUser, req);
		//decrementOnlineUsers(loggedUser, req);
	}
	
	private void deleteUserFromLoggedUsersMap(User loggedUser,
			String currentSessionID, HttpServletRequest req) {
		// mapLoggedUsers = Map cuya 'key' es un User.id
		// y cuyo 'value' es su lista de sesiones; si tiene varias sesiones
		// entonces todas salvo la mas reciente estan pendientes de invalidar
		Map<Long, List<HttpSession>> mapLoggedUsers = getMapLoggedUsers(req);
		if( mapLoggedUsers == null ){ return; }
		List<HttpSession> userOpenSessions=mapLoggedUsers.get(loggedUser.getId());
		if( userOpenSessions==null || userOpenSessions.isEmpty() ) return;
		
		// de dicha lista elimina la sesion en cuestion:
		userOpenSessions.removeIf(
				(HttpSession s) -> s.getId().equals(currentSessionID));
		
		if( userOpenSessions.isEmpty() ){
			// si tras aquella eliminacion la lista ha quedado vacia,
			// podemos eliminar la entrada en el Map:
			mapLoggedUsers.remove(loggedUser.getId());
		}else{
			// De todos modos hay que actualizar la lista en el Map:
			mapLoggedUsers.put(loggedUser.getId(), userOpenSessions);
		}
		req.getServletContext().setAttribute("LOGGED_USERS",mapLoggedUsers);
	}
	
	private void deleteUserFromDeactivatedUsersList(
			User loggedUser, HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		List<Long> listOfUsersIDs = (List<Long>) req.getServletContext()
				.getAttribute("DEACTIVATED_USERS");
		if( listOfUsersIDs!=null ){
			listOfUsersIDs.remove(loggedUser.getId());
			req.getServletContext().setAttribute(
					"DEACTIVATED_USERS", listOfUsersIDs);
		}
	}
	/*
	private void decrementOnlineUsers(
			User loggedUser, HttpServletRequest req) {
		Integer onlineUsers = (Integer) req.getServletContext()
				.getAttribute("NUM_LOGGED_USERS");
		if( onlineUsers!=null ){
			req.getServletContext().setAttribute(
					"NUM_LOGGED_USERS", onlineUsers-=1);
		}
	}
	*/
	
	@SuppressWarnings("unchecked")
	private Map<Long, List<HttpSession>> getMapLoggedUsers(
			HttpServletRequest req){
		Map<Long, List<HttpSession>> mapLoggedUsers = 
				(Map<Long, List<HttpSession>>) req.getServletContext()
				.getAttribute("LOGGED_USERS");
		return mapLoggedUsers;
	}
}
