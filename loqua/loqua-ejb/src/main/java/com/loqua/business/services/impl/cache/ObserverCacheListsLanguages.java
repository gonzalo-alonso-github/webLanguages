package com.loqua.business.services.impl.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.model.Language;

public class ObserverCacheListsLanguages implements Observer{
	
	@Override
	public void update(Observable observable, Object object) {
		ServiceLanguage serviceLanguage = 
				new LocatorLocalEjbServices().getServiceLanguage();

		List<Language> listLanguages = serviceLanguage.getListAllLanguagesFromDB();
		Map<Long,Language> mapLanguages = new HashMap<Long,Language>();
		for(Language language : listLanguages){
			mapLanguages.put(language.getId(), language);
		}
		Cache.getCacheListsLanguages().setAllLanguages(mapLanguages);
	}
}
