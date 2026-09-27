package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.OutboxEventoEntity;

/** 
 * Repositorio para la entidad OutboxEventoEntity.
 * Gestiona los eventos pendientes de publicación mediante el patrón outbox.
 */
public interface OutboxEventoRepository extends JpaRepository<OutboxEventoEntity, UUID> {

	List<OutboxEventoEntity> findTop100ByPublicadoEnIsNullOrderByOcurridoEnAsc();
}
