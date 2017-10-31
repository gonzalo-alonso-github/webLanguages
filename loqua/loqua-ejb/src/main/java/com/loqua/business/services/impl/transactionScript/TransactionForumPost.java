package com.loqua.business.services.impl.transactionScript;

import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.ForumPost;
import com.loqua.persistence.ForumPostJPA;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de la entidad
 * {@link ForumPost} (clase padre de {@link Comment} y {@link Correction}).<br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionForumPost {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a la entidad
	 * {@link ForumPost} (clase padre de {@link Comment} y {@link Correction})
	 */
	private static final ForumPostJPA forumPostJPA = new ForumPostJPA();
	
	
	/**
	 * Consulta los ForumPost segun su atributo 'id'; esto es,
	 * todas las participaciones del foro ya sean comentarios o correcciones
	 * @param forumPostId atributo 'id' del ForumPost que se consulta
	 * @return ForumPost cuyo atributo 'id' coincide con el parametro dado,
	 * o null si no existe
	 */
	public ForumPost getForumPostByID(Long forumPostId){
		ForumPost result = new ForumPost();
		try{
			result = forumPostJPA.getForumPostByID(forumPostId);
		}catch (EntityNotPersistedException ex) {
			return null;
		}
		return result;
	}
	
	/*
	 * Halla los ForumPost creados por el usuario dado; esto es, 
	 * todas sus participaciones en el foro ya sean comentarios o correcciones
	 * @param user User autor de los ForumPost que se consultan
	 * @return lista de ForumPost cuyo atributo 'user' coincide
	 * con el parametro recibido
	public List<ForumPost> getForumPostsByUser(User user){
		return forumPostJPA.getForumPostsByUser(user);
	}
	*/
}
