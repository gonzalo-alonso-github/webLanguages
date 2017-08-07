package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Language;
import com.loqua.model.ForumThread;

public interface ServiceForumThread {

	/* A priori no se va a utilizar ninguno de los metodos
	que contienen el sufijo 'FromCache'. Si se utilizan, entonces conviene
	descomentar el uso de la Cache aquellos metodos de la clase
	TansactionThread que realicen creaciones y actualizaciones:
	TansactionThread.restCreateForumThread
	TansactionThread.updateAllDataByThread
	TansactionThread.updateDataByThread */
	
	ForumThread getThreadById(Long threadId);
	
	ForumThread getThreadByGuid(String guid) throws EntityNotFoundException;
	
	List<ForumThread> getAllThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long category);
	List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long category,
			Integer offsetNews, int numNewsToReturn);
	List<ForumThread> getThreadsByLanguagesAndCategoryFromCache(
			List<Language> listLanguages, Long category,
			Integer offsetNews, int numNewsToReturn);
	
	Integer getNumThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long category);
	Integer getNumThreadsByLanguagesAndCategoryFromCache(
			List<Language> listLanguages, Long category);
	
	List<ForumThread> getAllThreadsByCategoryFromDB(Long category);
	List<ForumThread> getThreadsByCategoryFromDB(Long category,
			Integer offsetNews, int numNewsToReturn);
	List<ForumThread> getThreadsByCategoryFromCache(Long category,
			Integer offsetNews, int numNewsToReturn);
	
	Integer getNumThreadsByCategoryFromDB(Long category);
	Integer getNumThreadsByCategoryFromCache(Long category);

	List<ForumThread> getMostValuedThreadsOfTheMonthFromDB();
	List<ForumThread> getMostValuedThreadsOfTheMonthFromCache();
	
	List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB();
	List<ForumThread> getMostCommentedThreadsOfTheMonthFromCache();
	
	List<ForumThread> getLastThreadsByCategoryFromDB(Long category);
	List<ForumThread> getLastThreadsByCategoryFromCache(Long category);
	
	// Para el cliente Aggregator
	List<ForumThread> getAllForumThreadGUIDsInLastHour();

	boolean threadAlreadyVotedByUser(Long userId, Long threadId)
			throws EntityNotFoundException;
	
	// Para el cliente Aggregator
	void restCreateForumThread(ForumThread threadToCreate, boolean justNow)
			throws EntityAlreadyFoundException, Exception;
	/*void restCreateForumThreadsByList(List<ForumThread> threads, boolean justNow)
			throws EntityAlreadyFoundException, Exception;*/
	
	void updateAllDataByThread(ForumThread threadToUpdate, boolean justNow) 
			throws EntityNotFoundException;
	
	void updateDataByThread(ForumThread threadToUpdate, boolean justNow) 
			throws EntityNotFoundException;
	
	ForumThread voteThread(Long userId, ForumThread threadToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException;

	void incrementCountVisits(ForumThread threadToVisit)
			throws EntityNotFoundException;
}