package com.loqua.persistence.exception;

/**
 * Se lanza cuando se intenta crear una entidad
 * que ya existe en el sistema de persistencia
 */
@SuppressWarnings("serial")
public class EntityAlreadyPersistedException extends Exception {

	public EntityAlreadyPersistedException() {
	}

	public EntityAlreadyPersistedException(String message) {
		super(message);
	}

	public EntityAlreadyPersistedException(Throwable cause) {
		super(cause);
	}

	public EntityAlreadyPersistedException(String message, Throwable cause) {
		super(message, cause);
	}
}
