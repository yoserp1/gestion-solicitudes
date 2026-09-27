package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.ObservacionEntity;

/** 
 * Repositorio para la entidad ObservacionEntity.
 * Gestiona la persistencia de observaciones de solicitudes.
 */
public interface ObservacionRepository extends JpaRepository<ObservacionEntity, UUID> {

	/** 
	 * Lista cronológicamente las observaciones de una solicitud.
	 * @param solicitudId ID de la solicitud.
	 * @return Lista de observaciones ordenadas cronológicamente.
	 */
	List<ObservacionEntity> findBySolicitudIdOrderByCreadaEnAsc(UUID solicitudId);
}
