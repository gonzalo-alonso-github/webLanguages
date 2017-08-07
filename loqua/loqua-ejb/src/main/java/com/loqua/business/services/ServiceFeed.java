package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;

public interface ServiceFeed {

	/* A priori no se va a utilizar ninguno de los metodos
	que contienen el sufijo 'FromCache'. Si se utilizan, entonces conviene
	descomentar el uso de la Cache aquellos metodos de la clase
	TansactionFeed que realicen creaciones y actualizaciones:
	TansactionFeed.createFeedCategory
	TansactionFeed.updateFeedCategory */
	
	Feed getFeedByID(Long feedID) throws EntityNotFoundException;
	
	List<Feed> restGetAllFeeds();
	
	List<Long> getAllFeedCategoriesIdsFromDB();
	List<Long> getAllFeedCategoriesIdsFromCache();
	
	List<FeedCategory> getAllFeedCategoriesFromDB();
	List<FeedCategory> getAllFeedCategoriesFromCache();
	/*
	void createFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityAlreadyFoundException;

	void updateFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityNotFoundException;
	*/
}