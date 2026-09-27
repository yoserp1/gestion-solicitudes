package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una categoría de solicitud en la base de datos.
 */
@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
public class CategoriaEntity {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 30)
	private String codigo;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(nullable = false)
	private boolean activa;
}