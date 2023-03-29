package com.imss.sivimss.rolespermisos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertirGenerico {

	private static final Logger log = LoggerFactory.getLogger(ConvertirGenerico.class);
	@SuppressWarnings("unchecked")
	public static <T> T convertInstanceOfObject(Object o) {
	    try {
	       return (T) o;
	    } catch (ClassCastException e) {
			log.error("Error.. {}",e.getMessage() );
	        return null;
	    }
	}
}
