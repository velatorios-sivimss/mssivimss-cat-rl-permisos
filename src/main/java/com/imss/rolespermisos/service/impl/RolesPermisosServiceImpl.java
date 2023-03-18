package com.imss.rolespermisos.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.rolespermisos.beans.Funcionalidad;
import com.imss.rolespermisos.beans.NivelOficina;
import com.imss.rolespermisos.beans.Permiso;
import com.imss.rolespermisos.beans.RolPermiso;
import com.imss.rolespermisos.beans.Velatorio;
import com.imss.rolespermisos.exception.BadRequestException;
import com.imss.rolespermisos.model.request.FuncionalidadPermisosDto;
import com.imss.rolespermisos.model.request.RolesPermisosRequest;
import com.imss.rolespermisos.model.response.FuncionalidadResponse;
import com.imss.rolespermisos.model.response.NivelOficinaResponse;
import com.imss.rolespermisos.model.response.PermisoResponse;
import com.imss.rolespermisos.model.response.VelatorioResponse;
import com.imss.rolespermisos.service.RolesPermisosService;
import com.imss.rolespermisos.util.AppConstantes;
import com.imss.rolespermisos.util.ConvertirGenerico;
import com.imss.rolespermisos.util.DatosRequest;
import com.imss.rolespermisos.util.ProviderServiceRestTemplate;
import com.imss.rolespermisos.util.Response;

@Service
public class RolesPermisosServiceImpl implements RolesPermisosService {

	@Value("${endpoints.dominio-consulta}")
	private String urlDominioConsulta;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;


	@Autowired
	private ModelMapper modelMapper;

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(RolesPermisosServiceImpl.class);
	
	private String consultaGenerica = "/generico/consulta";
	
	@Override
	public Response<Object> consultarRolesPermisos (DatosRequest request, Authentication authentication) throws IOException {
		RolPermiso rolPermiso= new RolPermiso();
		return providerRestTemplate.consumirServicio(rolPermiso.obtenerRolesPermisos(request).getDatos(), urlDominioConsulta + "/generico/paginado",
				authentication);


	}

	@Override
	public Response<Object> consultaNiveles(DatosRequest request, Authentication authentication) throws IOException {
		NivelOficina nivelOficina= new NivelOficina();
		List<NivelOficinaResponse> nivelOficinaResponses;
		Response<Object> response = providerRestTemplate.consumirServicio(nivelOficina.obtenerNivelOficina().getDatos(),
				urlDominioConsulta + consultaGenerica, authentication);
		if (response.getCodigo() == 200) {
			nivelOficinaResponses = Arrays.asList(modelMapper.map(response.getDatos(), NivelOficinaResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(nivelOficinaResponses));
		}
		return response;
	}

	@Override
	public Response<Object> consultaVelatorios(DatosRequest request, Authentication authentication) throws IOException {
		Velatorio velatorio= new Velatorio();
		List<VelatorioResponse> velatorioResponses;
		Response<Object> response = providerRestTemplate.consumirServicio(velatorio.obtenerVelatorio().getDatos(),
				urlDominioConsulta + consultaGenerica, authentication);
		if (response.getCodigo() == 200) {
			velatorioResponses = Arrays.asList(modelMapper.map(response.getDatos(), VelatorioResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(velatorioResponses));
		}
		return response;
	}
	
	@Override
	public Response<Object> consultarPermisos(DatosRequest request, Authentication authentication) throws IOException {
		Permiso permisos= new Permiso();
		List<PermisoResponse> permisoResponses;
		Response<Object> response = providerRestTemplate.consumirServicio(permisos.obtenerPermiso().getDatos(),
				urlDominioConsulta + consultaGenerica, authentication);
		if (response.getCodigo() == 200) {
			permisoResponses = Arrays.asList(modelMapper.map(response.getDatos(), PermisoResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(permisoResponses));
		}
		return response;
	}

	@Override
	public Response<Object> consultarFuncionalidades(DatosRequest request, Authentication authentication) throws IOException {
		Funcionalidad funcionalidad= new Funcionalidad();
		List<FuncionalidadResponse> funcionalidadResponses;
		Response<Object> response = providerRestTemplate.consumirServicio(funcionalidad.obtenerFuncionalidad().getDatos(),
				urlDominioConsulta + consultaGenerica, authentication);
		if (response.getCodigo() == 200) {
			funcionalidadResponses = Arrays.asList(modelMapper.map(response.getDatos(), FuncionalidadResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(funcionalidadResponses));
		}
		return response;
	}
	
	@Override
	public Response<Object> agregarRolPermiso(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		
		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);
		RolPermiso rolPermiso= new RolPermiso(rolesPermisosRequest);
	
		return providerRestTemplate.consumirServicio(rolPermiso.insertar().getDatos(), urlDominioConsulta + "/generico/crear",
				authentication);
	}

	@SuppressWarnings("unused")
	@Override
	public Response<Object> actualizarRolPermiso(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		FuncionalidadPermisosDto funcionalidadPermisosDto = gson.fromJson((String) authentication.getPrincipal(), FuncionalidadPermisosDto.class);

		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);

		if (rolesPermisosRequest.getIdFuncionalidad() == null || rolesPermisosRequest.getIdPermiso() == null || rolesPermisosRequest.getIdRol() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		RolPermiso rolPermiso= new RolPermiso(rolesPermisosRequest);
		
		return providerRestTemplate.consumirServicio(rolPermiso.actualizar().getDatos(), urlDominioConsulta + "/generico/actualizar",
				authentication);
	}
}
