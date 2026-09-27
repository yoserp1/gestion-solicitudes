package com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** 
 * Representa la proyección persistida de una solicitud para consultas analíticas. 
 */
@Entity
@Immutable
@Table(name = "solicitud_proyeccion")
public class SolicitudProyeccionEntity {

	@Id
	@Column(name = "solicitud_id", nullable = false)
	private UUID solicitudId;

	@Column(name = "categoria_id", nullable = false)
	private UUID categoriaId;

	@Column(name = "categoria_codigo", nullable = false, length = 30)
	private String categoriaCodigo;

	@Column(name = "categoria_nombre", nullable = false, length = 100)
	private String categoriaNombre;

	@Column(name = "estado", nullable = false, length = 20)
	private String estado;

	@Column(name = "registrada_en", nullable = false)
	private OffsetDateTime registradaEn;

	@Column(name = "actualizada_en", nullable = false)
	private OffsetDateTime actualizadaEn;

	@Column(name = "version", nullable = false)
	private Long version;
}
