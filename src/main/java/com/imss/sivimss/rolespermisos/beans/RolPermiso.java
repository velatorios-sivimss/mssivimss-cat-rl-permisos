package com.imss.sivimss.rolespermisos.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.rolespermisos.model.request.RolesPermisosRequest;
import com.imss.sivimss.rolespermisos.util.AppConstantes;
import com.imss.sivimss.rolespermisos.util.DatosRequest;
import com.imss.sivimss.rolespermisos.util.QueryHelper;

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
	private String permisos;
	private Date fechaCreacion;
	private Integer idFuncionalidad;
	private Integer idPermiso;
	private Integer idUsuarioAlta;
	private Integer idUsuarioModifica;
	private String fechaModifica;
	private static final String NOW = "CURRENT_TIMESTAMP()";
	private static final String IND_ACTIVO = "IND_ACTIVO";
	private static final String FROMROLFUNPERM = " FROM SVC_ROL_FUNCIONALIDAD_PERMISO srfp ";
	private static final String INNERJOINSVTMENU = " INNER JOIN SVT_MENU sm ON sm.ID_MODULO = srfp.ID_FUNCIONALIDAD  ";
	private static final String FEC_CREACION = "FEC_CREACION";
	private static final String ID_USUARIO_ALTA = "ID_USUARIO_ALTA";
	private static final String ID_ROL = "ID_ROL";
	private static final String ID_FUNCIONALIDAD = "ID_FUNCIONALIDAD";
	private static final String ID_PERMISO = "ID_PERMISO";

	public RolPermiso(RolesPermisosRequest rolesPermisosRequest) {
		this.idRol = rolesPermisosRequest.getIdRol();
		this.nombre = rolesPermisosRequest.getNombre();
		this.nivel = rolesPermisosRequest.getNivel();
		this.estatus = rolesPermisosRequest.getEstatus();
		this.permisos = rolesPermisosRequest.getPermisos();
		this.fechaCreacion = rolesPermisosRequest.getFechaCreacion();
		this.idFuncionalidad = rolesPermisosRequest.getIdFuncionalidad();
		this.idPermiso = rolesPermisosRequest.getIdPermiso();
		this.idUsuarioAlta = rolesPermisosRequest.getIdUsuarioAlta();
		this.idUsuarioModifica = rolesPermisosRequest.getIdUsuarioModifica();
		this.fechaModifica = rolesPermisosRequest.getFechaModifica();
	}

	public DatosRequest obtenerRolesPermisos(DatosRequest request) {

		String query = "SELECT  srfp.ID_ROL AS 'idRol', sm.DES_TITULO AS funcionalidad, sm.ID_MODULO  as idFuncionalidad"
				+ ", sr.DES_ROL AS 'nombre', sno.DES_NIVELOFICINA AS 'nivel' , srfp.IND_ACTIVO  AS 'estatus'"
				+ ", GROUP_CONCAT(sp.DES_PERMISO) AS permisos, srfp.FEC_CREACION as fechaCreacion " 
				+ FROMROLFUNPERM
				+ " INNER JOIN SVC_ROL sr ON srfp.ID_ROL = sr.ID_ROL "
				+ " INNER JOIN SVC_NIVEL_OFICINA sno ON sr.ID_OFICINA = sno.ID_OFICINA "
				+ " INNER JOIN SVC_PERMISO sp ON srfp.ID_PERMISO = sp.ID_PERMISO "
				+ INNERJOINSVTMENU
				+ " WHERE srfp.IND_ACTIVO = 1 GROUP BY 1,2";
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);

		return request;
	}

	public DatosRequest obtenerFiltroRolPermiso()  {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query = "SELECT sr.ID_ROL AS idRol, sr.DES_ROL AS nombre, sm.DES_TITULO AS funcionalidad, sm.ID_MODULO AS idFuncionalidad  "
				+ ", sno.DES_NIVELOFICINA AS nivel, sr.IND_ACTIVO AS estatus, GROUP_CONCAT(sp.DES_PERMISO) AS permisos "
				+ " FROM SVC_NIVEL_OFICINA sno " + " INNER JOIN SVC_ROL sr ON sr.ID_OFICINA  = sno.ID_OFICINA "
				+ " INNER JOIN SVC_ROL_FUNCIONALIDAD_PERMISO srfp ON srfp.ID_ROL =sr.ID_ROL AND srfp.IND_ACTIVO "
				+ INNERJOINSVTMENU
				+ " INNER JOIN SVC_PERMISO sp ON srfp.ID_PERMISO = sp.ID_PERMISO  "
				+ " WHERE sno.ID_OFICINA = " + this.nivel + " AND sr.IND_ACTIVO = " + this.estatus
				+ " AND sr.ID_ROL = " + this.idRol;
				if ( this.permisos != null )
					query = query + " AND srfp.ID_PERMISO IN(" + this.permisos + ") ";
				query = query + " GROUP BY 1,3";

		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}
	public DatosRequest obtenerDetalleRolPermiso() {

		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query = "SELECT  srfp.ID_ROL AS 'idRol',sm.ID_MODULO AS idFuncionalidad, sm.DES_TITULO AS funcionalidad "
				+ " , sr.DES_ROL AS 'nombre', sno.ID_OFICINA  AS 'nivel' "
				+ " ,sr.IND_ACTIVO AS 'estatus',  GROUP_CONCAT(sp.DES_PERMISO) AS permisos "
				+ " , srfp.FEC_CREACION AS fechaCreacion " + FROMROLFUNPERM
				+ " INNER JOIN SVC_ROL sr ON srfp.ID_ROL = sr.ID_ROL "
				+ " INNER JOIN SVC_NIVEL_OFICINA sno ON sr.ID_OFICINA = sno.ID_OFICINA "
				+ " INNER JOIN SVC_PERMISO sp ON srfp.ID_PERMISO = sp.ID_PERMISO "
				+ INNERJOINSVTMENU
				+ " WHERE srfp.IND_ACTIVO = 1 AND srfp.ID_ROL = " + this.idRol + " AND srfp.ID_FUNCIONALIDAD = "
				+ this.idFuncionalidad + " GROUP BY 1,2";
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}


	public DatosRequest buscarRolPermiso() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query = "SELECT  srfp.ID_ROL AS 'id', srfp.ID_FUNCIONALIDAD  AS funcionalidad , srfp.ID_PERMISO  AS 'idPermiso' "
				+ FROMROLFUNPERM + " WHERE srfp.ID_ROL = " + this.idRol + " AND srfp.ID_FUNCIONALIDAD = "
				+ this.idFuncionalidad + " AND  srfp.ID_PERMISO =  " + this.idPermiso;
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);

		return request;
	}

	public DatosRequest insertar() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();

		final QueryHelper q = new QueryHelper("INSERT INTO SVC_ROL_FUNCIONALIDAD_PERMISO");
		q.agregarParametroValues(ID_ROL, "'" + this.idRol + "'");
		q.agregarParametroValues(ID_FUNCIONALIDAD, "'" + this.idFuncionalidad + "'");
		q.agregarParametroValues(ID_PERMISO, "'" + this.idPermiso + "'");
		q.agregarParametroValues(IND_ACTIVO, "" + this.estatus);
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
		q.agregarParametroValues(IND_ACTIVO, "" + this.estatus);
		if (this.idUsuarioModifica == null) {
			q.agregarParametroValues(ID_USUARIO_ALTA, "'" + this.idUsuarioAlta + "'");
			q.agregarParametroValues(FEC_CREACION, NOW);
		} else {
			q.agregarParametroValues("ID_USUARIO_MODIFICA", "'" + this.idUsuarioModifica + "'");
			q.agregarParametroValues("FEC_ACTUALIZACION", NOW);
		}
		q.addWhere("ID_ROL = " + this.idRol);
		q.addWhere("AND ID_FUNCIONALIDAD = " + this.idFuncionalidad);
		if (this.estatus == 1)
			q.addWhere("AND ID_PERMISO = " + this.idPermiso);
		String query = q.obtenerQueryActualizar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}

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

	public DatosRequest obtenerFuncionalidad() {

		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query = " SELECT sm.ID_MODULO AS idFuncionalidad, sm.DES_TITULO AS desFuncionalidad "
				+ " FROM SVT_MENU sm "
				+ " WHERE IFNULL(sm.ID_TABLA_PADRE,0) > 0 AND IFNULL(sm.ID_MODULO,0) > 0 "
				+ " AND sm.ID_MODULO IN ( "
				+ " SELECT rfp.ID_FUNCIONALIDAD "
				+ " FROM SVC_ROL_FUNCIONALIDAD_PERMISO rfp "
				+ " WHERE rfp.IND_ACTIVO = 1 AND rfp.ID_ROL = " + this.idRol
				+ " GROUP BY rfp.ID_FUNCIONALIDAD)";
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
}
