package com.loqua.business.services.impl.transactionScript;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;
import com.loqua.persistence.FeedJPA;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link Feed} y {@link FeedCategory}. <br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionFeed {

	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link Feed} y {@link FeedCategory}
	 */
	private static final FeedJPA feedJPA = new FeedJPA();
	
	/**
	 * Consulta fuentes de noticias (Feed) segun su atributo 'id'
	 * @param feedId atributo 'id' del Feed que se consulta
	 * @return Feed cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	public Feed getFeedByID(Long feedId) throws EntityNotFoundException {
		Feed result = new Feed();
		try {
			result = feedJPA.getFeedById(feedId);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	/**
	 * Consulta todas las fuentes de noticias (Feed) disponibles
	 * @return lista de todos los Feed disponibles
	 */
	public List<Feed> getAllFeeds() {
		// Este metodo see llama desde el cliente Aggregator
		return feedJPA.getAllFeeds();
	}
	
	/**
	 * Consulta todas las categorias de noticias (Feed) disponibles
	 * @return lista de todos los atributos 'id' de los FeedCategories
	 * disponibles
	 */
	public List<Long> getAllFeedCategoriesIdsFromDB() {
		return feedJPA.getAllFeedCategoriesIds();
	}
	
	/**
	 * Consulta todas las categorias de noticias (Feed) disponibles
	 * @return lista de todos los FeedCategories disponibles
	 */
	public List<FeedCategory> getAllFeedCategoriesFromDB() {
		return feedJPA.getAllFeedCategories();
	}
	
	/*
	 * Agrega al sistema un nuevo objeto FeedCategory
	 * @param feedCategory objeto FeedCategory que se guarda
	 * @throws EntityAlreadyFoundException
	public void createFeedCategory(FeedCategory feedCategory)
			throws EntityAlreadyFoundException{
		try {
			feedJPA.createFeedCategory(feedCategory);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	*/
	
	/*
	 * Actualiza un objeto FeedCategory dado
	 * @param feedCategory objeto FeedCategory que se actualiza
	 * @throws EntityNotFoundException
	public void updateFeedCategory(FeedCategory feedCategory)
			throws EntityNotFoundException {
		try {
			feedJPA.updateFeedCategory(feedCategory);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	*/
}
