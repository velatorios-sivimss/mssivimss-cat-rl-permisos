package com.imss.sivimss.rolespermisos.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.rolespermisos.util.DatosRequest;
import com.imss.sivimss.rolespermisos.util.Response;

public interface RolesPermisosService {

	Response<Object> consultarRolesPermisos(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> consultarDetalleRolPermiso(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> consultaFiltroRolPermiso(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> consultarPermisos(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> consultarFuncionalidades(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> agregarRolPermiso(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> actualizarRolPermiso(DatosRequest request, Authentication authentication) throws IOException;

}