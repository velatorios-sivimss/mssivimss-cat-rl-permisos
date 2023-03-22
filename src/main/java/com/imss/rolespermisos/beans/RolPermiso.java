package com.imss.rolespermisos.beans;



import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.rolespermisos.model.request.RolesPermisosRequest;
import com.imss.rolespermisos.util.AppConstantes;
import com.imss.rolespermisos.util.DatosRequest;
import com.imss.rolespermisos.util.QueryHelper;

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
public class RolPermiso {
	private Integer idRol;
	private String nombre;
	private String nivel;
	private Integer estatus;
	private String permiso;
	private Date fechaCreacion;
	private Integer idFuncionalidad;
	private Integer idPermiso;
	private Integer idUsuarioAlta;
	private Integer idUsuarioModifica;
	private String fechaModifica;
	private static final String NOW = "NOW()";
	private static final String CVE_ESTATUS = "CVE_ESTATUS";
	private static final String FROMROLFUNPERM = " FROM svc_rol_funcionalidad_permiso srfp ";
	private static final String FEC_CREACION = "FEC_CREACION";
	private static final String ID_USUARIO_ALTA = "ID_USUARIO_ALTA";
	
	
	public RolPermiso(RolesPermisosRequest rolesPermisosRequest) {
		this.idRol = rolesPermisosRequest.getIdRol();
		this.nombre = rolesPermisosRequest.getNombre();
		this.nivel = rolesPermisosRequest.getNivel();
		this.estatus = rolesPermisosRequest.getEstatus();
		this.permiso = rolesPermisosRequest.getPermiso();
		this.fechaCreacion = rolesPermisosRequest.getFechaCreacion();
		this.idFuncionalidad = rolesPermisosRequest.getIdFuncionalidad();
		this.idPermiso = rolesPermisosRequest.getIdPermiso();
		this.idUsuarioAlta = rolesPermisosRequest.getIdUsuarioAlta();
		this.idUsuarioModifica = rolesPermisosRequest.getIdUsuarioModifica();
		this.fechaModifica = rolesPermisosRequest.getFechaModifica();
	}

	public DatosRequest obtenerRolesPermisos(DatosRequest request) {
		
		String query = "SELECT  srfp.ID_ROL AS 'idRol', sf.DES_FUNCIONALIDAD AS funcionalidad, sr.DES_ROL AS 'nombre', sno.DES_NIVELOFICINA  AS 'nivel' "
				+ " ,sr.CVE_ESTATUS AS 'estatus',  GROUP_CONCAT(sp.DES_PERMISO) AS permiso "
				+ " , srfp.FEC_CREACION AS fechaCreacion "
				+ FROMROLFUNPERM
				+ " INNER JOIN svc_rol sr ON srfp.ID_ROL = sr.ID_ROL "
				+ " INNER JOIN svc_nivel_oficina sno ON sr.ID_OFICINA = sno.ID_OFICINA "
				+ " INNER JOIN svc_permiso sp ON srfp.ID_PERMISO = sp.ID_PERMISO "
				+ " INNER JOIN svc_funcionalidad sf ON sf.ID_FUNCIONALIDAD = srfp.ID_FUNCIONALIDAD "
				+ " WHERE srfp.CVE_Estatus = 1 GROUP BY 1,2";	
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);

		return request;
	}
public DatosRequest obtenerDetalleRolPermiso() {

	DatosRequest request = new DatosRequest();
	Map<String, Object> parametro = new HashMap<>();
	String query = "SELECT  srfp.ID_ROL AS 'idRol', sf.DES_FUNCIONALIDAD AS funcionalidad, sr.DES_ROL AS 'nombre', sno.ID_OFICINA  AS 'nivel' "
				+ " ,sr.CVE_ESTATUS AS 'estatus',  GROUP_CONCAT(sp.DES_PERMISO) AS permisos "
				+ " , srfp.FEC_CREACION AS fechaCreacion "
				+ FROMROLFUNPERM
				+ " INNER JOIN svc_rol sr ON srfp.ID_ROL = sr.ID_ROL "
				+ " INNER JOIN svc_nivel_oficina sno ON sr.ID_OFICINA = sno.ID_OFICINA "
				+ " INNER JOIN svc_permiso sp ON srfp.ID_PERMISO = sp.ID_PERMISO "
				+ " INNER JOIN svc_funcionalidad sf ON sf.ID_FUNCIONALIDAD = srfp.ID_FUNCIONALIDAD "
				+ " WHERE srfp.CVE_Estatus = 1 AND srfp.ID_ROL = " + this.idRol  
				+ " AND srfp.ID_FUNCIONALIDAD = " + this.idFuncionalidad  
				+ " GROUP BY 1,2";	
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}
	public DatosRequest buscarRolPermiso() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query = "SELECT  srfp.ID_ROL AS 'id', srfp.ID_FUNCIONALIDAD  AS funcionalidad , srfp.ID_PERMISO  AS 'idPermiso' "
				+ FROMROLFUNPERM
				+ " WHERE srfp.ID_ROL = " + this.idRol  
				+ " AND srfp.ID_FUNCIONALIDAD = " + this.idFuncionalidad  
				+ " AND  srfp.ID_PERMISO =  " + this.idPermiso ;	
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}


	public DatosRequest insertar() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();

		final QueryHelper q = new QueryHelper("INSERT INTO SVC_ROL_FUNCIONALIDAD_PERMISO");
		q.agregarParametroValues("ID_ROL", "'" + this.idRol + "'");
		q.agregarParametroValues("ID_FUNCIONALIDAD", "'" + this.idFuncionalidad + "'");
		q.agregarParametroValues("ID_PERMISO", "'" + this.idPermiso + "'");
		q.agregarParametroValues(CVE_ESTATUS, "" + this.estatus);
		q.agregarParametroValues(ID_USUARIO_ALTA, "'" + this.idUsuarioAlta + "'");
		q.agregarParametroValues(FEC_CREACION, NOW);
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}

	public DatosRequest actualizar() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();

		final QueryHelper q = new QueryHelper("UPDATE SVC_ROL_FUNCIONALIDAD_PERMISO");
		q.agregarParametroValues(CVE_ESTATUS, "" + this.estatus);
		if (this.idUsuarioModifica == null) {
			q.agregarParametroValues(ID_USUARIO_ALTA, "'" + this.idUsuarioAlta + "'");
			q.agregarParametroValues(FEC_CREACION, NOW);
		}else {
			q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + this.idUsuarioModifica + "'");
			q.agregarParametroValues("FEC_ACTUALIZACION", NOW);
		}
		q.addWhere("ID_ROL = " + this.idRol);
		q.addWhere("AND ID_FUNCIONALIDAD = " + this.idFuncionalidad);
		q.addWhere("AND ID_PERMISO = " + this.idPermiso);
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}

	public DatosRequest actualizarAInactivo() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();

		final QueryHelper q = new QueryHelper("UPDATE SVC_ROL_FUNCIONALIDAD_PERMISO");
		q.agregarParametroValues(CVE_ESTATUS, "" + this.estatus);
		if (this.idUsuarioModifica == null) {
			q.agregarParametroValues(ID_USUARIO_ALTA, "'" + this.idUsuarioAlta + "'");
			q.agregarParametroValues(FEC_CREACION, NOW);
		}else {
			q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + this.idUsuarioModifica + "'");
			q.agregarParametroValues("FEC_ACTUALIZACION", NOW);
		}
		q.addWhere("ID_ROL = " + this.idRol);
		q.addWhere("AND ID_FUNCIONALIDAD = " + this.idFuncionalidad);
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
}
