package com.loqua.presentation.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.loqua.presentation.logging.LoquaLogger;

/** Maneja un Map&lt;String, String&gt; donde cada par almacena uno de
 * los parametros de la 'query string' de la URL de la peticion HTTP.
 * Esta clase es utilizada por los Filtros para comprobar
 * si todos los parametros son aceptables. */
public class MapQueryString {
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Map&lt;String, String&gt;, que almacena los parametros de la
	 * 'query string' de la peticion HTTP, donde la clave es el nombre del
	 * parametro y el contenido es el valor del mismo */
	private Map<String, String> mapQueryString = null;
	
	/** Constructor de la clase que inicializa el Map {@link #mapQueryString}.
	 * @param req la peticion HTTP necesaria para inicializar el Map indicado */
	public MapQueryString(HttpServletRequest req){
		loadQueryStringMap(req);
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Inicializa el Map&lt;String, String&gt; {@link #mapQueryString}
	 * a partir de la 'query string' perteneciente a la peticion HTML dada
	 * (es decir, a partir de los parametros anexos a la URL de la peticion).
	 * @param req peticion HTML de la cual se extrae la 'query string' con
	 * la que se inicializa el Map
	 */
	private void loadQueryStringMap(HttpServletRequest req) {
		// no conviene utilizar 'request.getParameter("nombre_parametro")',
		// porque si existe un 'ancla' al final de la url lo asociara con
		// el valor del ultimo parametro. Por eso cobra sentido crear este Map.
		mapQueryString = new HashMap<String, String>();
		String parameters = req.getQueryString();
		if( parameters==null || parameters.isEmpty() ) return;
		int anchorIndex = parameters.indexOf("#");
		if( anchorIndex!=-1 ){
			parameters = parameters.substring(0,anchorIndex);
		}
		try{
			String[] parametersArray = parameters.split("&");
			for(int i=0; i<parametersArray.length; i++){
				String[] pair = parametersArray[i].split("=");
				mapQueryString.put(pair[0], pair[1]);
			}
		}catch( Exception e ){
			// este catch evita que una url mal escrita genere aqui una
			// ArrayIndexOutOfBoundsException.
			// ejemplo: .../loqua-web/pages/registeredUser/forum.xhtml?&page=2
			// cuando lo correcto seria:
			// .../loqua-web/pages/registeredUser/forum.xhtml?page=2
			log.error("Unexpected Exception at 'loadQueryStringMap()'. "
					+ "Check if URL request is bad formed: "
					+ req.getRequestURL().toString()+"?"+parameters);
		}
	}

	/**
	 * Comprueba si el Map {@link #mapQueryString} ya ha sido inicializado.
	 * @return
	 * 'true' si el Map no es null ni esta vacio. <br>
	 * 'false' si el Map es null o esta vacio.
	 */
	public boolean loadedQueryStringMap(){
		return ( mapQueryString!=null && mapQueryString.isEmpty()==false );
	}
	
	/**
	 * Encapsula el metodo 'get' del Map {@link #mapQueryString}
	 * @param mapKey clave del Map cuyo valor asociado sera devuelto
	 * @return el valor del Map que esta mapeado a la clave indicada por el
	 * parametro dado, o null si el Map no contiene esa clave
	 */
	public String get(String mapKey){
		return mapQueryString.get(mapKey);
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public Map<String, String> getMapQueryString() {
		return mapQueryString;
	}
	public void setMapQueryString(Map<String, String> mapQueryString) {
		this.mapQueryString = mapQueryString;
	}
}
