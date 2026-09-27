package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.SolicitudEntity;

/** 
 * Repositorio para la entidad SolicitudEntity.
 * Gestiona la persistencia y búsqueda filtrada de solicitudes.
 */
public interface SolicitudRepository extends JpaRepository<SolicitudEntity, UUID>, JpaSpecificationExecutor<SolicitudEntity> {

	/** 
	 * Lista solicitudes junto con su categoría.
	 * @param spec Especificación para filtrar las solicitudes.
	 * @param pageable Información de paginación.
	 * @return Página de solicitudes con su categoría.
	 */
	@Override
	@EntityGraph(attributePaths = "categoria")
	Page<SolicitudEntity> findAll(Specification<SolicitudEntity> spec, Pageable pageable);

	/** 
	 * Obtiene una solicitud junto con su categoría.
	 * @param id ID de la solicitud.
	 * @return Optional con la solicitud y su categoría si existe.
	 */
	@EntityGraph(attributePaths = "categoria")
	Optional<SolicitudEntity> findWithCategoriaById(UUID id);
}