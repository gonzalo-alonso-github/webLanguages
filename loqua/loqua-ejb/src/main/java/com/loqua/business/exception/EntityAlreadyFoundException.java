package com.loqua.business.exception;

/**
 * Se lanza cuando se intenta obtener una entidad
 * que ya existe en el sistema de persistencia
 */
@SuppressWarnings("serial")
public class EntityAlreadyFoundException extends Exception {

	public EntityAlreadyFoundException() {
	}

	public EntityAlreadyFoundException(String message) {
		super(message);
	}

	public EntityAlreadyFoundException(Throwable cause) {
		super(cause);
	}

	public EntityAlreadyFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
