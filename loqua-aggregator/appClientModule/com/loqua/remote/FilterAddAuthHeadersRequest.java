package com.loqua.remote;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.xml.bind.DatatypeConverter;

/**
 * Se encarga de interceptar las peticiones HTTP salientes
 * antes de ser enviadas, con el fin de agregar a los paquetes HTTP
 * una cabecera ('header') que contenga la contrase&ntilde;a necesaria
 * para que la peticion sea aceptada por el filtro 'FilterRESTServices'
 * del servidor REST remoto (implementado en el proyecto 'loqua-web').
 * @author Gonzalo
 */
public class FilterAddAuthHeadersRequest implements ClientRequestFilter {

	/** Contrase&ntilde;a, cifrada con hash, que servira para que
	 * la peticion REST pase el filtro de autenticacion del servidor
	 * (implementado en la clase 'FilterRESTServices' del proyecto 'loqua-web').
	 */
	private final String password;

	/** Constructor que inicializa los atributos de la clase.
	 * @param password valor para inicializar el atributo {@link #password}
	 */
	public FilterAddAuthHeadersRequest(String password) {
		this.password = password;
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		/* Se descarta el tipo de autenticacion Basic:
		una autenticacion Basic requiere editar el fichero 'standalone',
		cosa que a la postre demandara ciertos conocimientos de JBoss y Wildfly
		que resulta preferible no implicar en este proyecto.
		Se opta por realizar la autenticacion a traves del filtro
		'FilterRESTServices' del servidor (en el proyecto 'loqua-web') */
		String base64Pass = DatatypeConverter
				.printBase64Binary(password.getBytes("UTF-8"));
		requestContext.getHeaders().add("NotStandard-Filter", base64Pass);
	}
}
