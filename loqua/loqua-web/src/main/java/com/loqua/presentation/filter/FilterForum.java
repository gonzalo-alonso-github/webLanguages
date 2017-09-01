package com.loqua.presentation.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedProperty;
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

import com.loqua.infrastructure.Factories;
import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.presentation.bean.BeanUserData;
import com.loqua.presentation.bean.applicationBean.BeanSettingsForumPage;
import com.loqua.presentation.logging.LoquaLogger;
import com.loqua.presentation.util.MapQueryString;
import com.loqua.presentation.util.VerifierAjaxRequest;

/**
 * Este filtro se aplica a la pagina de 'forum.xhtml'
 * (tanto para usuarios anonimos como para registrados y administradores).
 * Por tanto existen dos filtros para dicha pagina: el 'FilterAuthorization...'
 * y este. Y el orden en que se aplican esta definido en el fichero de despliegue
 * web.xml.
 */
@WebFilter(
		dispatcherTypes = { DispatcherType.REQUEST },
		urlPatterns = { "/pages/anonymousUser/forum.xhtml",
				"/pages/registeredUser/forum.xhtml",
				"/pages/admin_user/forum.xhtml"},
		initParams = { 
			@WebInitParam(
				name="errorAnonymous",
				value="/pages/anonymousUser/forum.xhtml"),
			@WebInitParam(
				name="errorRegistered",
				value="/pages/registeredUser/forum.xhtml"),
			@WebInitParam(
				name="errorAdmin",
				value="/pages/admin_user/forum.xhtml")
		})


public class FilterForum implements Filter {

	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	// Necesitamos acceder a los parametros de inicializacion en
	// el metodo doFilter, asi que necesitamos la variable
	// config que se inicializara en init()
	FilterConfig config = null;
	
	Long requestedCategory = 0L;
	int requestedPage = 0;
	MapQueryString queryStringMap;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanUserData}")
	private BeanUserData beanUserData;

	/**
	 * Default constructor.
	 */
	public FilterForum() {
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
	
	private void initBeanUser(HttpSession session) {
		// Buscamos el beanUserData en la sesion.
		beanUserData = null;
		beanUserData = (BeanUserData)session.getAttribute("beanUserData");
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
		initBeanUser(session);
		
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
		if( queryStringMap.loadedQueryStringMap()
				&& verifyParameters(req)==false ){
			// A diferencia del 'FilterForumThread',
			// si la URL tiene queryString y sus parametros no son correctos,
			// redirecciona (no se pasa el filtro):
			res.sendRedirect(req.getContextPath() + errorPage);
		    return;
		}
		chain.doFilter(request, response);
	}
	
	/**
	 * Comprueba que la url de la peticion a la pagina 'forum.xhtml'
	 * contiene el parametro 'page' (pagina del 'hilo' que se va a consultar).
	 * @return 'true' si el parametro de la url es correcto
	 */
	private boolean verifyParameters(HttpServletRequest req){
		try{
			if( !verifyRequestedCategory(req) ) return false;
			return verifyRequestedPage(req);
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
	private boolean verifyRequestedCategory(HttpServletRequest req){
		try{
			String requestedCategoryParam = queryStringMap.get("category");
			if( requestedCategoryParam==null ){
				requestedCategory=null;
				return true;
			}
			requestedCategory = Long.parseLong( requestedCategoryParam );
			// a diferencia del parametro 'page', este no admite el valor 0
			// ni tampoco cualquier valor < 0
			if( requestedCategory<=0L ){ return false; }
			if( getAllFeedCategoriesIds().contains(requestedCategory)==false ){
				return false;
			}
			return true;
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
	private List<Long> getAllFeedCategoriesIds(){
		List<Long> result = new ArrayList<Long>();
		try{
			result = Factories.getService().getServiceFeed()
					.getAllFeedCategoriesIdsFromDB(); //FromCache();
		}catch( Exception e ){
			log.error("Unexpected Exception at 'getAllFeedCategoriesIds()'");
		}
		return result;
	}
	
	private boolean verifyRequestedPage(HttpServletRequest req){
		try{
			String requestedPageParam = queryStringMap.get("page");
			if( requestedPageParam==null ){ return true; }
			requestedPage = Integer.parseInt( requestedPageParam );
			// la pagina 0 del foro es la misma que la 1 (se permite el valor 0)
			// pero obviamente si ese valor es < 0, no pasa el filtro:
			if( requestedPage<0 ){ return false; }
			
			int totalNumThreads = loadTotalNumThreads();
			Integer numberOfListElementsPerPage = 
					BeanSettingsForumPage.getNumNewsPerPageStatic();
			int sizePaginationBar = ((int)Math.ceil(
					(float)totalNumThreads/numberOfListElementsPerPage));
			
			// si se pretende entrar en una pagina que no existe, no pasa filtro:
			if( requestedPage > sizePaginationBar ){ return false; }
			return true;
		} catch( NumberFormatException nfe ){
			return false;
		}
	}
	
	private int loadTotalNumThreads() {
		int totalNumThreads = 0;
		try{
			List<Language> listLanguages = new ArrayList<Language>();
			if( beanUserData!=null ){
				listLanguages = beanUserData.getListUserLanguages();
			}
			if( listLanguages.isEmpty()==false ){
				// si no esta vacia, obtener las noticias mas recientes,
				// de dichos idiomas, de la categoria elegida
				totalNumThreads = Factories.getService().getServiceThread()
						.getNumThreadsByLanguagesAndCategoryFromDB(
								listLanguages, requestedCategory); //FromCache
			}else{
				// si esta vacia, obtener las noticias mas recientes,
				// de cualquier idioma, de la categoria elegida
				totalNumThreads = Factories.getService().getServiceThread()
						.getNumThreadsByCategoryFromDB(
								requestedCategory); //FromCache
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at 'loadTotalNumThreads()'");
		}
		return totalNumThreads;
	}
}
