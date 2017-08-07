package com.loqua.remote.model.compoundkeys;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CorrectionAgreeKey implements Serializable{
	Long correction;
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
