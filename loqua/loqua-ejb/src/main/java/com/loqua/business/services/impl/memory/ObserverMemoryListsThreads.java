package com.loqua.business.services.impl.memory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.loqua.business.services.ServiceFeed;
import com.loqua.business.services.ServiceThread;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.model.FeedCategory;
import com.loqua.model.ForumThread;

public class ObserverMemoryListsThreads implements Observer{
	
	@Override
	public void update(Observable observable, Object object) {
		ServiceThread serviceThread =
				new LocatorLocalEjbServices().getServiceThread();
		ServiceFeed serviceFeed=new LocatorLocalEjbServices().getServiceFeed();
		List<FeedCategory> categories = serviceFeed.getAllFeedCategoriesFromDB();
		
		List<ForumThread> mostValuedThreadsOfTheMonth = 
				serviceThread.getMostValuedThreadsOfTheMonthFromDB();
		List<ForumThread> mostCommenteThreadsOfTheMonth = 
				serviceThread.getMostCommentedThreadsOfTheMonthFromDB();
		
		Memory.getMemoryListsThreads().setMostValuedThreadsOfTheMonth(
				mostValuedThreadsOfTheMonth);
		Memory.getMemoryListsThreads().setMostCommentedThreadsOfTheMonth(
				mostCommenteThreadsOfTheMonth);
		Memory.getMemoryListsThreads().setListAllCategories(categories);
		
		loadMaps(categories);
	}
	
	private void loadMaps(List<FeedCategory> categories){
		ServiceThread serviceNew=new LocatorLocalEjbServices().getServiceThread();
		
		// Carga en mapLastThreadsByCategory las ultimas noticias, sin filtrar categoria
		MemoryListsThreads.mapLastThreadsByCategory.put(0L,
				serviceNew.getLastThreadsByCategoryFromDB(0L));
		// Carga en mapThreadsByCategory todas las noticias, sin filtrar categoria
		MemoryListsThreads.mapThreadsByCategory.put(0L,
				serviceNew.getThreadsByCategoryFromDB(0L));
		// Carga en mapThreadsByLanguagesAndCategory todas las noticias, 
		// sin filtrar categoria
		loadMapLanguagesCombinations(0L);
		
		// Carga en los Maps las noticias de cada categoria existente:
		for(FeedCategory category : categories){
			Long categoryId = category.getId();
			// Cargar mapLastThreadsByCategory; cada categoria en una key:
			MemoryListsThreads.mapLastThreadsByCategory.put(categoryId,
					serviceNew.getLastThreadsByCategoryFromDB(categoryId));
			// Cargar mapThreadsByCategory; cada categoria en una key
			MemoryListsThreads.mapThreadsByCategory.put(categoryId,
					serviceNew.getThreadsByCategoryFromDB(categoryId));
			// Cargar mapThreadsByLanguagesAndCategory; cada categoria en una key
			loadMapLanguagesCombinations(categoryId);
		}
	}
	
	private void loadMapLanguagesCombinations(Long category) {
		LanguagesCombinations langCombin = new LanguagesCombinations(category);
		Map<String, List<ForumThread>> mapLanguagesCombinations 
			= new HashMap<String, List<ForumThread>>();
		mapLanguagesCombinations.putAll(langCombin.getMapLanguagesCombinations());
		MemoryListsThreads.mapThreadsByLanguagesAndCategory.put(
				category, mapLanguagesCombinations);
	}
}
