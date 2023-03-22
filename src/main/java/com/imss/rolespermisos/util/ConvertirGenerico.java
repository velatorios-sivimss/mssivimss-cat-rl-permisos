package com.imss.rolespermisos.util;

public class ConvertirGenerico {

	@SuppressWarnings("unchecked")
	public static <T> T convertInstanceOfObject(Object o) {
	    try {
	       return (T) o;
	    } catch (ClassCastException e) {
	        return null;
	    }
	}
}
