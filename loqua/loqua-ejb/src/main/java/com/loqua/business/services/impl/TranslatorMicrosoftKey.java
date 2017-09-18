package com.loqua.business.services.impl;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Administra la clave 'appId' del componente Microsoft Translator. <br/>
 * La clase {@link MapCredentials}, en su clave 'azure_blob', almacena la key
 * que da permiso para el uso de la API de Microsoft Translator, y tambien
 * la URL que, combinandose con aquella, genera como respuesta un 'accessToken'
 * (que caduca cada diez minutos) el cual
 * a su vez sirve para obtener el 'appId' que permite a la aplicacion enviar
 * cadenas de texto al Translator y recibir su traduccion en un idioma dado
 * @author Gonzalo
 */
public abstract class TranslatorMicrosoftKey {

	/** Array que contiene la URL donde se obtiene el 'accessToken'
	 * (en la posicion '0') y la clave de la API Translator necesaria tambien
	 * para obtener dicho 'accessToken' (en la posicion '1'). <br/>
	 * El 'access token' es una serie de caracteres, y con el se obtiene
	 * la clave 'appId' para poder enviar y recibir datos del Translator */
	private static String[] credentials = 
			MapCredentials.getInstance().getDecrypted("translator");
	
	/** Clave que que permite a la aplicacion enviar
	 * cadenas de texto al Translator y recibir su traduccion en un idioma dado
	 */
	private static String appId ="";
	
	/**
	 * Genera una 'appId' de la API Microsoft Translator. La 'appId' caduca
	 * cada diez minutos; a partir de entonces es preciso obtener una clave
	 * nueva para que el metodo Translate.execute() no genere una
	 * ArgumentException
	 * @return nueva clave 'appId' para poder enviar y recibir datos del
	 * Translator
	 * @throws Exception
	 */
	public static String getNewAppId() throws Exception {
		String result ="";
		String urlAccessToken = credentials[0];
		String key = credentials[1];
		HttpClient httpclient = HttpClients.createDefault();
		
		// Se envia a la URL dada la clave de la API Translator
        URIBuilder builder;
		builder = new URIBuilder(urlAccessToken);
		URI uri = builder.build();
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", "multipart/form-data");
		request.setHeader("Ocp-Apim-Subscription-Key", key);
		
		/* Se obtiene la respuesta HttpResponse, que devuelve un
		'access token' del cual se obiene la clave 'appId */
		StringEntity reqEntity = new StringEntity("{body}");
		request.setEntity(reqEntity);
		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();
		if (entity != null){
			result = EntityUtils.toString(entity);
			appId = result;
		}else{
			throw new Exception();
		}
		return result;
	}
	
	public static String getAppId() {
		return appId;
	}
	public static void setAppId(String newAppId) {
		appId = newAppId;
	}
}
