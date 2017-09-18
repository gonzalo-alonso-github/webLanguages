package com.loqua.business.services.impl;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

/**
 * Permite el acceso de lectura y escritura a la
 * 'cuenta de almacenamiento' de Azure en la que se guardan todas las imagenes
 * manejadas por la aplicacion, en formato Blob
 * @author Gonzalo
 */
public class ManagementBlobs {
	
	/** Array que contiene el nombre de la cuenta de almacenamiento de Azure
	 * (en la posicion '0') y su clave de acceso (en la posicion '1') */
	private static String[] credentials = 
			MapCredentials.getInstance().getDecrypted("azure_blob");
	
	/** Cliente para acceder a la cuenta de almacenamiento de Azure */
	private static CloudBlobClient serviceClient;
    
	/**
	 * Efectua la conexion remota con la cuenta de almacenamiento de Azure
	 * @throws InvalidKeyException
	 * @throws URISyntaxException
	 */
	private static void loadSeviceClient() 
			throws InvalidKeyException, URISyntaxException {
		String accountName = credentials[0];
		String accountKey = credentials[1];
    	String storageConnectionString =
    	        "DefaultEndpointsProtocol=http;"
    	        + "AccountName=" + accountName + ";"
    	        + "AccountKey=" + accountKey;
    	CloudStorageAccount account = CloudStorageAccount.parse(
    			storageConnectionString);
        serviceClient = account.createCloudBlobClient();
    }
	
	/**
	 * Conecta el cliente CloudBlobClient de la clase con el contenedor
	 * de Blobs de la cuenta de almacenamiento de Azure
	 * @param containerName nombre del contenedor de Blobs
	 * @return objeto CloudBlobContainer que representa al contenedor
	 * de Blobs de la cuenta de almacenamiento de Azure
	 * @throws InvalidKeyException
	 * @throws URISyntaxException
	 * @throws StorageException
	 */
	private static CloudBlobContainer loadAzureContainer(String containerName)
			throws InvalidKeyException, URISyntaxException, StorageException {
		if( serviceClient==null ){ loadSeviceClient(); }
    	CloudBlobContainer container = 
    			serviceClient.getContainerReference(containerName);
		return container;
	}
    
	/**
	 * Solicita una imagen, de la extension indicada, al contenedor de la
	 * cuenta de almacenamiento de Azure.
	 * Si no encuentra la imagen con la extension indicada,
	 * comprobara si la imagen existe con alguna de las extensiones de la lista
	 * recibida. <br/>Este metodo acelera la busqueda de la imagen cuando desde
	 * el cliente se conoce de antemano su extension, en cuyo caso no se
	 * perderia tiempo en buscarla en todas las extensiones.
	 * Por ejemplo, conociendo que todos los iconos de banderas de paises
	 * son imagenes '.gif', se solicitan mediante este metodo
	 * @param imageCode nombre de la imagen que se solicita
	 * @param containerName nombre del contenedor de la cuenta de
	 * almacenamiento de Azure, donde se busca la imagen solicitada
	 * @param expectedExtension extension, supuestamente conocida, de la imagen
	 * solicitada. Este parametro se utiliza para facilitar la busqueda de la
	 * imagen en el contenedor.
	 * @param extensions lista de extensiones en las que se buscara
	 * la imagen solicitada, si no se encuenrtra en la extension indicada por el
	 * parametro 'expectedExtension'. Como indica el fichero 'users.properties'
	 * del proyecto 'loqua-web', las extensiones permitidas para una imagen
	 * de perfil de usuario son: 'jpeg', 'jpg', 'png', 'bmp'. El contenedor
	 * tambien admite '.gif'.
	 * @return fichero de imagen en formato byte[], cuyo nombre coincide con
	 * el parametro 'imageCode', descargada del contenedor indicado por el
	 * parametro 'containerName'. Si no existe, devuelve null
	 * @throws Exception
	 */
	public static byte[] getImageFromAzureStorage(String imageCode, 
    		String containerName, String expectedExtension,
    		List<String> extensions)
    		throws Exception {
    	CloudBlobContainer container = loadAzureContainer(containerName);
        CloudBlockBlob blob = 
        		container.getBlockBlobReference(imageCode+"."+expectedExtension);
        if( ! blob.exists() ){
        	blob = verifyBlobAllExtensions(imageCode, container, extensions);
        }
        return downloadBlobToBytes(blob);
    }
	
