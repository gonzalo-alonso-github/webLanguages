package com.loqua.business.services.impl.transactionScript;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.cache.Cache;
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
	
	public List<Feed> getAllFeeds() {
		// Este metodo see llama desde el cliente Aggregator
		return feedJPA.getAllFeeds();
	}
	
	public List<Long> getAllFeedCategoriesIdsFromDB() {
		return feedJPA.getAllFeedCategoriesIds();
	}
	public List<Long> getAllFeedCategoriesIdsFromCache() {
		// Este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Cache
		List<Long> result = Cache.getCacheListsThreads()
				.getListAllCategoriesIds();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result = Cache.getCacheListsThreads().getListAllCategoriesIds();
		}
		return result;
	}
	
	public List<FeedCategory> getAllFeedCategoriesFromDB() {
		return feedJPA.getAllFeedCategories();
	}
	
	public List<FeedCategory> getAllFeedCategoriesFromCache() {
		// A diferencia de this.getAllFeedCategoriesFromDB(),
		// este metodo evita hacer un acceso a base de datos,
		// puesto que devuelve la lista guardada en Cache
		List<FeedCategory> result = Cache.getCacheListsThreads()
				.getListAllCategories();
		if( result.isEmpty() ){
			// Pero si esta vacia, entonces
			// activamos desde ahora la actualizacion periodica de la Cache:
			Cache.getCacheListsThreads().changed();
			Cache.updateCacheListsThreads(true);
			result=Cache.getCacheListsThreads().getListAllCategories();
		}
		return result;
	}
	/*
	public void createFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityAlreadyFoundException{
		try {
			feedJPA.createFeedCategory(feedCategory);
			// Queda comentado. A priori no se va a usar la Cache
			// Cache.getCacheListsThreads().changed();
			// Cache.updateCacheListsThreads(justNow);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	public void updateFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityNotFoundException {
		try {
			feedJPA.updateFeedCategory(feedCategory);
			// Queda comentado. A priori no se va a usar la Cache
			// Cache.getCacheListsThreads().changed();
			// Cache.updateCacheListsThreads(justNow);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	*/
}
