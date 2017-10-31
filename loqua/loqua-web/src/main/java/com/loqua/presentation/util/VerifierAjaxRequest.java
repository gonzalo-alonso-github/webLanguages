package com.loqua.presentation.util;

import javax.servlet.http.HttpServletRequest;

/** Provee la logica necesaria para comprobar si una peticion HTTP
 * es una peticion Ajax. */
public abstract class VerifierAjaxRequest {

	/**
	 * Comprueba si la peticion HTTP indicada es una peticion Ajax.
	 * @param req peticion HTTP que se comprueba
	 * @return
	 * 'true' si la peticion HTTP es una peticion Ajax. <br>
	 * 'false' si no es una peticion Ajax.
	 */
	public static boolean isAJAXRequest(HttpServletRequest req) {
		boolean result = false;
		String facesRequest = req.getHeader("Faces-Request");
		if (facesRequest != null && facesRequest.equals("partial/ajax")) {
			result = true;
		}
		return result;
	}
}
