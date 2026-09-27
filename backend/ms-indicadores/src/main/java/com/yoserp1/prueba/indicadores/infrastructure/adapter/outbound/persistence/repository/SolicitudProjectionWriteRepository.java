package com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.repository;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.entity.SolicitudProyeccionEntity;

/**
 * Mantiene las proyecciones e idempotencia del consumidor de solicitudes.
 */
public interface SolicitudProjectionWriteRepository extends Repository<SolicitudProyeccionEntity, UUID> {

	@Modifying
	@Query(value = """
			INSERT INTO evento_procesado (event_id, event_type, procesado_en)
			SELECT :eventId, :eventType, :procesadoEn
			WHERE NOT EXISTS (SELECT 1 FROM evento_procesado WHERE event_id = :eventId)
			""", nativeQuery = true)
	int registrarEventoProcesado(
			@Param("eventId") UUID eventId,
			@Param("eventType") String eventType,
			@Param("procesadoEn") OffsetDateTime procesadoEn);

	@Modifying
	@Query(value = """
			INSERT INTO solicitud_proyeccion
			(solicitud_id, categoria_id, categoria_codigo, categoria_nombre, estado, registrada_en, actualizada_en, version)
			VALUES (:solicitudId, :categoriaId, :categoriaCodigo, :categoriaNombre, :estado, :registradaEn, :actualizadaEn, :version)
			""", nativeQuery = true)
	void registrarSolicitud(
			@Param("solicitudId") UUID solicitudId,
			@Param("categoriaId") UUID categoriaId,
			@Param("categoriaCodigo") String categoriaCodigo,
			@Param("categoriaNombre") String categoriaNombre,
			@Param("estado") String estado,
			@Param("registradaEn") OffsetDateTime registradaEn,
			@Param("actualizadaEn") OffsetDateTime actualizadaEn,
			@Param("version") long version);

	@Modifying
	@Query(value = """
			UPDATE solicitud_proyeccion
			SET estado = :estado, actualizada_en = :actualizadaEn, version = :version
			WHERE solicitud_id = :solicitudId AND version < :version
			""", nativeQuery = true)
	void actualizarEstado(
			@Param("solicitudId") UUID solicitudId,
			@Param("estado") String estado,
			@Param("actualizadaEn") OffsetDateTime actualizadaEn,
			@Param("version") long version);

	@Modifying
	@Query(value = """
			INSERT INTO transicion_proyeccion
			(event_id, solicitud_id, categoria_id, estado_destino, ocurrido_en)
			VALUES (:eventId, :solicitudId, :categoriaId, :estadoDestino, :ocurridoEn)
			""", nativeQuery = true)
	void registrarTransicion(
			@Param("eventId") UUID eventId,
			@Param("solicitudId") UUID solicitudId,
			@Param("categoriaId") UUID categoriaId,
			@Param("estadoDestino") String estadoDestino,
			@Param("ocurridoEn") OffsetDateTime ocurridoEn);
}