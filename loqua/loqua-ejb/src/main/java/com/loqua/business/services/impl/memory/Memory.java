package com.loqua.business.services.impl.memory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class Memory {

	private static MemoryListsUsers memoryListsUsers = new MemoryListsUsers();
	private static MemoryListsLanguages memoryListsLanguages = 
			new MemoryListsLanguages();
	private static MemoryListsThreads memoryListsThreads = new MemoryListsThreads();
	private static TaskUpdateUsers taskUpdateUsers = new TaskUpdateUsers();
	private static TaskUpdateLanguages taskUpdateLanguages = 
			new TaskUpdateLanguages();
	private static TaskUpdateThreads taskUpdateThreads = new TaskUpdateThreads();

	public static void updateMemoryListsUsers(boolean justNow) {
		if( justNow==false && MemoryListsUsers.startedScheduledTask==true ){
			return;
		}else{
			if(MemoryListsUsers.startedScheduledTask==false){
				Memory.createObservers();
				MemoryListsUsers.startedScheduledTask=true;
			}
			updateMemory(taskUpdateUsers);
		}
	}
	
	public static void updateMemoryListsLanguages() {
		// Este metodo es utilizado desde el EjbServiceLanguage
		if(MemoryListsLanguages.startedScheduledTask==true){
			return;
		}
		Memory.createObservers();
		MemoryListsLanguages.startedScheduledTask=true;
		updateMemory(taskUpdateLanguages);
	}
	
	public static void updateMemoryListsThreads(boolean justNow) {
		// Este metodo es utilizado desde el EjbServiceThread
		if( justNow==false && MemoryListsThreads.startedScheduledTask==true ){
			return;
		}else{
			if(MemoryListsThreads.startedScheduledTask==false){
				Memory.createObservers();
				MemoryListsThreads.startedScheduledTask=true;
			}
			updateMemory(taskUpdateThreads);
		}
	}
	
	static void updateMemory(TimerTask taskUpdateMemory) {
		// Ejecuta por primera vez la tarea de notificar a los Observers:
		taskUpdateMemory.run();
		// Programa dicha tarea para que se repita periodicamente:
		ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(10);
		pool.scheduleWithFixedDelay(
				taskUpdateMemory,1,1,TimeUnit.HOURS);
	}
	
	static void createObservers() {
		ObserverMemoryListsUsers observerListsUsers =
				new ObserverMemoryListsUsers();
		ObserverMemoryListsLanguages observerMemoryListsLanguages =
				new ObserverMemoryListsLanguages();
		ObserverMemoryListsThreads observerMemoryListsThreads =
				new ObserverMemoryListsThreads();
		memoryListsUsers.addObserver( observerListsUsers );
		memoryListsLanguages.addObserver( observerMemoryListsLanguages );
		memoryListsThreads.addObserver( observerMemoryListsThreads );
	}
	
	public static MemoryListsUsers getMemoryListsUsers() {
		return memoryListsUsers;
	}
	public static void setMemoryListsUsers( MemoryListsUsers listsUsers) {
		Memory.memoryListsUsers = listsUsers;
	}
	
	public static MemoryListsLanguages getMemoryListsLanguages() {
		return memoryListsLanguages;
	}
	public static void setMemoryListsLanguages(MemoryListsLanguages listsLangs){
		Memory.memoryListsLanguages = listsLangs;
	}
	
	public static MemoryListsThreads getMemoryListsThreads() {
		return memoryListsThreads;
	}
	public static void setMemoryListsThreads(MemoryListsThreads listsThreads) {
		Memory.memoryListsThreads = listsThreads;
	}
	
	public static TaskUpdateLanguages getTaskUpdateLanguages(){
		return taskUpdateLanguages;
	}
}

class TaskUpdateUsers extends TimerTask {
    public void run() {
    	// este 'Observable.notifyObservers()' llaman automaticamente
    	// al metodo 'update' del Observer correspondiente...,
    	// pero solo si previamente se ha llamado al metodo 'setChanged'
    	// del Observable.
    	// Dicho 'Observable.setChanged()' es utilizado desde el EjbServiceUser 
        Memory.getMemoryListsUsers().notifyObservers();
    }
}

class TaskUpdateLanguages extends TimerTask {
    public void run() {
    	// este 'Observable.notifyObservers()' llaman automaticamente
    	// al metodo 'update' del Observer correspondiente...,
    	// pero solo si previamente se ha llamado al metodo 'setChanged'
    	// del Observable.
    	// Dicho 'Observable.setChanged()' es utilizado en EjbServiceLanguage
        Memory.getMemoryListsLanguages().notifyObservers();
    }
}

class TaskUpdateThreads extends TimerTask {
    public void run() {
    	// Primero actualizamos los lenguages, antes que las noticias:
    	// Memory.updateMemory( Memory.getTaskUpdateLanguages() );
    	
    	// este 'Observable.notifyObservers()' llama automaticamente
    	// al metodo 'update' del Observer correspondiente...,
    	// pero solo si previamente se ha llamado al metodo 'setChanged'
    	// del Observable.
    	// Dicho 'Observable.setChanged()' es utilizado desde el EjbServiceThread
        Memory.getMemoryListsThreads().notifyObservers();
    }
}
