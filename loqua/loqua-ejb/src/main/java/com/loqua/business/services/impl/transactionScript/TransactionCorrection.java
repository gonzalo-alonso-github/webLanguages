package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.User;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.CorrectionJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

public class TransactionCorrection {
	
	private static final CorrectionJPA correctionJPA = new CorrectionJPA();
	private static final TransactionUser transactionUser = 
			new TransactionUser();
	private static final TransactionPublication transactionPub = 
			new TransactionPublication();
	
	public Correction getCorrectionById(Long correctionId)
			/*throws EntityNotFoundException*/ {
		Correction result = new Correction();
		try{
			result = correctionJPA.getCorrectionById(correctionId);
		}catch (EntityNotPersistedException ex) {
			//throw new EntityNotFoundException(ex);
			return null;
		}
		return result;
	}
	
	public Correction getApprovedCorrectionByComment(Long comment) {
		Correction result = new Correction();
		try{
			result = correctionJPA.getApprovedCorrectionByComment(comment);
		}catch (EntityNotPersistedException ex) {
			result=null;
		}
		return result;
	}
	
	public List<Correction> getNotApprovedCorrectionsByComment(Long comment){
		List<Correction> result = new ArrayList<Correction>();
		try{
			result = correctionJPA.getNotApprovedCorrectionsByComment(comment);
		}catch (EntityNotPersistedException ex) {
			return result;
		}
		return result;
	}

	public Integer getNumCorrectionAgrees(Long correctionID) {
		Integer result = null;
		try{
			result = correctionJPA.getNumCorrectionAgrees(correctionID);
		}catch (EntityNotPersistedException ex) {
			result = 0;
		}
		return result;
	}
	
	public Integer getNumCorrectionDisagrees(Long correctionID) {
		Integer result = null;
		try{
			result = correctionJPA.getNumCorrectionDisagrees(correctionID);
		}catch (EntityNotPersistedException ex) {
			result = 0;
		}
		return result;
	}

