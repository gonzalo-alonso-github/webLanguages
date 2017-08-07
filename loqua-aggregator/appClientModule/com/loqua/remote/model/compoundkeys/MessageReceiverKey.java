package com.loqua.remote.model.compoundkeys;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MessageReceiverKey implements Serializable{
	Long message;
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
