package com.loqua.remote.model.compoundkeys;

import com.loqua.remote.model.Language;
import com.loqua.remote.model.User;
import com.loqua.remote.model.UserNativeLanguage;

import java.io.Serializable;

/**
 * Representa la clave primaria de la entidad {@link UserNativeLanguage}
 * @author Gonzalo
 */
public class UserNativeLanguageKey implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Clave primaria de la entidad {@link Language}
	 * a la que esta asociado el objeto UserNativeLanguageKey
	 */
	Long language;
	/** Clave primaria de la entidad {@link User}
	 * a la que esta asociado el objeto UserNativeLanguageKey
	 */
	Long user;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserNativeLanguageKey other = (UserNativeLanguageKey) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		return true;
	}
}
