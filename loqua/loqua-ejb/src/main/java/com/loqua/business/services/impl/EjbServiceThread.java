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
	public List<ForumThread> getAllThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long category) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if( category==null || category==0 ){
			result= transactionThread.getThreadsByLanguagesFromDB(
					listLanguagesIDs);
		}else{
			result= transactionThread.getAllThreadsByLanguagesAndCategoryFromDB(
					listLanguagesIDs, category);
		}
		return result;
	}
	
	@Override
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		return transactionThread.getThreadsByLanguagesAndCategoryFromDB(
				listLanguages, category, offsetThreads, numThreadsToReturn);
	}
	
	@Override
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromCache(
			List<Language> listLanguages, Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		return transactionThread.getThreadsByLanguagesAndCategoryFromCache(
				listLanguages, category, offsetThreads, numThreadsToReturn);
	}
	
	@Override
	public Integer getNumThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long category){
		return transactionThread.getNumThreadsByLanguagesAndCategoryFromDB(
				listLanguages, category);
	}
	
	@Override
	public Integer getNumThreadsByLanguagesAndCategoryFromCache(
			List<Language> listLanguages, Long category){
		return transactionThread.getNumThreadsByLanguagesAndCategoryFromCache(
				listLanguages, category);
	}
	
	@Override
	public List<ForumThread> getAllThreadsByCategoryFromDB(Long category) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if( category==null || category==0 ){
			result = transactionThread.getThreads();
		}else{
			result= transactionThread.getAllThreadsByCategoryFromDB(category);
		}
		return result;
	}
	
	@Override
	public List<ForumThread> getThreadsByCategoryFromDB(Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		return transactionThread.getThreadsByCategoryFromDB(
				category,offsetThreads,numThreadsToReturn);
	}
	
	@Override
	public List<ForumThread> getThreadsByCategoryFromCache(Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		return transactionThread.getThreadsByCategoryFromCache(
				category,offsetThreads,numThreadsToReturn);
	}
	
	@Override
	public Integer getNumThreadsByCategoryFromDB(Long category){
		return transactionThread.getNumThreadsByCategoryFromDB(category);
	}
	
	@Override
	public Integer getNumThreadsByCategoryFromCache(Long category){
		return transactionThread.getNumThreadsByCategoryFromCache(category);
	}
	
	@Override
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromDB() {
		return transactionThread.getMostValuedThreadsOfTheMonthFromDB();
	}
	
	@Override
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromCache() {
		return transactionThread.getMostValuedThreadsOfTheMonthFromCache();
	}
	
	@Override
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB() {
		return transactionThread.getMostCommentedThreadsOfTheMonthFromDB();
	}
	
	@Override
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromCache() {
		return transactionThread.getMostCommentedThreadsOfTheMonthFromCache();
	}
	
	@Override
	public List<ForumThread> getLastThreadsByCategoryFromDB(Long category) {
		return transactionThread.getLastThreadsByCategoryFromDB(category);
	}
	
	@Override
	public List<ForumThread> getLastThreadsByCategoryFromCache(Long category) {
		return transactionThread.getLastThreadsByCategoryFromCache(category);
	}
	
	@Override
	public List<ForumThread> getAllForumThreadGUIDsInLastHour() {
		// Para el cliente Aggregator
		return transactionThread.getAllForumThreadGUIDsInLastHour();
	}
	
	@Override
	public boolean threadAlreadyVotedByUser(Long userId, Long threadId)
			throws EntityNotFoundException {
		return transactionThread.threadAlreadyVotedByUser(userId, threadId);
	}
	
	@Override
	public void restCreateForumThread(ForumThread threadToCreate, boolean justNow) 
			throws EntityAlreadyFoundException, Exception {
		// Para el cliente Aggregator
		transactionThread.restCreateForumThread(threadToCreate, justNow);
	}
	/*
	@Override
	public void restCreateForumThreadsByList(
			List<ForumThread> threadsToCreate, boolean justNow) 
			throws EntityAlreadyFoundException, Exception {
		// Para el cliente Aggregator
		transactionThread.restCreateForumThreadsByList(threadsToCreate,justNow);
	}
	*/
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
