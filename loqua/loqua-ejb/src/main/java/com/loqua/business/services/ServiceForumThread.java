package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadInfo;
import com.loqua.model.ForumThreadVoter;
import com.loqua.model.Language;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link ForumThread}, {@link ForumThreadInfo} y {@link ForumThreadVoter}
 * @author Gonzalo
 */
public interface ServiceForumThread {
	
	/**
	 * Consulta hilos del foro segun su atributo 'id'
	 * @param threadId atributo 'id' del ForumThread que se consulta
	 * @return objeto ForumThread cuyo atributo 'id'
	 * coincide con el parametro dado, o null si no existe
	 */
	ForumThread getThreadById(Long threadId);
	
	/**
	 * Consulta hilos del foro segun su atributo 'guid'
	 * @param guid atributo homonimo del ForumThread que se consulta
	 * @return objeto ForumThread cuyo atributo 'guid' coincide
	 * con el parametro dado
	 */
	ForumThread getThreadByGuid(String guid) throws EntityNotFoundException;
	
	/*
	 * Halla todos los hilos del foro pertenecientes a los lenguajes indicados
	 * y a la categoria dada
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @param categoryId atributo 'id' del FeedCategory al que pertenecen
	 * los ForumThread que se desean consultar
	 * @return lista de ForumThread que pertenecen a los Language
	 * y al FeedCategory dados
	List<ForumThread> getAllThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long categoryId);
	*/
	
