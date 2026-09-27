package com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.projection;

import java.util.UUID;

/** 
 * Expone los datos necesarios para agrupar solicitudes en los indicadores. 
 */
public interface SolicitudIndicadorProjection {

	UUID getCategoriaId();

	String getCategoriaCodigo();

	String getCategoriaNombre();

	String getEstado();
}