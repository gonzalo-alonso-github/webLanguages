package com.loqua.business.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionThread;
import com.loqua.business.services.serviceLocal.LocalServiceThread;
import com.loqua.business.services.serviceRemote.RemoteServiceThread;
import com.loqua.model.Language;
import com.loqua.model.ForumThread;

@Stateless
@WebService(name="ServiceThread")
public class EjbServiceThread 
		implements LocalServiceThread, RemoteServiceThread {

	private static final TransactionThread transactionThread = 
			new TransactionThread();
	
	@Override
	public ForumThread getThreadById(Long threadId) {
		return transactionThread.getThreadById(threadId);
	}
	
	@Override
	public ForumThread getThreadByGuid(String guid) throws EntityNotFoundException {
		return transactionThread.getThreadByGuid(guid);
	}
	
	@Override
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long category) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if( category==null || category==0 ){
			result= transactionThread.getThreadsByLanguagesFromDB(
					listLanguagesIDs);
		}else{
			result= transactionThread.getThreadsByLanguagesAndCategoryFromDB(
					listLanguagesIDs, category);
		}
		return result;
	}
	
	@Override
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromMemory(
			List<Language> listLanguages, Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		return transactionThread.getThreadsByLanguagesAndCategoryFromMemory(
				listLanguages, category, offsetThreads, numThreadsToReturn);
	}
	
	@Override
	public Integer getNumThreadsByLanguagesAndCategoryFromMemory(
			List<Language> listLanguages, Long category){
		return transactionThread.getNumThreadsByLanguagesAndCategoryFromMemory(
				listLanguages, category);
	}
	
	@Override
	public List<ForumThread> getThreadsByCategoryFromDB(Long category) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if( category==null || category==0 ){
			result = transactionThread.getThreads();
		}else{
			result= transactionThread.getThreadsByCategoryFromDB(category);
		}
		return result;
	}
	
	@Override
	public List<ForumThread> getThreadsByCategoryFromMemory(Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		return transactionThread.getThreadsByCategoryFromMemory(
				category,offsetThreads,numThreadsToReturn);
	}
	
	public Integer getNumThreadsByCategoryFromMemory(Long category){
		return transactionThread.getNumThreadsByCategoryFromMemory(category);
	}
	
	@Override
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromDB() {
		return transactionThread.getMostValuedThreadsOfTheMonthFromDB();
	}
	
	@Override
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromMemory() {
		return transactionThread.getMostValuedThreadsOfTheMonthFromMemory();
	}
	
	@Override
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB() {
		return transactionThread.getMostCommentedThreadsOfTheMonthFromDB();
	}
	
	@Override
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromMemory() {
		return transactionThread.getMostCommentedThreadsOfTheMonthFromMemory();
	}
	
	@Override
	public List<ForumThread> getLastThreadsByCategoryFromDB(Long category) {
		return transactionThread.getLastThreadsByCategoryFromDB(category);
	}
	
	@Override
	public List<ForumThread> getLastThreadsByCategoryFromMemory(Long category) {
		return transactionThread.getLastThreadsByCategoryFromMemory(category);
	}
	
	@Override
	public boolean threadAlreadyVotedByUser(Long userId, Long threadId)
			throws EntityNotFoundException {
		return transactionThread.threadAlreadyVotedByUser(userId, threadId);
	}
	
	@Override
	public void createThread(ForumThread threadToCreate, boolean justNow) 
			throws EntityAlreadyFoundException {
		transactionThread.create(threadToCreate, justNow);
	}
	
	@Override
	public void updateAllDataByThread(ForumThread threadToUpdate,
			boolean justNow) throws EntityNotFoundException{
		transactionThread.updateAllDataByThread(threadToUpdate, justNow);
	}
	
	@Override
	public void updateDataByThread(ForumThread threadToUpdate, boolean justNow)
			throws EntityNotFoundException{
		transactionThread.updateDataByThread(threadToUpdate, justNow);
	}
	
	@Override
	public ForumThread voteThread(Long userId, ForumThread threadToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		// Este metodo lanza "EntityAlreadyFoundException"
		// porque crea en bdd una entidad "CommentVoter"
		return transactionThread.voteThread(userId, threadToVote);
	}

	@Override
	public void incrementCountVisits(ForumThread threadToVisit)
			throws EntityNotFoundException {
		// Este metodo no lanza "EntityAlreadyFoundException"
		// porque no crea ninguna nueva entidad en bdd
		transactionThread.incrementCountVisits(threadToVisit);
	}
}
