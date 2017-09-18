package com.loqua.scheduler;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.loqua.logging.LoquaLogger;

/**
 * Inicia o termina la tarea de {@link JobCrawler}, estableciendo
 * una periodicidad de ejecucion de una vez cada hora en punto.
 * @author Gonzalo
 */
public class SchedulerCrawler {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Objeto Scheduler de la API Quartz que asocia una tarea (objeto 'Job',
	 * en este caso {@link JobCrawler}) con el lanzador que indica
	 * la periodicidad de su ejecucion (objeto 'Trigger') */
	private static Scheduler scheduler;
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Crea la programacion de la tarea {@link JobCrawler} estableciendo
	 * una periodicidad de ejecucion de una vez cada hora en punto.
	 * @throws SchedulerException
	 */
	public void execute() throws SchedulerException{
		// Se crea una instancia de Scheduler
		SchedulerFactory sf = new StdSchedulerFactory();
		scheduler = sf.getScheduler();
		
		/* Se crea una instancia de Job (representa la tarea
		que va a tener lugar en el momento que señalara el Trigger) */
		JobDetail job = newJob(JobCrawler.class)
				.withIdentity("nameJobCrawler", "groupJobsCrawler")
				.build();
		
		/* Se crea una instancia de Trigger (representa al lanzador de tareas)
		La expresion Cron indica la periodicidad: cada hora en punto */
		// "0 0 * * * ?"
		CronTrigger trigger = newTrigger()
				.withIdentity("nameTriggerCrawler", "groupJobsCrawler")
				.withSchedule(cronSchedule("0 0 * * * ?"))
				.build();
		log.info("'execute()': Schedule job every hour (0 0 * * * ?)");
		
		// El objeto Scheduler asocia la tarea (Job) con el lanzador (Trigger)
		scheduler.scheduleJob(job, trigger);
		
		// Se inician las tareas programadas
		log.info("'execute()': Starting Scheduler...");
		scheduler.start();
		
		/* El scheduler no finalizará hasta que se haga 'scheduler.shutdown'
		La clase Main se encarga de eso */
	}
	
	/** Finaliza la programacion de la tarea. La tarea no volvera a ejecutarse
	 * hasta que el programador sea iniciado de nuevo.
	 * @throws SchedulerException
	 */
	public void shutdown() throws SchedulerException{
		scheduler.shutdown();
		log.info("'shutdown()': Scheduler terminated");
	}
}