	/**
	 * Halla los hilos del foro pertenecientes a los lenguajes indicados
	 * y a la categoria dada, aplicando un offset y limitando su numero
	 * @param listLanguages lista Language a los que pertenecen
	 * los ForumThread que se desean consultar
	 * @param categoryId atributo 'id' del FeedCategory al que pertenecen
	 * los ForumThread que se desean consultar
	 * @param offset de los ForumThread devueltos
	 * @param limitNumThreads limite maximo de ForumThread devueltos
	 * @return lista de ForumThread que pertenecen a los Language
	 * y al FeedCategory dados, aplicando el offset y el limite de elementos
	 * indicado
	 */
	List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long categoryId,
			Integer offset, int limitNumThreads);
	
	/**
	 * Halla el numero de hilos del foro pertenecientes a los lenguajes
	 * indicados y a la categoria dada
	 * @param listLanguages lista Language a los que pertenecen
	 * los ForumThread que se desean consultar
	 * @param categoryId atributo 'id' del FeedCategory al que pertenecen
	 * los ForumThread que se desean consultar
	 * @return cantidad de ForumThread que pertenecen a los Language
	 * y al FeedCategory dados
	 */
	Integer getNumThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long categoryId);
	
	/*
	 * Halla todos los hilos del foro pertenecientes a la categoria dada
	 * @param categoryId atributo 'id' del FeedCategory al que pertenecen
	 * los ForumThread que se desean consultar
	 * @return lista de ForumThread que pertenecen al FeedCategory dado
	 * List<ForumThread> getAllThreadsByCategoryFromDB(Long categoryId); */
	
	/**
	 * Halla todos los hilos del foro pertenecientes a la categoria dada,
	 * aplicando un offset y limitando su numero
	 * @param categoryId atributo 'id' del FeedCategory al que pertenecen
	 * los ForumThread que se desean consultar
	 * @param offset de los ForumThread devueltos
	 * @param limitNumThreads limite maximo de ForumThread devueltos
	 * @return lista de ForumThread que pertenecen al FeedCategory dado,
	 * aplicando el offset y el limite maximo recibidos por parametro
	 */
	List<ForumThread> getThreadsByCategoryFromDB(Long categoryId,
			Integer offset, int limitNumThreads);
	
	/**
	 * Halla el numero de hilos del foro que pertenecen a la categoria dada
	 * @param categoryId atributo 'id' del FeedCategory al que pertenecen los
	 * ForumThread que se consultan
	 * @return cantidad de ForumThread que pertenecen al FeedCategory dado
	 */
	Integer getNumThreadsByCategoryFromDB(Long categoryId);

	/**
	 * Halla los hilos del foro mas votados por los usuarios en el ultimo mes
	 * @return lista de los ForumThread publicados en el ultimo mes,
	 * cuyo ForumThreadInfo asociado tiene los mayores valores
	 * del atributo 'countVotes'
	 */
	List<ForumThread> getMostValuedThreadsOfTheMonthFromDB();
	
	/**
	 * Halla los hilos del foro que tienen mas comentarios de usuarios
	 * desde el ultimo mes
	 * @return lista de los ForumThread publicados en el ultimo mes,
	 * cuyo ForumThreadInfo asociado tiene los mayores valores
	 * del atributo 'countComments'
	 */
	List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB();
	
	/**
	 * Halla los ultimos hilos del foro mas recientes que pertenecen a la
	 * categoria dada
	 * @param categoryId atributo 'name' del FeedCategory al que pertenecen
	 * los ForumThread que se consultan
	 * @return lista de los ForumThread cuya fecha es mas reciente,
	 * segun su atributo 'date', pertenecientes al FeedCategory dado
	 */
	List<ForumThread> getLastThreadsByCategoryFromDB(Long categoryId);
	
	// Para el cliente Aggregator
	/**
	 * Halla los hilos del foro que han sido creados en la ultima hora
	 * @return lista de los ForumThread cuyo atributo 'date' esta comprendido
	 * entre la fecha actual y la de la ultima hora
	 */
	List<ForumThread> getAllForumThreadsInLastHour();

	/**
	 * Comprueba si el usuario dado ha puntuado el hilo indicado del foro
	 * @param userId atributo 'id' del User que se consulta
	 * @param threadId atributo 'id' del ForumThread que se consulta
	 * @return
	 * true: si el usuario ya ha puntuado el hilo del foro <br/>
	 * false: si el usuario aun no ha puntuado el hilo del foro
	 */
	boolean threadAlreadyVotedByUser(Long userId, Long threadId)
			throws EntityNotFoundException;
	
	// Para el cliente Aggregator
	/**
	 * Agrega al sistema el ForumThread indicado
	 * @param threadToCreate objeto ForumThread que se desea guardar
	 * @throws EntityAlreadyFoundException
	 * @throws Exception
	 */
	void restCreateForumThread(ForumThread threadToCreate)
			throws EntityAlreadyFoundException, Exception;
	/*
	 * Agrega a la base de datos los objetos ForumThread dados
	 * @param threadsToCreate lista de ForumThread que se desean guardar
	 * @throws EntityAlreadyFoundException
	 * @throws Exception
	 * void restCreateForumThreadsByList(List<ForumThread> threads)
			throws EntityAlreadyFoundException, Exception;*/
	
	/*
	 * Actualiza en el sistema el objeto ForumThread dado
	 * y tambien su ForumThreadInfo asociado
	 * @param threadToUpdate objeto ForumThread que se desea actualizar
	 * @throws EntityNotFoundException
	void updateAllDataByThread(ForumThread threadToUpdate) 
			throws EntityNotFoundException;
	*/
	/*
	 * Actualiza en el sistema el objeto ForumThread dado
	 * @param threadToUpdate objeto ForumThread que se desea actualizar
	 * @throws EntityNotFoundException
	void updateDataByThread(ForumThread threadToUpdate) 
			throws EntityNotFoundException;
	*/
	
	/**
	 * Incremeta la puntuacion de un hilo del foro votado por un usuario,
	 * crea la relacion ForumThreadVoter entre el usuario y el hilo,
	 * y actualiza los cambios en la base de datos
	 * @param userId atributo 'id' del User que reealiza el voto
	 * @param threadToVote objeto ForumThread que recibe el voto
	 * @return objeto ForumThread, una vez que ha sido actualizado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	ForumThread voteThread(Long userId, ForumThread threadToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException;

	/**
	 * Incremeta el numero de visitas recibidas por un hilo del foro
	 * @param threadToUpdate objeto ForumThread que se actualiza
	 * @throws EntityNotFoundException
	 */
	void incrementCountVisits(ForumThread threadToUpdate)
			throws EntityNotFoundException;
}