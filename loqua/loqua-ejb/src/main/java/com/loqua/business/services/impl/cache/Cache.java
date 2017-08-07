package com.loqua.business.services.impl.cache;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class Cache {

	private static CacheListsUsers cacheListsUsers = new CacheListsUsers();
	private static CacheListsLanguages cacheListsLanguages = 
			new CacheListsLanguages();
	private static CacheListsThreads cacheListsThreads = new CacheListsThreads();
	private static TaskUpdateUsers taskUpdateUsers = new TaskUpdateUsers();
	private static TaskUpdateLanguages taskUpdateLanguages = 
			new TaskUpdateLanguages();
	private static TaskUpdateThreads taskUpdateThreads = new TaskUpdateThreads();

	public static void updateCacheListsUsers(boolean justNow) {
		if( justNow==false && CacheListsUsers.startedScheduledTask==true ){
			return;
		}else{
			if(CacheListsUsers.startedScheduledTask==false){
				Cache.createObservers();
				CacheListsUsers.startedScheduledTask=true;
			}
			updateCache(taskUpdateUsers);
		}
	}
	
	public static void updateCacheListsLanguages() {
		// Este metodo es utilizado desde el EjbServiceLanguage
		if(CacheListsLanguages.startedScheduledTask==true){
			return;
		}
		Cache.createObservers();
		CacheListsLanguages.startedScheduledTask=true;
		updateCache(taskUpdateLanguages);
	}
	
	public static void updateCacheListsThreads(boolean justNow) {
		// Este metodo es utilizado desde el EjbServiceThread
		if( justNow==false && CacheListsThreads.startedScheduledTask==true ){
			return;
		}else{
			if(CacheListsThreads.startedScheduledTask==false){
				Cache.createObservers();
				CacheListsThreads.startedScheduledTask=true;
			}
			updateCache(taskUpdateThreads);
		}
	}
	
	static void updateCache(TimerTask taskUpdateCache) {
		// Ejecuta la tarea de notificar a los Observers:
		taskUpdateCache.run();
		// Programa dicha tarea para que se repita periodicamente:
		ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(10);
		pool.scheduleWithFixedDelay(
				taskUpdateCache,1,1,TimeUnit.HOURS);
	}
	
	static void createObservers() {
		ObserverCacheListsUsers observerListsUsers =
				new ObserverCacheListsUsers();
		ObserverCacheListsLanguages observerCacheListsLanguages =
				new ObserverCacheListsLanguages();
		ObserverCacheListsThreads observerCacheListsThreads =
				new ObserverCacheListsThreads();
		cacheListsUsers.addObserver( observerListsUsers );
		cacheListsLanguages.addObserver( observerCacheListsLanguages );
		cacheListsThreads.addObserver( observerCacheListsThreads );
	}
	
	public static CacheListsUsers getCacheListsUsers() {
		return cacheListsUsers;
	}
	public static void setCacheListsUsers( CacheListsUsers listsUsers) {
		Cache.cacheListsUsers = listsUsers;
	}
	
	public static CacheListsLanguages getCacheListsLanguages() {
		return cacheListsLanguages;
	}
	public static void setCacheListsLanguages(CacheListsLanguages listsLangs){
		Cache.cacheListsLanguages = listsLangs;
	}
	
	public static CacheListsThreads getCacheListsThreads() {
		return cacheListsThreads;
	}
	public static void setCacheListsThreads(CacheListsThreads listsThreads) {
		Cache.cacheListsThreads = listsThreads;
	}
	
	public static TaskUpdateLanguages getTaskUpdateLanguages(){
		return taskUpdateLanguages;
	}
}

class TaskUpdateUsers extends TimerTask {
    public void run() {
    	// este 'Observable.notifyObservers()' llama automaticamente
    	// al metodo 'update' del Observer correspondiente...,
    	// pero solo si previamente se ha llamado al metodo 'setChanged'
    	// del Observable.
    	// Dicho 'Observable.setChanged()' es utilizado desde el EjbServiceUser 
        Cache.getCacheListsUsers().notifyObservers();
    }
}


class TaskUpdateLanguages extends TimerTask {
    public void run() {
    	// este 'Observable.notifyObservers()' llaman automaticamente
    	// al metodo 'update' del Observer correspondiente...,
    	// pero solo si previamente se ha llamado al metodo 'setChanged'
    	// del Observable.
    	// Dicho 'Observable.setChanged()' es utilizado en EjbServiceLanguage
        Cache.getCacheListsLanguages().notifyObservers();
    }
}

class TaskUpdateThreads extends TimerTask {
    public void run() {
    	// Primero actualizamos los lenguages, antes que las noticias:
    	// Cache.updateCache( Cache.getTaskUpdateLanguages() );
    	
    	// este 'Observable.notifyObservers()' llama automaticamente
    	// al metodo 'update' del Observer correspondiente...,
    	// pero solo si previamente se ha llamado al metodo 'setChanged'
    	// del Observable.
    	// Dicho 'Observable.setChanged()' es utilizado desde el EjbServiceThread
        Cache.getCacheListsThreads().notifyObservers();
    }
}
