package com.imss.flujo.service.impl;

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
import com.imss.flujo.beans.Usuario;
import com.imss.flujo.exception.BadRequestException;
import com.imss.flujo.model.request.UsuarioDto;
import com.imss.flujo.model.request.UsuarioRequest;
import com.imss.flujo.model.response.UsuarioResponse;
import com.imss.flujo.service.UsuarioService;
import com.imss.flujo.util.AppConstantes;
import com.imss.flujo.util.ConvertirGenerico;
import com.imss.flujo.util.DatosRequest;
import com.imss.flujo.util.ProviderServiceRestTemplate;
import com.imss.flujo.util.QueryHelper;
import com.imss.flujo.util.Response;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Value("${endpoints.dominio-consulta}")
	private String urlDominioConsulta;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;

	@Autowired
	private ModelMapper modelMapper;

	private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

	@Override
	public Response<?> consultarUsuarios(DatosRequest request, Authentication authentication) throws IOException {
		Usuario usuario= new Usuario();
		return providerRestTemplate.consumirServicio(usuario.obtenerUsuarios(request).getDatos(), urlDominioConsulta + "/generico/paginado",
				authentication);


	}

	@Override
	public Response<?> buscarUsuario(DatosRequest request, Authentication authentication) throws IOException {
		Usuario usuario= new Usuario();
		return providerRestTemplate.consumirServicio(usuario.buscarUsuario(request).getDatos(), urlDominioConsulta + "/generico/paginado",
				authentication);
	}

	@Override
	public Response<?> detalleUsuario(DatosRequest request, Authentication authentication) throws IOException {
		Usuario usuario= new Usuario();
		return providerRestTemplate.consumirServicio(usuario.detalleUsuario(request).getDatos(), urlDominioConsulta + "/generico/consulta",
				authentication);
	}

	@Override
	public Response<?> catalogoUsuario(DatosRequest request, Authentication authentication) throws IOException {
		Usuario usuario= new Usuario();
		List<UsuarioResponse> usuarioResponses;
		Response<?> response = providerRestTemplate.consumirServicio(usuario.catalogoUsuario().getDatos(),
				urlDominioConsulta + "/generico/consulta", authentication);
		if (response.getCodigo() == 200) {
			usuarioResponses = Arrays.asList(modelMapper.map(response.getDatos(), UsuarioResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(usuarioResponses));
		}
		return response;
	}

	@Override
	public Response<?> agregarUsuario(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);

		UsuarioRequest usuarioRequest = gson.fromJson(datosJson, UsuarioRequest.class);
		Usuario usuario= new Usuario(usuarioRequest);
		usuario.setClaveAlta(usuarioDto.getCorreo());
		
		return providerRestTemplate.consumirServicio(usuario.insertar().getDatos(), urlDominioConsulta + "/generico/crear",
				authentication);
	}

	@Override
	public Response<?> actualizarUsuario(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);

		UsuarioRequest usuarioRequest = gson.fromJson(datosJson, UsuarioRequest.class);
		if (usuarioRequest.getId() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		Usuario usuario= new Usuario(usuarioRequest);
		usuario.setClaveModifica(usuarioDto.getCorreo());
		
		return providerRestTemplate.consumirServicio(usuario.actualizar().getDatos(), urlDominioConsulta + "/generico/actualizar",
				authentication);
	}

	@Override
	public Response<?> cambiarEstatusUsuario(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);

		UsuarioRequest usuarioRequest = gson.fromJson(datosJson, UsuarioRequest.class);
		if (usuarioRequest.getId() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		Usuario usuario= new Usuario(usuarioRequest);
		usuario.setClaveBaja(usuarioDto.getCorreo());
		return providerRestTemplate.consumirServicio(usuario.cambiarEstatus().getDatos(), urlDominioConsulta + "/generico/actualizar",
				authentication);
	}

}
