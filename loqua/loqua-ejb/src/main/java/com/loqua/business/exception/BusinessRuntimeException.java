package com.loqua.business.exception;

@SuppressWarnings("serial")
/**
 * Se lanza cuando sucede cualquier excepcion
 * de indole general en la capa de negocio
 */
public class BusinessRuntimeException extends RuntimeException {

	public BusinessRuntimeException() {
		super();
	}

	public BusinessRuntimeException(String message) {
		super(message);
	}

	public BusinessRuntimeException(Throwable cause) {
		super(cause);
	}

	public BusinessRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
