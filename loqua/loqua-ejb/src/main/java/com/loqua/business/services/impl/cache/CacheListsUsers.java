package com.loqua.business.services.impl.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.loqua.business.services.impl.MapEntityCounterByDate;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.model.User;

public class CacheListsUsers extends Observable{

	static boolean startedScheduledTask = false;
	private static List<User> mostValuedUsersOfTheMonth = new ArrayList<User>();
	private static List<User> mostActiveUsersOfTheMonth = new ArrayList<User>();
	private static int numRegisteredUsers = -1;
	private static MapEntityCounterByDate numLastRegistrations = 
			new MapEntityCounterByDate();
	
	public void changed() {
		setChanged();
	}

	public List<User> getMostValuedUsersOfTheMonth() {
		return mostValuedUsersOfTheMonth;
	}
	public void setMostValuedUsersOfTheMonth(
			List<User> mostValuedUsersOfTheMonth) {
		CacheListsUsers.mostValuedUsersOfTheMonth =
				mostValuedUsersOfTheMonth;
	}
	
	public List<User> getMostActiveUsersOfTheMonth() {
		return mostActiveUsersOfTheMonth;
	}
	public void setMostActiveUsersOfTheMonth(
			List<User> mostActiveUsersOfTheMonth) {
		CacheListsUsers.mostActiveUsersOfTheMonth =
				mostActiveUsersOfTheMonth;
	}

	public MapEntityCounterByDate getNumLastRegistrations() {
		return numLastRegistrations;
	}
	public void setNumLastRegistrations(
			MapEntityCounterByDate numLastRegistrations) {
		CacheListsUsers.numLastRegistrations = numLastRegistrations;
	}
	public void updateNumLastRegistrationsFromDB() {
		// Se llama a este metodo cada vez que se registra un usuario
		// (desde TransactionUser.create() )
		MapEntityCounterByDate resultFromDB = null;
		resultFromDB = new LocatorLocalEjbServices()
					.getServiceUser().getNumLastRegistrationsFromDB();
		numLastRegistrations = (resultFromDB==null) ?
				new MapEntityCounterByDate() : resultFromDB;
	}
	
	public int getNumRegisteredUsers() {
		return numRegisteredUsers;
	}
	public void setNumRegisteredUsers(int numUsers) {
		CacheListsUsers.numRegisteredUsers = numUsers;
	}
	public void updateNumRegisteredUsersFromDB() {
		// Se llama a este metodo
		// cada vez que un usuario se registre o cambie el estado 'removed'
		// (desde TransactionUser.create(), updateDataByUser() y
		// updateAllDataByUser() )
		numRegisteredUsers = new LocatorLocalEjbServices()
					.getServiceUser().getNumRegisteredUsersAndAdminFromDB();
	}
}
