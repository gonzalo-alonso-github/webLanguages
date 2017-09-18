package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceForumThread;
import com.loqua.business.services.impl.transactionScript.TransactionThread;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceThread;
import com.loqua.business.services.serviceRemote.RemoteServiceThread;
import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadInfo;
import com.loqua.model.ForumThreadVoter;
import com.loqua.model.Language;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link ForumThread}, {@link ForumThreadInfo} y {@link ForumThreadVoter}.<br/>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br/>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceThread} y {@link RemoteServiceThread},
 * que heredan de {@link ServiceForumThread}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceThread")
public class EjbServiceThread 
		implements LocalServiceThread, RemoteServiceThread {

	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link ForumThread}, {@link ForumThreadInfo}
	 * y {@link ForumThreadVoter},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionThread transactionThread = 
			new TransactionThread();
	
	
	@Override
	public ForumThread getThreadById(Long threadId) {
		return transactionThread.getThreadById(threadId);
	}
	
	@Override
	public ForumThread getThreadByGuid(String guid) throws EntityNotFoundException {
		return transactionThread.getThreadByGuid(guid);
	}
	/*
	@Override
	public List<ForumThread> getAllThreadsByLanguagesAndCategoryFromDB(
			List<Long> listLanguagesIDs, Long categoryId) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if( category==null || categoryId==0 ){
			result= transactionThread.getThreadsByLanguagesFromDB(
					listLanguagesIDs);
		}else{
			result= transactionThread.getAllThreadsByLanguagesAndCategoryFromDB(
					listLanguagesIDs, categoryId);
		}
		return result;
	}
	*/
	
	@Override
	public List<ForumThread> getThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long categoryId,
			Integer offset, int limitNumThreads) {
		return transactionThread.getThreadsByLanguagesAndCategoryFromDB(
				listLanguages, categoryId, offset, limitNumThreads);
	}
	
	@Override
	public Integer getNumThreadsByLanguagesAndCategoryFromDB(
			List<Language> listLanguages, Long categoryId){
		return transactionThread.getNumThreadsByLanguagesAndCategoryFromDB(
				listLanguages, categoryId);
	}
	/*
	@Override
	public List<ForumThread> getAllThreadsByCategoryFromDB() {
		List<ForumThread> result = new ArrayList<ForumThread>();
		if( category==null || category==0 ){
			result = transactionThread.getAllThreads();
		}else{
			result= transactionThread.getAllThreadsByCategoryFromDB(category);
		}
		return result;
	}
	*/
	
	@Override
	public List<ForumThread> getThreadsByCategoryFromDB(Long categoryId,
			Integer offset, int limitNumThreads) {
		return transactionThread.getThreadsByCategoryFromDB(
				categoryId, offset, limitNumThreads);
	}
	
	@Override
	public Integer getNumThreadsByCategoryFromDB(Long categoryId){
		return transactionThread.getNumThreadsByCategoryFromDB(categoryId);
	}
	
	@Override
	public List<ForumThread> getMostValuedThreadsOfTheMonthFromDB() {
		return transactionThread.getMostValuedThreadsOfTheMonthFromDB();
	}
	
	@Override
	public List<ForumThread> getMostCommentedThreadsOfTheMonthFromDB() {
		return transactionThread.getMostCommentedThreadsOfTheMonthFromDB();
	}
	
	@Override
	public List<ForumThread> getLastThreadsByCategoryFromDB(Long categoryId) {
		return transactionThread.getLastThreadsByCategoryFromDB(categoryId);
	}
	
	@Override
	public List<ForumThread> getAllForumThreadsInLastHour() {
		// Para el cliente Aggregator
		return transactionThread.getAllForumThreadsInLastHour();
	}
	
	@Override
	public boolean threadAlreadyVotedByUser(Long userId, Long threadId)
			throws EntityNotFoundException {
		return transactionThread.threadAlreadyVotedByUser(userId, threadId);
	}
	
	@Override
	public void restCreateForumThread(ForumThread threadToCreate) 
			throws EntityAlreadyFoundException, Exception {
		// Para el cliente Aggregator
		transactionThread.restCreateForumThread(threadToCreate);
	}
	/*
	@Override
	public void restCreateForumThreadsByList(
			List<ForumThread> threadsToCreate, boolean justNow) 
			throws EntityAlreadyFoundException, Exception {
		// Para el cliente Aggregator
		transactionThread.restCreateForumThreadsByList(threadsToCreate,justNow);
	}
	*/
	/*
	@Override
	public void updateAllDataByThread(ForumThread threadToUpdate)
			throws EntityNotFoundException{
		transactionThread.updateAllDataByThread(threadToUpdate);
	}
	
	@Override
	public void updateDataByThread(ForumThread threadToUpdate)
			throws EntityNotFoundException{
		transactionThread.updateDataByThread(threadToUpdate);
	}
	*/
	@Override
	public ForumThread voteThread(Long userId, ForumThread threadToVote)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		// Este metodo lanza "EntityAlreadyFoundException"
		// porque crea en bdd una entidad "CommentVoter"
		return transactionThread.voteThread(userId, threadToVote);
	}

	@Override
	public void incrementCountVisits(ForumThread threadToUpdate)
			throws EntityNotFoundException {
		// Este metodo no lanza "EntityAlreadyFoundException"
		// porque no crea ninguna nueva entidad en bdd
		transactionThread.incrementCountVisits(threadToUpdate);
	}
}
