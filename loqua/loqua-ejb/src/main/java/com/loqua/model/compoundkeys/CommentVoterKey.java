package com.loqua.model.compoundkeys;

import com.loqua.model.Comment;
import com.loqua.model.CommentVoter;
import com.loqua.model.User;

import java.io.Serializable;

/**
 * Representa la clave primaria de la entidad {@link CommentVoter}
 * @author Gonzalo
 */
public class CommentVoterKey implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Clave primaria de la entidad {@link Comment}
	 * a la que esta asociado el objeto CommentVoter
	 */
	Long comment;
	/** Clave primaria de la entidad {@link User}
	 * a la que esta asociado el objeto CommentVoter
	 */
	Long user;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((user == null) ? 0 : user.hashCode());
		result = prime * result
				+ ((comment == null) ? 0 : comment.hashCode());
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
		CommentVoterKey other = (CommentVoterKey) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		return true;
	}
}
