package testBusiness;

import static org.junit.Assert.assertEquals;
//import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.impl.utils.security.MapOccurrCounterByDate;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.model.User;

import logging.LoquaLogger;

public class TestUser {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static ServiceUser serviceUser;
	
	@BeforeClass
	public static void setUpClass(){
		serviceUser = new LocatorRemoteEjbServices().getServiceUser();
	}
	
	@Test
	public void testUserCRUD(){
		try {
			User userData = generateUser();
			
			// Crear usuario
			User addedUser = serviceUser.createUser(userData);
			User addedUserVerify = 
					serviceUser.getUserNotRemovedByEmail(userData.getEmail());
			assertEquals( addedUser, addedUserVerify );
			
			// Registrar usuario (precisa haber creado previamente un usuario)
			// incluye el envio de email a la direccion del usuario
			int numRegistBefore = getNumLastHourRegistrations();
			Map<String, Integer> limitRegistrations=getMapLimitRegistrations();
			serviceUser.sendEmailForRegister(
					addedUser, "content", "subject", limitRegistrations);
			assertTrue( numRegistBefore==1 );
			
			// Iniciar sesion con contrasena incorrecta:
			User wrongUser = serviceUser.getUserToLogin(
					addedUser.getEmail(), "incorrectPassword");
			assertTrue( wrongUser==null );
						
			// Iniciar sesion con usuario inactivo:
			User inactiveUser = serviceUser.getUserToLogin(
					addedUser.getEmail(), "Loqua_Test");
			assertTrue( inactiveUser!=null );
			assertTrue( ! inactiveUser.getActive() );
			
			// Activar el User registrado
			addedUser.setActive(true);
			addedUser.setUrlConfirm(null);
			serviceUser.updateAllDataByUser(addedUser);
			User activatedUser = serviceUser.getUserById(addedUser.getId());
			assertTrue( activatedUser.getActive() );
			
			// Iniciar sesion correctamente:
			User loggedUser = serviceUser.getUserToLogin(
					activatedUser.getEmail(), "Loqua_Test");
			assertTrue( loggedUser!=null );
			assertTrue( loggedUser.getActive() );
			assertTrue( ! loggedUser.getLocked() );
			
			// Registrar nuevo usuario otra vez, superando el limite por hora
			limitRegistrations.put("limitRegistrationsAtLastHour", 0);
			serviceUser.sendEmailForRegister(
					addedUser, "content", "subject", limitRegistrations);
			int numRegistAfter = getNumLastHourRegistrations();
			// (no se realiza la operacion: supera el limite por hora,
			// ahora establecido en 0)
			assertTrue( numRegistAfter==numRegistBefore );
			
			// Dar de baja la cuenta del usuario creado (datos privados)
			addedUser = serviceUser.deleteUserAccount(addedUser);
			assertTrue( addedUser.getRemoved() );
			
			// Eliminar definitivamente el User creado
			serviceUser.deleteUser(addedUser);
			User notExistingUser = serviceUser.getUserById(addedUser.getId());
			assertTrue( notExistingUser==null );
			
		} catch (Exception e) {
			e.printStackTrace();
    		log.error("Unexpected Exception at 'testUserCRUD()'");
		}
	}

	private int getNumLastHourRegistrations() {
		MapOccurrCounterByDate lastRegistrationsBefore =
				serviceUser.getNumLastRegistrationsFromDB();
		int numRegistBefore = 
				lastRegistrationsBefore.getOccurrencesLastHour();
		return numRegistBefore;
	}

	private User generateUser() {
		User userToGenerate = new User();
		
		userToGenerate.setEmail("loqua_test@gmail.com");
		userToGenerate.setNick("loqua_test");
		userToGenerate.getUserInfoPrivacity().setGender(true);
		
		userToGenerate.setRole(User.USER);
		userToGenerate.setActive(false);
		userToGenerate.setLocked(false);
		userToGenerate.setRemoved(false);
		userToGenerate.setLocale("es");
		userToGenerate.setDateRegistered(new Date());
		String hashedPassword = hashPassword("Loqua_Test");
		userToGenerate.setPassword(hashedPassword);
		
		return userToGenerate;
	}
	
	/**
	 * Cifra con hash la contrase&ntilde;a recibida.
	 * @param password contrase&ntilde;a que se cifra
	 * @return la contrase&ntilde;a, una vez cifrada
	 */
	private String hashPassword(String password){
		String salt = BCrypt.gensalt(12);
		String hashedPassword = BCrypt.hashpw(password, salt);
		return hashedPassword;
	}
	
	/**
	 * Llena la lista de propiedades utilizadas en las comprobaciones
	 * de la cantidad de acciones realizadas por los usuarios,
	 * a partir de los datos del fichero 'numActionsAtPeriod.properties'.
	 */
	private Map<String, Integer> getMapLimitRegistrations() {
		Map<String, Integer> mapResult = new HashMap<String, Integer>();
		String FILE_LOCATION = "/numActionsAtPeriod.properties";
		Properties properties = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream(FILE_LOCATION);
			properties.load(in);
			in.close();
			Set<Object> keys = properties.keySet();

			for (Object k : keys) {
				String key = (String) k;
				Integer value = 0;
				try{
					value = Integer.parseInt(properties.getProperty(key));
					mapResult.put(key, value);
				}catch (NumberFormatException e) {
					// No introduce esta propiedad en el Map y continua el bucle
					log.error("NumberFormatException at"
							+ "'loadMapActionLimitsProperties()'");
				}
			}
		} catch( Exception e) {
			String msg = "Properties file cannot be loaded";
			log.error("Unexpected Exception at"
					+ "'getMapLimitRegistrations()': " + msg);
		}
		return mapResult;
	}
}
