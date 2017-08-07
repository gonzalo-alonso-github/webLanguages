package com.loqua.remote;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.xml.bind.DatatypeConverter;

public class FilterAddAuthHeadersRequest implements ClientRequestFilter {

	private final String password;

	public FilterAddAuthHeadersRequest(String password) {
		this.password = password;
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		String base64Pass = DatatypeConverter
				.printBase64Binary(password.getBytes("UTF-8"));
		/* Decido descartar el tipo de autenticacion Basic:
		una autenticacion Basic requiere editar el fichero 'standalone',
		cosa que a la postre demandara ciertos conocimientos de JBoss y Wildfly
		que prefiero no implicar en este proyecto.
		Se opta por realizar la autenticacion en el FilterREST del servidor */
		requestContext.getHeaders().add("NotStandard-Filter", base64Pass);
	}
}
