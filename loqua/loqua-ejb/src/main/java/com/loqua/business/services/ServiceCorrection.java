package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.User;

public interface ServiceCorrection {

	Correction getCorrectionById(Long correctionId);
	
	Correction getApprovedCorrectionByComment(Long commentId);
	
	List<Correction> getNotApprovedCorrectionsByComment(Long commentId);

	Integer getNumCorrectionAgrees(Long correctionId);
	
	Integer getNumCorrectionDisagrees(Long correctionId);

	boolean getUserAgreeCorrection(Long userId, Long correctionId)
			throws EntityNotFoundException;
	
	boolean getUserDisagreeCorrection(Long userId, Long correctionId)
			throws EntityNotFoundException;

	void acceptCorrection(Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException;

	void deleteCorrection(Correction correction) throws EntityNotFoundException;

	void recommendCorrection(Long userId, Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	void dissuadeCorrection(Long userId, Long correctionId)
			throws EntityAlreadyFoundException;
	
	Correction sendCorrection(Comment commentToCorrect,
			String text, String code, User user)
			throws EntityAlreadyFoundException, EntityNotFoundException;

	void updateTextCorrection(Correction correctionToUpdate, 
			String plainTextCorrection, String textCodeCorrection)
			throws EntityNotFoundException;
}