package com.loqua.business.services.impl.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.loqua.model.Language;

public class MemoryListsLanguages extends Observable{

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
		MemoryListsLanguages.allLanguages = allLanguages;
	}
}
