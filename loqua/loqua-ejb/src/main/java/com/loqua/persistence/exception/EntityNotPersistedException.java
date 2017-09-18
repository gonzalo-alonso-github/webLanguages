package com.loqua.persistence.exception;

/**
 * Representa la Exception que se lanza desde la capa de persistencia
 * cuando se intenta obtener una entidad que no existe en la base de datos.
 * No esta provocada por errores en el codigo, sino que depende del estado
 * de la base de datos, y sera conveniente capturarla o relanzarla
 * cuando se produzca
 */
public class EntityNotPersistedException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public EntityNotPersistedException() {}

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
