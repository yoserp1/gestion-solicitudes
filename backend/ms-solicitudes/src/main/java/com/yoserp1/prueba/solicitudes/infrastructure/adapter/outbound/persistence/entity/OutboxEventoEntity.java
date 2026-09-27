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
 * Representa un evento en la tabla de outbox en la base de datos.
 */
@Entity
@Table(name = "outbox_evento")
@Getter
@Setter
@NoArgsConstructor
public class OutboxEventoEntity {
	@Id
	@Column(name = "event_id")
	private UUID eventId;

	@Column(name = "aggregate_id", nullable = false)
	private UUID aggregateId;

	@Column(name = "event_type", nullable = false, length = 80)
	private String eventType;

	@Column(nullable = false, columnDefinition = "nvarchar(max)")
	private String payload;

	@Column(name = "ocurrido_en", nullable = false)
	private OffsetDateTime ocurridoEn;

	@Column(name = "publicado_en")
	private OffsetDateTime publicadoEn;

	@Column(nullable = false)
	private int intentos;
}