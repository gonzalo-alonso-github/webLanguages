package com.loqua.presentation.bean.applicationBean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.loqua.model.User;

/**
 * Bean de ambito de aplicacion que contiene metodos pueden ser accedidos
 * desde cualquier pagina del sitio en cualquier sesion de usuario.
 * Generalmente son metodos necesarios para facilitar el codigo xhtml
 * de las vistas.
 * @author Gonzalo
 */
public class BeanUtils implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. */
	public BeanUtils() {}

	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Recorta una cadena de texto limitando su longitud al numero de
	 * caracteres indicado.<br/>
	 * El efecto generado por este metodo sobre una cadena de texto en HTML
	 * equivale a aplicar sobre ella el siguiente estilo CSS:
	 * style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis;".
	 * Este metodo es necesario porque hay ocasiones en que dicho estilo CSS
	 * no genera efecto: eso sucede cuando se intenta aplicarlo sobre
	 * una expresion EL de JSF. Un ejemplo en xhtml:
	 * <br/>El texto de la 'variableEL' se vera recortado,
	 * que es la forma esperada:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;#{ beanUtils.limitTextLength( 10, variableEL ) }
	 * <br/>El texto de la 'variableEL' se vera completo,
	 * que es la forma no deseada:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&ltdiv style="white-space:nowrap; overflow:hidden;
	 * text-overflow:ellipsis;"&gt #{variableEL} &lt/div&gt
	 * @param limit limite maximo de caracteres permitidos en el texto dado,
	 * contando espacios en blanco
	 * @param text texto que se va a recortar
	 * @return todos los primeros caracteres del texto
	 * hasta incluir la posicion indicada por limit
	 */
	public String limitTextLength(int limit, String text){
		if( text.length()>limit ){
			text = text.substring(0, limit);
			text += "...";
		}
		return text;
	}
	
	/**
	 * Recorta el texto dado para hallar la parte previa a la aparicion del
	 * carater '?1'. Este metodo es empleado por ejemplo en
	 * BeanPublication.getUserPublicationPart1,
	 * que envia por parametro uno de los valores predefinidos en los ficheros
	 * 'events_X.properties'. De forma que puede darse el siguiente ejemplo:
	 * dado el texto<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"has alcanzado los ?1 comentarios"<br/>
	 * entonces este metodo devolvera:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"has alcanzado los"<br/>
	 * @param textToPart Texto que se recorta
	 * @return Texto previo a la aparicion del caracter '?1'
	 */
	public static String getTextPart1(String textToPart){
		int indexOfValue = textToPart.indexOf("?1");
		if( indexOfValue>0 ){
			textToPart = textToPart.substring( 0,indexOfValue-1 );
		}else{
			textToPart = "";
		}
		return textToPart;
	}
	
	/**
	 * Recorta el texto dado para hallar la parte posterior a la aparicion del
	 * carater '?1'. Este metodo es empleado por ejemplo en
	 * BeanPublication.getUserPublicationPart2,
	 * que envia por parametro uno de los valores predefinidos en los ficheros
	 * 'events_X.properties'. De forma que puede darse el siguiente ejemplo:
	 * dado el texto<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"has alcanzado los ?1 comentarios"<br/>
	 * entonces este metodo devolvera:<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"comentarios"<br/>
	 * @param textToPart Texto que se recorta
	 * @return Texto posterior a la aparicion del caracter '?1'
	 */
	public static String getTextPart2(String textToPart){
		int indexOfValue = textToPart.indexOf("?1");
		if( indexOfValue>=0 ){
			textToPart = textToPart.substring(
					indexOfValue+2, textToPart.length());
		}
		return textToPart;
	}
	
	/**
	 * Dado un valor entero y un limite maximo para dicho valor, comprueba si
	 * el valor supera el limite. En tal caso el resultado devuelto sera dicho
	 * limite; de lo contrario se devolvera el valor dado.
	 * @param limit limite maximo
	 * @param value valor que se comprueba
	 * @return Si value <= limit, devuelve el 'value' en formato String.<br/>
	 * Si value > limit, devuelve el 'limit' precedido del signo '&gt;'
	 */
	public String limitNumericValue(int limit, int value){
		if( value<=limit ){
			return String.valueOf(value);
		}else{
			return "> ".concat(String.valueOf(limit));
		}
	}
	
	/**
	 * Halla la URL del directorio donde se ubican todas las paginas
	 * correspondientes al tipo de usuario de aquel que maneja esta sesion,
	 * sea anonimo, registrado, o administrador.
	 * @return la URL obtenida
	 */
	public static String getUrlUserPages(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest)
				facesContext.getExternalContext().getRequest();
		User loggedUser = getLoggedUser();
		String pages= getPathForUserRole(loggedUser);
		String currentUrl = req.getRequestURL().toString();
		String url = currentUrl.substring(
					0,currentUrl.length()-req.getRequestURI().length())
				+ req.getContextPath() + "/pages/" + pages + "/";
		return url;
	}
	
	/*
	public static String getUrnUserPages(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest)
				facesContext.getExternalContext().getRequest();
		User loggedUser = getLoggedUser();
		String pages= getPathForUserRole(loggedUser);
		String url = req.getContextPath() + "/pages/" + pages + "/";
		return url;
	}
	*/
	
	/**
	 * Halla el nombre del directorio en el que se ubican las paginas
	 * correspondientes al tipo del usuario dado,
	 * sea anonimo, registrado, o administrador
	 * @param loggedUser usuario que se comprueba
	 * @return el nombre obtenido
	 */
	private static String getPathForUserRole(User loggedUser) {
		String pages;
		if( loggedUser==null ){
			pages="anonymousUser";
		}else if( loggedUser.getRole().equals(User.ADMINISTRATOR) ){
			pages="admin_user";
		}else{
			pages="registeredUser";
		}
		return pages;
	}
	
	/**
	 * Busca en el Map&lt;String,Object&gt; de la sesion el usuario logueado.
	 * <br/>Para obtener el usuario logueado evita utilizar a {@link BeanLogin},
	 * que es de ambito de sesion y por tanto no conviente invocarlo desde
	 * este BeanUtils, que es de ambito de aplicacion.
	 * @return
	 * el usuario que ha iniciado esta sesion.
	 * Si no la ha iniciado, devuelve null.
	 */
	public static User getLoggedUser(){
		Map<String, Object> session = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		User loggedUser = (User) session.get("LOGGED_USER");
		return loggedUser;
	}
	
	/**
	 * Halla el valor de la variable 'NUM_LOGGED_USERS' guardada en el
	 * contexto de aplicacion.
	 * @return el numero de usuarios con sesion iniciada
	 */
	public int getNumOnlineUsers(){
		Map<String, Object> application = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
		int result = 0;
		if( application.containsKey("NUM_LOGGED_USERS") ){
			result = (Integer)application.get("NUM_LOGGED_USERS");
		}
		return result;
	}
}