package com.loqua.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.impl.transactionScript.TransactionCorrection;
import com.loqua.business.services.serviceLocal.LocalServiceCorrection;
import com.loqua.business.services.serviceRemote.RemoteServiceCorrection;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.User;

@Stateless
@WebService(name="ServiceCorrection")
public class EjbServiceCorrection 
		implements LocalServiceCorrection, RemoteServiceCorrection {

	private static final TransactionCorrection transactionCorr = 
			new TransactionCorrection();
	
	@Override
	public Correction getCorrectionById(Long correctionId) {
		return transactionCorr.getCorrectionById(correctionId);
	}
	
	@Override
	public Correction getApprovedCorrectionByComment(Long comment) {
		return transactionCorr.getApprovedCorrectionByComment(comment);
	}
	
	@Override
	public List<Correction> getNotApprovedCorrectionsByComment(Long comment) {
		return transactionCorr.getNotApprovedCorrectionsByComment(comment);
	}
	
	@Override
	public Integer getNumCorrectionAgrees(Long correctionID) {
		return transactionCorr.getNumCorrectionAgrees(correctionID);
	}
	
	@Override
	public Integer getNumCorrectionDisagrees(Long correctionID) {
		return transactionCorr.getNumCorrectionDisagrees(correctionID);
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
