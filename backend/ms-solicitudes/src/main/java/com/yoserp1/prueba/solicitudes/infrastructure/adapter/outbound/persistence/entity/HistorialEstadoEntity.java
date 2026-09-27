package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.yoserp1.prueba.solicitudes.domain.model.EstadoSolicitud;
import com.yoserp1.prueba.solicitudes.domain.model.RolActor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un registro del historial de estados de una solicitud en la base de datos.
 */
@Entity
@Table(name = "historial_estado")
@Getter
@Setter
@NoArgsConstructor
public class HistorialEstadoEntity {
	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "solicitud_id", nullable = false)
	private SolicitudEntity solicitud;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_origen", length = 20)
	private EstadoSolicitud estadoOrigen;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_destino", nullable = false, length = 20)
	private EstadoSolicitud estadoDestino;

	@Column(name = "actor_id", nullable = false, length = 100)
	private String actorId;

	@Enumerated(EnumType.STRING)
	@Column(name = "actor_rol", nullable = false, length = 20)
	private RolActor actorRol;

	@Column(nullable = false, length = 500)
	private String motivo;

	@Column(name = "ocurrido_en", nullable = false)
	private OffsetDateTime ocurridoEn;
}