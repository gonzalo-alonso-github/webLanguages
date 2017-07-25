package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;

public interface ServiceFeed {

	Feed getFeedByID(Long feedID) throws EntityNotFoundException;
	
	List<Long> getAllFeedCategoriesIdsFromMemory();
	
	List<FeedCategory> getAllFeedCategoriesFromDB();
	List<FeedCategory> getAllFeedCategoriesFromMemory();
	/*
	void createFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityAlreadyFoundException;

	void updateFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityNotFoundException;
	*/
}