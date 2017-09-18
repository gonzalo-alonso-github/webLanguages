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
 * Define el filtro que se aplica sobre todas las paginas exclusivas
 * de usuarios registrados no administradores.
 * Este filtro impide que ningun usuario de otro tipo acceda a dichas paginas.
 * <br/>
 * El ciclo de JSF es interceptado por el Filtro antes de que el navegador
 * muestre la pagina sobre la que este se aplica, y se ejecuta inmediatamene
 * despues de los manejadores de navegacion (NavigationHandler) y de vista
 * (ViewHandler). <br/>
 * Puesto que se definen varios filtros sobre las mismas paginas, es coveniente
 * indicar, en el fichero web.xml, el orden en que se aplican.
 * @author Gonzalo
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/pages/registeredUser/*" },
		initParams = { 
			@WebInitParam(
				name="anonymousIndex",
				value="/pages/anonymousUser/forum.xhtml"),
			@WebInitParam(
				name="adminIndex",
				value="/pages/admin_user/profile_me.xhtml"),
			@WebInitParam(
				name="multisessionWarningPage",
				value="/pages/anonymousUser/multisessionWarning.xhtml"),
			@WebInitParam(
				name="userIndex",
				value="/pages/registeredUser/profile_me.xhtml")
		})
public class FilterAuthorizationRegisteredUser implements Filter {

	/** Se utliza para acceder a los parametros de inicializacion
	 * definidos en las anotaciones de esta clase */
	FilterConfig config = null;

	/** Constructor sin parametros de la clase */
	public FilterAuthorizationRegisteredUser() {}

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
		// En el resto de casos se verifica que se haya hecho login previamente
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		if( req.getSession(false)==null ){
			session = req.getSession(true);
		}
		String anonymousIndex = config.getInitParameter("anonymousIndex");
		String adminIndex = config.getInitParameter("adminIndex");
		String multisessionWarnPage =
				config.getInitParameter("multisessionWarningPage");
		String userIndex =
				config.getInitParameter("userIndex");
		
		// Si el usuario esta autenticado, el beanLogin deberia haber escrito
		// en el Map de la sesion la variable "LOGGED_USER" de tipo User
		User loggedUser = (User) session.getAttribute("LOGGED_USER");
		
		if( loggedUser == null ){
			// Si el usuario no esta autenticado redirecciona a pagina de inicio
			res.sendRedirect(req.getContextPath() + anonymousIndex);
			return;
		}
		if( loggedUser.getRole().equals(User.ADMINISTRATOR) ){
			// Si usuario es de rol ADMIN, redirecciona a pagina de inicio
			res.sendRedirect(req.getContextPath() + adminIndex);
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
			res.sendRedirect(req.getContextPath() + userIndex);
		    return;
		}
		if( userRepeatedlyLogged(loggedUser, req) ){
			/* El usuario, tras loguearse,
			abre un segundo navegador o dispositivo
			donde vuelve a loguearse con la misma cuenta.
			El usuario, con el primer navegador o dispositivo, intenta
			navegar por la pagina: entonces entrara en este condicional:
			pues se debera anular esta sesion y dejar solo la mas reciente: */
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
	
	/**
	 * Comprueba si el usuario ha sido desactivado mientras tenia
	 * una sesion iniciada. En el momento en que eso sucede, el
	 * {@link BeanUserEditProfile} guarda en la lista 'DEACTIVATED_USERS'
	 * (almacenada en el contexto de aplicacion) el identificador del usuario.
	 * Por tanto este metodo accede a dicha lista para realizar
	 * la comprobacion.
	 * @param loggedUser usuario que se verifica
	 * @param req peticion HTTP
	 * @return
	 * 'true' si el usuario esta desactivado <br/>
	 * 'false' si el usuario no esta desactivado
	 */
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
	
	/**
	 * Comprueba si la pagina de perfil que visita el usuario es la suya propia,
	 * o si es el perfil de otro usuario.
	 * @param loggedUser usuario que se verifica
	 * @param req peticion HTTP
	 * @return
	 * 'true' si el usuario esta visitando su propio perfil <br/>
	 * 'false' si el usuario esta visitando un perfil ajeno
	 */
	private boolean visitOwnUserProfile(
			HttpServletRequest req, Long loggedUserId){
		// req.getServletPath() = /pages/registeredUser/(...).xhtml
		int beginPageNameInUrl = req.getServletPath().lastIndexOf("/")+1;
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
	
	/**
	 * Comprueba el usuario ya tenia iniciada una sesion en otro navegador 
	 * o dispositivo
	 * @param loggedUser usuario que se verifica
	 * @param req peticion HTTP
	 * @return
	 * 'true' si el usuario ya tenia una sesion iniciada <br/>
	 * 'false' si el usuario no tiene otra sesion iniciada
	 */
	private boolean userRepeatedlyLogged(User loggedUser,
			HttpServletRequest req){
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
		
		// if( userPendingToCloseSessions.contains(req.getSession(false)) )
		// siempre devuelve false, por tanto hay que comprobarlo con un bucle:
		for(HttpSession s : userPendingToCloseSessions){
			if( s.getId().equals(req.getSession(false).getId()) ){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Elimina todo rastro de la sesion del usuario en las variables del
	 * contexto de la aplicacion
	 * @param loggedUser usuario cuyos datos se eliminan
	 * @param currentSessionID sesion actual del usuario
	 * @param req peticion HTTP
	 */
	private void deleteUserFromMemory(User loggedUser, String currentSessionID,
			HttpServletRequest req){
		deleteUserFromLoggedUsersMap(loggedUser, currentSessionID, req);
		deleteUserFromDeactivatedUsersList(loggedUser, req);
		//decrementOnlineUsers(loggedUser, req);
	}
	
	/**
	 * Elimina los datos guardados del usuario en la variable de contexto
	 * 'LOGGED_USERS', que es un Map cuya clave es el identificador del usuario
	 * y cuyo 'value' es su lista de sesiones
	 * @param loggedUser usuario cuyos datos se eliminan
	 * @param currentSessionID sesion actual del usuario
	 * @param req peticion HTTP
	 */
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
	
	/**
	 * Elimina los datos guardados del usuario en la variable de contexto
	 * 'DEACTIVATED_USERS', que es una lista donde se almacenan los usuarios
	 * que, con su sesion iniciada, han sido desactivados
	 * @param loggedUser usuario cuyos datos se eliminan
	 * @param req peticion HTTP
	 */
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
	 * Decrementa el valor de la variable de contexto
	 * 'NUM_LOGGED_USERS', que indica e numero de usuarios con sesion iniciada
	 * @param loggedUser usuario cuyos datos se eliminan
	 * @param currentSessionID sesion actual del usuario
	 * @param req peticion HTTP
	private void decrementOnlineUsers(HttpServletRequest req) {
		Integer onlineUsers = (Integer) req.getServletContext()
				.getAttribute("NUM_LOGGED_USERS");
		if( onlineUsers!=null ){
			req.getServletContext().setAttribute(
					"NUM_LOGGED_USERS", onlineUsers-=1);
		}
	}
	*/
	
	/**
	 * Halla del contexto de aplicacion el Map que almacena todas las sesiones
	 * de usuarios activas
	 * @param req peticion HTTP
	 * @return un Map cuya clave es el identificador del usuario y cuyo valor
	 * es la lista de sesiones que tiene iniciadas
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
