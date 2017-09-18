package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadInfo;
import com.loqua.model.ForumThreadVoter;
import com.loqua.model.Language;
import com.loqua.persistence.ThreadJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link ForumThread}, {@link ForumThreadInfo} y {@link ForumThreadVoter}.<br/>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionThread {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link ForumThread}, {@link ForumThreadInfo} y {@link ForumThreadVoter}
	 */
	private static final ThreadJPA threadJPA = new ThreadJPA();
	
	
	/**
	 * Consulta hilos del foro segun su atributo 'id'
	 * @param threadId atributo 'id' del ForumThread que se consulta
	 * @return objeto ForumThread cuyo atributo 'id'
	 * coincide con el parametro dado, o null si no existe
	 */
	public ForumThread getThreadById(Long threadId) {
		ForumThread result = new ForumThread();
		try {
			result = threadJPA.getThreadById(threadId);
		} catch (EntityNotPersistedException ex) {
			return null;
		}
		return result;
	}
	
	/**
	 * Consulta hilos del foro segun su atributo 'guid'
	 * @param guid atributo homonimo del ForumThread que se consulta
	 * @return objeto ForumThread cuyo atributo 'guid' coincide
	 * con el parametro dado
	 */
	public ForumThread getThreadByGuid(String guid)
			throws EntityNotFoundException {
		ForumThread result = new ForumThread();
		try {
			result = threadJPA.getThreadByGuid(guid);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Halla los hilos del foro pertenecientes a los lenguajes indicados
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @return lista de ForumThread que pertenecen a los Language dados
	 */
	public List<ForumThread> getThreadsByLanguagesFromDB(
			List<Long> listLanguagesIDs) {
		return threadJPA.getAllThreadsByLanguages(listLanguagesIDs);
	}

	/*
	 * Halla todos los hilos del foro pertenecientes a los lenguajes indicados
	 * y a la categoria dada
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @param category atributo 'id' del FeedCategory al que pertenecen
	 * los ForumThread que se desean consultar
	 * @return lista de ForumThread que pertenecen a los Language
	 * y al FeedCategory dados
	public List<ForumThread> getAllThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long categoryId){
		if( categoryId==null || categoryId==0 ){
			return threadJPA.getAllThreadsByLanguages(
					listLanguagesIDs);
		}else{
			return threadJPA.getAllThreadsByLanguagesAndCategory(
					listLanguagesIDs, categoryId);
		}
	}
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
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long categoryId,
			Integer offset, int limitNumThreads) {
		List<Long> listLanguagesIDs=listLanguagesToLanguageIDs(listLanguages);
		offset = (offset==null || offset==0)? 0 : (offset-1)*limitNumThreads;
		if( categoryId==null || categoryId==0 ){
			return threadJPA.getThreadsByLanguages(
					listLanguagesIDs, offset, limitNumThreads);
		}else{
			return threadJPA.getThreadsByLanguagesAndCategory(
					listLanguagesIDs, categoryId, offset, limitNumThreads);
		}
	}
	
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
	public Integer getNumThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long categoryId){
		Integer result = 0;
		List<Long> listLanguagesIDs=listLanguagesToLanguageIDs(listLanguages);
		if( categoryId==null || categoryId==0 ){
			result = threadJPA.getNumThreadsByLanguages(listLanguagesIDs);
		}else{
			result = threadJPA.getNumThreadsByLanguagesAndCategory(
					listLanguagesIDs, categoryId);
		}
		return result;
	}
	
	/**
	 * Convierte una lista de Language en un a lista de atributos 'id' de
	 * dichos Language
	 * @param languages lista de Language que se desea convertir
	 * @return la lista resultante de atributos 'id' de los Language indicados
	 */
	private List<Long> listLanguagesToLanguageIDs(List<Language> languages){
		List<Long> result = new ArrayList<Long>();
		for(Language language : languages){
			result.add(language.getId());
		}
		return result;
	}
	
	/*
	 * Consulta todos los hilos del foro disponibles
	 * @return lista de todos los ForumThread
	public List<ForumThread> getAllThreads() {
		return threadJPA.getAllThreads();
	}
	*/
	
	/*
	 * Halla todos los hilos del foro pertenecientes a la categoria dada
	 * @param categoryId atributo 'id' del FeedCategory al que pertenecen
	 * los ForumThread que se desean consultar
	 * @return lista de ForumThread que pertenecen al FeedCategory dado
	public List<ForumThread> getAllThreadsByCategoryFromDB(Long categoryId) {
		return threadJPA.getAllThreadsByCategory(categoryId);
	}
	*/
	
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
	public List<ForumThread> getThreadsByCategoryFromDB(Long categoryId,
			Integer offset, int limitNumThreads) {
		offset = (offset==null || offset==0)? 0 : (offset-1)*limitNumThreads;
		if( categoryId==null || categoryId==0 ){
			return threadJPA.getThreads(offset, limitNumThreads);
		}else{
			return threadJPA.getThreadsByCategory(
					categoryId, offset, limitNumThreads);
		}
	}
	
	/**
	 * Halla el numero de hilos del foro que pertenecen a la categoria dada
	 * @param categoryId atributo 'id' del FeedCategory al que pertenecen los
	 * ForumThread que se consultan
	 * @return cantidad de ForumThread que pertenecen al FeedCategory dado
	 */
	public Integer getNumThreadsByCategoryFromDB(Long categoryId){
		Integer result = 0;
		if( categoryId==null || categoryId==0 ){
			result = threadJPA.getNumThreads();
		}else{
			result = threadJPA.getNumThreadsByCategory(categoryId);
		}
		return result;
	}
	
	/**
	 * Halla los hilos del foro mas votados por los usuarios en el ultimo mes
	 * @return lista de los ForumThread publicados en el ultimo mes,
	 * cuyo ForumThreadInfo asociado tiene los mayores valores
	 * del atributo 'countVotes'
	 */
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromDB() {
		return threadJPA.getMostValuedThreadsOfTheMonth();
	}
	
	/**
	 * Halla los hilos del foro que tienen mas comentarios de usuarios
	 * desde el ultimo mes
	 * @return lista de los ForumThread publicados en el ultimo mes,
	 * cuyo ForumThreadInfo asociado tiene los mayores valores
	 * del atributo 'countComments'
	 */
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB() {
		return threadJPA.getMostCommentedThreadsOfTheMonth();
	}
	
	/**
	 * Halla los ultimos hilos del foro mas recientes que pertenecen a la
	 * categoria dada
	 * @param categoryId atributo 'name' del FeedCategory al que pertenecen
	 * los ForumThread que se consultan
	 * @return lista de los ForumThread cuya fecha es mas reciente,
	 * segun su atributo 'date', pertenecientes al FeedCategory dado
	 */
	public List<ForumThread> getLastThreadsByCategoryFromDB(Long categoryId) {
		return threadJPA.getLastThreadsByCategory(categoryId);
	}
	
	/**
	 * Halla los hilos del foro que han sido creados en la ultima hora
	 * @return lista de los ForumThread cuyo atributo 'date' esta comprendido
	 * entre la fecha actual y la de la ultima hora
	 */
	public List<ForumThread> getAllForumThreadsInLastHour() {
		// Este metodo se llama desde el cliente Aggregator
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		calendar.clear();
		calendar.set(currentYear, currentMonth, currentDay, currentHour, 0);
		// Date que representa a la actual hora en punto menos un segundo:
		Date currentHourDate = new Date(calendar.getTimeInMillis() - 1000);
		// Date que representa a la anterior hora en punto:
		Date previousHourDate = new Date(calendar.getTimeInMillis()-3600000);
		return threadJPA.getAllForumThreadsInLastHour(
				currentHourDate,previousHourDate);
	}
	
	/**
	 * Comprueba si el usuario dado ha puntuado el hilo indicado del foro
	 * @param userId atributo 'id' del User que se consulta
	 * @param threadId atributo 'id' del ForumThread que se consulta
	 * @return
	 * true: si el usuario ya ha puntuado el hilo del foro <br/>
	 * false: si el usuario aun no ha puntuado el hilo del foro
	 */
	public boolean threadAlreadyVotedByUser(Long userId, Long threadId){
		List<ForumThreadVoter> threadVoters = getThreadVoters(threadId);
		for( ForumThreadVoter threadVoter : threadVoters ){
			if( threadVoter.getUser().getId() == userId ) return true;
		}
		return false;
	}
	
	/**
	 * Consulta todos los usuarios que han puntuado el hilo indicado del foro
	 * @param threadId atributo 'id' del ForumThread al cual pertenecen
	 * los ForumThreadVoter que se desean consultar
	 * @return lista de ForumThreadVoter que pertenecen al ForumThread
	 * recibido por parametro
	 */
	private List<ForumThreadVoter> getThreadVoters(Long threadId) {
		return threadJPA.getThreadVoters(threadId);
	}
	
	/**
	 * Agrega al sistema el ForumThread indicado
	 * @param threadToCreate objeto ForumThread que se desea guardar
	 * @throws EntityAlreadyFoundException
	 * @throws Exception
	 */
	public void restCreateForumThread(ForumThread threadToCreate)
			throws EntityAlreadyFoundException, Exception{
		try {
			threadJPA.restCreateForumThread(threadToCreate);
		} catch( EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	/*
	 * Agrega a la base de datos los objetos ForumThread dados
	 * @param threadsToCreate lista de ForumThread que se desean guardar
	 * @throws EntityAlreadyFoundException
	 * @throws Exception
	public void restCreateForumThreadsByList(List<ForumThread> threadsToCreate)
			throws EntityAlreadyFoundException, Exception{
		try {
			threadJPA.restCreateForumThreadsByList(threadsToCreate);
		} catch( EntityAlreadyPersistedException ex ) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	*/
	
	/**
	 * Actualiza en el sistema el objeto ForumThread dado
	 * y tambien su ForumThreadInfo asociado
	 * @param threadToUpdate objeto ForumThread que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	private void updateAllDataByThread(ForumThread threadToUpdate)
			throws EntityNotFoundException {
		try {
			threadJPA.updateAllDataByThread(threadToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Actualiza en el sistema el objeto ForumThread dado
	 * @param threadToUpdate objeto ForumThread que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updateDataByThread(ForumThread threadToUpdate)
			throws EntityNotFoundException {
		try {
			threadJPA.updateDataByThread(threadToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
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
	public ForumThread voteThread(Long userId, ForumThread threadToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		// Este metodo lanza "EntityAlreadyFoundException"
		// porque crea en bdd una entidad "CommentVoter"
		try {
			// Primero: actualiza en bdd el ThreadInfo (numero de votos)
			ForumThreadInfo threadInfo = threadToVote.getForumThreadInfo();
			threadInfo.setCountVotes( threadInfo.getCountVotes()+1 );
			threadToVote.setForumThreadInfo( threadInfo );
			updateAllDataByThread( threadToVote );
			// Segundo: genera y guarda en bdd el nuevo ThreadVoter:
			threadJPA.createThreadVoter(userId, threadToVote.getId());
			// Por ultimo devuelve el ForumThread editado:
			return threadToVote;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Incremeta el numero de visitas recibidas por un hilo del foro
	 * @param threadToUpdate objeto ForumThread que se actualiza
	 * @throws EntityNotFoundException
	 */
	public void incrementCountVisits(ForumThread threadToUpdate)
			throws EntityNotFoundException {
		ForumThreadInfo threadInfoToUpdate =
				threadToUpdate.getForumThreadInfo();
		threadInfoToUpdate.incrementCountVisits();
		threadToUpdate.setForumThreadInfo(threadInfoToUpdate);
		updateAllDataByThread(threadToUpdate);
	}

	/**
	 * Incremeta el numero de comentarios publicados en un hilo del foro
	 * @param threadToUpdate objeto ForumThread que se actualiza
	 * @throws EntityNotFoundException
	 */
	public void incrementCountComments(ForumThread threadToUpdate)
			throws EntityNotFoundException {
		ForumThreadInfo threadInfoToUpdate =
				threadToUpdate.getForumThreadInfo();
		threadInfoToUpdate.incrementCountComments();
		threadToUpdate.setForumThreadInfo(threadInfoToUpdate);
		updateAllDataByThread(threadToUpdate);
	}
}
