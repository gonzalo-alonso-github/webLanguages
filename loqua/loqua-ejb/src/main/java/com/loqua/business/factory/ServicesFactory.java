package com.loqua.business.factory;

import com.loqua.business.services.ServiceComment;
import com.loqua.business.services.ServiceContact;
import com.loqua.business.services.ServiceCorrection;
import com.loqua.business.services.ServiceCountry;
import com.loqua.business.services.ServiceCredentials;
import com.loqua.business.services.ServiceFeed;
import com.loqua.business.services.ServiceForumPost;
import com.loqua.business.services.ServiceForumThread;
import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.ServiceMessage;
import com.loqua.business.services.ServicePublication;
import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.ServiceUserAccessDataChange;
import com.loqua.business.services.impl.EjbServiceComment;
import com.loqua.business.services.impl.EjbServiceContact;
import com.loqua.business.services.impl.EjbServiceCorrection;
import com.loqua.business.services.impl.EjbServiceCountry;
import com.loqua.business.services.impl.EjbServiceFeed;
import com.loqua.business.services.impl.EjbServiceForumPost;
import com.loqua.business.services.impl.EjbServiceLanguage;
import com.loqua.business.services.impl.EjbServiceMessage;
import com.loqua.business.services.impl.EjbServicePublication;
import com.loqua.business.services.impl.EjbServiceThread;
import com.loqua.business.services.impl.EjbServiceUser;
import com.loqua.business.services.impl.EjbServiceUserAccessDataChange;
import com.loqua.model.ChangeEmail;
import com.loqua.model.ChangePassword;
import com.loqua.model.Comment;
import com.loqua.model.CommentQuoteTo;
import com.loqua.model.Contact;
import com.loqua.model.ContactRequest;
import com.loqua.model.Correction;
import com.loqua.model.CorrectionAgree;
import com.loqua.model.CorrectionDisagree;
import com.loqua.model.Country;
import com.loqua.model.Credentials;
import com.loqua.model.Feed;
import com.loqua.model.FeedCategory;
import com.loqua.model.ForumPost;
import com.loqua.model.ForumThread;
import com.loqua.model.ForumThreadInfo;
import com.loqua.model.ForumThreadVoter;
import com.loqua.model.Language;
import com.loqua.model.PrivacityData;
import com.loqua.model.Publication;
import com.loqua.model.PublicationReceiver;
import com.loqua.model.User;
import com.loqua.model.UserInfo;
import com.loqua.model.UserInfoPrivacity;
import com.loqua.model.UserNativeLanguage;
import com.loqua.model.UserPracticingLanguage;

/**
 * Define la fachada que encapsula todos los servicios de la capa de negocio
 * @author Gonzalo
 */
public interface ServicesFactory{

	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceComment}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link Comment} y {@link CommentQuoteTo}
	 * @return objeto EJB que implementa a {@link ServiceComment}
	 */
	public ServiceComment getServiceComment();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceContact}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link Contact} y {@link ContactRequest}
	 * @return objeto EJB que implementa a {@link ServiceContact}
	 */
	public ServiceContact getServiceContact();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceCorrection}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link Correction}, {@link CorrectionAgree} y {@link CorrectionDisagree}
	 * @return objeto EJB que implementa a {@link ServiceCorrection}
	 */
	public ServiceCorrection getServiceCorrection();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceCountry}
	 * que da acceso a las transacciones correspondientes a la entidad
	 * {@link Country}
	 * @return objeto EJB que implementa a {@link ServiceCountry}
	 */
	public ServiceCountry getServiceCountry();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceFeed}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link Feed} y {@link FeedCategory}
	 * @return objeto EJB que implementa a {@link ServiceFeed}
	 */
	public ServiceFeed getServiceFeed();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceLanguage}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link Language}, {@link UserNativeLanguage}
	 * y {@link UserPracticingLanguage}
	 * @return objeto EJB que implementa a {@link ServiceLanguage}
	 */
	public ServiceLanguage getServiceLanguage();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceMessage}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link 'Message'} y {@link 'MessageReceiver'}
	 * @return objeto EJB que implementa a {@link ServiceMessage}
	 */
	public ServiceMessage getServiceMessage();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceForumPost}
	 * que da acceso a las transacciones correspondientes a la entidad
	 * {@link ForumPost} (que es clase padre de {@link Comment}
	 * y {@link Correction})
	 * @return objeto EJB que implementa a {@link ServiceForumPost}
	 */
	public ServiceForumPost getServiceForumPost();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceThread}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link ForumThread}, {@link ForumThreadInfo} y {@link ForumThreadVoter}
	 * @return objeto EJB que implementa a {@link ServiceForumThread}
	 */
	public ServiceForumThread getServiceThread();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServicePublication}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link Publication} y {@link PublicationReceiver}
	 * @return objeto EJB que implementa a {@link ServicePublication}
	 */
	public ServicePublication getServicePublication();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceUser}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link User}, {@link UserInfo}, {@link UserInfoPrivacity}
	 * y {@link PrivacityData}
	 * @return objeto EJB que implementa a {@link ServiceUser}
	 */
	public ServiceUser getServiceUser();
	
	/**
	 * Halla en el registro JNDI el objeto
	 * {@link EjbServiceUserAccessDataChange}
	 * que da acceso a las transacciones correspondientes a las entidades
	 * {@link ChangeEmail} y {@link ChangePassword}
	 * @return objeto EJB que implementa a {@link ServiceUserAccessDataChange}
	 */
	public ServiceUserAccessDataChange getServiceUserAccessDataChange();
	
	/**
	 * Halla en el registro JNDI el objeto {@link EjbServiceUserCredentials}
	 * que da acceso a las transacciones correspondientes a la entidad
	 * {@link Credentials}
	 * @return objeto EJB que implementa a {@link ServiceCredentials}
	 */
	public ServiceCredentials getServiceCredentials();
}
