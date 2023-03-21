package com.imss.rolespermisos.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.rolespermisos.util.AppConstantes;
import com.imss.rolespermisos.util.DatosRequest;

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
public class Permiso {

	private Integer id;
	private String nombre;

	public DatosRequest obtenerPermiso() {

			DatosRequest request = new DatosRequest();
			Map<String, Object> parametro = new HashMap<>();
			String query = "SELECT ID_PERMISO, DES_PERMISO FROM SVC_PERMISO";
			String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
		}

	public DatosRequest obtenerPermisoId() {

			DatosRequest request = new DatosRequest();
			Map<String, Object> parametro = new HashMap<>();
			String query = "SELECT ID_PERMISO AS id FROM SVC_PERMISO";
			String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
		}

}
