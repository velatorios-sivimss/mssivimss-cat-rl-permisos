package com.imss.sivimss.rolespermisos.model.request;



import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@JsonIgnoreType(value = true)
public class RolesPermisosRequest {
	private Integer idRol;
	private String nombre;
	private String nivel;
	private Integer estatus;
	private String permisos;
	private Date fechaCreacion;
	private Integer idFuncionalidad;
	private Integer idPermiso;
	private Integer idUsuarioAlta;
	private Integer idUsuarioModifica;
	private String fechaModifica;
	private String pagina;
	private String datos;
	private String tamanio;
}
