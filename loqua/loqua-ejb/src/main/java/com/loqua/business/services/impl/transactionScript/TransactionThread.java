package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.cache.Cache;
import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadInfo;
import com.loqua.model.ForumThreadVoter;
import com.loqua.model.Language;
import com.loqua.persistence.ThreadJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionThread {

	private static final ThreadJPA threadJPA = new ThreadJPA();
	
	public ForumThread getThreadById(Long threadId) {
		ForumThread result = new ForumThread();
		try {
			result = threadJPA.getThreadById(threadId);
		} catch (EntityNotPersistedException ex) {
			return null;
		}
		return result;
	}
	
	public ForumThread getThreadByGuid(String guid) throws EntityNotFoundException {
		ForumThread result = new ForumThread();
		try {
			result = threadJPA.getThreadByGuid(guid);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public List<ForumThread> getThreadsByLanguagesFromDB(
			List<Long> listLanguagesIDs) {
		return threadJPA.getAllThreadsByLanguages(listLanguagesIDs);
	}

	public List<ForumThread> getAllThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long category){
		if( category==null || category==0 ){
			return threadJPA.getAllThreadsByLanguages(
					listLanguagesIDs);
		}else{
			return threadJPA.getAllThreadsByLanguagesAndCategoryFromDB(
					listLanguagesIDs, category);
		}
	}
	
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		List<Long> listLanguagesIDs=listLanguagesToLanguageIDs(listLanguages);
		offsetThreads=(offsetThreads==null || offsetThreads==0)? 
				0 : (offsetThreads-1)*numThreadsToReturn;
		return threadJPA.getThreadsByLanguagesAndCategory(
				listLanguagesIDs, category, offsetThreads, numThreadsToReturn);
	}
	
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromCache(
			List<Language> listLanguages, Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		// A diferencia de this.getThreadsByLanguagesAndCategoryFromDB(...),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Cache
		List<ForumThread> result = new ArrayList<ForumThread>();
		List<Long> listLanguagesIDs=listLanguagesToLanguageIDs(listLanguages);
		offsetThreads=(offsetThreads==null || offsetThreads==0)? 
				0 : (offsetThreads-1)*numThreadsToReturn;
		result= Cache.getCacheListsThreads().getThreadsByLanguagesAndCategory(
				listLanguagesIDs, category, offsetThreads, numThreadsToReturn);
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result= Cache.getCacheListsThreads().getThreadsByLanguagesAndCategory(
					listLanguagesIDs, category, offsetThreads, numThreadsToReturn);
		}
		return result;
	}
	
	public Integer getNumThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long category){
		Integer result = 0;
		List<Long> listLanguagesIDs=listLanguagesToLanguageIDs(listLanguages);
		if( category==null || category==0 ){
			result = threadJPA.getNumThreadsByLanguages(listLanguagesIDs);
		}else{
			result = threadJPA.getNumThreadsByLanguagesAndCategory(
					listLanguagesIDs, category);
		}
		return result;
	}
	
	public Integer getNumThreadsByLanguagesAndCategoryFromCache(
			List<Language> listLanguages, Long category){
		Integer result = null;
		List<Long> listLanguagesIDs=listLanguagesToLanguageIDs(listLanguages);
		result = Cache.getCacheListsThreads()
				.getNumThreadsByLanguagesAndCategory(listLanguagesIDs, category);
		if( result==null ){
			// Si el valor no estaba guardado en Cache
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result = Cache.getCacheListsThreads()
				.getNumThreadsByLanguagesAndCategory(listLanguagesIDs, category);
		}
		return result;
	}
	
	private List<Long> listLanguagesToLanguageIDs(List<Language> languages){
		List<Long> result = new ArrayList<Long>();
		for(Language language : languages){
			result.add(language.getId());
		}
		return result;
	}
	
	public List<ForumThread> getThreads() {
		return threadJPA.getThreads();
	}
	
	public List<ForumThread> getAllThreadsByCategoryFromDB(Long category) {
		return threadJPA.getAllThreadsByCategory(category);
	}
	
	public List<ForumThread> getThreadsByCategoryFromDB(Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		offsetThreads=(offsetThreads==null || offsetThreads==0)? 
				0 : (offsetThreads-1)*numThreadsToReturn;
		if( category==null || category==0 ){
			return threadJPA.getThreads(offsetThreads, numThreadsToReturn);
		}else{
			return threadJPA.getThreadsByCategory(
				category, offsetThreads, numThreadsToReturn);
		}
	}
	
	public List<ForumThread> getThreadsByCategoryFromCache(Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		// A diferencia de this.getThreadsByCategoryFromBD(...),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Cache
		List<ForumThread> result = new ArrayList<ForumThread>();
		offsetThreads=(offsetThreads==null || offsetThreads==0)? 
				0 : (offsetThreads-1)*numThreadsToReturn;
		result= Cache.getCacheListsThreads().getThreadsByCategory(
				category, offsetThreads, numThreadsToReturn);
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result= Cache.getCacheListsThreads().getThreadsByCategory(
					category, offsetThreads, numThreadsToReturn);
		}
		return result;
	}
	
	public Integer getNumThreadsByCategoryFromDB(Long category){
		Integer result = 0;
		if( category==null || category==0 ){
			result = threadJPA.getNumThreads();
		}else{
			result = threadJPA.getNumThreadsByCategory(category);
		}
		return result;
	}
	
	public Integer getNumThreadsByCategoryFromCache(Long category){
		Integer result = null;
		result = Cache.getCacheListsThreads().getNumThreadsByCategory(category);
		if( result==null ){
			// Si el valor no estaba guardado en Cache
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result= Cache.getCacheListsThreads().getNumThreadsByCategory(category);
		}
		return result;
	}
	
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromDB() {
		return threadJPA.getMostValuedThreadsOfTheMonth();
	}
	
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromCache() {
		// A diferencia de this.getMostValuedThreadsOfTheMonthFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Cache
		List<ForumThread> result = Cache.getCacheListsThreads()
				.getMostValuedThreadsOfTheMonth();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result = Cache.getCacheListsThreads().getMostValuedThreadsOfTheMonth();
		}
		return result;
	}
	
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB() {
		return threadJPA.getMostCommentedThreadsOfTheMonth();
	}
	
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromCache() {
		// A diferencia de this.getMostCommentedThreadsOfTheMonthFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Cache
		List<ForumThread> result = Cache.getCacheListsThreads()
				.getMostCommentedThreadsOfTheMonth();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result=Cache.getCacheListsThreads().getMostCommentedThreadsOfTheMonth();
		}
		return result;
	}
	
	public List<ForumThread> getLastThreadsByCategoryFromDB(Long category) {
		return threadJPA.getLastThreadsByCategory(category);
	}
	
	public List<ForumThread> getLastThreadsByCategoryFromCache(Long category) {
		// A diferencia de this.getLastThreadsByCategoryFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Cache
		List<ForumThread> result = Cache.getCacheListsThreads()
				.getLastThreadsByCategory(category);
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result=Cache.getCacheListsThreads()
					.getLastThreadsByCategory(category);
		}
		return result;
	}
	
	public List<ForumThread> getAllForumThreadGUIDsInLastHour() {
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
		return threadJPA.getAllForumThreadGUIDsInLastHour(
				currentHourDate,previousHourDate);
	}
	
	public boolean threadAlreadyVotedByUser(Long userId, Long threadId){
		List<ForumThreadVoter> threadVoters = getThreadVoters(threadId);
		for( ForumThreadVoter threadVoter : threadVoters ){
			if( threadVoter.getUser().getId() == userId ) return true;
		}
		return false;
	}
	
	private List<ForumThreadVoter> getThreadVoters(Long threadId) {
		return threadJPA.getThreadVoters(threadId);
	}
	
	public void restCreateForumThread(ForumThread threadToCreate, boolean justNow)
			throws EntityAlreadyFoundException, Exception{
		try {
			threadJPA.restCreateForumThread(threadToCreate);
			/* Queda comentado. A priori no se va a usar la Cache
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(justNow); */
		} catch( EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	/*
	public void restCreateForumThreadsByList(
			List<ForumThread> threadsToCreate, boolean justNow)
			throws EntityAlreadyFoundException, Exception{
		try {
			threadJPA.restCreateForumThreadsByList(threadsToCreate);
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(justNow);
		} catch( EntityAlreadyPersistedException ex ) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	*/
	public void updateAllDataByThread(ForumThread threadToUpdate,
			boolean justNow) throws EntityNotFoundException {
		try {
			threadJPA.updateAllDataByThread(threadToUpdate);
			/* Queda comentado. A priori no se va a usar la Cache
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(justNow); */
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void updateDataByThread(ForumThread threadToUpdate, boolean justNow)
			throws EntityNotFoundException {
		try {
			threadJPA.updateDataByThread(threadToUpdate);
			/* Queda comentado. A priori no se va a usar la Cache
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(justNow); */
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	public ForumThread voteThread(Long userId, ForumThread threadToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		// Este metodo lanza "EntityAlreadyFoundException"
		// porque crea en bdd una entidad "CommentVoter"
		try {
			// Primero: actualiza en bdd el ThreadInfo (numero de votos)
			ForumThreadInfo threadInfo = threadToVote.getForumThreadInfo();
			threadInfo.setCountVotes( threadInfo.getCountVotes()+1 );
			threadToVote.setForumThreadInfo( threadInfo );
			updateAllDataByThread( threadToVote, true );
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
	
	public void incrementCountVisits(ForumThread threadToUpdate)
			throws EntityNotFoundException {
		// Este metodo no lanza "EntityAlreadyFoundException"
		// porque no crea ninguna nueva entidad en bdd
		ForumThreadInfo threadInfoToUpdate =
				threadToUpdate.getForumThreadInfo();
		threadInfoToUpdate.incrementCountVisits();
		threadToUpdate.setForumThreadInfo(threadInfoToUpdate);
		updateAllDataByThread(threadToUpdate, true);
	}

	public void incrementCountComments(ForumThread threadToUpdate)
			throws EntityNotFoundException {
		ForumThreadInfo threadInfoToUpdate =
				threadToUpdate.getForumThreadInfo();
		threadInfoToUpdate.incrementCountComments();
		threadToUpdate.setForumThreadInfo(threadInfoToUpdate);
		updateAllDataByThread(threadToUpdate, true);
	}
}