	/**
	 * Solicita una imagen al contenedor de la cuenta de almacenamiento
	 * de Azure. Siempre comprobara si la imagen existe con alguna de las
	 * extensiones de la lista recibida por parametro
	 * @param imageCode nombre de la imagen que se solicita
	 * @param containerName nombre del contenedor de la cuenta de
	 * almacenamiento de Azure, donde se busca la imagen solicitada
	 * @param extensions lista de extensiones en las que se buscara
	 * la imagen solicitada. Como indica el fichero 'users.properties'
	 * del proyecto 'loqua-web', las extensiones permitidas para una imagen
	 * de perfil de usuario son: 'image/jpeg', 'image/jpg', 'image/png',
	 * 'image/bmp'. El contenedor tambien admite 'image/gif'.
	 * @return fichero de imagen en formato byte[], cuyo nombre coincide con
	 * el parametro 'imageCode', descargada del contenedor indicado por el
	 * parametro 'containerName'. Si no existe, devuelve null
	 * @throws Exception
	 */
    public static byte[] getImageFromAzureStorage(String imageCode, 
    		String containerName, List<String> extensions)
    		throws Exception {
    	CloudBlobContainer container = loadAzureContainer(containerName);
        CloudBlockBlob blob = verifyBlobAllExtensions(
        		imageCode, container, extensions);
        return downloadBlobToBytes(blob);
    }
    
    /**
     * Comprueba que la imagen solicitada existe en el contenedor de Azure,
     * en alguna de las extensiones de la lista recibida por parametro
     * @param nombre de la imagen que se solicita
	 * @param containerName nombre del contenedor de la cuenta de
	 * almacenamiento de Azure, donde se busca la imagen solicitada
     * @param extensions lista de extensiones en las que se buscara
	 * la imagen solicitada
     * @return objeto CloudBlockBlob que referencia a la imagen, o null si
     * no la encuentra en el contenedor indicado
     * @throws URISyntaxException
     * @throws StorageException
     */
	private static CloudBlockBlob verifyBlobAllExtensions(String imageCode,
			CloudBlobContainer container, List<String> extensions)
			throws URISyntaxException, StorageException {
		CloudBlockBlob blob;
		for( String type: extensions ){
			// Elimina el prefijo 'image/'
			String extension = type.substring(6);
			blob = container.getBlockBlobReference(imageCode+"."+extension);
			if( blob.exists() ){
				return blob;
			}
		}
		return null;
	}
	
	/**
	 * Descarga la imagen, desde el contenedor de Blob de la cuenta de
	 * almacenamiento de Azure, 
	 * convirtiendola en un array byte[]
	 * @param blob referencia al blob ubicado en el contenedor de Azure
	 * @return fichero de imagen, en formato byte[] descargado del contenedor
	 * de la cuenta de almacenamiento Azure, o null si no existe
	 * @throws StorageException
	 */
	private static byte[] downloadBlobToBytes(CloudBlockBlob blob)
			throws StorageException {
		if( blob!=null ){
	    	int bytesLength=((Long)blob.getProperties().getLength()).intValue();
        	byte[] result=new byte[bytesLength];
        	blob.downloadToByteArray(result, 0);
        	return result;
        }
        return null;
	}
	
	/**
	 * Guarda la imagen dada en el contenedor de la
	 * cuenta de almacenamiento de Azure
	 * @param imageCode nombre de la imagen que va a guardar
	 * @param imageBytes fichero de imagen en formato byte[]
	 * @param containerName nombre del contenedor de la cuenta de
	 * almacenamiento de Azure, donde se guardara la imagen dada
	 * @throws Exception
	 */
	public static void updateImageToAzureStorage(String imageCode,
			byte[] imageBytes, String containerName)
			throws Exception {
		CloudBlobContainer container = loadAzureContainer(containerName);
		CloudBlockBlob blob = container.getBlockBlobReference(imageCode);
		blob.uploadFromByteArray(imageBytes, 0, imageBytes.length);
	}
}
