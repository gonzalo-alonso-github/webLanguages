package com.loqua.model.types;

import java.io.Serializable;

/**
 * Representa el tipo de un cambio de contrase&ntilde;a:
 * <ul>
 * <li>'RESTORE': es una restauracion de contrase&ntilde;a por parte del
 * usuario</li>
 * <li>'EDIT': es una edicion de contrase&ntilde;a por parte del usuario</li>
 * </ul>
 * @author Gonzalo
 */
public enum TypeChangePassword implements Serializable{
	RESTORE,
	EDIT
}
