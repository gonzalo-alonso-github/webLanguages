package com.loqua.persistence.exception;

/**
 * Representa la RuntimeException que se lanza desde la capa de persistencia
 * cuando sucede fallo interno de la aplicacion en la capa de persistencia
 * Puede ser causada por algun error en el codigo y no es preciso relanzarla
 * en cada capa de abstraccion
 */
public class PersistenceRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public PersistenceRuntimeException() {
		super();
	}

	public PersistenceRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistenceRuntimeException(String message) {
		super(message);
	}

	public PersistenceRuntimeException(Throwable cause) {
		super(cause);
	}
}
