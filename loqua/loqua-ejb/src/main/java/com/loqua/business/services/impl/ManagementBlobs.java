package com.loqua.business.services.impl;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class ManagementBlobs {
	
	private static String[] credentials = 
			MapCredentials.getInstance().getDecrypted("azure_blob");
	private static CloudBlobClient serviceClient;
    
	// Efectua la conexion remota con la cuenta de almacenamiento de Azure
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
	
	// Carga el contenedor de imagenes de la cuenta de Azure
	private static CloudBlobContainer loadAzureContainer(String containerName)
			throws InvalidKeyException, URISyntaxException, StorageException {
		if( serviceClient==null ){ loadSeviceClient(); }
    	CloudBlobContainer container = 
    			serviceClient.getContainerReference(containerName);
		return container;
	}
    
	// Solicita una imagen a la cuenta de Azure.
	// Si no encuentra la imagen con la extension indicada,
	// comprobara si la imagen existe con otras extensiones permitidas
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
	
	// Solicita una imagen a la cuenta de Azure.
	// Mientras no encuentre la imagen al terminar de buscarla,
	// comprobara si la imagen existe con otras extensiones permitidas
    public static byte[] getImageFromAzureStorage(String imageCode, 
    		String containerName, List<String> extensions)
    		throws Exception {
    	CloudBlobContainer container = loadAzureContainer(containerName);
        CloudBlockBlob blob = verifyBlobAllExtensions(
        		imageCode, container, extensions);
        return downloadBlobToBytes(blob);
    }
    
    // Comprueba que la imagen solicitada existe en el contenedor de Azure,
    // en alguna de las extensiones de imagen permitidas (gif, jpg o png).
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
	
	// Descarga la imagen desde la cuenta de Azure,
	// convirtiendola en un array byte[]
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

	// Carga en la cuenta de Azure la imagen dada
	public static void updateImageToAzureStorage(String imageCode,
			byte[] imageBytes, String containerName)
			throws Exception {
		CloudBlobContainer container = loadAzureContainer(containerName);
		CloudBlockBlob blob = container.getBlockBlobReference(imageCode);
		blob.uploadFromByteArray(imageBytes, 0, imageBytes.length);
	}
}
