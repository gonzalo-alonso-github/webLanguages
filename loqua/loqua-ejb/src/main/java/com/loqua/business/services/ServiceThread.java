package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Language;
import com.loqua.model.ForumThread;

public interface ServiceThread {

	ForumThread getThreadById(Long threadId);
	
	ForumThread getThreadByGuid(String guid) throws EntityNotFoundException;
	
	List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long category);
	List<ForumThread> getThreadsByLanguagesAndCategoryFromMemory(
			List<Language> listLanguages, Long category,
			Integer offsetNews, int numNewsToReturn);
	Integer getNumThreadsByLanguagesAndCategoryFromMemory(
			List<Language> listLanguages, Long category);
	
	List<ForumThread> getThreadsByCategoryFromDB(Long category);
	List<ForumThread> getThreadsByCategoryFromMemory(Long category,
			Integer offsetNews, int numNewsToReturn);
	Integer getNumThreadsByCategoryFromMemory(Long category);

	List<ForumThread> getMostValuedThreadsOfTheMonthFromDB();
	List<ForumThread> getMostValuedThreadsOfTheMonthFromMemory();
	
	List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB();
	List<ForumThread> getMostCommentedThreadsOfTheMonthFromMemory();
	
	List<ForumThread> getLastThreadsByCategoryFromDB(Long category);
	List<ForumThread> getLastThreadsByCategoryFromMemory(Long category);

	boolean threadAlreadyVotedByUser(Long userId, Long threadId)
			throws EntityNotFoundException;
	
	void createThread(ForumThread threadToCreate, boolean justNow)
			throws EntityAlreadyFoundException;

	void updateAllDataByThread(ForumThread threadToUpdate, boolean justNow) 
			throws EntityNotFoundException;
	
	void updateDataByThread(ForumThread threadToUpdate, boolean justNow) 
			throws EntityNotFoundException;
	
	ForumThread voteThread(Long userId, ForumThread threadToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException;

	void incrementCountVisits(ForumThread threadToVisit)
			throws EntityNotFoundException;
}