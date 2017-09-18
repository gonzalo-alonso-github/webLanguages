package com.loqua.model.compoundkeys;

import com.loqua.model.Correction;
import com.loqua.model.CorrectionAgree;
import com.loqua.model.User;

import java.io.Serializable;

/**
 * Representa la clave primaria de la entidad {@link CorrectionAgree}
 * @author Gonzalo
 */
public class CorrectionAgreeKey implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Clave primaria de la entidad {@link Correction}
	 * a la que esta asociado el objeto CorrectionAgree
	 */
	Long correction;
	/** Clave primaria de la entidad {@link User}
	 * a la que esta asociado el objeto CorrectionAgree
	 */
	Long user;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((correction == null) ? 0 : correction.hashCode());
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
		CorrectionAgreeKey other = (CorrectionAgreeKey) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (correction == null) {
			if (other.correction != null)
				return false;
		} else if (!correction.equals(other.correction))
			return false;
		return true;
	}
}
