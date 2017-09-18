package com.loqua.business.services.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Maneja un Map&lt;String, Integer&gt; para guardar el numero de ocurrencias
 * de cualquier eventualidad en distintos lapsos de tiempo
 * @author Gonzalo
 */
public class MapEntityCounterByDate{
	
	/** Guarda el numero de ocurrencias
	 * de cualquier eventualidad en cierto lapso de tiempo,
	 * donde la 'key' es el nombre del lapso de tiempo
	 * ('lastMinute', 'lastFiveMinutes', 'lastQuarter', 'lastHour', 'lastDay',
	 * 'lastWeek', 'lastMonth' y 'lastYear') y el 'value'
	 * es la cantidad de veces que el suceso ha ocurrido en dicho lapso
	 */
	private Map<String, Integer> map;
	
	/** Constructor sin parametros. <br/>
	 * Iniializa a -1 todos los valores del Map&lt;String, Integer&gt;
	 * de la clase
	 */
	public MapEntityCounterByDate(){
		map = new HashMap<String, Integer>();
		setOccurrencesLastMinute(-1);
		setOccurrencesLastFiveMinutes(-1);
		setOccurrencesLastQuarter(-1);
		setOccurrencesLastHour(-1);
		setOccurrencesLastDay(-1);
		setOccurrencesLastWeek(-1);
		setOccurrencesLastMonth(-1);
		setOccurrencesLastYear(-1);
	}
	
	/**
	 * Comprueba si el Map&lt;String, Integer&gt; de la clase no ha sido
	 * alterado desde su inicializacion
	 * @return
	 * true: si el Map&lt;String, Integer&gt; de la clase no ha sido
	 * alterado desde su inicializacion<br/>
	 * false: si ha habido alguna escritura en el 
	 * Map&lt;String, Integer&gt; de la clase 
	 */
	public boolean isEmpty() {
		for( Integer value : map.values() ){
			if( value!=-1 ) return false;
		}
		return true;
	}
	
	/**
	 * Encapsula el metodo 'put' del Map&lt;String, Integer&gt; de la clase
	 * para introducir en la clave 'lastMinute' el valor recibido por parametro
	 * @param occurrencesLastMinute valor que se introduce en el Map
	 */
	public void setOccurrencesLastMinute(int occurrencesLastMinute){
		map.put("lastMinute", occurrencesLastMinute);
	}
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, Integer&gt; de la clase
	 * para hallar el valor de la clave 'lastMinute'
	 * @return el valor del Map que esta mapeado a la clave 'lastMinute'
	 */
	public int getOccurrencesLastMinute(){
		return map.get("lastMinute");
	}
	
	/**
	 * Encapsula el metodo 'put' del Map&lt;String, Integer&gt; de la clase
	 * para introducir en la clave 'lastFiveMinutes' el valor recibido por
	 * parametro
	 * @param occurrencesLastFiveMinutes valor que se introduce en el Map
	 */
	public void setOccurrencesLastFiveMinutes(int occurrencesLastFiveMinutes){
		map.put("lastFiveMinutes", occurrencesLastFiveMinutes);
	}
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, Integer&gt; de la clase
	 * para hallar el valor de la clave 'lastFiveMinutes'
	 * @return el valor del Map que esta mapeado a la clave 'lastFiveMinutes'
	 */
	public int getOccurrencesLastFiveMinutes(){
		return map.get("lastFiveMinutes");
	}
	
	/**
	 * Encapsula el metodo 'put' del Map&lt;String, Integer&gt; de la clase
	 * para introducir en la clave 'lastQuarter' el valor recibido por parametro
	 * @param occurrencesLastQuarter valor que se introduce en el Map
	 */
	public void setOccurrencesLastQuarter(int occurrencesLastQuarter){
		map.put("lastQuarter", occurrencesLastQuarter);
	}
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, Integer&gt; de la clase
	 * para hallar el valor de la clave 'lastQuarter'
	 * @return el valor del Map que esta mapeado a la clave 'lastQuarter'
	 */
	public int getOccurrencesLastQuarter(){
		return map.get("lastQuarter");
	}
	
	/**
	 * Encapsula el metodo 'put' del Map&lt;String, Integer&gt; de la clase
	 * para introducir en la clave 'lastHour' el valor recibido por parametro
	 * @param occurrencesLastHour valor que se introduce en el Map
	 */
	public void setOccurrencesLastHour(int occurrencesLastHour){
		map.put("lastHour", occurrencesLastHour);
	}
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, Integer&gt; de la clase
	 * para hallar el valor de la clave 'lastHour'
	 * @return el valor del Map que esta mapeado a la clave 'lastHour'
	 */
	public int getOccurrencesLastHour(){
		return map.get("lastHour");
	}
	
	/**
	 * Encapsula el metodo 'put' del Map&lt;String, Integer&gt; de la clase
	 * para introducir en la clave 'lastDay' el valor recibido por parametro
	 * @param occurrencesLastDay valor que se introduce en el Map
	 */
	public void setOccurrencesLastDay(int occurrencesLastDay){
		map.put("lastDay", occurrencesLastDay);
	}
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, Integer&gt; de la clase
	 * para hallar el valor de la clave 'lastDay'
	 * @return el valor del Map que esta mapeado a la clave 'lastDay'
	 */
	public int getOccurrencesLastDay(){
		return map.get("lastDay");
	}
	
	/**
	 * Encapsula el metodo 'put' del Map&lt;String, Integer&gt; de la clase
	 * para introducir en la clave 'lastWeek' el valor recibido por parametro
	 * @param occurrencesLastWeek valor que se introduce en el Map
	 */
	public void setOccurrencesLastWeek(int occurrencesLastWeek){
		map.put("lastWeek", occurrencesLastWeek);
	}
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, Integer&gt; de la clase
	 * para hallar el valor de la clave 'lastWeek'
	 * @return el valor del Map que esta mapeado a la clave 'lastWeek'
	 */
	public int getOccurrencesLastWeek(){
		return map.get("lastWeek");
	}
	
	/**
	 * Encapsula el metodo 'put' del Map&lt;String, Integer&gt; de la clase
	 * para introducir en la clave 'lastMonth' el valor recibido por parametro
	 * @param occurrencesLastMonth valor que se introduce en el Map
	 */
	public void setOccurrencesLastMonth(int occurrencesLastMonth){
		map.put("lastMonth", occurrencesLastMonth);
	}
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, Integer&gt; de la clase
	 * para hallar el valor de la clave 'lastMonth'
	 * @return el valor del Map que esta mapeado a la clave 'lastMonth'
	 */
	public int getOccurrencesLastMonth(){
		return map.get("lastMonth");
	}
	
	/**
	 * Encapsula el metodo 'put' del Map&lt;String, Integer&gt; de la clase
	 * para introducir en la clave 'lastYear' el valor recibido por parametro
	 * @param occurrencesLastYear valor que se introduce en el Map
	 */
	public void setOccurrencesLastYear(int occurrencesLastYear){
		map.put("lastYear", occurrencesLastYear);
	}
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, Integer&gt; de la clase
	 * para hallar el valor de la clave 'lastYear'
	 * @return el valor del Map que esta mapeado a la clave 'lastYear'
	 */
	public int getOccurrencesLastYear(){
		return map.get("lastYear");
	}
}
