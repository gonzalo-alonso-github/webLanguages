package com.loqua.business.services.impl;

import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceCountry;
import com.loqua.business.services.impl.transactionScript.TransactionCountry;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceCountry;
import com.loqua.business.services.serviceRemote.RemoteServiceCountry;
import com.loqua.model.Country;

/**
 * Da acceso a las transacciones correspondientes a la entidad
 * {@link Country}. <br>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceCountry} y {@link RemoteServiceCountry},
 * que heredan de {@link ServiceCountry}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceCountry")
public class EjbServiceCountry 
		implements LocalServiceCountry, RemoteServiceCountry {

	/** Objeto de la capa de negocio que realiza la logica relativa a la
	 * entidad {@link Country},
	 * incluyendo procedimientos 'CRUD' de dicha entidad */
	private static final TransactionCountry transactionCountry = 
			new TransactionCountry();
	
	@Override
	public Map<Long,Country> getAllCountries() {
		return transactionCountry.getAllCountries();
	}

	@Override
	public Country getCountryById(Long countryId)
			throws EntityNotFoundException {
		return transactionCountry.getCountryById(countryId);
	}
	
	/*
	@Override
	public Country getCountryOriginByUser(Long userID) 
			throws EntityNotFoundException {
		return transactionCountry.getCountryOriginByUser(userID);
	}

	@Override
	public Country getCountryLocationByUser(Long userID) 
			throws EntityNotFoundException {
		return transactionCountry.getCountryLocationByUser(userID);
	}
	*/
}
