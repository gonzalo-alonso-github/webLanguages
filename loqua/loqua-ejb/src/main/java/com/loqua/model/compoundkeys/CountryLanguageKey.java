package com.loqua.model.compoundkeys;

import com.loqua.model.Country;
import com.loqua.model.Language;

import java.io.Serializable;

/**
 * Representa la clave primaria de la entidad CountryLanguage
 * @author Gonzalo
 */
public class CountryLanguageKey implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Clave primaria de la entidad {@link Country}
	 * a la que esta asociado el objeto CountryLanguage
	 */
	Long country;
	/** Clave primaria de la entidad {@link Language}
	 * a la que esta asociado el objeto CountryLanguage
	 */
	Long language;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((country == null) ? 0 : country.hashCode());
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
		CountryLanguageKey other = (CountryLanguageKey) obj;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		return true;
	}
}
