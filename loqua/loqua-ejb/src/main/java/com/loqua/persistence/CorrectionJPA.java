package com.loqua.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import com.loqua.model.Correction;
import com.loqua.model.CorrectionAgree;
import com.loqua.model.CorrectionDisagree;
import com.loqua.model.User;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;
import com.loqua.persistence.exception.PersistenceRuntimeException;
import com.loqua.persistence.util.JPA;

/**
 * Efectua en la base de datos las operaciones 'CRUD' de elementos
 * {@link Correction}, {@link CorrectionAgree} y {@link CorrectionDisagree}
 * @author Gonzalo
 */
public class CorrectionJPA {
	
	/** Mensaje de la RuntimeException producida al efectuar una transaccion
	 * o lectura a la base de datos */
	private static final String PERSISTENCE_GENERAL_EXCEPTION=
			"PersistenceGeneralException: Infraestructure or technical problem"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Correction'
	 * en la base de datos */
	private static final String CORRECTION_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Correction' entity not found"
			+ " at Persistence layer";
	/** Mensaje de la excepcion producida al repetirse la entidad 'Correction'
	 * en la base de datos */
	private static final String CORRECTION_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'Correction' entity already"
			+ " found at Persistence layer";
	
	/** Mensaje de la excepcion producida al no encontrar la entidad 'Comment'
	 * en la base de datos */
	private static final String COMMENT_NOT_PERSISTED_EXCEPTION=
			"EntityNotPersistedException: 'Comment' entity not found"
			+ " at Persistence layer";
	
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'CorrectionAgree' en la base de datos */
	private static final String CORRECTIONAGREE_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'CorrectionAgree' entity already"
			+ " found at Persistence layer";
	
	/** Mensaje de la excepcion producida al repetirse la entidad
	 * 'CorrectionDisagree' en la base de datos */
	private static final String CORRECTIONDISAGREE_ALREADY_PERSISTED_EXCEPTION=
			"EntityAlreadyPersistedException: 'CorrectionDisagree' entity"
			+ " already found at Persistence layer";
	
