package com.loqua.business.exception;

/**
 * Representa la Exception que se lanza desde la capa de negocio
 * cuando se intenta obtener una entidad que no existe en la base de datos.
 * No esta provocada por errores en el codigo, sino que depende del estado
 * de la base de datos, y sera conveniente capturarla o relanzarla
 * cuando se produzca
 */
public class EntityNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
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
