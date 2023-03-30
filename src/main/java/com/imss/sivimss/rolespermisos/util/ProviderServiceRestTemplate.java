package com.imss.sivimss.rolespermisos.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.rolespermisos.security.jwt.JwtTokenProvider;


@Service
public class ProviderServiceRestTemplate {

	@Autowired
	private RestTemplateUtil restTemplateUtil;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	private static final Logger log = LoggerFactory.getLogger(ProviderServiceRestTemplate.class);

	public Response<Object> consumirServicio(Map<String, Object> dato, String url, Authentication authentication)
			throws IOException {
		try {
			Response respuestaGenerado = restTemplateUtil.sendPostRequestByteArrayToken(url,
					new EnviarDatosRequest(dato), jwtTokenProvider.createToken((String) authentication.getPrincipal()),
					Response.class);
			return validarResponse(respuestaGenerado);
		} catch (IOException exception) {
			log.error("Ha ocurrido un error al recuperar la informacion");
			throw exception;
		}
	}

	public Response<Object> consumirServicioReportes(Map<String, Object> dato, String nombreReporte, String tipoReporte,
			String url, Authentication authentication) throws IOException {
		try {
			Response respuestaGenerado = restTemplateUtil.sendPostRequestByteArrayReportesToken(url,
					new DatosReporteDTO(dato, nombreReporte, tipoReporte),
					jwtTokenProvider.createToken((String) authentication.getPrincipal()), Response.class);
			return validarResponse(respuestaGenerado);
		} catch (IOException exception) {
			log.error("Ha ocurrido un error al recuperar la informacion");
			throw exception;
		}
	}

	public Response<Object> validarResponse(Response respuestaGenerado) {
		String codigo = respuestaGenerado.getMensaje().substring(0, 3);
		if (codigo.equals("500") || codigo.equals("404") || codigo.equals("400") || codigo.equals("403")) {
			Gson gson = new Gson();
			String mensaje = respuestaGenerado.getMensaje().substring(7, respuestaGenerado.getMensaje().length() - 1);

			ErrorsMessageResponse apiExceptionResponse = gson.fromJson(mensaje, ErrorsMessageResponse.class);

			respuestaGenerado = Response.builder().codigo((int) apiExceptionResponse.getCodigo()).error(true)
					.mensaje(apiExceptionResponse.getMensaje()).datos(apiExceptionResponse.getDatos()).build();

		}
		return respuestaGenerado;
	}

	public Response<Object> respuestaProvider(String e) {
		StringTokenizer exeception = new StringTokenizer(e, ":");
		Gson gson = new Gson();
		int i = 0;
		int totalToken = exeception.countTokens();
		StringBuilder mensaje = new StringBuilder("");
		int codigoError = HttpStatus.INTERNAL_SERVER_ERROR.value();
		int isExceptionResponseMs = 0;
		while (exeception.hasMoreTokens()) {
			String str = exeception.nextToken();
			i++;
			if (i == 2) {
				String[] palabras = str.split("\\.");
				for (String palabra : palabras) {
					if ("BadRequestException".contains(palabra)) {
						codigoError = HttpStatus.BAD_REQUEST.value();
					} else if ("ResourceAccessException".contains(palabra)) {
						codigoError = HttpStatus.REQUEST_TIMEOUT.value();

					}
				}
			} else if (i == 3) {

				if (str.trim().chars().allMatch( Character::isDigit )) {
					isExceptionResponseMs = 1;
				}

				mensaje.append(codigoError == HttpStatus.REQUEST_TIMEOUT.value()
						?AppConstantes.CIRCUITBREAKER
						: str);

			} else if (i >= 4 && isExceptionResponseMs == 1) {
				if (i == 4) {
					mensaje.delete(0, mensaje.length());
				}
				mensaje.append(str).append(i != totalToken ? ":" : "");

			}
		}
		
		Response response;
		try {
			if(mensaje.length() < 3)
				response = new Response<>(true, codigoError, mensaje.toString().trim(), Collections.emptyList());
			else {
			response = isExceptionResponseMs == 1 ? gson.fromJson(mensaje.substring(2, mensaje.length() - 1), Response.class)
				: new Response<>(true, codigoError, mensaje.toString().trim(), Collections.emptyList());
			}
		} catch (Exception e2) {
			return new Response<>(true, HttpStatus.REQUEST_TIMEOUT.value(), AppConstantes.CIRCUITBREAKER, Collections.emptyList());
		}
		
		return response;
	}

}