package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.infrastructure.Factories;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsActionLimits;
import com.loqua.presentation.bean.requestBean.BeanActionResult;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar las operaciones
 * relativas al funcionamiento de las sesiones de usuario. Incluye los metodos
 * de inicio y cierre de sesion de usuarios, y maneja las listas de sesiones
 * de usuario que son almacenadas en el contexto de aplicacion.
 * @author Gonzalo
 */
public class BeanLogin implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Direccion de email introducida por el usuario
	 * al intentar iniciar sesion ('loguearse') */
	private String email;
	
	/** Contrase&ntilde;a introducida por el usuario
	 * al intentar iniciar sesion ('loguearse') */
	private String password;
	
	/** Objeto {@link User} correspondiente al usuario que ha iniciado sesion
	 * (que se ha 'logueado'). */
	private User loggedUser;
	
	/** Objeto {@link BeanActionResult} que guarda y muestra el resultado
	 * del intento de inicio de sesion del usuario */
	private BeanActionResult actionLogin;

	/** Inyeccion de dependencia del {@link BeanForum} */
	@ManagedProperty(value="#{beanForum}")
	private BeanForum beanForum;
	
	/** Inyeccion de dependencia del {@link BeanUserData} */
	@ManagedProperty(value="#{beanUserData}")
	private BeanUserData beanUserData;
	
	/** Inyeccion de dependencia del {@link BeanSettingsActionLimits} */
	@ManagedProperty(value="#{beanSettingsActionLimits}")
	private BeanSettingsActionLimits beanSettingsActionLimits;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean.
	 * Inicializa el bean inyectado {@link BeanSettingsActionLimits} */
	@PostConstruct
	public void init() {
		initBeanSettingsActionLimits();
	}
	
	/** Inicializa el objeto {@link BeanSettingsActionLimits} inyectado */
	private void initBeanSettingsActionLimits() {
		// Buscamos el BeanSettingsActionLimits en la sesion.
		beanSettingsActionLimits = null;
		beanSettingsActionLimits = (BeanSettingsActionLimits)
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("beanSettingsActionLimits");
		// si no existe lo creamos e inicializamos:
		if (beanSettingsActionLimits == null) { 
			beanSettingsActionLimits = new BeanSettingsActionLimits();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap()
					.put("beanSettingsActionLimits", beanSettingsActionLimits);
		}
	}
	
	/** Destructor del bean. */
	@PreDestroy
	public void end(){
		close();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Efectua la verificacion del inicio de sesion del usuario
	 * @param beanActionResult el bean que mostrara en la vista
	 * el resultado de la accion
	 * @return
	 * enlace a la pagina de inicio del usuario logueado,
	 * o a la pagina de error si se produce alguna excepcion,
	 * o 'null' si las credenciales introducidas no corresponden a ningun
	 * usuario
	 */
	public String verify(BeanActionResult beanActionResult) {
		init();
		actionLogin = beanActionResult;
		cleanAllSessionScopedBeansUsedBeforeLogin();
		String result = "";
		try {
			if( ! verifyLimitLoginFails() ) return null;
			loggedUser = Factories.getService().getServiceUser()
					.getUserToLogin(email, password);
			if(loggedUser==null){
				Factories.getService().getServiceUser().incrementLoginFails(email);
				actionLogin.setMsgActionResult("errorLoginBadCredentials");
				result = null;
			}else{
				result = resultVerify();
			}
		} catch (Exception e) {
			log.error("Unexpected Exception at 'verify()'");
			result = "errorUnexpectedAnonymous";
		}
		cleanAllSessionScopedBeansUsedBeforeLogin();
		return result;
	}

	/**
	 * Comprueba si el usuario tiene permiso para iniciar sesion,
	 * segun el numero de intentos de inicio de sesion fallidos
	 * con el mismo 'email'.
	 * @return
	 * 'true' si aun no se ha alcanzado el numero de intentos de inicio
	 * de sesion fallidos con el mismo 'email' <br>
	 * 'false' si se alcanzo el numero de intentos de inicio
	 * de sesion fallidos con el mismo 'email'
	 */
	private boolean verifyLimitLoginFails() {
		boolean result = true;
		Integer numFails = Factories.getService().getServiceUser()
					.getNumLoginFails(email);
		// eso obtiene el numFailedLogins del User de ese email y con removed a false
		if( numFails==null ){
			actionLogin.setMsgActionResult("errorLoginBadCredentials");
			return false;
		}
		Map<String, Integer> mapActionsLimits = 
				beanSettingsActionLimits.getMapActionLimitsProperties();
		Integer limitFailedLogins = mapActionsLimits.get("limitFailedLogins");
		if( numFails >= limitFailedLogins ){
			actionLogin.setMsgActionResult("errorLoginNumFails");
			result = false;
		}
		return result;
	}
	
	/**
	 * Comprueba si el usuario tiene permiso para iniciar sesion,
	 * segun el estado (propiedades {@link User#active} y
	 * {@link User#locked}) al que corresponde el 'email' introducido.
	 * @return
	 * 'true' si el usuario es de tipo Administrador, o bien no esta
	 * desactivado ni bloqueado <br>
	 * 'false' 'true' si el usuario no es de tipo Administrador, y ademas esta
	 * desactivado o bloqueado
	 * @throws EntityNotFoundException
	 */
	private String resultVerify() throws EntityNotFoundException {
		String result = "errorUnexpectedAnonymous";
		if( loggedUser.getRole().equals(User.ADMINISTRATOR) ){
			// Si el usuario encontrado es administrador,
			// no se comprueba nada mas:
			Factories.getService().getServiceUser().resetLoginFails(loggedUser);
			result = "successLoginAdmin";
		}else /*if( loggedUser.getRole().equals(User.USER) )*/{
			// Si el usuario encontrado no es administrador,
			// se comprueba su estado de "activo" y "bloqueado":
			if( loggedUser.getActive()==false ){
				actionLogin.setMsgActionResult("errorLoginDeactivatedUser");
				return null;
			}
			if( loggedUser.getLocked()==true ){
				actionLogin.setMsgActionResult("errorLoginLockedUser");
				return null;
			}
			Factories.getService().getServiceUser().resetLoginFails(loggedUser);
			result = "successLoginUser";
		}
		filterIfAlreadyLogged();
		putUserInSessionAndAppContext();
		return result;
	}
	
	/**
	 * Guarda en el contexto de aplicacion al usuario que, 
	 * tras haber iniciado sesion, vuelve a iniciar sesion
	 * con las mismas credenciales (puede suceder cuando accede desde varios
	 * navegadores o dispositivos distintos).<br>
	 * Los filtros de autorizacion se encargaran de finalizar la sesion antigua,
	 * gracias a la informacion que este metodo almacena en el contexto.
	 */
	private void filterIfAlreadyLogged() {
		Map<Long, List<HttpSession>> mapLoggedUsers = getMapLoggedUsers();
		List<HttpSession> previousSessions=mapLoggedUsers.get(loggedUser.getId());
		// Si el usuario no estaba ya 'logueado', salir del metodo
		if( previousSessions==null || previousSessions.isEmpty()){ return; }
		
		String currentSessionID = ((HttpSession)FacesContext
				.getCurrentInstance().getExternalContext().getSession(false))
				.getId();
		// Si la sesion iniciada anterior es la misma que esta, salir del metodo
		if( previousSessions.contains(currentSessionID) ){ return; }
	}
	
	/**
	 * Guarda en el contexto de sesion el usuario que inicia sesion con exito
	 * (variable 'LOGGED_USER'). <br>
	 * Tambien lo guarda en el contexto de aplicacion (en el Map 'LOGGED_USERS',
	 * si no lo estaba ya) y ademas actualiza el numero de usuarios conectados.
	 */
	private void putUserInSessionAndAppContext() {
		Map<String, Object> session = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		session.put("LOGGED_USER", loggedUser);
		if( ! userAlreadyLogged() ){
			incrementOnlineUsersInAppContext();
		}
		addLoggedUserInAppContext();
	}

	/**
	 * Incrementa el numero de usuarios con sesion iniciada en la aplicacion.
	 */
	private void incrementOnlineUsersInAppContext() {
		// Incrementa el numero de usuarios "NUM_LOGGED_USERS"
		// (una variable de contexto de aplicacion)
		Map<String, Object> application = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
		int onlineUsers = 0;
		if( application.containsKey("NUM_LOGGED_USERS") ){
			onlineUsers = (Integer)application.get("NUM_LOGGED_USERS");
		}
		application.put("NUM_LOGGED_USERS", onlineUsers+=1);
	}
	
	/**
	 * Agrega el usuario recien conectado a la lista de contexto de aplicacion,
	 * esto es, el Map 'LOGGED_USERS'.
	 */
	private void addLoggedUserInAppContext(){
		// Agrega el usuario conectado aL Map "LOGGED_USERS"
		// (una variable de contexto de aplicacion)
		Map<String, Object> application = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		Map<Long, List<HttpSession>> mapLoggedUsers = getMapLoggedUsers();
		List<HttpSession> previousSessions=mapLoggedUsers.get(loggedUser.getId());
		if(previousSessions==null) previousSessions=new ArrayList<HttpSession>();
		previousSessions.add(session);
		mapLoggedUsers.put(loggedUser.getId(), previousSessions);
		application.put("LOGGED_USERS", mapLoggedUsers);
	}
	
	/** Decrementa el numero de usuarios con sesion iniciada, almacenado en
	 * la variable 'NUM_LOGGED_USERS' de contexto de aplicacion. */
	private void decrementOnlineUsers() {
		if( loggedUser==null ) return;
		Map<String, Object> application = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
		// Primero decrementa el numero de usuarios "NUM_LOGGED_USERS"
		// (una variable de contexto de aplicacion)
		int onlineUsers = 1;
		if( application.containsKey("NUM_LOGGED_USERS") ){
			onlineUsers = (Integer)application.get("NUM_LOGGED_USERS");
		}
		application.put("NUM_LOGGED_USERS", onlineUsers-=1);
		
		// Despues elimina el usuario conectado de la lista "LOGGED_USERS"
		Map<Long, List<HttpSession>> mapLoggedUsers = getMapLoggedUsers();
		mapLoggedUsers.remove(loggedUser.getId());
		application.put("LOGGED_USERS", mapLoggedUsers);
	}
	
	/**
	 * Comprueba si el usuario tiene una sesion iniciada; esto es,
	 * si se encuentra guardado en la variable 'LOGGED_USERS'
	 * de contexto de aplicacion
	 * @return
	 * 'true' si el usuario tiene una sesion iniciada. <br>
	 * 'false' si el usuario no tiene una sesion iniciada.
	 */
	private boolean userAlreadyLogged() {
		return getMapLoggedUsers().containsKey(loggedUser.getId());
	}
	
	/**
	 * Halla del contexto de aplicacion todos los usuarios con sesion iniciada.
	 * @return un Map donde la clave es el atributo 'id' del objeto User,
	 * y donde el valor es la lista de sesiones que tiene abiertas.
	 */
	@SuppressWarnings("unchecked")
	private Map<Long, List<HttpSession>> getMapLoggedUsers(){
		Map<String, Object> application = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
		Map<Long,List<HttpSession>> mapLoggedUsers=
				new HashMap<Long,List<HttpSession>>();
		if( application.containsKey("LOGGED_USERS") ){
			mapLoggedUsers = (Map<Long, List<HttpSession>>)
					application.get("LOGGED_USERS");
		}
		return mapLoggedUsers;
	}
	
	/**
	 * Efectua el cierre de sesion de un usuario
	 * @return
	 * enlace a la pagina principal del foro para usuarios anonimos,
	 * o a la pagina de error si se produce alguna excepcion
	 */
	public String close() {
		decrementOnlineUsers();
		email = password = null;
		loggedUser = null;
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		sessionMap.clear();
		ExternalContext ec = 
				FacesContext.getCurrentInstance().getExternalContext();
	    ec.invalidateSession();
	    return "successLogout";
	}
	
	/**
	 * Elimina de la memoria todos los beans de sesion inicializados
	 * antes del inicio de sesion del usuario. De esta manera se evita
	 * que dichos Beans mantengan informacion ajena a la sesion actual.
	 */
	private void cleanAllSessionScopedBeansUsedBeforeLogin(){
		beanForum = (BeanForum)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanForum");
		if (beanForum != null) { 
			beanForum.end();
		}
		
		beanUserData = (BeanUserData)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanUserData");
		if (beanUserData != null) {
			beanUserData.end();
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public User getLoggedUser() {
		return loggedUser;
	}
	public User getLoggedUserAsCopy() {
		User copyLoggedUser = new User(loggedUser);
		return copyLoggedUser;
	}
	public void setLoggedUser(User logged) {
		this.loggedUser = logged;
	}
}
