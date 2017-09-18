package com.loqua.remote;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.loqua.remote.services.RestServiceFeed;
import com.loqua.remote.services.RestServiceForumThread;

/** Representa el conjunto de servicios REST remotos que son consumidos
 * por esta aplicacion cliente. Crea y prepara los objetos que invocan
 * las peticiones HTTP destinadas a dichos servicios. */
public class RestTarget {

	/** Objeto utilizado para invocar peticiones HTTP a la interfaz
	 * ServiceForumThread del servidor REST
	 * (implementada en el proyecto 'loqua-web') */
	private static RestServiceForumThread serviceForumThread;
	/** Objeto utilizado para invocar peticiones HTTP a la interfaz
	 * ServiceForumThread del servidor REST
	 * (implementada en el proyecto 'loqua-web') */
	private static RestServiceFeed serviceFeed;

	// // // //
	// METODOS
	// // // //
	
	/**
	 * Inicializa los objetos {@link #serviceForumThread} y {@link #serviceFeed}
	 * que invocaran las peticiones HTTP hacia los servicios remotos REST.<br/>
	 * Utiliza la API de JAX-RS 2.0 para convertir las interfaces
	 * de los RestService (ver {@link RestServiceForumThread} y
	 * {@link RestServiceForumThread}) en objetos capaces de invocar
	 * peticiones HTTP.
	 * @param url direccion URL donde se encuentran los servicios REST remotos
	 * @param key contrase&ntilde;a, cifrada con hash, que servira para que
	 * la peticion REST pase el filtro de autenticacion del servidor
	 * (implementado en la clase 'FilterRESTServices' del proyecto 'loqua-web')
	 */
	public static void initializeRestServices(String url, String key) {
		Client client = ClientBuilder.newClient();
		ResteasyWebTarget target = (ResteasyWebTarget) client.target(url);
		client.register(new FilterAddAuthHeadersRequest(key));
		serviceForumThread = target.proxy(RestServiceForumThread.class);
		serviceFeed = target.proxy(RestServiceFeed.class);
	}
	
	// // // //
	// GETTERS
	// // // //
	
	/** Devuelve la instancia estatica del objeto {@link #serviceForumThread} */
	public static RestServiceForumThread getServiceForumThreadStatic() {
		return serviceForumThread;
	}

	/** Devuelve la instancia estatica del objeto {@link #serviceFeed} */
	public static RestServiceFeed getServiceFeedStatic() {
		return serviceFeed;
	}
}
