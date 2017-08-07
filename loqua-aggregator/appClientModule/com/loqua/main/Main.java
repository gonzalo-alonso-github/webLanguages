package com.loqua.main;

import org.quartz.SchedulerException;

import com.loqua.remote.RestTarget;
import com.loqua.scheduler.SchedulerCrawler;

public class Main {

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
			// Termina el programa:
			try{ scheduler.shutdown(); }catch( SchedulerException se ){}
			return;
		}
	}
}