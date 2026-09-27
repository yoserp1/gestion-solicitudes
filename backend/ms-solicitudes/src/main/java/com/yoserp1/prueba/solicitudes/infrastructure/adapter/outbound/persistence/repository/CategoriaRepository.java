package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.CategoriaEntity;

/**
 * Repositorio para la entidad CategoriaEntity.
 * Gestiona la persistencia del catálogo de categorías.
 */
public interface CategoriaRepository extends JpaRepository<CategoriaEntity, UUID> {
	/** 
	 * Lista las categorías activas ordenadas por nombre.
	 * @return Lista de categorías activas ordenadas por nombre.
	 */
	List<CategoriaEntity> findByActivaTrueOrderByNombreAsc();
}
