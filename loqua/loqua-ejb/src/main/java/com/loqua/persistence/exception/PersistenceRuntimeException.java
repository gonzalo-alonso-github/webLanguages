package com.loqua.persistence.exception;

/**
 * Se lanza cuando sucede cualquier excepcion
 * de tipo esctructural en la persistencia
 */
@SuppressWarnings("serial")
public class PersistenceRuntimeException extends RuntimeException {
	
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
