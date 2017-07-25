package com.loqua.business.services.impl.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.ServiceThread;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.model.Language;
import com.loqua.model.ForumThread;

public class LanguagesCombinations {
	
	private static Map<String, List<ForumThread>> mapLanguagesCombinations
		= new HashMap<String, List<ForumThread>>();
	private ServiceLanguage serviceLanguage = 
			new LocatorLocalEjbServices().getServiceLanguage();
	private ServiceThread serviceNew = 
			new LocatorLocalEjbServices().getServiceThread();
	private Long category;
	
	public LanguagesCombinations(Long category){
		this.category = category;
		loadMapLanguagesCombinations();
	}
	
	private void loadMapLanguagesCombinations() {
		List<Language> allLanguages = new ArrayList<Language>(
				serviceLanguage.getAllLanguagesFromMemory().values() );
		List<Long> allLanguageLocales = 
				listLanguagesToLanguageIDs(allLanguages);
		for(int lenCombin=1; lenCombin<=allLanguages.size(); lenCombin++){
			loadCombinationsToMap(allLanguageLocales, lenCombin, 0, 
					new ArrayList<Long>());
		}
	}
	
	private List<Long> listLanguagesToLanguageIDs(List<Language> languages){
		List<Long> result = new ArrayList<Long>();
		for(Language language : languages){
			result.add(language.getId());
		}
		return result;
	}
	
	private void loadCombinationsToMap(List<Long> allLanguageLocales,
			int lenComb, int startPosition, List<Long> combination){
        if (lenComb == 0){
        	mapLanguagesCombinations.put(
        			combination.toString(),
        			serviceNew.getThreadsByLanguagesAndCategoryFromDB(
        					combination,category));
            return;
        }       
        for(int i = startPosition; i <= allLanguageLocales.size()-lenComb; i++){
        	combination.add(allLanguageLocales.get(i));
        	loadCombinationsToMap(allLanguageLocales,lenComb-1,i+1,combination);
        	combination.remove(combination.size()-1);
        }
    }
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public Map<String, List<ForumThread>> getMapLanguagesCombinations(){
		return mapLanguagesCombinations;
	}
}
