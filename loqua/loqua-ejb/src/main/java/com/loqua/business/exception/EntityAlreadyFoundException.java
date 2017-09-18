package com.loqua.business.exception;

/**
 * Representa la Exception que se lanza desde la capa de negocio
 * cuando se intenta crear una entidad que ya existe en la base de datos.
 * No esta provocada por errores en el codigo, sino que depende del estado
 * de la base de datos, y sera conveniente capturarla o relanzarla
 * cuando se produzca
 */
public class EntityAlreadyFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
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
