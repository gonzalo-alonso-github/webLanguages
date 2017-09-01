package com.loqua.logging;

import org.apache.log4j.Logger;

public class LoquaLogger {

	private static final Logger loggerDebug = Logger.getLogger("debugAggregator");
	private String triggerName;
	
	public LoquaLogger(){
		triggerName = "";
	}
	
	public LoquaLogger(String triggerName){
		this.triggerName = triggerName;
	}
	
	// // // //
	// METODOS
	// // // //
	
	public void trace(String msgLog){
		if( loggerDebug.isTraceEnabled() ){
			loggerDebug.trace(triggerName + ": " + msgLog);
		}
	}
	
	public void debug(String msgLog){
		if( loggerDebug.isDebugEnabled() ){
			loggerDebug.debug(triggerName + ": " + msgLog);
		}
	}
	
	public void info(String msgLog){
		if( loggerDebug.isInfoEnabled() ){
			loggerDebug.info(triggerName + ": " + msgLog);
		}
	}
	
	public void warn(String msgLog){
		loggerDebug.warn(triggerName + ": " + msgLog);
	}
	
	public void error(String msgLog){
		loggerDebug.error(triggerName + ": " + msgLog);
	}
	
	public void fatal(String msgLog){
		loggerDebug.fatal(triggerName + ": " + msgLog);
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public static Logger getLoggerDebug() {
		return loggerDebug;
	}
}
