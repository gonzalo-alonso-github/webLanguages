package com.loqua.remote.model.compoundkeys;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ForumThreadVoterKey implements Serializable{
	Long forumThread;
	Long user;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((forumThread == null) ? 0 : forumThread.hashCode());
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
		ForumThreadVoterKey other = (ForumThreadVoterKey) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (forumThread == null) {
			if (other.forumThread != null)
				return false;
		} else if (!forumThread.equals(other.forumThread))
			return false;
		return true;
	}
}
