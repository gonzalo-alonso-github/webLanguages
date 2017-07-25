package com.loqua.business.services.impl;

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

public class ManagementEmail {
	
	private static String[] credentials = 
			MapCredentials.getInstance().getDecrypted("generic_email");
	private static Properties properties;
	private static Session session;
    
    public static void sendEmail(User userReceiver,String content,String subject)
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
	        // Enviamos el correo...
	        // ATENCION: comprobar que los Antivirus no bloqueen esta orden
	        // El Avast es especialmente problematico.
	        Transport.send(emailMessage);
		}catch(MessagingException e){
			throw new BusinessRuntimeException(e);
		}
    }
    
    private static void setup() throws MessagingException {
		//propiedades de la conexion
		properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.transport.protocol", "smtps");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		
		//creamos la sesion
		session = createSession();
	}
    
	private static Session createSession() {
		Session session = null;
		String accountName = credentials[0];
		String accountKey = credentials[1];
		try{
			// Mejor usar Session.getInstance y no Session.getDefaultInstance
			// para evitar SecurityException
			session = Session.getInstance(properties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication(){
						return new PasswordAuthentication(
								accountName, accountKey);}
				});
			// La siguiente linea imprime el Debug
			// session.setDebug(true);
		} catch( SecurityException e ){
			throw new BusinessRuntimeException(e);
		}
		return session;
	}
}
