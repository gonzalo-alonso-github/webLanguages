package com.loqua.business.exception;

/**
 * Representa la RuntimeException que se lanza desde la capa de negocio
 * cuando sucede fallo interno de la aplicacion en las capas de negocio
 * o de persistencia.
 * Puede ser causada por algun error en el codigo y no es preciso relanzarla
 * en cada capa de abstraccion
 */
public class BusinessRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
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
