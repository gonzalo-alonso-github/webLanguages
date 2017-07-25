package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.memory.Memory;
import com.loqua.model.Language;
import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadInfo;
import com.loqua.model.ForumThreadVoter;
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
		return threadJPA.getThreadsByLanguages(listLanguagesIDs);
	}

	public List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long category){
		return threadJPA.getThreadsByLanguagesAndCategory(
				listLanguagesIDs, category);
	}
	
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromMemory(
			List<Language> listLanguages, Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		// A diferencia de this.getThreadsByLanguagesAndCategoryFromDB(...),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<ForumThread> result = new ArrayList<ForumThread>();
		List<Long> listLanguagesIDs=listLanguagesToLanguageIDs(listLanguages);
		offsetThreads=(offsetThreads==null || offsetThreads==0)? 
				0 : (offsetThreads-1)*numThreadsToReturn;
		result= Memory.getMemoryListsThreads().getThreadsByLanguagesAndCategory(
				listLanguagesIDs, category, offsetThreads, numThreadsToReturn);
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result= Memory.getMemoryListsThreads().getThreadsByLanguagesAndCategory(
					listLanguagesIDs, category, offsetThreads, numThreadsToReturn);
		}
		return result;
	}
	
	public Integer getNumThreadsByLanguagesAndCategoryFromMemory(
			List<Language> listLanguages, Long category){
		Integer result = null;
		List<Long> listLanguagesIDs=listLanguagesToLanguageIDs(listLanguages);
		result = Memory.getMemoryListsThreads()
				.getNumThreadsByLanguagesAndCategory(listLanguagesIDs, category);
		if( result==null ){
			// Si el valor no estaba guardado en memoria
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result = Memory.getMemoryListsThreads()
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
	
	public List<ForumThread> getThreadsByCategoryFromDB(Long category) {
		return threadJPA.getThreadsByCategory(category);
	}
	
	public List<ForumThread> getThreadsByCategoryFromMemory(Long category,
			Integer offsetThreads, int numThreadsToReturn) {
		// A diferencia de this.getThreadsByCategoryFromBD(...),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<ForumThread> result = new ArrayList<ForumThread>();
		offsetThreads=(offsetThreads==null || offsetThreads==0)? 
				0 : (offsetThreads-1)*numThreadsToReturn;
		result= Memory.getMemoryListsThreads().getThreadsByCategory(
				category, offsetThreads, numThreadsToReturn);
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result= Memory.getMemoryListsThreads().getThreadsByCategory(
					category, offsetThreads, numThreadsToReturn);
		}
		return result;
	}
	
	public Integer getNumThreadsByCategoryFromMemory(Long category){
		Integer result = null;
		result = Memory.getMemoryListsThreads().getNumThreadsByCategory(category);
		if( result==null ){
			// Si el valor no estaba guardado en memoria
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result= Memory.getMemoryListsThreads().getNumThreadsByCategory(category);
		}
		return result;
	}
	
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromDB() {
		return threadJPA.getMostValuedThreadsOfTheMonth();
	}
	
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromMemory() {
		// A diferencia de this.getMostValuedThreadsOfTheMonthFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<ForumThread> result = Memory.getMemoryListsThreads()
				.getMostValuedThreadsOfTheMonth();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result = Memory.getMemoryListsThreads().getMostValuedThreadsOfTheMonth();
		}
		return result;
	}
	
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB() {
		return threadJPA.getMostCommentedThreadsOfTheMonth();
	}
	
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromMemory() {
		// A diferencia de this.getMostCommentedThreadsOfTheMonthFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<ForumThread> result = Memory.getMemoryListsThreads()
				.getMostCommentedThreadsOfTheMonth();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result=Memory.getMemoryListsThreads().getMostCommentedThreadsOfTheMonth();
		}
		return result;
	}
	
	public List<ForumThread> getLastThreadsByCategoryFromDB(Long category) {
		return threadJPA.getLastThreadsByCategory(category);
	}
	
	public List<ForumThread> getLastThreadsByCategoryFromMemory(Long category) {
		// A diferencia de this.getLastThreadsByCategoryFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<ForumThread> result = Memory.getMemoryListsThreads()
				.getLastThreadsByCategory(category);
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result=Memory.getMemoryListsThreads()
					.getLastThreadsByCategory(category);
		}
		return result;
	}
	
	public List<ForumThreadVoter> getThreadVoters(Long threadId) {
		return threadJPA.getThreadVoters(threadId);
	}
	
	public boolean threadAlreadyVotedByUser(Long userId, Long threadId){
		List<ForumThreadVoter> threadVoters = getThreadVoters(threadId);
		for( ForumThreadVoter threadVoter : threadVoters ){
			if( threadVoter.getUser().getId() == userId ) return true;
		}
		return false;
	}
	
	public void create(ForumThread newThreadToCreate, boolean justNow)
			throws EntityAlreadyFoundException{
		try {
			threadJPA.create(newThreadToCreate);
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(justNow);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	public void updateAllDataByThread(ForumThread threadToUpdate,
			boolean justNow) throws EntityNotFoundException {
		try {
			threadJPA.updateAllDataByThread(threadToUpdate);
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(justNow);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void updateDataByThread(ForumThread threadToUpdate, boolean justNow)
			throws EntityNotFoundException {
		try {
			threadJPA.updateDataByThread(threadToUpdate);
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(justNow);
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
