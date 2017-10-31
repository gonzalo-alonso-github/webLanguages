package com.loqua.business.services.impl.utils.externalAccounts;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.loqua.business.exception.BusinessRuntimeException;
import com.loqua.model.User;

/**
 * Permite el envio de correos electronicos por parte de la aplicacion,
 * util tanto a la hora de confirmar registros de usuarios
 * como de confirmar cambios en sus de datos de acceso
 * @author Gonzalo
 */
public class ManagementEmail {
	
	/** Array que contiene la direccion de email que emite los correos
	 * (en la posicion '0') y su clave de acceso (en la posicion '1') */
	private static String[] credentials = 
			MapCredentials.getInstance().getDecrypted("generic_email");
	
	/** Datos de acceso al servicio SMTP para el envio de correo electronico */
	private static Properties properties;
	
	/** Sesion mail necesaria para conectarse al servidor SMTP */
	private static Session session;
    
	/**
	 * Envia un correo electronico con el contenido indicado a la direccion
	 * email del usuaro dado. Conviene comprobar que los Antivirus del sistema
	 * no bloquean la orden de Transport.send()
	 * @param userReceiver usuario destinatario del correo enviado 
	 * @param content texto del cuerpo del correo enviado
	 * @param subject titulo (o asunto) del correo enviado
	 * @throws BusinessRuntimeException
	 */
    public static void sendEmail(
    		User userReceiver, String content, String subject)
    		throws BusinessRuntimeException {
		try{
			String accountName = credentials[0];
			setup();
            //Construimos el Mensaje
	        Message emailMessage = new MimeMessage(session);
	        emailMessage.setFrom(new InternetAddress(accountName));
	        String addressReceiver = userReceiver.getEmail();
	        
	        emailMessage.setRecipients(Message.RecipientType.TO,
	        		InternetAddress.parse(addressReceiver));
	        emailMessage.setSubject(subject);
	        emailMessage.setText(content);
	        // Enviar el correo
	        // Comprobar que los Antivirus no bloqueen esta orden
	        // Avast es especialmente problematico
	        Transport.send(emailMessage);
		}catch(MessagingException e){
			throw new BusinessRuntimeException(e);
		}
    }
    
    /**
     * Prepara la sesion mail estableciendo las propiedades de acceso al
     * servicio SMTP
     * @throws MessagingException
     */
    private static void setup() throws MessagingException {
		properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.transport.protocol", "smtps");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		// crear la sesion
		session = createSession();
	}
    
    /**
     * Carga la sesion de email segun las credenciales de la cuenta de correo
     * electronico almacenada en el String[] 'credentials' de esta clase
     * @return el objeto Session generado
     */
	private static Session createSession() {
		Session session = null;
		String accountName = credentials[0];
		String accountKey = credentials[1];
		try{
			// Usar Session.getInstance y no Session.getDefaultInstance
			// para evitar SecurityException
			session = Session.getInstance(properties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication(){
						return new PasswordAuthentication(
								accountName, accountKey);}
				});
			// La siguiente linea imprime el Debug:
			// session.setDebug(true);
		} catch( SecurityException e ){
			throw new BusinessRuntimeException(e);
		}
		return session;
	}
}
