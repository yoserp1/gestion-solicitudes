package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un registro de idempotencia de comandos en la base de datos.
 */
@Entity
@Table(name = "idempotencia_comando")
@Getter
@Setter
@NoArgsConstructor
public class IdempotenciaComandoEntity {
	@Id
	private UUID clave;

	@Column(nullable = false, length = 80)
	private String operacion;

	@Column(name = "actor_id", nullable = false, length = 100)
	private String actorId;

	@Column(name = "request_hash", nullable = false, length = 64, columnDefinition = "char(64)")
	private String requestHash;

	@Column(name = "recurso_id")
	private UUID recursoId;

	@Column(name = "creado_en", nullable = false)
	private OffsetDateTime creadoEn;
}