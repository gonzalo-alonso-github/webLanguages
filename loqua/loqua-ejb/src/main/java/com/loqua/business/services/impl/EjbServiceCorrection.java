package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceCorrection;
import com.loqua.business.services.impl.transactionScript.TransactionCorrection;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceCorrection;
import com.loqua.business.services.serviceRemote.RemoteServiceCorrection;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.CorrectionAgree;
import com.loqua.model.CorrectionDisagree;
import com.loqua.model.User;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link Correction}, {@link CorrectionAgree} y {@link CorrectionDisagree}.
 * <br/>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br/>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceCorrection} y {@link RemoteServiceCorrection},
 * que heredan de {@link ServiceCorrection}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceCorrection")
public class EjbServiceCorrection 
		implements LocalServiceCorrection, RemoteServiceCorrection {

	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Correction}, {@link CorrectionAgree}
	 * y {@link CorrectionDisagree},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionCorrection transactionCorr = 
			new TransactionCorrection();
	
	
	@Override
	public Correction getCorrectionById(Long correctionId) {
		return transactionCorr.getCorrectionById(correctionId);
	}
	
	@Override
	public Correction getApprovedCorrectionByComment(Long commentId) {
		return transactionCorr.getApprovedCorrectionByComment(commentId);
	}
	
	@Override
	public List<Correction> getNotApprovedCorrectionsByComment(Long commentId){
		return transactionCorr.getNotApprovedCorrectionsByComment(commentId);
	}
	
	@Override
	public Integer getNumCorrectionAgrees(Long correctionId) {
		return transactionCorr.getNumCorrectionAgrees(correctionId);
	}
	
	@Override
	public Integer getNumCorrectionDisagrees(Long correctionId) {
		return transactionCorr.getNumCorrectionDisagrees(correctionId);
	}

	@Override
	public boolean getUserAgreeCorrection(Long userId, Long correctionId)
			throws EntityNotFoundException {
		return transactionCorr.getUserAgreeCorrection(userId,correctionId);
	}
	
	@Override
	public boolean getUserDisagreeCorrection(Long userId, Long correctionId)
			throws EntityNotFoundException {
		return
			transactionCorr.getUserDisagreeCorrection(userId,correctionId);
	}

	@Override
	public void acceptCorrection(Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		transactionCorr.acceptCorrection(correction);
	}
	
	@Override
	public void deleteCorrection(Correction correction)
			throws EntityNotFoundException {
		transactionCorr.deleteCorrection(correction);
	}

	@Override
	public void recommendCorrection(Long userId, Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		transactionCorr.recommendCorrection(userId, correction);
	}
	
	@Override
	public void dissuadeCorrection(Long userId, Long correctionId)
			throws EntityAlreadyFoundException {
		transactionCorr.dissuadeCorrection(userId, correctionId);
	}
	
	@Override
	public Correction sendCorrection(Comment commentToCorrect,
			String text, String code, User user)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		return transactionCorr.sendCorrection(commentToCorrect, text, code, user);
	}

	@Override
	public void updateTextCorrection(Correction corrToUpdate, 
			String text, String code)
			throws EntityNotFoundException{
		transactionCorr.updateTextCorrection(corrToUpdate, text, code);
	}
}
