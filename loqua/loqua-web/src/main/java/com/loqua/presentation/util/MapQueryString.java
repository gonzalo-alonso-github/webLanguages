package com.loqua.presentation.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

// Esta clase se utiliza desde los Filter para refactorizar codigo compartido
public class MapQueryString {
	
	Map<String, String> mapQueryString = null;
	String parameters;
	
	public MapQueryString(HttpServletRequest req){
		loadQueryStringMap(req);
	}
	
	// // // //
	// METODOS
	// // // //
	
	void loadQueryStringMap(HttpServletRequest req) {
		// no conviene utilizar 'request.getParameter("nombre_parametro")',
		// porque si existe un 'ancla' al final de la url lo asociara con
		// el valor del ultimo parametro. Por eso cobra sentido crear este Map.
		mapQueryString = new HashMap<String, String>();
		parameters = req.getQueryString();
		if( parameters==null || parameters.isEmpty() ) return;
		int anchorIndex = parameters.indexOf("#");
		if( anchorIndex!=-1 ){
			parameters = parameters.substring(0,anchorIndex);
		}
		try{
			String[] parametersArray = parameters.split("&");
			for(int i=0; i<parametersArray.length; i++){
				String[] pair = parametersArray[i].split("=");
				mapQueryString.put(pair[0], pair[1]);
			}
		}catch( Exception e ){
			// este catch evita que una url mal escrita genere aqui una
			// ArrayIndexOutOfBoundsException.
			// ejemplo: .../loqua-web/pages/registeredUser/forum.xhtml?&page=2
			// cuando lo correcto seria:
			// .../loqua-web/pages/registeredUser/forum.xhtml?page=2
		}
	}

	public boolean loadedQueryStringMap(){
		return ( mapQueryString!=null && mapQueryString.isEmpty()==false );
	}
	
	public String get(String key){
		return mapQueryString.get(key);
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	Map<String, String> getMapQueryString() {
		return mapQueryString;
	}
	void setMapQueryString(Map<String, String> mapQueryString) {
		this.mapQueryString = mapQueryString;
	}
}
