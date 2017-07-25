package com.loqua.business.services.impl;

import java.util.HashMap;
import java.util.Map;

/*
 * Esta clase posee un objeto HashMap, pero en su lugar podria funcionar un Hashtable
 * Para este proposito es indiferente usar Hashtable o HashMap,
 * no importa que el objeto sea sincronizado o no,
 * pero se elige HashMap porque permite valores nulos
 */
/*
 * En lugar de hacer que esta clase posea un objeto HashMap,
 * una alternativa seria hacer que la propia clase herede de HashMap.
 * Sin embargo eso permitira manejarla desde afuera editando claves y valores,
 * mientras que la intencion es encapsular el HashMap, para que desde afuera
 * solo se utilicen los metodos aqui definidos.
 */
public class MapEntityCounterByDate{
	private Map<String, Integer> map;
	
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
	
	public boolean isEmpty() {
		for( Integer value : map.values() ){
			if( value!=-1 ) return false;
		}
		return true;
	}
	
	public void setOccurrencesLastMinute(int occurrencesLastMinute){
		map.put("lastMinute", occurrencesLastMinute);
	}
	public int getOccurrencesLastMinute(){
		return map.get("lastMinute");
	}
	
	public void setOccurrencesLastFiveMinutes(int occurrencesLastFiveMinutes){
		map.put("lastFiveMinutes", occurrencesLastFiveMinutes);
	}
	public int getOccurrencesLastFiveMinutes(){
		return map.get("lastFiveMinutes");
	}
	
	public void setOccurrencesLastQuarter(int occurrencesLastQuarter){
		map.put("lastQuarter", occurrencesLastQuarter);
	}
	public int getOccurrencesLastQuarter(){
		return map.get("lastQuarter");
	}
	
	public void setOccurrencesLastHour(int occurrencesLastHour){
		map.put("lastHour", occurrencesLastHour);
	}
	public int getOccurrencesLastHour(){
		return map.get("lastHour");
	}
	
	public void setOccurrencesLastDay(int occurrencesLastDay){
		map.put("lastDay", occurrencesLastDay);
	}
	public int getOccurrencesLastDay(){
		return map.get("lastDay");
	}
	
	public void setOccurrencesLastWeek(int occurrencesLastWeek){
		map.put("lastWeek", occurrencesLastWeek);
	}
	public int getOccurrencesLastWeek(){
		return map.get("lastWeek");
	}
	
	public void setOccurrencesLastMonth(int occurrencesLastMonth){
		map.put("lastMonth", occurrencesLastMonth);
	}
	public int getOccurrencesLastMonth(){
		return map.get("lastMonth");
	}
	
	public void setOccurrencesLastYear(int occurrencesLastYear){
		map.put("lastYear", occurrencesLastYear);
	}
	public int getOccurrencesLastYear(){
		return map.get("lastYear");
	}
}
