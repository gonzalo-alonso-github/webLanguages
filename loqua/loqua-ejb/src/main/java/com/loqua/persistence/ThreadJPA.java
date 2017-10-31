package com.loqua.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadInfo;
import com.loqua.model.ForumThreadVoter;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link ForumThread}, {@link ForumThreadInfo} y {@link ForumThreadVoter}
 * @author Gonzalo
 */
public class ThreadJPA {
	
	/*
	METODOS PARA OBTENER UNA NOTICIA SEGUN SU UNIQUE KEY 
		getThreadById
		getThreadByGuid
	METODOS PARA LISTAR NOTICIAS EN EL FORO
	- Para listar todas las noticias:
		getAllThreads
		getThreads (con offset y limite maximo)
		getNumThreads
	- Para listar todas las noticias segun idiomas practicados:
		getAllThreadsByLanguages
		getThreadsByLanguages (con offset y limite maximo)
		getNumThreadsByLanguages
	- Para listar todas las noticias segun idiomas practicados y categoria:
		getAllThreadsByLanguagesAndCategory
		getThreadsByLanguagesAndCategory (con offset y limite maximo)
		getNumThreadsByLanguagesAndCategory
	- Para listar todas las noticias segun categoria:
		getAllThreadsByCategory
		getThreadsByCategory (con offset y limite maximo)
		getNumThreadsByCategory
	OTROS METODOS PARA LISTAR NOTICIAS
		getMostValuedThreadsOfTheMonth
		getMostCommentedThreadsOfTheMonth
		getLastThreadsByCategory
		getAllForumThreadGUIDsInLastHour
		getThreadVoters
	METODOS DE CREATE/DELETE/UPDATE
		restCreateForumThread
		createThreadVoter
		deleteThreadVoter (solo para test)
		updateAllDataByThread
		updateDataByThread
	*/
	
	/** Valor por defecto del limite maximo de noticias que se muestran
	 * en el foro */
	private static final int MAX_NEWS_TO_RETURN = 1000;
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad
	 * 'ForumThread' en la base de datos */
	private static final String FORUMTHREAD_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'ForumThread' entity not found"
			+ " at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad 'ForumThread'
	 * en la base de datos */
	private static final String FORUMTHREAD_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ForumThread' entity already"
			+ " found at Persistence layer";
	
	/** Mensaje de la excepcion producida al repetirse la entidad 'ThreadVoter'
	 * en la base de datos */
	private static final String THREADVOTER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ForumThreadVoter' entity already"
			+ " found at Persistence layer";
	
