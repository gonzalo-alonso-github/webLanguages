package com.loqua.business.services.impl.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.loqua.business.services.ServiceFeed;
import com.loqua.business.services.ServiceForumThread;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.model.FeedCategory;
import com.loqua.model.ForumThread;

public class ObserverCacheListsThreads implements Observer{
	
	@Override
	public void update(Observable observable, Object object) {
		ServiceForumThread serviceThread =
				new LocatorLocalEjbServices().getServiceThread();
		ServiceFeed serviceFeed=new LocatorLocalEjbServices().getServiceFeed();
		List<FeedCategory> categories = serviceFeed.getAllFeedCategoriesFromDB();
		
		List<ForumThread> mostValuedThreadsOfTheMonth = 
				serviceThread.getMostValuedThreadsOfTheMonthFromDB();
		List<ForumThread> mostCommenteThreadsOfTheMonth = 
				serviceThread.getMostCommentedThreadsOfTheMonthFromDB();
		
		Cache.getCacheListsThreads().setMostValuedThreadsOfTheMonth(
				mostValuedThreadsOfTheMonth);
		Cache.getCacheListsThreads().setMostCommentedThreadsOfTheMonth(
				mostCommenteThreadsOfTheMonth);
		Cache.getCacheListsThreads().setListAllCategories(categories);
		
		loadMaps(categories);
	}
	
	private void loadMaps(List<FeedCategory> categories){
		ServiceForumThread serviceNew=new LocatorLocalEjbServices().getServiceThread();
		
		// Carga en mapLastThreadsByCategory las ultimas noticias, sin filtrar categoria
		CacheListsThreads.mapLastThreadsByCategory.put(0L,
				serviceNew.getLastThreadsByCategoryFromDB(0L));
		// Carga en mapThreadsByCategory todas las noticias, sin filtrar categoria
		CacheListsThreads.mapThreadsByCategory.put(0L,
				serviceNew.getAllThreadsByCategoryFromDB(0L));
		// Carga en mapThreadsByLanguagesAndCategory todas las noticias, 
		// sin filtrar categoria
		loadMapLanguagesCombinations(0L);
		
		// Carga en los Maps las noticias de cada categoria existente:
		for(FeedCategory category : categories){
			Long categoryId = category.getId();
			// Cargar mapLastThreadsByCategory; cada categoria en una key:
			CacheListsThreads.mapLastThreadsByCategory.put(categoryId,
					serviceNew.getLastThreadsByCategoryFromDB(categoryId));
			// Cargar mapThreadsByCategory; cada categoria en una key
			CacheListsThreads.mapThreadsByCategory.put(categoryId,
					serviceNew.getAllThreadsByCategoryFromDB(categoryId));
			// Cargar mapThreadsByLanguagesAndCategory; cada categoria en una key
			loadMapLanguagesCombinations(categoryId);
		}
	}
	
	private void loadMapLanguagesCombinations(Long category) {
		LanguagesCombinations langCombin = new LanguagesCombinations(category);
		Map<String, List<ForumThread>> mapLanguagesCombinations 
			= new HashMap<String, List<ForumThread>>();
		mapLanguagesCombinations.putAll(langCombin.getMapLanguagesCombinations());
		CacheListsThreads.mapThreadsByLanguagesAndCategory.put(
				category, mapLanguagesCombinations);
	}
}
