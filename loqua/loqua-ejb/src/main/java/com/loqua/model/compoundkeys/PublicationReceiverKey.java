package com.loqua.model.compoundkeys;

import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;

import java.io.Serializable;

/**
 * Representa la clave primaria de la entidad {@link PublicationReceiver}
 * @author Gonzalo
 */
public class PublicationReceiverKey implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Clave primaria de la entidad {@link Publication}
	 * a la que esta asociado el objeto PubicationReceiver
	 */
	Long publication;
	/** Clave primaria de la entidad {@link User}
	 * a la que esta asociado el objeto PubicationReceiver
	 */
	Long user;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((publication == null) ? 0 : publication.hashCode());
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
		PublicationReceiverKey other = (PublicationReceiverKey) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (publication == null) {
			if (other.publication != null)
				return false;
		} else if (!publication.equals(other.publication))
			return false;
		return true;
	}
}