	/** Mensaje de la excepcion producida al repetirse la entidad 'ThreadVoter'
	 * en la base de datos */
	private static final String THREADVOTER_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'ForumThreadVoter' entity not found"
					+ " at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Thread.getThreadById'
	 * @param threadId atributo 'id' del ForumThread que se consulta
	 * @return ForumThread cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
	public ForumThread getThreadById(Long threadId)
			throws EntityNotPersistedException {
		ForumThread result = new ForumThread();
		try{
			result = (ForumThread) JPA.getManager()
				.createNamedQuery("Thread.getThreadById")
				.setParameter(1, threadId)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					FORUMTHREAD_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Thread.getThreadByGUID'
	 * @param guid atributo 'guid' del ForumThread que se consulta
	 * @return ForumThread cuyo atributo 'guid' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
	public ForumThread getThreadByGuid(String guid) 
			throws EntityNotPersistedException {
		ForumThread result = new ForumThread();
		try{
			result = (ForumThread) JPA.getManager()
				.createNamedQuery("Thread.getThreadByGUID")
				.setParameter(1, guid)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					FORUMTHREAD_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/*
	 * Realiza la consulta JPQL 'ForumThread.getThreadsInOrder'
	 * @return lista de todos los ForumThread de la base de datos
	@SuppressWarnings("unchecked")
	public List<ForumThread> getAllThreads(){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery("Thread.getThreadsInOrder")
					.setMaxResults(MAX_NEWS_TO_RETURN) // limit
					.getResultList();				
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	*/
	
	/**
	 * Realiza la consulta JPQL 'ForumThread.getThreadsInOrder'
	 * @param offset offset de los ForumThread devueltos
	 * @param limitNumThreads limite maximo de ForumThread devueltos
	 * @return lista de ForumThread, aplicando el offset y el limite maximo
	 * indicados en los parametrros
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getThreads(
			Integer offset, int limitNumThreads){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery("Thread.getThreadsInOrder")
					.setFirstResult(offset) // offset
					.setMaxResults(limitNumThreads) // limit
					.getResultList();				
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'ForumThread.getNumThreads'
	 * @return cantidad ForumThread de la base de datos
	 */
	public Integer getNumThreads() {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery(
						"Thread.getNumThreads", Long.class)
					.setMaxResults(MAX_NEWS_TO_RETURN) // limit
					.getResultList().stream().findFirst().orElse(null);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	/**
	 * Realiza la consulta 'Thread.getThreadsByLanguages'
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @return lista de ForumThread que pertenecen a los Language dados
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getAllThreadsByLanguages(
			List<Long> listLanguagesIDs) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery("Thread.getThreadsByLanguages")
					.setMaxResults(MAX_NEWS_TO_RETURN) // limit
					.setParameter(1, listLanguagesIDs)
					.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta 'Thread.getThreadsByLanguages'
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @param offset offset de los ForumThread devueltos
	 * @param limitNumThreads limite maximo de ForumThread devueltos
	 * @return lista de ForumThread que pertenecen a los Language dados,
	 * aplicando el offset y el limite recibidos por parametro
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getThreadsByLanguages(
			List<Long> listLanguagesIDs,
			Integer offset, int limitNumThreads){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery(
						"Thread.getThreadsByLanguages")
					.setFirstResult(offset) // offset
					.setMaxResults(limitNumThreads) // limit
					.setParameter(1, listLanguagesIDs)
					.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Thread.getNumThreadsByLanguages'
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @return cantidad de ForumThread que pertenecen a los Language dados
	 */
	public Integer getNumThreadsByLanguages(List<Long> listLanguagesIDs) {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery(
						"Thread.getNumThreadsByLanguages",Long.class)
					.setMaxResults(MAX_NEWS_TO_RETURN) // limit
					.setParameter(1, listLanguagesIDs)
					.getResultList().stream().findFirst().orElse(null);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	/*
	 * Realiza la consulta JPQL 'Thread.getThreadsByLanguagesAndCategory'
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @param category atributo 'id' del FeedCategory
	 * al que pertenecen los ForumThread que se desean consultar
	 * @return lista de ForumThread que pertenecen a los Language y al
	 * FeedCategory dados
	@SuppressWarnings("unchecked")
	public List<ForumThread> getAllThreadsByLanguagesAndCategory(
			List<Long> listLanguagesIDs, Long category){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery(
						"Thread.getThreadsByLanguagesAndCategory")
					.setMaxResults(MAX_NEWS_TO_RETURN) // limit
					.setParameter(1, category)
					.setParameter(2, listLanguagesIDs)
					.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	*/
	
	/**
	 * Realiza la consulta JPQL 'Thread.getThreadsByLanguagesAndCategory'
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @param category atributo 'id' del FeedCategory
	 * al que pertenecen los ForumThread que se desean consultar
	 * @param offset offset de los ForumThread devueltos
	 * @param limitNumThreads limite maximo de ForumThread devueltos
	 * @return lista de ForumThread que pertenecen a los Language y al
	 * FeedCategory dados, aplicando el offset y el limite maximo recibidos
	 * por parametro
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getThreadsByLanguagesAndCategory(
			List<Long> listLanguagesIDs, Long category,
			Integer offset, int limitNumThreads){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery(
						"Thread.getThreadsByLanguagesAndCategory")
					.setFirstResult(offset) // offset
					.setMaxResults(limitNumThreads) // limit
					.setParameter(1, category)
					.setParameter(2, listLanguagesIDs)
					.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL
	 * 'Thread.getNumThreadsByLanguagesAndCategory'
	 * @param listLanguagesIDs lista de atributos 'id' de los Language
	 * a los que pertenecen los ForumThread que se desean consultar
	 * @param category atributo 'id' del FeedCategory
	 * @return cantidad de ForumThread que pertenecen a los Language y al
	 * FeedCategory dados
	 */
	public Integer getNumThreadsByLanguagesAndCategory(
			List<Long> listLanguagesIDs, Long category) {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery(
						"Thread.getNumThreadsByLanguagesAndCategory",Long.class)
					.setMaxResults(MAX_NEWS_TO_RETURN) // limit
					.setParameter(1, category)
					.setParameter(2, listLanguagesIDs)
					.getResultList().stream().findFirst().orElse(null);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	/*
	 * Realiza la consulta JPQL 'Thread.getAllThreadsByCategory'
	 * @param category atributo 'id' del FeedCategory al que pertenecen los
	 * ForumThread que se consultan
	 * @return lista de ForumThread que pertenecen al FeedCategory dado
	@SuppressWarnings("unchecked")
	public List<ForumThread> getAllThreadsByCategory(Long category){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery("Thread.getThreadsByCategory")
					.setMaxResults(MAX_NEWS_TO_RETURN) // limit
					.setParameter(1, category)
					.getResultList();				
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	*/
	
	/**
	 * Realiza la consulta JPQL 'Thread.getThreadsByCategory'
	 * @param category atributo 'id' del FeedCategory al que pertenecen los
	 * ForumThread que se consultan
	 * @param offset offset de los ForumThread devueltos
	 * @param limitNumThreads limite maximo de ForumThread devueltos
	 * @return lista de ForumThread que pertenecen al FeedCategory dado,
	 * aplicando el offset y el limite maximo recibidos por parametro
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getThreadsByCategory(Long category,
			Integer offset, int limitNumThreads){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery("Thread.getThreadsByCategory")
					.setFirstResult(offset) // offset
					.setMaxResults(limitNumThreads) // limit
					.setParameter(1, category)
					.getResultList();				
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Thread.getNumThreadsByCategory'
	 * @param category atributo 'id' del FeedCategory al que pertenecen los
	 * ForumThread que se consultan
	 * @return cantidad de ForumThread que pertenecen al FeedCategory dado
	 */
	public Integer getNumThreadsByCategory(Long category) {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery(
						"Thread.getNumThreadsByCategory", Long.class)
					.setMaxResults(MAX_NEWS_TO_RETURN) // limit
					.setParameter(1, category)
					.getResultList().stream().findFirst().orElse(null);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	/**
	 * Realiza la consulta JPQL 'Thread.getMostValuedThreadsOfTheMonth'
	 * @return lista de los ForumThread publicados en el ultimo mes,
	 * cuyo ForumThreadInfo asociado tiene
	 * los mayores valores del atributo 'countVotes'
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getMostValuedThreadsOfTheMonth() {
		List<ForumThread> result = new ArrayList<ForumThread>();
		LocalDateTime currentTime = LocalDateTime.now();
		int currentMonth = currentTime.getMonth().getValue();
		int currentYear = currentTime.getYear();
		try{
			result = (List<ForumThread>) JPA.getManager()
				.createNamedQuery("Thread.getMostValuedThreadsOfTheMonth")
				.setMaxResults(3) // limit
				.setParameter(1, currentMonth)
				.setParameter(2, currentYear)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Thread.getMostCommentedThreadsOfTheMonth'
	 * @return lista de los ForumThread publicados en el ultimo mes,
	 * cuyo ForumThreadInfo asociado tiene
	 * los mayores valores del atributo 'countComments'
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getMostCommentedThreadsOfTheMonth() {
		List<ForumThread> result = new ArrayList<ForumThread>();
		LocalDateTime currentTime = LocalDateTime.now();
		int currentMonth = currentTime.getMonth().getValue();
		int currentYear = currentTime.getYear();
		try{
			result = (List<ForumThread>) JPA.getManager()
				.createNamedQuery("Thread.getMostCommentedThreadsOfTheMonth")
				.setMaxResults(3) // limit
				.setParameter(1, currentMonth)
				.setParameter(2, currentYear)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Thread.getLastThreadsByCategory'
	 * @param categoryName atributo 'name' del FeedCategory al que pertenecen
	 * los ForumThread que se consultan
	 * @return lista de los ForumThread cuya fecha es mas reciente,
	 * segun su atributo 'date', pertenecientes al FeedCategory dado
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getLastThreadsByCategory(Long categoryName) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
				.createNamedQuery("Thread.getLastThreadsByCategory")
				.setParameter(1, categoryName)
				.setMaxResults(3) // limit
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Thread.getAllForumThreadGUIDsInLastHour'
	 * @param currentDate fecha minima de los ForumThread que se consultan
	 * @param lastHourDate fecha maxima de los ForumThread que se consultan
	 * @return lista de los ForumThread cuyo atributo 'date' esta comprendido
	 * entre las dos fechas recibidas por parametro
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThread> getAllForumThreadsInLastHour(
			Date currentDate, Date lastHourDate){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
				.createNamedQuery("Thread.getAllForumThreadGUIDsInLastHour")
				.setParameter(1, currentDate)
				.setParameter(2, lastHourDate)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza la consulta JPQL 'Thread.getThreadVoters'
	 * @param threadId atributo 'id' del ForumThread al cual pertenecen los
	 * ForumThreadVoter que se desean consultar
	 * @return lista de ForumThreadVoter que pertenecen al ForumThread
	 * recibido por parametro
	 */
	@SuppressWarnings("unchecked")
	public List<ForumThreadVoter> getThreadVoters( Long threadId ){
		List<ForumThreadVoter> result = new ArrayList<ForumThreadVoter>();
		try{
			result = (List<ForumThreadVoter>) JPA.getManager()
				.createNamedQuery("Thread.getThreadVoters")
				.setParameter(1, threadId)
				.getResultList();
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Agrega a la base de datos el objeto ForumThread dado
	 * @param threadToCreate objeto ForumThread que se desea guardar
	 * @throws EntityAlreadyPersistedException
	 * @throws Exception
	 */
	public ForumThread restCreateForumThread(ForumThread threadToCreate)
			throws EntityAlreadyPersistedException, Exception{
		try{
			/* Feed feed = JPA.getManager().find(
					Feed.class, threadToCreate.getFeed().getId());
			threadToCreate.setFeed(feed); */
			JPA.getManager().persist( threadToCreate );
			JPA.getManager().persist( threadToCreate.getForumThreadInfo() );
			JPA.getManager().flush();
			JPA.getManager().refresh(threadToCreate);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					FORUMTHREAD_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new /*PersistenceRuntimeException*/Exception(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return threadToCreate;
	}
	
	/*
	 * Agrega a la base de datos los objetos ForumThread dados
	 * @param threadsToCreate lista de ForumThread que se desean guardar
	 * @throws EntityAlreadyPersistedException
	 * @throws Exception
	public void restCreateForumThreadsByList(List<ForumThread> threadsToCreate)
			throws EntityAlreadyPersistedException, Exception{
		try{
			for( ForumThread threadToCreate : threadsToCreate ){
				JPA.getManager().persist( threadToCreate );
				JPA.getManager().persist(threadToCreate.getForumThreadInfo());
				JPA.getManager().flush();
				JPA.getManager().refresh(threadToCreate);
			}
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					FORUMTHREAD_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new Exception(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	*/
	
	/**
	 * Elimina de la base de datos el objeto ForumThread indicado.
	 * @param threadId identificador del ForumThread que se elimina
	 * @throws EntityNotPersistedException
	 */
	public void deleteForumThread(Long threadId) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Thread.deleteForumThread")
				.setParameter(1, threadId)
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					THREADVOTER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	/**
	 * Genera un objeto ForumThreadVoter a partir de los parametros recibidos
	 * y lo agrega a la base de datos
	 * (es decir: crea una relacion 'User difunde ForumThread')
	 * @param userId atributo 'id' del User autor del ForumThreadVoter
	 * que se genera
	 * @param threadId atributo 'id' del ForumThread asociado al
	 * ForumThreadVoter que se genera
	 * @throws EntityAlreadyPersistedException
	 * @throws EntityNotPersistedException
	 */
	public void createThreadVoter(Long userId, Long threadId)
			throws EntityAlreadyPersistedException, EntityNotPersistedException {
		try{
			// En lugar de recibir por parametro el User y el ForumThread,
			// debemos obtener dichas entidades desde la bdd mediante JPA,
			// para evitar que esten en estado 'detatched' al hacer el persist
			User u = JPA.getManager().find(User.class, userId);
			ForumThread t = JPA.getManager().find(ForumThread.class, threadId);
			
			// Generar el ForumThreadVoter:
			ForumThreadVoter threadVoter = new ForumThreadVoter();
			threadVoter.setUser(u);
			threadVoter.setForumThread(t);
			
			// Guardar el ForumThreadVoter en bdd:
			JPA.getManager().persist(threadVoter);
			JPA.getManager().flush();
			JPA.getManager().refresh(threadVoter);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					THREADVOTER_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	/**
	 * Elimina de la base de datos el objeto TheadVoter
	 * que asocia al User y al ForumThread indicados.
	 * @param userId el User al que esta asociado el ForumTheadVoter
	 * @param thread ForumThread al que esta asociado el ForumTheadVoter
	 * @throws EntityNotPersistedException
	 */
	public void deleteThreadVoter(Long userId, ForumThread thread) 
			throws EntityNotPersistedException {
		try{
			JPA.getManager()
				.createNamedQuery("Thread.deleteVotersOfThread")
				.setParameter(1, userId)
				.setParameter(2, thread.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					THREADVOTER_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	/**
	 * Actualiza en la base de datos el objeto ForumThread dado y
	 * tambien su ForumThreadInfo asociado
	 * @param threadToUpdate objeto ForumThread que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
	public void updateAllDataByThread(ForumThread threadToUpdate)
			throws EntityNotPersistedException {
		try{
			JPA.getManager().merge( threadToUpdate.getForumThreadInfo() );
			JPA.getManager().merge( threadToUpdate );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					FORUMTHREAD_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	/**
	 * Actualiza en la base de datos el objeto ForumThread dado
	 * @param threadToUpdate objeto ForumThread que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
	public void updateDataByThread(ForumThread threadToUpdate)
			throws EntityNotPersistedException {
		try{	
			JPA.getManager().merge( threadToUpdate );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					FORUMTHREAD_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
}
