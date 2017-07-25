package com.loqua.presentation.util;

import javax.servlet.http.HttpServletRequest;

public abstract class VerifierAjaxRequest {

	public static boolean isAJAXRequest(HttpServletRequest req) {
		boolean result = false;
		String facesRequest = req.getHeader("Faces-Request");
		if (facesRequest != null && facesRequest.equals("partial/ajax")) {
			result = true;
		}
		return result;
	}
}
