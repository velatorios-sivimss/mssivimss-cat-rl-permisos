package com.imss.flujo.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.flujo.util.DatosRequest;
import com.imss.flujo.util.Response;

public interface UsuarioService {

	Response<?> consultarUsuarios(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> buscarUsuario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> detalleUsuario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> catalogoUsuario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> agregarUsuario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> actualizarUsuario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> cambiarEstatusUsuario(DatosRequest request, Authentication authentication) throws IOException;
}
