package com.loqua.business.services.impl.transactionScript;

import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.CommentVoter;
import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadInfo;
import com.loqua.model.ForumThreadVoter;
import com.loqua.model.PrivacityData;
import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;
import com.loqua.model.UserInfo;
import com.loqua.model.UserInfoPrivacity;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.CommentJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link Comment} y {@link CommentQuoteTo}. <br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionComment {
	
	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link Comment} y {@link CommentQuoteTo} */
	private static final CommentJPA commentJPA = new CommentJPA();
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
	 * y {@link PrivacityData},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionUser transactionUser = 
			new TransactionUser();
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Publication} y {@link PublicationReceiver},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionPublication transactionPub = 
			new TransactionPublication();
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link ForumThread}, {@link ForumThreadInfo}
	 * y {@link ForumThreadVoter},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionThread transactionThread = 
			new TransactionThread();
	
	
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
	public List<CommentQuoteTo> getCommentsQuotedByComment(Long actorComment){
		return commentJPA.getCommentsQuotedByComment(actorComment);
	}

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
	public List<Comment> getCommentsByThread(Long threadId,
			Integer offset, Integer limitNumComments){
		List<Comment> result = null;
		offset = (offset==null || offset==0)? 0 : (offset-1)*limitNumComments;
		result = commentJPA.getCommentsByThread(
				threadId, offset, limitNumComments);
		return result;
	}
	
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
	public List<Comment> getCommentsByThreadReverseOrder(Long threadId,
			Integer offset, Integer limitNumComments){
		List<Comment> result = null;
		offset = (offset==null || offset==0)? 0 : (offset-1)*limitNumComments;
		result = commentJPA.getCommentsByThreadReverseOrder(
				threadId, offset, limitNumComments);
		return result;
	}

	/**
	 * Halla el numero de comentarios que pertenecen a un hilo determinado
	 * del foro
	 * @param threadId atributo 'id' del ForumThread (hilo del foro) al que
	 * pertenecen los comentarios
	 * @return cantidad de Comment que pertenecen al ForumThread dado
	 * @throws EntityNotFoundException
	 */
	public Integer getNumCommentsByThread(Long threadId)
			throws EntityNotFoundException {
		Integer result = 0;
		try{
			result = commentJPA.getNumCommentsByThread(threadId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Halla la lista de votantes de un comentario dado
	 * @param commentId atributo 'id' del Comment (hilo del foro) al que
	 * pertenecen los CommentVoter que se consultan
	 * @return lista de CommentVoter asociados al Comment dado
	 */
	public List<CommentVoter> getCommentVoters(Long commentId) {
		return commentJPA.getCommentVoters(commentId);
	}
	
	/**
	 * Comprueba si un usuario ya ha dado su voto a un comentario
	 * @param userId atributo 'id' del User que se consulta
	 * @param commentId atributo 'id' del Comment que se consulta
	 * @return
	 * true: si el usuario dado ya ha votado el comentario indicado <br>
	 * false: si el usuario dado nunca ha votado el comentario indicado
	 */
	public boolean commentAlreadyVotedByUser(Long userId, Long commentId){
		List<CommentVoter> commentVoters = getCommentVoters(commentId);
		for( CommentVoter commentVoter : commentVoters ){
			if( commentVoter.getUser().getId() == userId ) return true;
		}
		return false;
	}
	
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
	public Comment voteComment(Long userId, Comment commentToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			// Actualiza en bdd el Comment (numero de votos)
			commentToVote.setNumVotes( commentToVote.getNumVotes()+1 );
			commentJPA.updateComment( commentToVote );
			
			// Genera y guarda en bdd el nuevo CommentVoter:
			commentJPA.createCommentVoter( userId, commentToVote.getId() );
			
			// Actualiza los puntos del usuario:
			transactionUser.incrementCommentVotes(commentToVote.getUser());
			// Si es preciso, se crean las Publication necesarias:
			generatePublicationsToCommentVote(commentToVote);
			
			// Por ultimo devuelve el Comment editado:
			return commentToVote;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Comprueba si es necesaria la generacion de alguna Publication tras
	 * producirse el voto de un comentario. Las Publication se generaran en los
	 * siguientes casos: <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- El voto del comentario es el primero que recibe
	 * el usuario autor del mismo en ese preciso comentario<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- El total de votos en todos los comentarios
	 * del usuario alcanza una cantidad multiplo de 100<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- El incremento en la puntuacion del usuario
	 * hace que este ascienda en la clasificacion hasta el
	 * top de los primeros 100 usuarios, o 50, o 25, o 20, o 15, o 10, o 5
	 * o superiores <br>
	 * @param comment Comment que, por haber sido votado,
	 * provoca la creacion de Publications
	 * @throws EntityNotFoundException
	 * @throws EntityAlreadyFoundException
	 */
	private void generatePublicationsToCommentVote(Comment comment)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		User userComment = comment.getUser();
		TypePrivacity privacity=userComment.getPrivacityData().getPublications();
		if( comment.getNumVotes()==1 ){
			// si es el primer voto en este comentario, genera publicacion:
			transactionPub.generatePublication(
					privacity, comment.getId() ,102L, userComment);
		}
		Integer totalVotes = userComment.getUserInfo().getCountVotesComments();
		if( totalVotes%100 == 0 ){
			// si los votos recibidos en todos los comments son multiplo de 100:
			transactionPub.generatePublication(
					privacity, totalVotes.longValue(), 3L, userComment);
		}
		// si el autor llega al top-100/50/25/20/15/10/5... tambien la genera:
		transactionUser.generatePublicationForTopUsers( userComment );
	}
	
	/**
	 * Genera un comentario en un hilo del foro, incrementa la puntuacion
	 * del usuario, comprueba si es preciso generar una publicacion para el
	 * evento y actualiza todos los cambios en la base de datos
	 * @param comment los datos del comentario que se va a agregar
	 * @return el Comment generado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	public Comment sendComment(Comment comment)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			User user = comment.getUser();
			ForumThread thread = comment.getForumThread();
			int posIndex = getNumCommentsByThread(thread.getId()) + 1;
			comment.setNumVotesThis(0).setPositionIndexThis(posIndex)
				.setDateThis(new Date())
				/*.setPostType("TypeComment")*/;
			comment = commentJPA.createComment(comment);
			// Se incrementa el numero de comentarios de la noticia:
			transactionThread.incrementCountComments(thread);
			// Se actualiza la fecha de 'dateLastComment' de la noticia:
			thread.setDateLastComment(new Date());
			transactionThread.updateDataByThread(thread);
			// Se incrementan los puntos del usuario:
			transactionUser.incrementCountComments(user);
			// Y, si corresponde, se crean las Publication necesarias:
			generatePublicationsToSendComment(comment);
			return comment;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Comprueba si es necesaria la generacion de alguna Publication tras
	 * crear un comentario. Las Publication se generaran en los
	 * siguientes casos: <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- El usuario autor del comentario ha alcanzado
	 * los 50/100/500/1000/(...) comentarios <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- El incremento en la puntuacion del usuario
	 * hace que este ascienda en la clasificacion hasta el
	 * top de los primeros 100 usuarios, o 50, o 25, o 20, o 15, o 10, o 5
	 * o superiores <br>
	 * @param comment Comment que, por haber sido creado,
	 * provoca la creacion de Publications
	 * @throws EntityNotFoundException
	 * @throws EntityAlreadyFoundException
	 */
	private void generatePublicationsToSendComment(Comment comment)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		User userComment = comment.getUser();
		TypePrivacity privacity=userComment.getPrivacityData().getPublications();
		
		Integer numComments = userComment.getUserInfo().getCountComments();
		if( reachedXComments(numComments) ){
			// comprueba si ese numero ha sido alcanzado otras veces anteriores
			// (puede darse si habia borrado comentarios)
			if( transactionPub.achievementNumCommentsAlreadyPassedByUser(
					numComments.longValue(), userComment.getId()) ){
				// en cuyo caso no genera la publicacion:
				return;
			}// else
			// si el logro aun no habia sido alcanzado, genera la publicacion:
			transactionPub.generatePublication(
					privacity, numComments.longValue(), 1L, userComment);
		}
		// ya se han incrementado los puntos del usuario.
		// Si el autor llega al top-100/50/25/20/15/10/5... genera publicacion:
		transactionUser.generatePublicationForTopUsers( userComment );
	}

	/**
	 * Comprueba si el numero recibido (comentarios publicados por un usuario)
	 * ha alcanzado una cantidad considerada como logro; es decir, si coincide
	 * con uno de los numeros de la serie '50, 100, 500, 1000, 5000...'
	 * @param numComments numero de comentarios publicados por un usuario
	 * @return
	 * true: si el numero dado esta en la serie '50, 100, 500, 1000, 5000...'
	 * <br>
	 * false: si el numero dado no esta la serie '50, 100, 500, 1000, 5000...'
	 */
	private boolean reachedXComments(Integer numComments) {
		// si el num de comentarios es 50/100/500/1000/... devuelve 'true':
		int reduction = 0;
		if( numComments >= 50 ){
			String numCommentsString = numComments.toString();
			// exp. regular para uno o mas ceros al final de un numero: [0]+$
			numCommentsString = numCommentsString.replaceAll("[0]+$", "");
			reduction = Integer.parseInt(numCommentsString);
			if( reduction==1 || reduction==5 ){
				return true;
			}
		}
		return false;
	}
	
	/** Elimina del foro el comentario dado
	 * @param comm objeto Comment que se desea eliminar
	 * @throws EntityNotFoundException
	 */
	public void deleteComment(Comment comm) throws EntityNotFoundException {
		try {
			transactionPub.editPubsForDeletedPost(comm);
			commentJPA.deleteComment(comm);
			// Se decrementan los puntos del usuario:
			transactionUser.decrementCountComments(comm);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Actualiza en el foro el comentario dado
	 * @param commentToUpdate objeto Comment que se desea actualizar
	 * @param text texto plano actualizado del Comment
	 * @param code texto HTML actualizado del Comment
	 * @throws EntityNotFoundException
	 */
	public void updateTextComment(Comment commentToUpdate, String text,
			String code) throws EntityNotFoundException {
		try {
			commentToUpdate.setTextThis(text).setTextHtmlThis(code);
			commentJPA.updateComment(commentToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	/**
	 * Llama al metodo {@link #sendComment} y crea una asociacion
	 * (objeto CommentQuoteTo) entre el comentario resultante y
	 * el comentario 'commentToQuote'
	 * @param commentToQuote comentario que queda citado por el comentario
	 * generado
	 * @param commentToCreate comentario que se genera
	 * @return comentario generado en el foro, que cita al 'commentToQuote'
	 * dado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	public Comment quoteComment(Comment commentToQuote,Comment commentToCreate)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			// Se crea el Comment llamando a sendComment:
			Comment commentResult = sendComment(commentToCreate);
			// Se crea el CommentQuoteTo (la relacion de 'cita' entre Comments):
			commentJPA.createQuote(commentToQuote, commentResult);
			// Se crea la Publication necesaria:
			generatePublicationToQuoteComm(
					commentToQuote, commentToCreate.getUser());
			return commentResult;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	/**
	 * Crea la Publication correspondiete tras crear un comentario
	 * que cita a otro.
	 * @param commentToQuote Comment que, por haber sido citado por otro,
	 * provoca la creacion de Publications
	 * @param user User autor del comentario citado, que recibira
	 * la Publication en su pagina de perfil
	 * @throws EntityNotFoundException
	 * @throws EntityAlreadyFoundException
	 */
	private void generatePublicationToQuoteComm(
			Comment commentToQuote, User user)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		// comprobar  si el usuario esta citando un comentario de si mismo
		// (eso se permite), en cuyo caso no se genera la publicacion:
		if( ! user.equals( commentToQuote.getUser() ) ){
			transactionPub.generatePublication(TypePrivacity.PRIVATE,
					commentToQuote.getId(), 103L, commentToQuote.getUser());
		}
	}
}
