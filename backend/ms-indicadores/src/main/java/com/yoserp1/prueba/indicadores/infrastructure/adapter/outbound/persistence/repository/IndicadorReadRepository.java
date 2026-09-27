package com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.entity.SolicitudProyeccionEntity;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.projection.SolicitudIndicadorProjection;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.projection.TransicionIndicadorProjection;

/** 
 * Consulta las proyecciones persistidas utilizadas por los indicadores. 
 */
public interface IndicadorReadRepository extends Repository<SolicitudProyeccionEntity, UUID> {

	/** 
	 * Obtiene las solicitudes del período y categoría indicados. 
	 * 
	 * @param desde Fecha de inicio del período.
	 * @param hastaExclusivo Fecha de fin del período (exclusiva).
	 * @param categoriaId Identificador de la categoría (opcional).
	 * @return Lista de proyecciones de solicitudes que cumplen con los criterios especificados.
	 */
	@Query(value = """
			SELECT categoria_id AS categoriaId,
			       categoria_codigo AS categoriaCodigo,
			       categoria_nombre AS categoriaNombre,
			       estado AS estado
			FROM solicitud_proyeccion
			WHERE registrada_en >= :desde
			  AND registrada_en < :hastaExclusivo
			  AND (:categoriaId IS NULL OR categoria_id = :categoriaId)
			""", nativeQuery = true)
	List<SolicitudIndicadorProjection> obtenerSolicitudes(
			@Param("desde") OffsetDateTime desde,
			@Param("hastaExclusivo") OffsetDateTime hastaExclusivo,
			@Param("categoriaId") UUID categoriaId);

	/** 
	 * Obtiene las transiciones del período y categoría indicados. 
	 * 
	 * @param desde Fecha de inicio del período.
	 * @param hastaExclusivo Fecha de fin del período (exclusiva).
	 * @param categoriaId Identificador de la categoría (opcional).
	 * @return Lista de proyecciones de transiciones que cumplen con los criterios especificados.
	 */
	@Query(value = """
			SELECT solicitud_id AS solicitudId,
			       estado_destino AS estado,
			       CONVERT(VARCHAR(40), ocurrido_en, 127) AS ocurridoEn
			FROM transicion_proyeccion
			WHERE ocurrido_en >= :desde
			  AND ocurrido_en < :hastaExclusivo
			  AND (:categoriaId IS NULL OR categoria_id = :categoriaId)
			""", nativeQuery = true)
	List<TransicionIndicadorProjection> obtenerTransiciones(
			@Param("desde") OffsetDateTime desde,
			@Param("hastaExclusivo") OffsetDateTime hastaExclusivo,
			@Param("categoriaId") UUID categoriaId);

	/** 
	 * Obtiene la fecha del último evento procesado. 
	 * 
	 * @return Fecha del último evento procesado en formato ISO 8601.
	 */
	@Query(value = "SELECT CONVERT(VARCHAR(40), MAX(procesado_en), 127) FROM evento_procesado", nativeQuery = true)
	String obtenerActualizadoHasta();
}