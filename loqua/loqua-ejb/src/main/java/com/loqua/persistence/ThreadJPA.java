package com.loqua.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadVoter;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class ThreadJPA {
	
	private static final int MAX_NEWS_TO_RETURN = 1000;
	private static final String FORUMTHREAD_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Thread' entity not found"
			+ " at Persistence layer";
	private static final String FORUMTHREAD_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'FotumThread' entity already"
			+ " found at Persistence layer";
	private static final String THREADVOTER_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'ThreadVoter' entity already"
			+ " found at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
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
	
	@SuppressWarnings("unchecked")
	public List<ForumThread> getThreads(){
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
	
	@SuppressWarnings("unchecked")
	public List<ForumThread> getThreads(
			Integer offsetRows, int numThreadsToReturn){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery("Thread.getThreadsInOrder")
					.setFirstResult(offsetRows) // offset
					.setMaxResults(numThreadsToReturn) // limit
					.getResultList();				
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public Integer getNumThreads() {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery(
						"Thread.getNumThreads", Long.class)
					.getResultList().stream().findFirst().orElse(null);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
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
	
	@SuppressWarnings("unchecked")
	public List<ForumThread> getAllThreadsByLanguagesAndCategoryFromDB(
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
	
	@SuppressWarnings("unchecked")
	public List<ForumThread> getThreadsByLanguagesAndCategory(
			List<Long> listLanguagesIDs, Long category,
			Integer offsetRows, int numThreadsToReturn){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery(
						"Thread.getThreadsByLanguagesAndCategory")
					.setFirstResult(offsetRows) // offset
					.setMaxResults(numThreadsToReturn) // limit
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
	
	public Integer getNumThreadsByLanguages(List<Long> listLanguagesIDs) {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery(
						"Thread.getNumThreadsByLanguagesAndCategory",Long.class)
					.setParameter(1, listLanguagesIDs)
					.getResultList().stream().findFirst().orElse(null);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	public Integer getNumThreadsByLanguagesAndCategory(
			List<Long> listLanguagesIDs, Long category) {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery(
						"Thread.getNumThreadsByLanguagesAndCategory",Long.class)
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
	
	@SuppressWarnings("unchecked")
	public List<ForumThread> getThreadsByCategory(Long category,
			Integer offsetRows, int numThreadsToReturn){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = (List<ForumThread>) JPA.getManager()
					.createNamedQuery("Thread.getThreadsByCategory")
					.setFirstResult(offsetRows) // offset
					.setMaxResults(numThreadsToReturn) // limit
					.setParameter(1, category)
					.getResultList();				
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public Integer getNumThreadsByCategory(Long category) {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
					.createNamedQuery(
						"Thread.getNumThreadsByCategory", Long.class)
					.setParameter(1, category)
					.getResultList().stream().findFirst().orElse(null);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
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
	
	@SuppressWarnings("unchecked")
	public List<ForumThread> getAllForumThreadGUIDsInLastHour(
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
	
	public void restCreateForumThread(ForumThread threadToCreate)
			throws EntityAlreadyPersistedException, Exception{
		try{
			/*Feed feed = JPA.getManager().find(
					Feed.class, threadToCreate.getFeed().getId());
			threadToCreate.setFeed(feed);*/
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
	}
	/*
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
