package com.loqua.business.services.locator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.loqua.business.factory.ServicesFactory;
import com.loqua.business.services.ServiceComment;
import com.loqua.business.services.ServiceContact;
import com.loqua.business.services.ServiceCorrection;
import com.loqua.business.services.ServiceCountry;
import com.loqua.business.services.ServiceCredentials;
import com.loqua.business.services.ServiceFeed;
import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.ServiceMessage;
import com.loqua.business.services.ServiceThread;
import com.loqua.business.services.ServiceForumPost;
import com.loqua.business.services.ServicePublication;
import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.ServiceUserAccessDataChange;

public class LocatorLocalEjbServices implements ServicesFactory {
	
	private static final String SERVICE_COMMENT_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceComment!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceComment";
	private static final String SERVICE_CONTACT_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceContact!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceContact";
	private static final String SERVICE_CORRECTION_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceCorrection!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceCorrection";
	private static final String SERVICE_COUNTRY_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceCountry!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceCountry";
	private static final String SERVICE_FEED_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceFeed!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceFeed";
	private static final String SERVICE_LANGUAGE_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceLanguage!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceLanguage";
	private static final String SERVICE_MESSAGE_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceMessage!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceMessage";
	private static final String SERVICE_NEW_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceThread!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceThread";
	private static final String SERVICE_USERACCESSDATA_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceUserAccessDataChange!"
			+ "com.loqua.business.services.serviceLocal"
			+ ".LocalServiceUserAccessDataChange";
	private static final String SERVICE_FORUMPOST_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceForumPost!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceForumPost";
	private static final String SERVICE_PUBLICATION_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServicePublication!"
			+ "com.loqua.business.services.serviceLocal.LocalServicePublication";
	private static final String SERVICE_USER_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceUser!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceUser";
	private static final String SERVICE_CREDENTIALS_JNDI_KEY =
			"java:global/loqua-ear/loqua-ejb/"
			+ "EjbServiceCredentials!"
			+ "com.loqua.business.services.serviceLocal.LocalServiceCredentials";
	
	@Override
	public ServiceComment getServiceComment() {
		try {
			Context ctx = new InitialContext();
			return (ServiceComment) ctx.lookup( SERVICE_COMMENT_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceContact getServiceContact() {
		try {
			Context ctx = new InitialContext();
			return (ServiceContact) ctx.lookup( SERVICE_CONTACT_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceCorrection getServiceCorrection() {
		try {
			Context ctx = new InitialContext();
			return (ServiceCorrection) ctx.lookup(SERVICE_CORRECTION_JNDI_KEY);
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceCountry getServiceCountry() {
		try {
			Context ctx = new InitialContext();
			return (ServiceCountry) ctx.lookup( SERVICE_COUNTRY_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceFeed getServiceFeed() {
		try {
			Context ctx = new InitialContext();
			return (ServiceFeed) ctx.lookup( SERVICE_FEED_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceLanguage getServiceLanguage() {
		try {
			Context ctx = new InitialContext();
			return (ServiceLanguage) ctx.lookup( SERVICE_LANGUAGE_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceMessage getServiceMessage() {
		try {
			Context ctx = new InitialContext();
			return (ServiceMessage) ctx.lookup( SERVICE_MESSAGE_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceThread getServiceThread() {
		try {
			Context ctx = new InitialContext();
			return (ServiceThread) ctx.lookup( SERVICE_NEW_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceForumPost getServiceForumPost() {
		try {
			Context ctx = new InitialContext();
			return (ServiceForumPost) ctx.lookup(SERVICE_FORUMPOST_JNDI_KEY);
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServicePublication getServicePublication() {
		try {
			Context ctx = new InitialContext();
			return (ServicePublication) ctx.lookup(SERVICE_PUBLICATION_JNDI_KEY);
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}

	@Override
	public ServiceUser getServiceUser() {
		try {
			Context ctx = new InitialContext();
			return (ServiceUser) ctx.lookup( SERVICE_USER_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}
	
	@Override
	public ServiceUserAccessDataChange getServiceUserAccessDataChange() {
		try {
			Context ctx = new InitialContext();
			return (ServiceUserAccessDataChange) 
					ctx.lookup( SERVICE_USERACCESSDATA_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}
	
	@Override
	public ServiceCredentials getServiceCredentials() {
		try {
			Context ctx = new InitialContext();
			return (ServiceCredentials) 
					ctx.lookup( SERVICE_CREDENTIALS_JNDI_KEY );
		} catch (NamingException e) {
			throw new RuntimeException("JNDI problem", e);
		}
	}
}