	/**
	 * Realiza la consulta JPQL 'Correction.getCorrectionById'
	 * @param correctionId  atributo 'id' de la Correction que se consulta
	 * @return Correction cuyo atributo 'id' coincide con el parametro dado
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Correction.getNumAcceptedCorrectionsByUser'
	 * @param userID atributo 'id' del User al que pertenecen las
	 * Correction que se consultan
	 * @return cantidad de Correction que pertenecen al User dado, cuyo atribuo
	 * 'approved' es 'true'
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Correction.getApprovedCorrectionByComment'
	 * @param comment Comment al que pertenece la Correction aprobada
	 * (y por tanto, unica) que se consulta
	 * @return la unica Correction, perteneciente al Comment dado,
	 * cuyo atributo 'approved' es 'true'
	 * @throws EntityNotPersistedException
	 */
	public Correction getApprovedCorrectionByComment( Long comment )
			throws EntityNotPersistedException {
		Correction result = new Correction();
		try{
			result = JPA.getManager()
				.createNamedQuery("Correction.getApprovedCorrectionByComment",
						Correction.class)
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
	
	/**
	 * Realiza la consulta JPQL 'Correction.getNotApprovedCorrectionsByComment'
	 * @param comment Comment al que pertenecen las Correction no aprobadas
	 * que se consultan
	 * @return lista de Correction, pertenecientes al Comment dado,
	 * cuyo atributo 'approved' es 'false'
	 * @throws EntityNotPersistedException
	 */
	@SuppressWarnings("unchecked")
	public List<Correction> getNotApprovedCorrectionsByComment( Long comment )
			throws EntityNotPersistedException {
		List<Correction> result = new ArrayList<Correction>();
		try{
			result = (List<Correction>) JPA.getManager().createNamedQuery(
					"Correction.getNotApprovedCorrectionsByComment")
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

	/**
	 * Realiza la consulta JPQL 'Correction.getNumCorrectionAgrees'
	 * @param correctionID atributo 'id' de la Correction a la que pertenecen
	 * los CorrectionAgree que se consultan
	 * @return cantidad de CorrectionAgree que pertenecen a la Correction dada
	 * @throws EntityNotPersistedException
	 */
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
	
	/**
	 * Realiza la consulta JPQL 'Correction.getNumCorrectionDisagrees'
	 * @param correctionID atributo 'id' de la Correction a la que pertenecen
	 * los CorrectionDisgree que se consultan
	 * @return cantidad de CorrectionDisgree que pertenecen a la Correction dada
	 * @throws EntityNotPersistedException
	 */
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
	 * Realiza la consulta JPQL 'Correction.getUserAgreeCorrection'
	 * para hallar el numero de veces que el usuario ha dado
	 * su recomendacion a una correccion
	 * (como maximo 1 vez, como minimo 0 veces).
	 * @param userId atributo 'id' del User al que pertenecen los
	 * CorrectionAgree que se consultan
	 * @param correctionId atributo 'id' de la Correction a la que pertenecen
	 * los CorrectionAgree que se consultan
	 * @return
	 * true: si el usuario ha dado su recomendacion a la correccion<br>
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
	 * Realiza la consulta JPQL 'Correction.getUserDisagreeCorrection'
	 * para hallar el numero de veces que el usuario ha dado
	 * su desaprobacion a una correccion
	 * (como maximo 1 vez, como minimo 0 veces).
	 * @param userId atributo 'id' del User al que pertenecen los
	 * CorrectionAgree que se consultan
	 * @param correctionId atributo 'id' de la Correction a la que pertenecen
	 * los CorrectionAgree que se consultan
	 * @return
	 * true: si el usuario ha dado su recomendacion a la correccion<br>
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
	
	/**
	 * Actualiza en la base de datos el objeto Correction dado
	 * @param correction objeto Correction que se desea actualizar
	 * @throws EntityNotPersistedException
	 */
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
	/**
	 * Actualiza en la base de datos el objeto Correction dado
	 * estableciendo su atribuo 'approved' igual a 'true'
	 * @param correctionId atributo 'id' del Correction que se actualiza
	 * @throws EntityNotPersistedException
	 */
	public void setApprovedFalse(Long correctionId)
			throws EntityNotPersistedException {
		String namedQuery = "Correction.setApprovedFalse";
		try{
			JPA.getManager().createNamedQuery(namedQuery)
					.setParameter(1, correctionId)
					.executeUpdate();
		}catch( NoResultException ex ){
			throw new EntityNotPersistedException(
				CORRECTION_NOT_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}catch( Exception ex ){}
	}

	/**
	 * Elimina de la base de datos el objeto Correction dado
	 * @param correction objeto Correction que se desea eliminar
	 * @throws EntityNotPersistedException
	 */
	public void deleteCorrection(Correction correction)
			throws EntityNotPersistedException{
		try{
			/* Correction correction = 
					Jpa.getManager().find(Correction.class, correction.getId());
			Jpa.getManager().remove(correction); */
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

	/**
	 * Genera un objeto CorrectionAgree a partir de los parametros recibidos
	 * y lo agrega a la base de datos
	 * @param userId atributo 'id' del User autor del CorrectionAgree
	 * que se genera
	 * @param corrId atributo 'id' de la Correction a la que pertenece la
	 * CorrectionAgree que se genera
	 * @throws EntityAlreadyPersistedException
	 */
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
	
	/**
	 * Elimina de la base de datos el objeto CorrectionAgree que asocia al
	 * usuario y la correccion indicados
	 * @param userId usuario asociado al CorrectionAgree eliminado
	 * @param correctionId correccion asociada al CorrectionAgree eliminado
	 * @throws EntityNotPersistedException
	 */
	public void deleteAgreement(Long userId, Long correctionId)
			throws EntityNotPersistedException{
		try{
			JPA.getManager()
				.createNamedQuery("Correction.deleteAgreement")
				.setParameter(1, userId)
				.setParameter(2, correctionId)
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
	
	/**
	 * Genera un objeto CorrectionDisagree a partir de los parametros recibidos
	 * y lo agrega a la base de datos
	 * @param userId User autor del CorrectionAgree que se genera
	 * @param corrId Correction a la que pertenece la CorrectionAgree
	 * que se genera
	 * @throws EntityAlreadyPersistedException
	 */
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
					CORRECTIONDISAGREE_ALREADY_PERSISTED_EXCEPTION, ex);
		}catch( RuntimeException ex ){
			//HibernateException,IllegalArgumentException,ClassCastException...
			throw new PersistenceRuntimeException(
					PERSISTENCE_GENERAL_EXCEPTION, ex);
		}
	}

	/**
	 * Genera un objeto Correction a partir de los parametros recibidos
	 * y lo agrega a la base de datos
	 * @param correction los datos de la correccion que se agrega
	 * @return el objeto Correction agregado
	 * @throws EntityAlreadyPersistedException
	 * @throws EntityNotPersistedException
	 */
	public Correction createCorrection(Correction correction)
			throws EntityAlreadyPersistedException, EntityNotPersistedException{
		try{
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
