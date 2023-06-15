package com.imss.sivimss.rolespermisos.service.impl;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.rolespermisos.beans.RolPermiso;
import com.imss.sivimss.rolespermisos.exception.BadRequestException;
import com.imss.sivimss.rolespermisos.model.request.RolesPermisosRequest;
import com.imss.sivimss.rolespermisos.model.request.UsuarioDto;
import com.imss.sivimss.rolespermisos.model.response.FuncionalidadResponse;
import com.imss.sivimss.rolespermisos.model.response.PermisoResponse;
import com.imss.sivimss.rolespermisos.model.response.RolPermisoDetalleResponse;
import com.imss.sivimss.rolespermisos.service.RolesPermisosService;
import com.imss.sivimss.rolespermisos.util.AppConstantes;
import com.imss.sivimss.rolespermisos.util.ConvertirGenerico;
import com.imss.sivimss.rolespermisos.util.DatosRequest;
import com.imss.sivimss.rolespermisos.util.MensajeResponseUtil;
import com.imss.sivimss.rolespermisos.util.ProviderServiceRestTemplate;
import com.imss.sivimss.rolespermisos.util.Response;

@Service
public class RolesPermisosServiceImpl implements RolesPermisosService {


	@Value("${endpoints.mod-catalogos}")
	private String urlModCatalogos;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;

	RolPermiso rolPermiso = new RolPermiso();

	@Autowired
	private ModelMapper modelMapper;

	private static final Logger log = LoggerFactory.getLogger(RolesPermisosServiceImpl.class);
	private static final String NO_SE_ENCONTRO_INFORMACION = "45"; // No se encontró información relacionada a tu
																	// búsqueda.
	private static final String AGREGADO_CORRECTAMENTE = "30"; // Agregado correctamente.
	private static final String MODIFICADO_CORRECTAMENTE = "18"; // Modificado correctamente.
	private static final String ERROR_GUARDAR = "5"; // Error al guardar la información. Intenta nuevamente.

	private static final String CU003_NOMBRE = "GestionarRolesPermisos: ";
	private static final String CONSULTAR_PAGINADO = "/paginado";
	private static final String CONSULTAR = "/consulta";
	private static final String GUARDAR_DATOS = "/crear";
	private static final String ACTUALIZAR_DATOS = "/actualizar";
	private static final String CONULTA_FILTROS = "consu-filtrodonados: ";
	private static final String GENERAR_DOCUMENTO = "generarDocumento: ";
	private static final String ACTUALIZA_ROL = "Actualiza Rol: " ;
	private static final String INSERTA_ROL = "Inserta Rol: " ;
	
	@Override
	public Response<Object> consultarRolesPermisos(DatosRequest request, Authentication authentication)
			throws IOException {

		return MensajeResponseUtil.mensajeConsultaResponse(
				providerRestTemplate.consumirServicio(rolPermiso.obtenerRolesPermisos(request).getDatos(),
						urlModCatalogos + CONSULTAR_PAGINADO, authentication),
				NO_SE_ENCONTRO_INFORMACION);
	}

	@Override
	public Response<Object> consultaFiltroRolPermiso(DatosRequest request, Authentication authentication)
			throws IOException {

		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);
		rolPermiso = new RolPermiso(rolesPermisosRequest);
		List<RolPermisoDetalleResponse> permisoResponse;


		Response<Object> response = providerRestTemplate
				.consumirServicio(rolPermiso.obtenerFiltroRolPermiso().getDatos(), urlModCatalogos + CONSULTAR, authentication);
		if (response.getCodigo() == 200) {
			permisoResponse = Arrays.asList(modelMapper.map(response.getDatos(), RolPermisoDetalleResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(permisoResponse));
		}
		
		return MensajeResponseUtil.mensajeConsultaResponse(response, NO_SE_ENCONTRO_INFORMACION);
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
				rolPermiso.obtenerDetalleRolPermiso().getDatos(), urlModCatalogos + CONSULTAR, authentication);
		if (response.getCodigo() == 200) {
			permisoResponse = Arrays.asList(modelMapper.map(response.getDatos(), RolPermisoDetalleResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(permisoResponse));
		}
		return MensajeResponseUtil.mensajeConsultaResponse(response, NO_SE_ENCONTRO_INFORMACION);
	}

