package com.imss.rolespermisos.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.rolespermisos.util.DatosRequest;
import com.imss.rolespermisos.util.Response;

public interface RolesPermisosService {

	Response<?> consultarRolesPermisos(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> consultarDetalleRolPermiso(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> consultarPermisos(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> consultarFuncionalidades(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> agregarRolPermiso(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> actualizarRolPermiso(DatosRequest request, Authentication authentication) throws IOException;

}
