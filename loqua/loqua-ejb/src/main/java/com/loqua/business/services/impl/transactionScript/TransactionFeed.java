package com.loqua.business.services.impl.transactionScript;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.memory.Memory;
import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;
import com.loqua.persistence.FeedJPA;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionFeed {

	private static final FeedJPA feedJPA = new FeedJPA();
	
	public Feed getFeedByID(Long feedID) throws EntityNotFoundException {
		Feed result = new Feed();
		try {
			result = feedJPA.getFeedById(feedID);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	public List<Long> getAllFeedCategoriesIdsFromMemory() {
		// Este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<Long> result = Memory.getMemoryListsThreads()
				.getListAllCategoriesIds();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result = Memory.getMemoryListsThreads().getListAllCategoriesIds();
		}
		return result;
	}
	
	public List<FeedCategory> getAllFeedCategoriesFromDB() {
		return feedJPA.getAllFeedCategories();
	}
	
	public List<FeedCategory> getAllFeedCategoriesFromMemory() {
		// A diferencia de this.getAllFeedCategoriesFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Memory
		List<FeedCategory> result = Memory.getMemoryListsThreads()
				.getListAllCategories();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la memoria:
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(true);
			result=Memory.getMemoryListsThreads().getListAllCategories();
		}
		return result;
	}
	/*
	public void createFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityAlreadyFoundException{
		try {
			feedJPA.createFeedCategory(feedCategory);
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(justNow);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	public void updateFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityNotFoundException {
		try {
			feedJPA.updateFeedCategory(feedCategory);
			Memory.getMemoryListsThreads().changed();
			Memory.updateMemoryListsThreads(justNow);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	*/
}
