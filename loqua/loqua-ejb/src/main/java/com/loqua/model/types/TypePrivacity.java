package com.loqua.model.types;

import java.io.Serializable;

/**
 * Representa el tipo de privacidad de algun dato o informacion dada
 * por el usuario:
 * <ul>
 * <li>'PRIVATE': la informacion solo es visible para el propio usuario
 * y para los administradores</li>
 * <li>'CONTACTS': la informacion es visible para el propio usuario,
 * para sus contactos y para los administradores</li>
 * <li>'PUBLIC': la informacion es visible para todos los usuarios
 * registrados en la aplicacion</li>
 * </ul>
 * @author Gonzalo
 */
public enum TypePrivacity implements Serializable{
	PRIVATE,
	CONTACTS,
	PUBLIC
}
