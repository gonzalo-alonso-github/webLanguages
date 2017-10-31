package com.loqua.business.services.impl.transactionScript;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.CorrectionAgree;
import com.loqua.model.CorrectionDisagree;
import com.loqua.model.PrivacityData;
import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;
import com.loqua.model.UserInfo;
import com.loqua.model.UserInfoPrivacity;
import com.loqua.model.types.TypePrivacity;
import com.loqua.persistence.CorrectionJPA;
import com.loqua.persistence.exception.EntityAlreadyPersistedException;
import com.loqua.persistence.exception.EntityNotPersistedException;

/**
 * Da acceso a los procedimientos, dirigidos a la capa de persistencia,
 * correspondientes a las transacciones de las entidades
 * {@link Correction}, {@link CorrectionAgree} y {@link CorrectionDisagree}.
 * <br>
 * Este paquete de clases implementa el patron Transaction Script y
 * es el que, junto al modelo, concentra gran parte de la logica de negocio
 * @author Gonzalo
 */
public class TransactionCorrection {
	
	/** Objeto de la capa de persistencia que efectua sobre la base de datos
	 * las operaciones 'CRUD' relativas a las entidades
	 * {@link Correction}, {@link CorrectionAgree} y {@link CorrectionDisagree}
	 */
	private static final CorrectionJPA correctionJPA = new CorrectionJPA();
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
	 * y {@link PrivacityData},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionUser transactionUser = 
			new TransactionUser();
	
	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link Publication} y {@link PublicationReceiver},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionPublication transactionPub = 
			new TransactionPublication();
	
	
	/**
	 * Consulta correcciones segun su atributo 'id'
	 * @param correctionId atributo 'id' de la Correction que se consulta
	 * @return Correction cuyo atributo 'id' coincide con el parametro dado,
	 * o null si no existe
	 */
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
	
	/**
	 * Halla la correccion aprobada de un comentario
	 * @param commentId atributo 'id' del Comment al que pertenece la
	 * Correction aprobada que se consulta
	 * @return la unica Correction, perteneciente al Comment dado,
	 * cuyo atributo 'approved' es 'true', o null si no existe
	 */
	public Correction getApprovedCorrectionByComment(Long commentId) {
		Correction result = new Correction();
		try{
			result = correctionJPA.getApprovedCorrectionByComment(commentId);
		}catch (EntityNotPersistedException ex) {
			result=null;
		}
		return result;
	}
	
	/**
	 * Halla las correcciones aun no aprobadas de un comentario
	 * @param commentId Comment al que pertenecen las Correction
	 * que se consultan
	 * @return lista de Correction, pertenecientes al Comment dado,
	 * cuyo atributo 'approved' es 'false', o null si no existe
	 */
	public List<Correction> getNotApprovedCorrectionsByComment(Long commentId){
		List<Correction> result = new ArrayList<Correction>();
		try{
			result=correctionJPA.getNotApprovedCorrectionsByComment(commentId);
		}catch (EntityNotPersistedException ex) {
			return result;
		}
		return result;
	}

	/**
	 * Halla el numero de recomedaciones que ha recibido la correccion dada
	 * @param correctionId atributo 'id' de la Correction que se consulta
	 * @return cantidad de CorrectionAgree que pertenecen a la Correction dada
	 */
	public Integer getNumCorrectionAgrees(Long correctionId) {
		Integer result = null;
		try{
			result = correctionJPA.getNumCorrectionAgrees(correctionId);
		}catch (EntityNotPersistedException ex) {
			result = 0;
		}
		return result;
	}
	
	/**
	 * Halla el numero de desaprobaciones que ha recibido la correccion dada
	 * @param correctionId atributo 'id' de la Correction que se consulta
	 * @return cantidad de CorrectionDisgree que pertenecen a la Correction dada
	 */
	public Integer getNumCorrectionDisagrees(Long correctionId) {
		Integer result = null;
		try{
			result = correctionJPA.getNumCorrectionDisagrees(correctionId);
		}catch (EntityNotPersistedException ex) {
			result = 0;
		}
		return result;
	}

	/**
	 * Comprueba si el usuario dado ha dado su recomendacion a una correccion
	 * @param userId atributo 'id' del User que se consulta
	 * @param correctionId atributo 'id' de la Correction que se consulta 
	 * @return
	 * true: si el usuario ha dado su recomendacion a la correccion<br>
	 * false: si el usuario aun no ha dado su recomendacion a la correccion
	 * @throws EntityNotFoundException
	 */
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

	/**
	 * Comprueba si el usuario dado ha dado su desaprobacion a una correccion
	 * @param userId atributo 'id' del User que se consulta
	 * @param correctionId atributo 'id' de la Correction que se consulta 
	 * @return
	 * true: si el usuario ha dado su desaprobacion a la correccion<br>
	 * false: si el usuario aun no ha dado su desaprobacion a la correccion
	 * @throws EntityNotFoundException
	 */
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
	
	/**
	 * Actualiza en el foro la correccion dada 
	 * (no solo su contenido, sino todo su estado)
	 * @param correctionToUpdate objeto Correction que se desea actualizar
	 * @throws EntityNotFoundException
	 */
	public void updateCorrection(Correction correctionToUpdate)
			throws EntityNotFoundException {
		try {
			correctionJPA.updateCorrection(correctionToUpdate);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
		;
	}
	 
	/**
	 * Actualiza en el foro el contenido de la correccion dada
	 * @param corrToUpdate objeto Correction que se desea actualizar
	 * @param text texto plano del comentario creado
	 * @param code texto HTML del comentario creado
	 * @throws EntityNotFoundException
	 */
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
	
	/**
	 * Cambia el atributo 'approved' de una Correction al valor 'true',
	 * incrementa la puntuacion del usuario autor de dicha correccion,
	 * comprueba si es preciso generar una publicacion para el evento
	 * y actualiza todos los cambios en la base de datos
	 * @param correction Correction que se actualiza
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	public void acceptCorrection(Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException {
		checkIfAnotherCorrectionIsAccepted(correction);
		// Marcar como aprobada la correccion y actualizarla en bdd:
		correction.setApprovedThis(true).setDateApprovedThis(new Date());
		updateCorrection(correction);
		// Incrementar los puntos del usuario y actualizarlo en bdd:
		User user = correction.getUser();
		user.getUserInfo().incrementPointsByAcceptedCorrection();
		transactionUser.updateAllDataByUser(user);
		// Y, si corresponde, se crean las Publication necesarias:
		generatePublicationsForAcceptedCorrection(correction);
	}
	
	/**
	 * Dada una correccion a un comentario, comprueba si ya existe
	 * otra correccion aceptada para dicho comentario
	 * @param correction Correccion que se pretende aceptar
	 * @throws EntityNotFoundException
	 * @throws EntityNotFoundException 
	 */
	private void checkIfAnotherCorrectionIsAccepted(Correction correction)
			throws EntityNotFoundException, EntityNotFoundException {
		Long commentId = correction.getComment().getId();
		Correction prevCorrection = getApprovedCorrectionByComment(commentId);
		if( prevCorrection == null ){ return; }
		// Si ya existia una correccion aprobada para este comentario,
		// la pone como "no aprobada":
		prevCorrection.setApprovedThis(false);
		//updateCorrection(prevCorrection);
		try{
			correctionJPA.setApprovedFalse(prevCorrection.getId());
		}catch(EntityNotPersistedException e){
			throw new EntityNotFoundException(e);
		}
		// y le quita los puntos al autor de dicha correccion:
		User user = prevCorrection.getUser();
		user.getUserInfo().decrementPointsByDeletedCorrection(prevCorrection);
		transactionUser.updateAllDataByUser(user);
	}
	
	/**
	 * Comprueba si es necesaria la generacion de alguna Publication tras
	 * producirse la aceptacion de una correccion. Las Publication se generaran
	 * en los siguientes casos: <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- Siempre que una correccion es aceptada<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- El usuario autor de la correccion ha alcanzado
	 * las '10/25/50/100/250/500/(...) correcciones aprobadas <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;- El incremento en la puntuacion del usuario
	 * hace que este ascienda en la clasificacion hasta el
	 * top de los primeros 100 usuarios, o 50, o 25, o 20, o 15, o 10, o 5
	 * o superiores <br>
	 * @param corr Correccion que, por haber sido aceptada, genera 
	 * alguna Publication
	 * @throws EntityNotFoundException
	 * @throws EntityAlreadyFoundException
	 */
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
					totalCorrs.longValue(), userCorrector.getId()) ){
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
	
	/**
	 * Halla el numero de correcciones aceptadas alcanzadas por un usuario
	 * @param userID atributo 'id' deñ User que se consulta
	 * @return cantidad de Correction que pertenecen al User dado, cuyo atribuo
	 * 'approved' es 'true'
	 * @throws EntityNotFoundException
	 */
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
	
	/**
	 * Comprueba si el numero recibido (correcciones aprobadas de un usuario)
	 * ha alcanzado una cantidad considerada como logro; es decir, si coincide
	 * con uno de los numeros de la serie '10/25/50/100/250/500...'
	 * @param numCorrections numero de correcciones aprobadas
	 * publcadas por un usuario
	 * @return
	 * true: si el numero dado esta en la serie '10/25/50/100/250/500...'
	 * <br>
	 * false: si el numero dado no esta la serie '10/25/50/100/250/500...'
	 */
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
	
	/**
	 * Elimina del foro la correccion dada
	 * @param corr objeto Correction que se desea eliminar
	 * @throws EntityNotFoundException
	 */
	public void deleteCorrection(Correction corr)
			throws EntityNotFoundException {
		try {
			transactionPub.editPubsForDeletedPost(corr);
			correctionJPA.deleteCorrection(corr);
			if( corr.getApproved() ){
				User user = corr.getUser();
				user.getUserInfo().decrementPointsByDeletedCorrection(corr);
				transactionUser.updateAllDataByUser(user);
			}
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Crea la asociacion CorrectionAgree entre el User dado y la Correction
	 * indicada, comprueba si es preciso generar una publicacion para el evento
	 * y actualiza todos los cambios en la base de datos
	 * @param userId atributo 'id' del User que recomienda la correccion dada
	 * @param correction Correction que se actualiza
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
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
	
	/**
	 * Elimina la asociacion CorrectionAgree entre el User dado
	 * y la Correction indicada.
	 * @param userId usuario asociado al CorrectionAgree eliminado
	 * @param correction correccion asociada al CorrectionAgree eliminado
	 * @throws EntityNotFoundException
	 */
	public void deleteAgreementForTest(Long userId, Correction correction)
			throws EntityNotFoundException {
		try {
			correctionJPA.deleteAgreement(userId, correction.getId());
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Crea la asociacion CorrectionDisagree entre el User dado y la Correction
	 * indicada, y actualiza todos los cambios en la base de datos
	 * @param userId atributo 'id' del User que recomienda la correccion dada
	 * @param correctionId atributo 'id' de la Correction que se actualiza
	 * @throws EntityAlreadyFoundException
	 */
	public void dissuadeCorrection(Long userId, Long correctionId)
			throws EntityAlreadyFoundException {
		try {
			correctionJPA.createCorrectionDisagree(userId, correctionId);
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		}
	}
	
	/**
	 * Genera una correccion en un hilo del foro, comprueba si es preciso
	 * generar una publicacion para el evento y actualiza todos los cambios
	 * en la base de datos
	 * @param correctionData la correccion que se va a agregar
	 * @return el objeto Correction generado
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	public Correction sendCorrection(Correction correctionData)
			throws EntityAlreadyFoundException, EntityNotFoundException{
		try {
			Correction correctionResult = correctionJPA.createCorrection(
					correctionData);
			// Se crean las Publication necesarias:
			generatePublicationsToSendCorr(correctionData);
			// Se actualiza la noticia:
			// ForumThread thread = correctionData.getForumThread();
			// transactionThread.updateDataByThread(thread, true);
			return correctionResult;
		} catch (EntityAlreadyPersistedException ex) {
			throw new EntityAlreadyFoundException(ex);
		} catch (EntityNotPersistedException ex) {
			throw new EntityNotFoundException(ex);
		}
	}
	
	/**
	 * Genera las Publication para el usuario que recibe la correccion y
	 * tambien genera las Publication para el usuario que realiza la Correccion
	 * @param correction Correction que, por haber sido creada,
	 * provoca la creacion de Publication
	 * @throws EntityNotFoundException
	 * @throws EntityAlreadyFoundException
	 */
	private void generatePublicationsToSendCorr(Correction correction)
			throws EntityNotFoundException, EntityAlreadyFoundException {
		Comment commentToCorrect = correction.getComment();
		/* TypePrivacity.CONTACTS, porque esta publicacion
		no tendria sentido si solo fuera privada,
		ya que va dirigida a los contactos del usuario, mas que a el mismo */
		transactionPub.generatePublication(TypePrivacity.CONTACTS,
				commentToCorrect.getId(), 101L, commentToCorrect.getUser());
		// TypePrivacity.CONTACTS, por el mismo motivo
		transactionPub.generatePublication(TypePrivacity.CONTACTS,
				correction.getId(), 201L, correction.getUser());
	}
}
