package com.loqua.main;

import org.quartz.SchedulerException;

import com.loqua.logging.LoquaLogger;
import com.loqua.remote.RestTarget;
import com.loqua.scheduler.SchedulerCrawler;

public class Main {

	/**
	 * Manejador de logging
	 */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static final String URL_REST =
			"http://localhost:8080/loqua-web/rest/";
	
	public Main() {}

	public static void main(String[] args) {
		new Main().run();
	}
	
	private void run() {
		RestTarget.initializeRestServices(URL_REST, "UOHy0wV/73SFu7/QTuLz/Q==");
		SchedulerCrawler scheduler = new SchedulerCrawler();
		try{
			scheduler.execute();
		}catch( Exception e ){
			String msg = "Aggregator application will be terminated";
			log.error("Unexpected Exception at 'run()': " + msg);
			// Termina el programa:
			try{ 
				scheduler.shutdown();
			}catch(SchedulerException se ){
				msg = "Aggregator application could not be terminated";
				log.error("SchedulerException at 'run()': " + msg);
			}
			return;
		}
	}
}