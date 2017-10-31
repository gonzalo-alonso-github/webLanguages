package com.loqua.business.services.impl;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.loqua.business.services.ServiceForumPost;
import com.loqua.business.services.impl.transactionScript.TransactionForumPost;
import com.loqua.business.services.locator.LocatorLocalEjbServices;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.business.services.serviceLocal.LocalServiceForumPost;
import com.loqua.business.services.serviceRemote.RemoteServiceForumPost;
import com.loqua.model.Comment;
import com.loqua.model.Correction;
import com.loqua.model.ForumPost;

/**
 * Da acceso a las transacciones correspondientes a la entidad
 * {@link ForumPost} (que es clase padre de {@link Comment}
 * y {@link Correction}).<br>
 * La intencion de esta 'subcapa' de EJBs no es albergar mucha logica de negocio
 * (de ello se ocupa el modelo y el Transaction Script), sino hacer
 * que las transacciones sean controladas por el contenedor de EJB
 * (Wildfly en este caso), quien se ocupa por ejemplo de abrir las conexiones
 * a la base de datos mediate un datasource y de realizar los rollback. <br>
 * Al ser un EJB de sesion sin estado no puede ser instanciado desde un cliente
 * o un Factory Method, sino que debe ser devuelto mediante el registro JNDI.
 * Forma parte del patron Service Locator y se encapsula tras las fachadas
 * {@link LocalServiceForumPost} y {@link RemoteServiceForumPost},
 * que heredan de {@link ServiceForumPost}, producto de
 * {@link LocatorLocalEjbServices} o {@link LocatorRemoteEjbServices}
 * @author Gonzalo
 */
@Stateless
@WebService(name="ServiceForumPost")
public class EjbServiceForumPost
		implements LocalServiceForumPost, RemoteServiceForumPost {

	/** Objeto de la capa de negocio que realiza la logica relativa a la
	 * entidad {@link },
	 * incluyendo procedimientos 'CRUD' de dicha entidad */
	private static final TransactionForumPost transactionForumPost = 
			new TransactionForumPost();
	
	@Override
	public ForumPost getForumPostById(Long forumPostId){
		return transactionForumPost.getForumPostByID(forumPostId);
	}
	/*
	@Override
	public List<ForumPost> getForumPostsByUser(User user) {
		return transactionForumPost.getForumPostsByUser(user);
	}
	*/
}
