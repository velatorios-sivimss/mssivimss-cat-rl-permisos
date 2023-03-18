package com.imss.rolespermisos.model.request;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuncionalidadPermisosDto {


	private Integer idRol;
	private String nombre;
	private String nivel;
	private Integer estatus;
	private String permiso;
	private Date fechaCreacion;
	private Integer idFuncionalidad;
	private Integer idPermiso;
	private Integer idUsuarioAlta;
}
