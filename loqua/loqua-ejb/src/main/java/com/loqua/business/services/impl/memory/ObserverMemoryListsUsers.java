package com.loqua.business.services.impl.memory;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.model.User;

public class ObserverMemoryListsUsers implements Observer{
	
	@Override
	public void update(Observable observable, Object object) {
		ServiceUser serviceUser=new LocatorLocalEjbServices().getServiceUser();
		
		List<User> mostValuedUsersOfTheMonth = 
				serviceUser.getMostValuedUsersOfTheMonthFromDB();
		List<User> mostActiveUsersOfTheMonth = 
				serviceUser.getMostActiveUsersOfTheMonthFromDB();
		
		Memory.getMemoryListsUsers().setMostValuedUsersOfTheMonth(
				mostValuedUsersOfTheMonth);
		Memory.getMemoryListsUsers().setMostActiveUsersOfTheMonth(
				mostActiveUsersOfTheMonth);
	}
}