	@Override
	public Response<Object> consultarPermisos(DatosRequest request, Authentication authentication) throws IOException {

		List<PermisoResponse> permisoResponses;
		Response<Object> response = providerRestTemplate.consumirServicio(rolPermiso.obtenerPermiso().getDatos(),
				urlModCatalogos + CONSULTAR, authentication);
		if (response.getCodigo() == 200) {
			permisoResponses = Arrays.asList(modelMapper.map(response.getDatos(), PermisoResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(permisoResponses));
		}
		return response;
	}

	@Override
	public Response<Object> consultarFuncionalidades(DatosRequest request, Authentication authentication)
			throws IOException {

		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);
		rolPermiso = new RolPermiso(rolesPermisosRequest);

		List<FuncionalidadResponse> funcionalidadResponses;
		Response<Object> response = providerRestTemplate.consumirServicio(rolPermiso.obtenerFuncionalidad().getDatos(),
				urlModCatalogos + CONSULTAR, authentication);
		if (response.getCodigo() == 200) {
			funcionalidadResponses = Arrays.asList(modelMapper.map(response.getDatos(), FuncionalidadResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(funcionalidadResponses));
		}
		return response;
	}

	@Override
	public Response<Object> actualizarRolPermiso(DatosRequest request, Authentication authentication)
			throws IOException {

		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));

		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);

		if (rolesPermisosRequest.getIdFuncionalidad() == null || rolesPermisosRequest.getPermisos() == null
				|| rolesPermisosRequest.getIdRol() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, ERROR_GUARDAR);
		}
		rolPermiso = new RolPermiso(rolesPermisosRequest);
		reiniciarEstatus(rolPermiso, authentication);
		return agregarActualizarPermisos(rolPermiso, authentication, MODIFICADO_CORRECTAMENTE);
	}

	@Override
	public Response<Object> agregarRolPermiso(DatosRequest request, Authentication authentication) throws IOException {

		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));

		RolesPermisosRequest rolesPermisosRequest = gson.fromJson(datosJson, RolesPermisosRequest.class);

		if (rolesPermisosRequest.getIdFuncionalidad() == null || rolesPermisosRequest.getPermisos() == null
				|| rolesPermisosRequest.getIdRol() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, ERROR_GUARDAR);
		}
		rolPermiso = new RolPermiso(rolesPermisosRequest);

		return agregarActualizarPermisos(rolPermiso, authentication, AGREGADO_CORRECTAMENTE);
	}

	private List<Integer> extraerPermisosRol(String cadena) {
		ArrayList<Integer> num = new ArrayList<>();
		Matcher encontrar = Pattern.compile("\\d+").matcher(cadena);
		while (encontrar.find()) {
			num.add(Integer.parseInt(encontrar.group()));
		}
		return num;
	}

	private Response<Object> agregarActualizarPermisos(RolPermiso rolPermiso, Authentication authentication,
			String numeroMensaje) throws IOException {
		Response<Object> temp = null;

		Gson gson = new Gson();
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		List<Integer> listaPermisosRol = extraerPermisosRol(rolPermiso.getPermisos());

		try {
			for (Integer permiso : listaPermisosRol) {
				rolPermiso.setIdPermiso(permiso);
				if (Boolean.TRUE.equals(existeParametroCofig(rolPermiso, authentication))) {
					rolPermiso.setEstatus(1);
					rolPermiso.setIdUsuarioModifica(usuarioDto.getIdUsuario());
					Map<String, Object> envioDatos = rolPermiso.actualizar().getDatos();
					log.info( CU003_NOMBRE + ACTUALIZA_ROL + queryDecoded(envioDatos));
					temp = MensajeResponseUtil.mensajeResponse(providerRestTemplate.consumirServicio(
							envioDatos ,  urlModCatalogos + ACTUALIZAR_DATOS, authentication), numeroMensaje);
				} else {
					rolPermiso.setIdUsuarioAlta(usuarioDto.getIdUsuario());
					rolPermiso.setEstatus(1);
					Map<String, Object> envioDatos = rolPermiso.insertar().getDatos();
					log.info( CU003_NOMBRE + INSERTA_ROL + queryDecoded(envioDatos));
					temp = MensajeResponseUtil.mensajeResponse(providerRestTemplate.consumirServicio(
							envioDatos, urlModCatalogos + GUARDAR_DATOS, authentication), numeroMensaje);
				}
			}
		} catch (Exception e) {
			log.error("Error.. {}", e.getMessage());
			throw new BadRequestException(HttpStatus.BAD_REQUEST, ERROR_GUARDAR);
		}
		return temp;
	}

	private void reiniciarEstatus(RolPermiso rolPermiso, Authentication authentication) throws IOException {

		Gson gson = new Gson();
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		rolPermiso.setEstatus(0);
		rolPermiso.setIdUsuarioModifica(usuarioDto.getIdUsuario());
		Map<String, Object> envioDatos = rolPermiso.actualizar().getDatos();
		log.info( CU003_NOMBRE + ACTUALIZA_ROL + queryDecoded(envioDatos));
		providerRestTemplate.consumirServicio(envioDatos, urlModCatalogos + ACTUALIZAR_DATOS, authentication);
	}

	private Boolean existeParametroCofig(RolPermiso rolPermiso, Authentication authentication) throws IOException {
		Response<Object> existe = providerRestTemplate.consumirServicio(rolPermiso.buscarRolPermiso().getDatos(),
				urlModCatalogos + CONSULTAR, authentication);
		return !existe.getDatos().toString().equals("[]");

	}

	private String queryDecoded (Map<String, Object> envioDatos ) {
		return new String(DatatypeConverter.parseBase64Binary(envioDatos.get(AppConstantes.QUERY).toString()));
	}
}
