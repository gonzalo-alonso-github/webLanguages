package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceFeed;
import com.loqua.business.services.impl.transactionScript.TransactionFeed;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceFeed;
import com.loqua.business.services.serviceRemote.RemoteServiceFeed;
import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link Feed} y {@link FeedCategory}. <br/>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br/>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceFeed} y {@link RemoteServiceFeed},
 * que heredan de {@link ServiceFeed}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceFeed")
public class EjbServiceFeed 
		implements LocalServiceFeed, RemoteServiceFeed {

	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Feed} y {@link FeedCategory},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionFeed transactionFeed = 
			new TransactionFeed();
	
	
	@Override
	public Feed getFeedByID(Long feedID) throws EntityNotFoundException {
		return transactionFeed.getFeedByID(feedID);
	}
	
	@Override
	public List<Feed> restGetAllFeeds() {
		// Para el cliente Aggregator
		return transactionFeed.getAllFeeds();
	}
	
	@Override
	public List<Long> getAllFeedCategoriesIdsFromDB() {
		return transactionFeed.getAllFeedCategoriesIdsFromDB();
	}
	
	@Override
	public List<FeedCategory> getAllFeedCategoriesFromDB() {
		return transactionFeed.getAllFeedCategoriesFromDB();
	}
	
	/*
	@Override
	public void createFeedCategory(FeedCategory feedCategory) 
			throws EntityAlreadyFoundException {
		transactionFeed.createFeedCategory(feedCategory, justNow);
	}
	
	@Override
	public void updateFeedCategory(FeedCategory feedCategory)
			throws EntityNotFoundException {
		transactionFeed.updateFeedCategory(feedCategory, justNow);
	}
	*/
}
