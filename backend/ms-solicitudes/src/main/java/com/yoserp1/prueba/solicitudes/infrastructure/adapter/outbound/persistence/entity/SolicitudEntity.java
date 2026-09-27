package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.yoserp1.prueba.solicitudes.domain.model.EstadoSolicitud;
import com.yoserp1.prueba.solicitudes.domain.model.Prioridad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una solicitud en la base de datos.
 */
@Entity
@Table(name = "solicitud")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudEntity {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 15)
	private String codigo;

	@Column(nullable = false, length = 150)
	private String asunto;

	@Column(nullable = false, length = 2000)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "categoria_id", nullable = false)
	private CategoriaEntity categoria;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private Prioridad prioridad;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoSolicitud estado;

	@Column(name = "solicitante_id", nullable = false, length = 100)
	private String solicitanteId;

	@Column(name = "analista_id", length = 100)
	private String analistaId;

	@Column(name = "creada_en", nullable = false)
	private OffsetDateTime creadaEn;

	@Column(name = "actualizada_en", nullable = false)
	private OffsetDateTime actualizadaEn;

	@Version
	@Column(nullable = false)
	private Long version;
}