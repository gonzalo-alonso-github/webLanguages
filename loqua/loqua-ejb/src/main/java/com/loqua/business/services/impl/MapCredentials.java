package com.loqua.business.services.impl;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.loqua.infrastructure.Factories;

// Esta clase se utiliza desde los Filter para refactorizar codigo compartido
public class MapCredentials {
	
	private static MapCredentials instance = new MapCredentials();
	
	private Map<String, String[]> mapCredentials = null;
	private static final String KEY_DECRYPT = "l7jlk03da14vc2lv";
	
	public MapCredentials(){
		loadMapFromDB();
	}
	
	/**
    * @return instancia unica de la propia clase, patron Singleton
    */
	public static MapCredentials getInstance() {
		return instance;
	}
	
	// // // //
	// METODOS
	// // // //
	
	private void loadMapFromDB() {
		mapCredentials = new HashMap<String, String[]>();
		mapCredentials = Factories.getService().getServiceCredentials()
				.getAllCredentials();
	}
	
	public String[] get(String mapKey){
		return mapCredentials.get(mapKey);
	}
	
	public String[] getDecrypted(String mapKey){
		String[] result = {"", ""};
		String[] encryptedCredentials = mapCredentials.get(mapKey);
		String encryptedKey = encryptedCredentials[1];
		try {
			String decryptedKey = decodeAndDecrypt(encryptedKey);
			result[0] = encryptedCredentials[0];
			result[1] = decryptedKey;
		} catch (Exception e) {}
		return result;
	}
	
	private String decodeAndDecrypt(String encryptedStringBase64) 
			throws Exception{
		byte[] encryptedBytesBase64 =
				DatatypeConverter.parseBase64Binary(encryptedStringBase64);
		String algorithm = "AES";
		// AES, DESede, CTR, PKCS5PADDING, etc.
		SecretKey key=new SecretKeySpec(KEY_DECRYPT.getBytes("UTF-8"),algorithm);
		byte[] decryptedBytes = decrypt(encryptedBytesBase64, key, algorithm);
		String decryptedString = new String(decryptedBytes, "UTF8");
		return decryptedString;
	}
	
	private byte[] decrypt(
			byte[] dataToDecrypt, SecretKey key, String algorithm)
			throws Exception{
		Cipher c = Cipher.getInstance(algorithm);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedData = c.doFinal(dataToDecrypt);
		return decryptedData;
	}
}
