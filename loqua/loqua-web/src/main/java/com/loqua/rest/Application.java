package com.loqua.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.loqua.rest.impl.ImplRestServiceFeed;
import com.loqua.rest.impl.ImplRestServiceForumThread;

/** Establece cuales son los servicios REST que usuara RESTEasy,
 * indicandole las clases {@link ImplRestServiceForumThread}
 * y {@link ImplRestServiceFeed} */
@ApplicationPath("/rest")
public class Application extends javax.ws.rs.core.Application {
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> res = new HashSet<Class<?>>();
	res.add( ImplRestServiceForumThread.class );
	res.add( ImplRestServiceFeed.class );
	return res;
	}
}
