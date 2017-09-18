package com.loqua.business.services;

import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.ForumPost;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link ForumPost} (que son clase padre de {@link Comment}
 * y {@link Correction})
 * @author Gonzalo
 */
public interface ServiceForumPost {
	
	/**
	 * Consulta los ForumPost segun su atributo 'id'; esto es,
	 * todas las participaciones del foro ya sean comentarios o correcciones
	 * @param forumPostId atributo 'id' del ForumPost que se consulta
	 * @return ForumPost cuyo atributo 'id' coincide con el parametro dado,
	 * o null si no existe
	 */
	ForumPost getForumPostById(Long phorumPostId);
	
	/*
	 * Halla los ForumPost creados por el usuario dado; esto es, 
	 * todas sus participaciones en el foro ya sean comentarios o correcciones
	 * @param user User autor de los ForumPost que se consultan
	 * @return lista de ForumPost cuyo atributo 'user' coincide
	 * con el parametro recibido
	List<PhorumPost> getForumPostsByUser(User user);
	 */
}