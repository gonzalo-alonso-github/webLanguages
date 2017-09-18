package com.loqua.remote.model.compoundkeys;

import com.loqua.remote.model.Correction;
import com.loqua.remote.model.CorrectionDisagree;
import com.loqua.remote.model.User;

import java.io.Serializable;

/**
 * Representa la clave primaria de la entidad {@link CorrectionDisagree}
 * @author Gonzalo
 */
public class CorrectionDisagreeKey implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Clave primaria de la entidad {@link Correction}
	 * a la que esta asociado el objeto CorrectionDisagree
	 */
	Long correction;
	/** Clave primaria de la entidad {@link User}
	 * a la que esta asociado el objeto CorrectionDisagree
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
		CorrectionDisagreeKey other = (CorrectionDisagreeKey) obj;
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
