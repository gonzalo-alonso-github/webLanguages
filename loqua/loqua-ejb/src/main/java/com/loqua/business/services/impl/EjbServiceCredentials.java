package com.loqua.business.services.impl;

import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.services.ServiceCredentials;
import com.loqua.business.services.impl.transactionScript.TransactionCredentials;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceCredentials;
import com.loqua.business.services.serviceRemote.RemoteServiceCredentials;
import com.loqua.model.Credentials;

/**
 * Da acceso a las transacciones correspondientes a la entidad
 * {@link Credentials}. <br/>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br/>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceCredentials} y {@link RemoteServiceCredentials},
 * que heredan de {@link ServiceCredentials}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceCredentials")
public class EjbServiceCredentials
		implements LocalServiceCredentials, RemoteServiceCredentials {

	/** Objeto de la capa de negocio que realiza la logica relativa a la
	 * entidad {@link Credentials},
	 * incluyendo procedimientos 'CRUD' de dicha entidad */
	private static final TransactionCredentials tsCredentials = 
			new TransactionCredentials();
	
	
	@Override
	public Map<String, String[]> getAllCredentials() {
		return tsCredentials.getAllCredentials();
	}
}
