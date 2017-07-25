package com.loqua.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.CorrectionAgree;
import com.loqua.model.CorrectionDisagree;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

public class CorrectionJPA {
	
	private static final String CORRECTION_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Correction' entity not found"
			+ " at Persistence layer";
	private static final String COMMENT_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Comment' entity not found"
			+ " at Persistence layer";
	private static final String CORRECTION_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Correction' entity already"
			+ " found at Persistence layer";
	private static final String CORRECTIONAGREE_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'CorrectionAgreeE' entity already"
			+ " found at Persistence layer";
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	public Correction getCorrectionById(Long correctionId) 
			throws EntityNotPersistedException {
		Correction result = new Correction();
		try{
			// result = (Correction) Jpa.getManager().find(
			//		Correction.class, correctionID);
			result = (Correction) JPA.getManager()
					.createNamedQuery("Correction.getCorrectionById")
					.setParameter(1, correctionId)
					.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public int getNumAceptedCorrectionsByUser( Long userID )
			throws EntityNotPersistedException{
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
				.createNamedQuery("Correction.getNumAcceptedCorrectionsByUser")
				.setParameter(1, userID)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	public Correction getApprovedCorrectionByComment( Long comment )
			throws EntityNotPersistedException {
		Correction result = new Correction();
		try{
			result = JPA.getManager()
				.createNamedQuery("Correction.getApprovedCorrectionByComment",Correction.class)
				.setParameter(1, comment)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					COMMENT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Correction> getNotApprovedCorrectionsByComment( Long comment )
			throws EntityNotPersistedException {
		List<Correction> result = new ArrayList<Correction>();
		try{
			result = (List<Correction>) JPA.getManager()
				.createNamedQuery("Correction.getNotApprovedCorrectionsByComment")
				.setParameter(1, comment)
				.getResultList();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					COMMENT_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}

	public Integer getNumCorrectionAgrees(Long correctionID)
			throws EntityNotPersistedException {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
				.createNamedQuery("Correction.getNumCorrectionAgrees")
				.setParameter(1, correctionID)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}
	
	public Integer getNumCorrectionDisagrees(Long correctionID)
			throws EntityNotPersistedException {
		Long result = 0L;
		try{
			result = (Long) JPA.getManager()
				.createNamedQuery("Correction.getNumCorrectionDisagrees")
				.setParameter(1, correctionID)
				.getSingleResult();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result.intValue();
	}

	/**
	 * Realiza una consulta a la base de datos para obtener el numero
	 * de veces que el usuario que han dado su recomendacion a una correccion
	 * (como maximo 1 vez, como minimo 0 veces).
	 * @param userId valor ID del usuario
	 * @param correctionId valor ID de la correccion
	 * @return
	 * true: si el usuario ha dado su recomendacion a la correccion<br/>
	 * false: si el usuario aun no ha dado su recomendacion a la correccion
	 * @throws EntityNotPersistedException
	 */
	public boolean getUserAgreeCorrection(Long userId, Long correctionId)
			throws EntityNotPersistedException {
		boolean result = false;
		try{
			Long numAgreesUser = (Long) JPA.getManager()
				.createNamedQuery("Correction.getUserAgreeCorrection")
				.setParameter(1, userId)
				.setParameter(2, correctionId)
				.getSingleResult();
			result = (numAgreesUser>0L)?true:false;
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	/**
	 * Realiza una consulta a la base de datos para obtener el numero
	 * de veces que el usuario que han dado su desaprobacion a una correccion
	 * (como maximo 1 vez, como minimo 0 veces).
	 * @param userId valor ID del usuario
	 * @param correctionId valor ID de la correccion
	 * @return
	 * true: si el usuario ha dado su recomendacion a la correccion<br/>
	 * false: si el usuario aun no ha dado su recomendacion a la correccion
	 * @throws EntityNotPersistedException
	 */
	public boolean getUserDisagreeCorrection(Long userId, Long correctionId)
			throws EntityNotPersistedException {
		boolean result = false;
		try{
			Long numAgreesUser = (Long) JPA.getManager()
				.createNamedQuery("Correction.getUserDisagreeCorrection")
				.setParameter(1, userId)
				.setParameter(2, correctionId)
				.getSingleResult();
			result = (numAgreesUser>0L)?true:false;
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return result;
	}
	
	public void updateCorrection(Correction correction)
			throws EntityNotPersistedException {
		try{
			JPA.getManager().merge( correction );
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	public void deleteCorrection(Correction correction)
			throws EntityNotPersistedException{
		try{
			/*
			Correction correction = 
					Jpa.getManager().find(Correction.class, correction.getId());
			Jpa.getManager().remove(correction);
			*/
			JPA.getManager()
				.createNamedQuery("Correction.deleteById")
				.setParameter(1, correction.getId())
				.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
					CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	public void createCorrectionAgree(Long userId, Long corrId)
			throws EntityAlreadyPersistedException {
		try{
			// Debemos obtener dichas entidades desde la bdd mediante JPA,
			// para evitar que esten en estado 'detatched' al hacer el persist
			Correction corr = JPA.getManager().find( Correction.class, corrId );
			User user = JPA.getManager().find(User.class, userId);
			
			CorrectionAgree correctionAgree = new CorrectionAgree();
			correctionAgree.setCorrection(corr);
			correctionAgree.setUser(user);
			
			JPA.getManager().persist( correctionAgree );
			JPA.getManager().flush();
			JPA.getManager().refresh( correctionAgree );
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					CORRECTIONAGREE_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}
	
	public void createCorrectionDisagree(Long userId, Long corrId)
			throws EntityAlreadyPersistedException {
		try{
			// Debemos obtener dichas entidades desde la bdd mediante JPA,
			// para evitar que esten en estado 'detatched' al hacer el persist
			Correction corr = JPA.getManager().find( Correction.class, corrId );
			User user = JPA.getManager().find(User.class, userId);
			
			CorrectionDisagree correctionDisagree = new CorrectionDisagree();
			correctionDisagree.setCorrection(corr);
			correctionDisagree.setUser(user);
			
			JPA.getManager().persist( correctionDisagree );
			JPA.getManager().flush();
			JPA.getManager().refresh( correctionDisagree );
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					CORRECTIONAGREE_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	public Correction createCorrection(Comment commentToCorrect, String text,
			String code, User user)
			throws EntityAlreadyPersistedException, EntityNotPersistedException {
		Correction correction = null;
		try{
			correction = new Correction();
			correction.setTextThis(text).setTextHtmlThis(code).setApprovedThis(false)
				.setCommentThis(commentToCorrect).setUserThis(user)
				.setDateThis(new Date())
				.setForumThreadThis(commentToCorrect.getForumThread())
				/*.setPostType("TypeCorrection")*/;
			JPA.getManager().persist(correction);
			JPA.getManager().flush();
			JPA.getManager().refresh(correction);
		}catch( EntityExistsException ex ){
			throw new EntityAlreadyPersistedException(
					CORRECTION_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
		return correction;
	}
}
