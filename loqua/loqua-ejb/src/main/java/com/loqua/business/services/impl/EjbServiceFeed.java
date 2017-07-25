package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionFeed;
import com.loqua.business.services.serviceLocal.LocalServiceFeed;
import com.loqua.business.services.serviceRemote.RemoteServiceFeed;
import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;

@Stateless
@WebService(name="ServiceFeed")
public class EjbServiceFeed 
		implements LocalServiceFeed, RemoteServiceFeed {

	private static final TransactionFeed transactionFeed = 
			new TransactionFeed();
	
	@Override
	public Feed getFeedByID(Long feedID) throws EntityNotFoundException {
		return transactionFeed.getFeedByID(feedID);
	}
	
	@Override
	public List<Long> getAllFeedCategoriesIdsFromMemory() {
		return transactionFeed.getAllFeedCategoriesIdsFromMemory();
	}
	
	@Override
	public List<FeedCategory> getAllFeedCategoriesFromDB() {
		return transactionFeed.getAllFeedCategoriesFromDB();
	}
	
	@Override
	public List<FeedCategory> getAllFeedCategoriesFromMemory() {
		return transactionFeed.getAllFeedCategoriesFromMemory();
	}
	/*
	@Override
	public void createFeedCategory(FeedCategory feedCategory, boolean justNow) 
			throws EntityAlreadyFoundException {
		transactionFeed.createFeedCategory(feedCategory, justNow);
	}
	
	@Override
	public void updateFeedCategory(FeedCategory feedCategory, boolean justNow)
			throws EntityNotFoundException {
		transactionFeed.updateFeedCategory(feedCategory, justNow);
	}
	*/
}
