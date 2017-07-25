package com.loqua.business.exception;

/**
 * Se lanza cuando se intenta obtener una entidad
 * que no existe en el sistema de persistencia
 */
@SuppressWarnings("serial")
public class EntityNotFoundException extends Exception {

	public EntityNotFoundException() {
	}

	public EntityNotFoundException(String message) {
		super(message);
	}

	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