	public boolean getUserAgreeCorrection(Long userId, Long correctionId) 
			throws EntityNotFoundException {
		boolean result = false;
		if(userId==null) return false;
		try{
			result = correctionJPA.getUserAgreeCorrection(userId,correctionId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException();
		}
		return result;
	}

	public boolean getUserDisagreeCorrection(Long userId, Long correctionId) 
			throws EntityNotFoundException {
		boolean result = false;
		if(userId==null) return false;
		try{
			result = correctionJPA.getUserDisagreeCorrection(userId,correctionId);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException();
		}
		return result;
	}

	public void updateCorrection(Correction correctionToUpdate)
			throws EntityNotFoundException {
		try {
			correctionJPA.updateCorrection(correctionToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void acceptCorrection(Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		checkIfAnotherCorrectionIsAccepted(correction);
		// Marcar como aprobada la correccion y actualizarla en bdd:
		correction.setApprovedThis(true).setDateApprovedThis(new Date());
		updateCorrection(correction);
		// Incrementar los puntos del usuario y actualizarlo en bdd:
		User user = correction.getUser();
		user.getUserInfo().incrementPointsByAcceptedCorrection();
		transactionUser.updateAllDataByUser(user, true);
		// Y, si corresponde, se crean las Publication necesarias:
		generatePublicationsForAcceptedCorrection(correction);
	}
	
	private void checkIfAnotherCorrectionIsAccepted(Correction correction)
			throws EntityNotFoundException {
		Long commentId = correction.getComment().getId();
		Correction prevCorrection = getApprovedCorrectionByComment(commentId);
		if( prevCorrection == null ){ return; }
		// Si ya existia una correccion aprobada para este comentario,
		// la pone como "no aprobada":
		prevCorrection.setApprovedThis(false);
		updateCorrection(prevCorrection);
		// y le quita los puntos al autor de dicha correccion:
		User user = prevCorrection.getUser();
		user.getUserInfo().decrementPointsByDeletedCorrection(prevCorrection);
		transactionUser.updateAllDataByUser(user, true);
	}
	
	private void generatePublicationsForAcceptedCorrection(Correction corr)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		// "userReceiver" es quien recibe la correccion y decide aceptarla:
		User userReceiver = corr.getComment().getUser();
		/* TypePrivacity.CONTACTS, porque esta publicacion
		no tendria sentido si solo fuera privada,
		ya que va dirigida a los contactos del usuario, mas que a el mismo */
		transactionPub.generatePublication(
				TypePrivacity.CONTACTS, corr.getId(), 202L, userReceiver);
		// "userCorrector" es quien realizo la correccion que es aceptada:
		User userCorrector = corr.getUser();
		TypePrivacity privacityCorrector =
				userCorrector.getPrivacityData().getPublications();
		// La privacidad del Evento 204 es siempre PRIVATE:
		transactionPub.generatePublication(TypePrivacity.PRIVATE,
				corr.getId(), 204L, userCorrector);
		Integer totalCorrs = getNumAceptedCorrectionsByUser(
				userCorrector.getId());
		if( reachedXApprovedCorrections(totalCorrs) ){
			// comprueba si ese numero ha sido alcanzado otras veces anteriores
			// (puede darse si alguna correction aprobada haya sido sustituida)
			if( transactionPub.achievementNumCorrectionsAlreadyPassedByUser(
					totalCorrs.longValue(), 2L, userCorrector.getId()) ){
				// en cuyo caso no genera la publicacion:
				return;
			}// else
			// si el logro aun no habia sido alcanzado, genera la publicacion:
			transactionPub.generatePublication(privacityCorrector,
					totalCorrs.longValue(), 2L, userCorrector);
		}
		// si el usuario llega al top-100/50/25/20/15/10/5/(...)/1:
		transactionUser.generatePublicationForTopUsers( userCorrector );
	}
	
	private int getNumAceptedCorrectionsByUser(Long userID)
			throws EntityNotFoundException{
		int result = 0;
		try{
			result = correctionJPA.getNumAceptedCorrectionsByUser(userID);
		}catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		return result;
	}
	
	private boolean reachedXApprovedCorrections(Integer numCorrections) {
		// si el num de corrections es 10/25/50/100/250/500/... devuelve 'true':
		int reduction = 0;
		if( numCorrections >= 50 ){
			String numCorrsString = numCorrections.toString();
			// exp. regular para uno o mas ceros al final de un numero: [0]+$
			numCorrsString = numCorrsString.replaceAll("[0]+$", "");
			reduction = Integer.parseInt(numCorrsString);
			if( reduction==1 || reduction==25 || reduction==5 ){
				return true;
			}
		}
		return false;
	}
	
	public void deleteCorrection(Correction correction)
			throws EntityNotFoundException {
		try {
			transactionPub.editPubsForDeletedPost(correction);
			correctionJPA.deleteCorrection(correction);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	public void recommendCorrection(Long userId, Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		try {
			correctionJPA.createCorrectionAgree(userId, correction.getId());
			
			// Se genera la publicacion cuyo evento es el 203
			User userCorrector = correction.getUser();
			transactionPub.generatePublication(TypePrivacity.PRIVATE,
					correction.getId(), 203L, userCorrector);
			
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	public void dissuadeCorrection(Long userId, Long correctionId)
			throws EntityAlreadyFoundException {
		try {
			correctionJPA.createCorrectionDisagree(userId, correctionId);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}

	public Correction sendCorrection(Comment commentToCorrect,
			String text, String code, User user)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		try {
			Correction correction = correctionJPA.createCorrection(
					commentToCorrect, text, code, user);
			// Se crean las Publication necesarias:
			generatePublicationsToSendCorr(commentToCorrect, user, correction);
			// Se actualiza la noticia:
			// ForumThread thread = commentToCorrect.getForumThread();
			// transactionThread.updateDataByThread(thread, true);
			return correction;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}

	private void generatePublicationsToSendCorr(Comment commentToCorrect,
			User user, Correction correction)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		/* TypePrivacity.CONTACTS, porque esta publicacion
		no tendria sentido si solo fuera privada,
		ya que va dirigida a los contactos del usuario, mas que a el mismo */
		transactionPub.generatePublication(TypePrivacity.CONTACTS,
				commentToCorrect.getId(), 101L, commentToCorrect.getUser());
		// TypePrivacity.CONTACTS, por el mismo motivo
		transactionPub.generatePublication(TypePrivacity.CONTACTS,
				correction.getId(), 201L, user);
	}

	public void updateTextCorrection(Correction corrToUpdate, 
			String text, String code)
			throws EntityNotFoundException{
		try {
			corrToUpdate.setTextThis(text).setTextHtmlThis(code);
			correctionJPA.updateCorrection(corrToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
}
