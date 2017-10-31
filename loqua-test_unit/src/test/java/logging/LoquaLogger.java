package logging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/** Administra las caracteristicas del logging y se encarga de invocar al
 * 'logger' indicado para efectuar la salida de los mensajes de log. */
public class LoquaLogger {

	/** Indica el 'logger' que imprimira los mensajes de logging.
	 * El nombre del 'logger' debe estar definido en el fichero 'log4j.xml' */
	private static Logger loggerDebug;
	
	/** Nombre del 'trigger' (puede ser el nombre de una clase o de un metodo),
	 * que provoca la aparicion del mensaje de log */
	private String triggerName;
	
	/** Constructor sin parametros de la clase */
	public LoquaLogger(){
		triggerName = "";
		loadLogger();
	}
	
	/** Constructor que inicializa los atributos de la clase
	 * @param triggerName atributo {@link #triggerName} que se inicializa
	 */
	public LoquaLogger(String trigger){
		triggerName = trigger;
		loadLogger();
	}
	
	/**
	 * Inicializa el objeto Logger {@link #loggerDebug}
	 */
	private void loadLogger() {
		ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
		PropertyConfigurator.configure(classLoader.getResourceAsStream(
				"log4j.properties"));
		loggerDebug = LogManager.getLogger("debugTest");
	}
	
	// // // //
	// METODOS
	// // // //
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'trace' */
	public void trace(String msgLog){
		if( loggerDebug.isTraceEnabled() ){
			loggerDebug.trace(triggerName + ": " + msgLog);
		}
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'debug' */
	public void debug(String msgLog){
		if( loggerDebug.isDebugEnabled() ){
			loggerDebug.debug(triggerName + ": " + msgLog);
		}
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'info' */
	public void info(String msgLog){
		if( loggerDebug.isInfoEnabled() ){
			loggerDebug.info(triggerName + ": " + msgLog);
		}
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'warn' */
	public void warn(String msgLog){
		loggerDebug.warn(triggerName + ": " + msgLog);
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'error' */
	public void error(String msgLog){
		loggerDebug.error(triggerName + ": " + msgLog);
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'fatal' */
	public void fatal(String msgLog){
		loggerDebug.fatal(triggerName + ": " + msgLog);
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/** Devuelve el 'logger' que se ha establecido para
	 * efectuar la salida de los mensajes de log */
	public static Logger getLoggerDebug() {
		return loggerDebug;
	}
}
