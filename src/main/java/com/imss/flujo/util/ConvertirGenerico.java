package com.imss.flujo.util;

public class ConvertirGenerico {

	public static <T> T convertInstanceOfObject(Object o) {
	    try {
	       return (T) o;
	    } catch (ClassCastException e) {
	        return null;
	    }
	}
}
