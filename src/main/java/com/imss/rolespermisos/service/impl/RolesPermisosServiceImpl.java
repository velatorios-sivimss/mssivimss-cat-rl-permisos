package com.imss.rolespermisos.service.impl;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.rolespermisos.beans.RolPermiso;
import com.imss.rolespermisos.exception.BadRequestException;
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

	@Value("${endpoints.consulta-generica-paginado}")
	private String urlConsultaGenericoPaginado;
	
	@Value("${endpoints.consulta-generica}")
	private String urlConsultaGenerica;
	
	@Value("${endpoints.guardar-datos}")
	private String urlGuardarDatos;
	
	@Value("${endpoints.actualizar-datos}")
	private String urlActualizarDatos;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;

	RolPermiso rolPermiso = new RolPermiso();

	@Autowired
	private ModelMapper modelMapper;

	
	@Override
	public Response<?> consultarRolesPermisos(DatosRequest request, Authentication authentication)
			throws IOException {

		return providerRestTemplate.consumirServicio(rolPermiso.obtenerRolesPermisos(request).getDatos(),
				urlConsultaGenericoPaginado, authentication);
	}

	@Override
	public Response<?> consultarDetalleRolPermiso(DatosRequest request, Authentication authentication)
			throws IOException {
		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);
		rolPermiso = new RolPermiso(rolesPermisosRequest);
		List<RolPermisoDetalleResponse> permisoResponse;

		Response<?> response = providerRestTemplate.consumirServicio(
				rolPermiso.obtenerDetalleRolPermiso().getDatos(), urlConsultaGenerica,
				authentication);
		if (response.getCodigo() == 200) {
			permisoResponse = Arrays.asList(modelMapper.map(response.getDatos(), RolPermisoDetalleResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(permisoResponse));
		}
		return response;
	}

	@Override
	public Response<?> consultarPermisos(DatosRequest request, Authentication authentication) throws IOException {

		List<PermisoResponse> permisoResponses;
		Response<?> response = providerRestTemplate.consumirServicio(rolPermiso.obtenerPermiso().getDatos(),
				urlConsultaGenerica, authentication);
		if (response.getCodigo() == 200) {
			permisoResponses = Arrays.asList(modelMapper.map(response.getDatos(), PermisoResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(permisoResponses));
		}
		return response;
	}

	@Override
	public Response<?> consultarFuncionalidades(DatosRequest request, Authentication authentication)
			throws IOException {

		List<FuncionalidadResponse> funcionalidadResponses;
		Response<?> response = providerRestTemplate.consumirServicio(rolPermiso.obtenerFuncionalidad().getDatos(),
				urlConsultaGenerica, authentication);
		if (response.getCodigo() == 200) {
			funcionalidadResponses = Arrays.asList(modelMapper.map(response.getDatos(), FuncionalidadResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(funcionalidadResponses));
		}
		return response;
	}


	@Override
	public Response<?> actualizarRolPermiso(DatosRequest request, Authentication authentication)
			throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));

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


	private Response<?> actualizarPermisos(RolPermiso rolPermiso, Authentication authentication)
			throws IOException {
		Response<?> temp = null;

		List<Integer> listaPermisosRol = extraerPermisosRol(rolPermiso.getPermiso());

		rolPermiso.setEstatus(0);
		providerRestTemplate.consumirServicio(rolPermiso.actualizar().getDatos(),
				urlActualizarDatos, authentication);
		for (Integer permiso : listaPermisosRol) {
			rolPermiso.setIdPermiso(permiso);
			if (Boolean.TRUE.equals(existeParametroCofig(rolPermiso, authentication))) {
				rolPermiso.setEstatus(1);
				temp = providerRestTemplate.consumirServicio(rolPermiso.actualizar().getDatos(),
						urlActualizarDatos, authentication);
			} else {
				rolPermiso.setIdUsuarioAlta(rolPermiso.getIdUsuarioModifica());
				rolPermiso.setEstatus(1);
				temp = providerRestTemplate.consumirServicio(rolPermiso.insertar().getDatos(),
						urlGuardarDatos, authentication);
			}
		}
		return temp;
	}

	private Boolean existeParametroCofig(RolPermiso rolPermiso, Authentication authentication) throws IOException {
		Response<?> existe = providerRestTemplate.consumirServicio(rolPermiso.buscarRolPermiso().getDatos(),
				urlConsultaGenerica, authentication);
		return !existe.getDatos().toString().equals("[]");

	}

}
