package com.loqua.persistence.exception;

/**
 * Representa la Exception que se lanza desde la capa de persistencia
 * cuando se intenta crear una entidad que ya existe en la base de datos.
 * No esta provocada por errores en el codigo, sino que depende del estado
 * de la base de datos, y sera conveniente capturarla o relanzarla
 * cuando se produzca
 */
public class EntityAlreadyPersistedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public EntityAlreadyPersistedException() {}

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
