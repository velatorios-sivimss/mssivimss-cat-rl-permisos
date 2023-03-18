package com.imss.rolespermisos.controller;

import java.io.IOException;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imss.rolespermisos.service.RolesPermisosService;
import com.imss.rolespermisos.util.DatosRequest;
import com.imss.rolespermisos.util.Response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class RolPermisoController {

	private RolesPermisosService rolesPermisosService;
	
	@PostMapping("consultar/rolpermiso")
	public Response<Object> consultaLista(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
	
		return rolesPermisosService.consultarRolesPermisos(request,authentication);
      
	}
	
	@PostMapping("consultar/niveles")
	public Response<Object> consultaNiveles(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
	
		return rolesPermisosService.consultaNiveles(request,authentication);
      
	}
	
	@PostMapping("consultar/velatorios")
	public Response<Object> consultaVelatorios(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
	
		return rolesPermisosService.consultaVelatorios(request,authentication);
      
	}

	@PostMapping("consultar/permisos")
	public Response<Object> consultaPermisos(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
	
		return rolesPermisosService.consultarPermisos(request,authentication);
      
	}
	@PostMapping("consultar/funcionalidades")
	public Response<Object> consultaFuncionalidades(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
	
		return rolesPermisosService.consultarFuncionalidades(request,authentication);
      
	}

	@PostMapping("agregar/rolpermiso")
	public Response<Object> agregar(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
	
		return rolesPermisosService.agregarRolPermiso(request,authentication);
      
	}

	@PostMapping("actualizar/rolpermiso")
	public Response<Object> actualizar(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
	
		return rolesPermisosService.actualizarRolPermiso(request,authentication);
      
	}
}
