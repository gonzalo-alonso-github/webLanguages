package com.loqua.main;

import org.quartz.SchedulerException;

import com.loqua.logging.LoquaLogger;
import com.loqua.remote.RestTarget;
import com.loqua.scheduler.SchedulerCrawler;

/** Unica clase principal del proyecto 'loqua-aggregator' */
public class Main {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Dirreccion URL en la que se encuentra el servicio REST remoto
	 * (implementado en el proyecto 'loqua-web')*/
	private static final String URL_REST =
			"http://localhost:8080/loqua-web/rest/";
	
	/** Constructor sin parametros de la clase */
	public Main() {}

	/** Metodo principal de la clase */
	public static void main(String[] args) {
		new Main().run();
	}
	
	/** Inicia el funcionamiento de esta aplicacion cliente REST */
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