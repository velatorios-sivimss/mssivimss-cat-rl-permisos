package com.imss.rolespermisos.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
@JsonIgnoreType(value = true)
public class RolPermisoDetalleResponse {
	@JsonProperty(value = "idRol")
	private Integer idRol;
	@JsonProperty(value = "nombre")
	private String nombre;
	@JsonProperty(value = "funcionalidad")
	private String funcionalidad;
	@JsonProperty(value = "nivel")
	private String nivel;
	@JsonProperty(value = "estatus")
	private Boolean estatus;
	@JsonProperty(value = "permisos")
	private String permisos;
	@JsonProperty(value = "fechaCreacion")
	private String fechaCreacion;
}
