package com.loqua.business.services.impl.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.loqua.model.FeedCategory;
import com.loqua.model.ForumThread;

public class CacheListsThreads extends Observable{

	static boolean startedScheduledTask = false;
	private static List<ForumThread> mostValuedThreadsOfTheMonth
		= new ArrayList<ForumThread>();
	private static List<ForumThread> mostCommentededThreadsOfTheMonth
		= new ArrayList<ForumThread>();
	static Map<Long, List<ForumThread>> mapLastThreadsByCategory 
			= new HashMap<Long, List<ForumThread>>();
	static Map<Long, List<ForumThread>> mapThreadsByCategory 
			= new HashMap<Long, List<ForumThread>>();
	static Map<Long, Map<String, List<ForumThread>>> mapThreadsByLanguagesAndCategory 
			= new HashMap<Long, Map<String, List<ForumThread>>>();
	private static List<Long> listAllCategoriesIds = new ArrayList<Long>();
	private static List<FeedCategory> listAllCategories 
			= new ArrayList<FeedCategory>();
	
	public void changed() {
		setChanged();
	}

	public List<ForumThread> getMostValuedThreadsOfTheMonth() {
		return mostValuedThreadsOfTheMonth;
	}
	public void setMostValuedThreadsOfTheMonth(
			List<ForumThread> mostValuedThreads){
		CacheListsThreads.mostValuedThreadsOfTheMonth = mostValuedThreads;
	}
	
	public List<Long> getListAllCategoriesIds() {
		return listAllCategoriesIds;
	}
	public List<FeedCategory> getListAllCategories() {
		return listAllCategories;
	}
	public void setListAllCategories(List<FeedCategory> listAllCategories){
		CacheListsThreads.listAllCategories = listAllCategories;
		for(FeedCategory category : listAllCategories){
			listAllCategoriesIds.add(category.getId());
		}
	}
	
	public List<ForumThread> getMostCommentedThreadsOfTheMonth() {
		return mostCommentededThreadsOfTheMonth;
	}
	public void setMostCommentedThreadsOfTheMonth(
			List<ForumThread> mostCommentededThreads) {
		CacheListsThreads.mostCommentededThreadsOfTheMonth =
				mostCommentededThreads;
	}
	
	public List<ForumThread> getLastThreadsByCategory(Long category) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if(mapLastThreadsByCategory!=null && !mapLastThreadsByCategory.isEmpty()){
			if( category==null || category==0 ){
				result = mapLastThreadsByCategory.get(0L);
			}else{
				result = mapLastThreadsByCategory.get(category);
			}
		}
		return result;
	}
	
	public List<ForumThread> getThreadsByCategory(
			Long category, Integer offsetThreads, int numThreadsToReturn) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if( mapThreadsByCategory!=null && ! mapThreadsByCategory.isEmpty() ){
			List<ForumThread> allThreads = new ArrayList<ForumThread>();
			if( category==null || category==0 ){
				allThreads = mapThreadsByCategory.get(0L);
			}else{
				allThreads = mapThreadsByCategory.get(category);
			}
			int last = offsetThreads+numThreadsToReturn;
			for( int i=offsetThreads; i<allThreads.size() && i<last; i++ ){
				result.add( allThreads.get(i) );
			}
		}
		return result;
	}
	
	public Integer getNumThreadsByCategory(Long category) {
		Integer result = null;
		if( mapThreadsByCategory!=null && ! mapThreadsByCategory.isEmpty() ){
			if( category==null || category==0 ){
				result = mapThreadsByCategory.get(0L).size();
			}else{
				result = mapThreadsByCategory.get(category).size();
			}
		}
		return result;
	}
	
	public List<ForumThread> getThreadsByLanguagesAndCategory(
			List<Long> listLanguagesIDs,
			Long category, Integer offsetThreads, int numThreadsToReturn) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if( mapThreadsByLanguagesAndCategory!=null 
				&& ! mapThreadsByLanguagesAndCategory.isEmpty() ){
			List<ForumThread> allThreads = new ArrayList<ForumThread>();
			if( category==null || category==0 ){
				allThreads = mapThreadsByLanguagesAndCategory
						.get(0L).get(listLanguagesIDs.toString());
			}else{
				allThreads = mapThreadsByLanguagesAndCategory
						.get(category).get(listLanguagesIDs.toString());
			}
			int last = offsetThreads+numThreadsToReturn;
			for( int i=offsetThreads; i<allThreads.size() && i<last; i++ ){
				result.add( allThreads.get(i) );
			}
		}
		return result;
	}
	
	public Integer getNumThreadsByLanguagesAndCategory(
			List<Long> listLanguagesIDs, Long category) {
		Integer result = null;
		if( mapThreadsByLanguagesAndCategory!=null 
				&& ! mapThreadsByLanguagesAndCategory.isEmpty() ){
			if( category==null || category==0 ){
				result = mapThreadsByLanguagesAndCategory
						.get(0L).get(listLanguagesIDs.toString()).size();
			}else{
				result = mapThreadsByLanguagesAndCategory
						.get(category).get(listLanguagesIDs.toString()).size();
			}
		}
		return result;
	}
}
