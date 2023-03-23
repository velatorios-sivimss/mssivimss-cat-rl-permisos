package com.imss.rolespermisos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MensajeResponseUtil {
	
	private static final Logger log = LoggerFactory.getLogger(MensajeResponseUtil.class);
	
	private MensajeResponseUtil() {
		super();
	}

	
	public  static Response<?>mensajeResponse(Response<?> respuestaGenerado, String numeroMensaje) {
		Integer codigo = respuestaGenerado.getCodigo();
		if (codigo == 200) {
			respuestaGenerado.setMensaje(numeroMensaje);
		} else {
			log.info("Error.. {}",respuestaGenerado.getMensaje());
			respuestaGenerado.setMensaje("005");
		
		} 
		return respuestaGenerado;
	}
	




}
