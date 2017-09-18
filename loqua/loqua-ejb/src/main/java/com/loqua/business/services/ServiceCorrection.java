package com.loqua.business.services;

import java.util.List;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.CorrectionAgree;
import com.loqua.model.CorrectionDisagree;
import com.loqua.model.User;

/**
 * Define la fachada que encapsula el acceso al objeto EJB que maneja
 * las transacciones de las entidades
 * {@link Correction}, {@link CorrectionAgree} y {@link CorrectionDisagree}
 * @author Gonzalo
 */
public interface ServiceCorrection {

	/**
	 * Consulta correcciones segun su atributo 'id'
	 * @param correctionId atributo 'id' de la Correction que se consulta
	 * @return Correction cuyo atributo 'id' coincide con el parametro dado,
	 * o null si no existe
	 */
	Correction getCorrectionById(Long correctionId);
	
	/**
	 * Halla la correccion aprobada de un comentario
	 * @param commentId atributo 'id' del Comment al que pertenece la
	 * Correction aprobada que se consulta
	 * @return la unica Correction, perteneciente al Comment dado,
	 * cuyo atributo 'approved' es 'true', o null si no existe
	 */
	Correction getApprovedCorrectionByComment(Long commentId);
	
	/**
	 * Halla las correcciones aun no aprobadas de un comentario
	 * @param commentId Comment al que pertenecen las Correction
	 * que se consultan
	 * @return lista de Correction, pertenecientes al Comment dado,
	 * cuyo atributo 'approved' es 'false', o null si no existe
	 */
	List<Correction> getNotApprovedCorrectionsByComment(Long commentId);

	/**
	 * Halla el numero de recomedaciones que ha recibido la correccion dada
	 * @param correctionId atributo 'id' de la Correction que se consulta
	 * @return cantidad de CorrectionAgree que pertenecen a la Correction dada
	 */
	Integer getNumCorrectionAgrees(Long correctionId);
	
	/**
	 * Halla el numero de desaprobaciones que ha recibido la correccion dada
	 * @param correctionId atributo 'id' de la Correction que se consulta
	 * @return cantidad de CorrectionDisgree que pertenecen a la Correction dada
	 */
	Integer getNumCorrectionDisagrees(Long correctionId);
	
	/**
	 * Comprueba si el usuario dado ha dado su recomendacion a una correccion
	 * @param userId atributo 'id' del User que se consulta
	 * @param correctionId atributo 'id' de la Correction que se consulta 
	 * @return
	 * true: si el usuario ha dado su recomendacion a la correccion<br/>
	 * false: si el usuario aun no ha dado su recomendacion a la correccion
	 * @throws EntityNotFoundException
	 */
	boolean getUserAgreeCorrection(Long userId, Long correctionId)
			throws EntityNotFoundException;
	
	/**
	 * Comprueba si el usuario dado ha dado su desaprobacion a una correccion
	 * @param userId atributo 'id' del User que se consulta
	 * @param correctionId atributo 'id' de la Correction que se consulta 
	 * @return
	 * true: si el usuario ha dado su desaprobacion a la correccion<br/>
	 * false: si el usuario aun no ha dado su desaprobacion a la correccion
	 * @throws EntityNotFoundException
	 */
	boolean getUserDisagreeCorrection(Long userId, Long correctionId)
			throws EntityNotFoundException;

	/**
	 * Cambia el atributo 'approved' de una Correction dada al valor 'true',
	 * incrementa la puntuacion del usuario autor de dicha correccion,
	 * comprueba si es preciso generar una publicacion para el evento
	 * y actualiza todos los cambios en la base de datos
	 * @param correction Correction que se actualiza
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	void acceptCorrection(Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	/**
	 * Elimina del foro la correccion dada
	 * @param correction objeto Correction que se desea eliminar
	 * @throws EntityNotFoundException
	 */
	void deleteCorrection(Correction correction) throws EntityNotFoundException;
	
	/**
	 * Crea la asociacion CorrectionAgree entre el User dado y la Correction
	 * indicada, comprueba si es preciso generar una publicacion para el evento
	 * y actualiza todos los cambios en la base de datos
	 * @param userId atributo 'id' del User que recomienda la correccion dada
	 * @param correction Correction que se actualiza
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	void recommendCorrection(Long userId, Correction correction)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	/**
	 * Crea la asociacion CorrectionDisagree entre el User dado y la Correction
	 * indicada, y actualiza todos los cambios en la base de datos
	 * @param userId atributo 'id' del User que recomienda la correccion dada
	 * @param correctionId atributo 'id' de la Correction que se actualiza
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	void dissuadeCorrection(Long userId, Long correctionId)
			throws EntityAlreadyFoundException;
	
	/**
	 * Genera una correccion en un hilo del foro, comprueba si es preciso
	 * generar una publicacion para el evento y actualiza todos los cambios
	 * en la base de datos
	 * @param commentToCorrect comentario que recibe la correccion
	 * @param text texto plano de la correccion creada
	 * @param code texto HTML de la correccion creada
	 * @param user User autor de la Correction creada
	 * @return el objet Correction que se genera
	 * @throws EntityAlreadyFoundException
	 * @throws EntityNotFoundException
	 */
	Correction sendCorrection(Comment commentToCorrect,
			String text, String code, User user)
			throws EntityAlreadyFoundException, EntityNotFoundException;
	
	/**
	 * Actualiza en el foro el contenido de la correccion dada
	 * @param corrToUpdate objeto Correction que se desea actualizar
	 * @param text texto plano del comentario creado
	 * @param code texto HTML del comentario creado
	 * @throws EntityNotFoundException
	 */
	void updateTextCorrection(Correction corrToUpdate, 
			String text, String code)
			throws EntityNotFoundException;
}