package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link Feed} y {@link FeedCategory}
 * @author Gonzalo
 */
public interface ServiceFeed {
	
	/**
	 * Consulta fuentes de noticias (Feed) segun su atributo 'id'
	 * @param feedId atributo 'id' del Feed que se consulta
	 * @return Feed cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotFoundException
	 */
	Feed getFeedByID(Long feedID) throws EntityNotFoundException;
	
	/**
	 * Consulta todas las fuentes de noticias (Feed) disponibles
	 * @return lista de todos los Feed disponibles
	 */
	List<Feed> restGetAllFeeds();
	
	/**
	 * Consulta todas las categorias de noticias (Feed) disponibles
	 * @return lista de todos los atributos 'id' de los FeedCategories
	 * disponibles
	 */
	List<Long> getAllFeedCategoriesIdsFromDB();
	
	
	/**
	 * Consulta todas las categorias de noticias (Feed) disponibles
	 * @return lista de todos los FeedCategories disponibles
	 */
	List<FeedCategory> getAllFeedCategoriesFromDB();
	
	/*
	 * Agrega al sistema un nuevo objeto FeedCategory
	 * @param feedCategory objeto FeedCategory que se guarda
	 * @throws EntityAlreadyFoundException
	void createFeedCategory(FeedCategory feedCategory)
			throws EntityAlreadyFoundException;
	*/

	/*
	 * Actualiza un objeto FeedCategory dado
	 * @param feedCategory objeto FeedCategory que se actualiza
	 * @throws EntityNotFoundException
	void updateFeedCategory(FeedCategory feedCategory)
			throws EntityNotFoundException;
	*/
}