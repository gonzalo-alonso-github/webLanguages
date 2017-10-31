package com.loqua.business.services.impl.utils.externalAccounts;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.loqua.infrastructure.Factories;

/**
 * Maneja el Map&lt;String, String[]&gt; que contiene las credenciales
 * empleadas por la aplicacion
 * @author Gonzalo
 */
public class MapCredentials {
	
	/** Instancia de la clase, para implementar el patron Singleton */
	private static MapCredentials instance = new MapCredentials();
	
	/** Contiene las credenciales empleadas por la aplicacion
	 * (y obtenidas de la base de datos). La clave del Map es el nombre
	 * del servicio ('generic_email', 'azure_blob', 'translator', 'rest')
	 * y el valor del Map es el array de credenciales correspondiente
	 * (cuyas contrase&ntilde;as permanecen cifradas con hash, tal como estan
	 * en la base de datos)
	 */
	private Map<String, String[]> mapCredentials = null;
	
	/** Clave simetrica para descifrar las credenciales guardadas en la tabla
	 * 'Credentials' de la base de datos
	 */
	private static final String KEY_DECRYPT = "l7jlk03da14vc2lv";
	
	/** Constructor sin parametros */
	public MapCredentials(){
		loadMapFromDB();
	}
	
	/**
	 * Implementa el patron Singleton para hallar la misma instancia de esta
	 * clase desde cualquier parte del codigo y desde cualquier hilo de
	 * ejecucion
     * @return unica instancia de la propia clase
     */
	public static MapCredentials getInstance() {
		return instance;
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Carga los datos de la tabla 'Credentials' en el
	 * Map&lt;String, String[]&gt; de la clase
	 */
	private void loadMapFromDB() {
		mapCredentials = new HashMap<String, String[]>();
		mapCredentials = Factories.getService().getServiceCredentials()
				.getAllCredentials();
	}
	
	/**
	 * Encapsula el metodo 'get' del Map&lt;String, String[]&gt; de la clase
	 * @param mapKey clave del Map cuyo valor asociado sera devuelto
	 * @return el valor del Map que esta mapeado a la clave indicada por el
	 * parametro dado, o null si el Map no contiene esa clave
	 */
	public String[] get(String mapKey){
		return mapCredentials.get(mapKey);
	}
	
	/**
	 * Descifra y decodifica las credenciales indicadas en la clave
	 * recibida por parametro del Map&lt;String, String[]&gt; de la clase
	 * y las guarda en un array String[]
	 * @param mapKey clave del Map cuyas credenciales se descifran
	 * @return String[] cuya posicion '0' es el primer valor de la credencial
	 * (puede ser una direccion de email, un nombre de cuenta, una URL), y
	 * cuya posicion '1' es la contrase&ntilde;a de la credencial, ya
	 * descifrada y decodificada
	 */
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
	
	/**
	 * Descifra y decodifica la cadena de texto recibida por parametro
	 * @param encryptedStringBase64 cadena de texto que se descifra y
	 * decodifica
	 * @return la cadena de texto recibida por parametro, ya descifrada y
	 * decodificada
	 * @throws Exception
	 */
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
	
	/**
	 * Descifra un dato byte[] segun la clave simetrica dada
	 * @param dataToDecrypt dato que se desea descifrar
	 * @param key clave simetrica para descifrar el dato
	 * @param algorithm algoritmo de cifrdo de clave simetrica
	 * (puede ser 'AES', 'DESede', 'CTR', 'PKCS5PADDING')
	 * @return byte[] que representa el dato descifrado en base 64
	 * @throws Exception
	 */
	private byte[] decrypt(
			byte[] dataToDecrypt, SecretKey key, String algorithm)
			throws Exception{
		Cipher c = Cipher.getInstance(algorithm);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedData = c.doFinal(dataToDecrypt);
		return decryptedData;
	}
}
