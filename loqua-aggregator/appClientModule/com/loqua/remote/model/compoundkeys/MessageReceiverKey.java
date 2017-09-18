package com.loqua.remote.model.compoundkeys;

import com.loqua.remote.model.Message;
import com.loqua.remote.model.MessageReceiver;
import com.loqua.remote.model.User;

import java.io.Serializable;

/**
 * Representa la clave primaria de la entidad {@link MessageReceiver}
 * @author Gonzalo
 */
public class MessageReceiverKey implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Clave primaria de la entidad {@link Message}
	 * a la que esta asociado el objeto MessageReceiver
	 */
	Long message;
	/** Clave primaria de la entidad {@link User}
	 * a la que esta asociado el objeto MessageReceiver
	 */
	Long user;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((message == null) ? 0 : message.hashCode());
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
		MessageReceiverKey other = (MessageReceiverKey) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
}
