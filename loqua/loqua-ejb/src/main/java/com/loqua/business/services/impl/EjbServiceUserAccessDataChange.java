package com.loqua.business.services.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.exception.EntityAlreadyFoundException;
import com.loqua.business.exception.EntityNotFoundException;
import com.loqua.business.services.ServiceUserAccessDataChange;
import com.loqua.business.services.impl.transactionScript.TransactionUserAccessDataChange;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceUserAccessDataChange;
import com.loqua.business.services.serviceRemote.RemoteServiceUserAccessDataChange;
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.model.User;

/**
 * Da acceso a las transacciones correspondientes a las entidades
 * {@link ChangeEmail} y {@link ChangePassword}.<br>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceUserAccessDataChange}
 * y {@link RemoteServiceUserAccessDataChange},
 * que heredan de {@link ServiceUserAccessDataChange}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceUserAccessDataChange")
public class EjbServiceUserAccessDataChange
		implements LocalServiceUserAccessDataChange,
		RemoteServiceUserAccessDataChange {

	/** Objeto de la capa de negocio que realiza la logica relativa a las
	 * entidades {@link ChangeEmail} y {@link ChangePassword},
	 * incluyendo procedimientos 'CRUD' de dichas entidades */
	private static final TransactionUserAccessDataChange transactionDataChange = 
			new TransactionUserAccessDataChange();
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU EMAIL
	// // // // // // // // // // // // // // // //
	
	@Override
	public String sendEmailForEditEmail(User user, String newEmail,
			String content, String subject,
			Map<String, Integer> mapActionsLimits)
			throws EntityAlreadyFoundException {
		return transactionDataChange.sendEmailForEditEmail(
				user, newEmail, content, subject, mapActionsLimits);
	}
	
	@Override
	public void sendEmailForEditEmailSecondStep(ChangeEmail changeEmail,
			String contentSchema, String subject)
			throws EntityNotFoundException {
		transactionDataChange.sendEmailForEditEmailSecondStep(changeEmail,
				contentSchema, subject);
	}
	
	// // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME LA EDICION DE SU EMAIL
	// // // // // // // // // // // // // // // // // // // // //
	
	@Override
	public ChangeEmail getEmailChangeByUrlConfirm(String urlConfirm){
		return transactionDataChange.getEmailChangeByUrlConfirm(urlConfirm);
	}
	
	@Override
	public void updateEmailChange(ChangeEmail changeEmail) 
			throws EntityNotFoundException {
		transactionDataChange.updateEmailChange(changeEmail);
	}
	
	// // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO RESTAURE SU CONTRASENA
	// // // // // // // // // // // // // // // // // // //
	
	@Override
	public String sendEmailForPasswordRestore(User user,
			List<String> content, String subject,
			Map<String, Integer> mapActionsLimits) 
			throws EntityAlreadyFoundException {
		return transactionDataChange.sendEmailForPasswordRestore(
				user, content, subject, mapActionsLimits);
	}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO EDITE SU CONTRASENA
	// // // // // // // // // // // // // // // // // //
	
	@Override
	public String sendEmailForEditPassword(User user, String newPass,
			List<String> contentSchema, String subject,
			Map<String, Integer> mapActionsLimits) 
			throws EntityAlreadyFoundException {
		return transactionDataChange.sendEmailForEditPassword(user,
				newPass, contentSchema, subject, mapActionsLimits);
	}
	
	// // // // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA QUE EL USUARIO CONFIRME RESTAURACION/EDICION DE CONTRASENA
	// // // // // // // // // // // // // // // // // // // // // // // // //
	
	@Override
	public ChangePassword getPasswordChangeByUrlConfirm(
			String urlConfirm, String typeChangePassword){
		return transactionDataChange.getPasswordChangeByUrlConfirm(
				urlConfirm, typeChangePassword);
	}
	
	@Override
	public void updatePasswordChange(ChangePassword objectChangePassword)
			throws EntityNotFoundException {
		transactionDataChange.updatePasswordChange(objectChangePassword);
	}
}
