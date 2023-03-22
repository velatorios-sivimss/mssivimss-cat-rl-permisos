package com.imss.rolespermisos.service.impl;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.rolespermisos.beans.RolPermiso;
import com.imss.rolespermisos.exception.BadRequestException;
import com.imss.rolespermisos.model.request.FuncionalidadPermisosDto;
import com.imss.rolespermisos.model.request.RolesPermisosRequest;
import com.imss.rolespermisos.model.response.FuncionalidadResponse;
import com.imss.rolespermisos.model.response.PermisoResponse;
import com.imss.rolespermisos.model.response.RolPermisoDetalleResponse;
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

	RolPermiso rolPermiso = new RolPermiso();

	@Autowired
	private ModelMapper modelMapper;

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(RolesPermisosServiceImpl.class);

	private String consultaGenerica = "/generico/consulta";

	@Override
	public Response<Object> consultarRolesPermisos(DatosRequest request, Authentication authentication)
			throws IOException {

		return providerRestTemplate.consumirServicio(rolPermiso.obtenerRolesPermisos(request).getDatos(),
				urlDominioConsulta + "/generico/paginado", authentication);
	}

	@Override
	public Response<Object> consultarDetalleRolPermiso(DatosRequest request, Authentication authentication)
			throws IOException {
		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);
		rolPermiso = new RolPermiso(rolesPermisosRequest);
		List<RolPermisoDetalleResponse> permisoResponse;

		Response<Object> response = providerRestTemplate.consumirServicio(
				rolPermiso.obtenerDetalleRolPermiso().getDatos(), urlDominioConsulta + consultaGenerica,
				authentication);
		if (response.getCodigo() == 200) {
			permisoResponse = Arrays.asList(modelMapper.map(response.getDatos(), RolPermisoDetalleResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(permisoResponse));
		}
		return response;
	}

	@Override
	public Response<Object> consultarPermisos(DatosRequest request, Authentication authentication) throws IOException {

		List<PermisoResponse> permisoResponses;
		Response<Object> response = providerRestTemplate.consumirServicio(rolPermiso.obtenerPermiso().getDatos(),
				urlDominioConsulta + consultaGenerica, authentication);
		if (response.getCodigo() == 200) {
			permisoResponses = Arrays.asList(modelMapper.map(response.getDatos(), PermisoResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(permisoResponses));
		}
		return response;
	}

	@Override
	public Response<Object> consultarFuncionalidades(DatosRequest request, Authentication authentication)
			throws IOException {

		List<FuncionalidadResponse> funcionalidadResponses;
		Response<Object> response = providerRestTemplate.consumirServicio(rolPermiso.obtenerFuncionalidad().getDatos(),
				urlDominioConsulta + consultaGenerica, authentication);
		if (response.getCodigo() == 200) {
			funcionalidadResponses = Arrays.asList(modelMapper.map(response.getDatos(), FuncionalidadResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(funcionalidadResponses));
		}
		return response;
	}


	@SuppressWarnings("unused")
	@Override
	public Response<Object> actualizarRolPermiso(DatosRequest request, Authentication authentication)
			throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		FuncionalidadPermisosDto funcionalidadPermisosDto = gson.fromJson((String) authentication.getPrincipal(),
				FuncionalidadPermisosDto.class);

		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);

		if (rolesPermisosRequest.getIdFuncionalidad() == null || rolesPermisosRequest.getPermiso() == null
				|| rolesPermisosRequest.getIdRol() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		rolPermiso = new RolPermiso(rolesPermisosRequest);

		return actualizarPermisos(rolPermiso, authentication);
	}

	private List<Integer> extraerPermisosRol(String cadena) {
		ArrayList<Integer> num = new ArrayList<>();
		Matcher encontrar = Pattern.compile("\\d+").matcher(cadena);
		while (encontrar.find()) {
			num.add(Integer.parseInt(encontrar.group()));
		}
		return num;
	}

	private List<Integer> extraerTodosPermisos(List<PermisoResponse> permisos) {
		ArrayList<Integer> numPermiso = new ArrayList<>();
		for (PermisoResponse permiso : permisos)
			numPermiso.add(permiso.getIdPermiso());
		return numPermiso;
	}

	@SuppressWarnings("unused")
	private Response<Object> actualizarPermisos(RolPermiso rolPermiso, Authentication authentication)
			throws IOException {
		Response<Object> temp = null;

		Response<Object> listaPermisosBD = providerRestTemplate.consumirServicio(rolPermiso.obtenerPermiso().getDatos(),
				urlDominioConsulta + consultaGenerica, authentication);
		List<PermisoResponse> tempPermisos = Arrays
				.asList(modelMapper.map(listaPermisosBD.getDatos(), PermisoResponse[].class));
		List<Integer> listaPermisos = extraerTodosPermisos(tempPermisos);
		List<Integer> listaPermisosRol = extraerPermisosRol(rolPermiso.getPermiso());

		rolPermiso.setEstatus(0);
		providerRestTemplate.consumirServicio(rolPermiso.actualizar().getDatos(),
				urlDominioConsulta + "/generico/actualizar", authentication);
		for (Integer permiso : listaPermisosRol) {
			rolPermiso.setIdPermiso(permiso);
			if (Boolean.TRUE.equals(existeParametroCofig(rolPermiso, authentication))) {
				rolPermiso.setEstatus(1);
				temp = providerRestTemplate.consumirServicio(rolPermiso.actualizar().getDatos(),
						urlDominioConsulta + "/generico/actualizar", authentication);
			} else {
				rolPermiso.setIdUsuarioAlta(rolPermiso.getIdUsuarioModifica());
				rolPermiso.setEstatus(1);
				temp = providerRestTemplate.consumirServicio(rolPermiso.insertar().getDatos(),
						urlDominioConsulta + "/generico/crear", authentication);
			}
		}
		return temp;
	}

	private Boolean existeParametroCofig(RolPermiso rolPermiso, Authentication authentication) throws IOException {
		Response<Object> existe = providerRestTemplate.consumirServicio(rolPermiso.buscarRolPermiso().getDatos(),
				urlDominioConsulta + consultaGenerica, authentication);
		return !existe.getDatos().toString().equals("[]");

	}

}
