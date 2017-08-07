package com.loqua.business.factory;

import com.loqua.business.services.ServiceComment;
import com.loqua.business.services.ServiceContact;
import com.loqua.business.services.ServiceCorrection;
import com.loqua.business.services.ServiceCountry;
import com.loqua.business.services.ServiceCredentials;
import com.loqua.business.services.ServiceFeed;
import com.loqua.business.services.ServiceLanguage;
import com.loqua.business.services.ServiceMessage;
import com.loqua.business.services.ServiceForumThread;
import com.loqua.business.services.ServiceForumPost;
import com.loqua.business.services.ServicePublication;
import com.loqua.business.services.ServiceUser;
import com.loqua.business.services.ServiceUserAccessDataChange;

public interface ServicesFactory{

	public ServiceComment getServiceComment();
	public ServiceContact getServiceContact();
	public ServiceCorrection getServiceCorrection();
	public ServiceCountry getServiceCountry();
	public ServiceFeed getServiceFeed();
	public ServiceLanguage getServiceLanguage();
	public ServiceMessage getServiceMessage();
	public ServiceForumThread getServiceThread();
	public ServiceForumPost getServiceForumPost();
	public ServicePublication getServicePublication();
	public ServiceUser getServiceUser();
	public ServiceUserAccessDataChange getServiceUserAccessDataChange();
	public ServiceCredentials getServiceCredentials();
}
