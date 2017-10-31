package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link Comment} y {@link CommentQuoteTo}
 * @author Gonzalo
 */
public interface ServiceComment {
	
	/**
	 * Obtiene los comentarios citados por el comentario dado. <br>
	 * En esta version de la aplicacion no se permite que un comentario cite
	 * a mas de un comentario. Por eso la lista devuelta solo
	 * contiene un elemento a lo sumo.
	 * @param actorComment comentario que cita a los comentarios que se
	 * consultan
	 * @return lista de citas que pertenecen al comentario dado 
	 * (en esta version de la aplicacion sera uno, a lo sumo)
	 */
	List<CommentQuoteTo> getCommentsQuotedByComment(Long actorComment);
	
	/**
	 * Halla los comentarios que pertenecen a un hilo determinado del foro,
	 * ordenados ascendentemente por su fecha
	 * @param threadId atributo 'id' del ForumThread (hilo del foro) en el que
	 * se buscan los comentarios
	 * @param offset offset de los Comments devueltos
	 * @param limitNumComments  limite maximo de Comment devueltos
	 * @return lista de Comment que pertenecen al ForumThread dado,
	 * ordenados ascendentemente por su atributo 'date' hetedado de ForumPost
	 */
	List<Comment> getCommentsByThread(
			Long threadId, Integer offset, Integer limitNumComments );
	
	/**
	 * Halla los comentarios que pertenecen a un hilo determinado del foro,
	 * ordenados descendentemente por su fecha
	 * @param threadId atributo 'id' del ForumThread (hilo del foro) en el que
	 * se buscan los comentarios
	 * @param offset offset de los Comments devueltos
	 * @param limitNumComments limite maximo de Comment devueltos
	 * @return lista de Comment que pertenecen al ForumThread dado,
	 * ordenados descendentemente por su atributo 'date' hetedado de ForumPost
	 */
	List<Comment> getCommentsByThreadReverseOrder(
			Long threadId, Integer offset, Integer limitNumComments);
	
	/**
	 * Halla el numero de comentarios que pertenecen a un hilo determinado
	 * del foro
	 * @param threadId atributo 'id' del ForumThread (hilo del foro) al que
	 * pertenecen los comentarios
	 * @return cantidad de Comment que pertenecen al ForumThread dado
	 * @throws EntityNotFoundException
	 */
	Integer getNumCommentsByThread(Long threadId)
			throws EntityNotFoundException;
	
	/**
	 * Comprueba si un usuario ya ha dado su voto a un comentario
	 * @param userId atributo 'id' del User que se consulta
	 * @param commentId atributo 'id' del Comment que se consulta
	 * @return
	 * true: si el usuario dado ya ha votado el comentario indicado <br>
	 * false: si el usuario dado nunca ha votado el comentario indicado
	 */
	boolean commentAlreadyVotedByUser(Long userId, Long commentId)
			throws EntityNotFoundException;
	
	/**
	 * Incrementa el numero de votos de un comentario, genera la asociacion
	 * correspondiente entre el usuario votante y el comentario,
	 * comprueba si es preciso generar una publicacion para el evento
	 * y actualiza todos los cambios en la base de datos
	 * @param userId atributo 'id' del User que vota el comentario dado
	 * @param commentToVote Comment que recibe el voto
	 * @return el Comment que recibe el voto, una vez actualizado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	Comment voteComment(Long userId, Comment commentToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	/**
	 * Genera un comentario en un hilo del foro, incrementa la puntuacion
	 * del usuario, comprueba si es preciso generar una publicacion para el
	 * evento y actualiza todos los cambios en la base de datos
	 * @param comment los datos del comentario que se va a agregar
	 * @return el Comment generado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	Comment sendComment(Comment comment)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	/** Elimina del foro el comentario dado
	 * @param comment objeto Comment que se desea eliminar
	 * @throws EntityNotFoundException
	 */
	void deleteComment(Comment comment) throws EntityNotFoundException;
	
	/**
	 * Actualiza en el foro el comentario dado
	 * @param commentToUpdate objeto Comment que se desea actualizar
	 * @param text texto plano actualizado del Comment
	 * @param code texto HTML actualizado del Comment
	 * @throws EntityNotFoundException
	 */
	void updateTextComment(Comment commentToUpdate, String text, String code)
			throws EntityNotFoundException;
	
	/**
	 * Llama al metodo {@link #sendComment} y crea una asociacion
	 * (objeto CommentQuoteTo) entre el comentario resultante y
	 * el comentario 'commentToQuote'
	 * @param commentToQuote comentario que queda citado en el foro
	 * @param commentToCreate datos del comentario que se va a agregar
	 * @return comentario generado en el foro, que cita al 'commentToQuote'
	 * dado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	Comment quoteComment(Comment commentToQuote, Comment commentToCreate)
			throws EntityAlreadyFoundException, EntityNotFoundException;
}