package com.loqua.model.types;

import java.io.Serializable;

/**
 * Representa el tipo de usuario en la aplicacion:
 * <ul>
 * <li>'USER': es un usuario comun, sin permisos especiales</li>
 * <li>'ADMINISTRATOR': es un usuario administrador, con permisos adicionales
 * respecto al usuario comun</li>
 * </ul>
 * @author Gonzalo
 */
public enum TypeUserRole implements Serializable{
	USER,
	ADMINISTRATOR
}
