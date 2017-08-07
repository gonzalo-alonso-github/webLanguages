package com.loqua.business.services.impl.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.loqua.model.Language;

public class CacheListsLanguages extends Observable{

	static boolean startedScheduledTask = false;
	private static Map<Long,Language> allLanguages = 
			new HashMap<Long,Language>();
	
	public void changed() {
		setChanged();
	}

	public Map<Long,Language> getAllLanguages() {
		return allLanguages;
	}
	public void setAllLanguages(Map<Long,Language> allLanguages) {
		CacheListsLanguages.allLanguages = allLanguages;
	}
}
