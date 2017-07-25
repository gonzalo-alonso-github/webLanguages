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

public abstract class TranslatorMicrosoftKey {

	private static String[] credentials = 
			MapCredentials.getInstance().getDecrypted("translator");
	private static String appId ="";
	
	public static String getNewAppId() throws Exception {
		String result ="";
		String urlAccessToken = credentials[0];
		String key = credentials[1];
		HttpClient httpclient = HttpClients.createDefault();
        URIBuilder builder;
		builder = new URIBuilder(urlAccessToken);
		URI uri = builder.build();
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", "multipart/form-data");
		request.setHeader("Ocp-Apim-Subscription-Key", key);
		// Request body
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
