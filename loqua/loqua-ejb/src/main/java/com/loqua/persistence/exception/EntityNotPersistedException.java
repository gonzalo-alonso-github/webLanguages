package com.loqua.persistence.exception;

/**
 * Se lanza cuando se intenta obtener una entidad
 * que no existe en el sistema de persistencia
 */
@SuppressWarnings("serial")
public class EntityNotPersistedException extends Exception {

	public EntityNotPersistedException() {
	}

	public EntityNotPersistedException(String message) {
		super(message);
	}

	public EntityNotPersistedException(Throwable cause) {
		super(cause);
	}

	public EntityNotPersistedException(String message, Throwable cause) {
		super(message, cause);
	}
}
