package com.loqua.logging;

import org.apache.log4j.Logger;

/** Administra las caracteristicas del logging y se encarga de invocar al
 * 'logger' indicado para efectuar la salida de los mensajes de log. */
public class LoquaLogger {

	/** Indica el 'logger' que imprimira los mensajes de logging.
	 * El nombre del 'logger' debe estar definido en el fichero 'log4j.xml' */
	private static final Logger loggerDebug =
			Logger.getLogger("debugAggregator");
	
	/** Nombre del 'trigger' (puede ser el nombre de una clase o de un metodo),
	 * que provoca la aparicion del mensaje de log */
	private String triggerName;
	
	/** Constructor sin parametros de la clase */
	public LoquaLogger(){
		triggerName = "";
	}
	
	/** Constructor que inicializa los atributos de la clase
	 * @param triggerName atributo {@link #triggerName} que se inicializa
	 */
	public LoquaLogger(String triggerName){
		this.triggerName = triggerName;
	}
	
	// // // //
	// METODOS
	// // // //
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'trace'.
	 * @param msgLog mensaje que se muestra en la salida
	 */
	public void trace(String msgLog){
		if( loggerDebug.isTraceEnabled() ){
			loggerDebug.trace(triggerName + ": " + msgLog);
		}
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'debug'.
	 * @param msgLog mensaje que se muestra en la salida
	 */
	public void debug(String msgLog){
		if( loggerDebug.isDebugEnabled() ){
			loggerDebug.debug(triggerName + ": " + msgLog);
		}
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'info'.
	 * @param msgLog mensaje que se muestra en la salida
	 */
	public void info(String msgLog){
		if( loggerDebug.isInfoEnabled() ){
			loggerDebug.info(triggerName + ": " + msgLog);
		}
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'warn'.
	 * @param msgLog mensaje que se muestra en la salida
	 */
	public void warn(String msgLog){
		loggerDebug.warn(triggerName + ": " + msgLog);
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'error'.
	 * @param msgLog mensaje que se muestra en la salida
	 */
	public void error(String msgLog){
		loggerDebug.error(triggerName + ": " + msgLog);
	}
	
	/** Invoca al 'logger' para efectuar un mensaje de nivel 'fatal'.
	 * @param msgLog mensaje que se muestra en la salida
	 */
	public void fatal(String msgLog){
		loggerDebug.fatal(triggerName + ": " + msgLog);
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/** Halla el 'logger' que se ha establecido para
	 * efectuar la salida de los mensajes de log
	 * @return el objeto Logger de la clase */
	public static Logger getLoggerDebug() {
		return loggerDebug;
	}
}
