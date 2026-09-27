package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.HistorialEstadoEntity;

/** 
 * Repositorio para la entidad HistorialEstadoEntity.
 * Gestiona la persistencia del historial de estados.
 */
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstadoEntity, UUID> {

	/** 
	 * Lista cronológicamente las transiciones de una solicitud.
	 * @param solicitudId ID de la solicitud.
	 * @return Lista de transiciones de estado ordenadas cronológicamente.
	 */
	List<HistorialEstadoEntity> findBySolicitudIdOrderByOcurridoEnAsc(UUID solicitudId);
}
