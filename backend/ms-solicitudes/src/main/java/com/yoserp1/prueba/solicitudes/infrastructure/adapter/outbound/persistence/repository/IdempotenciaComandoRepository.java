package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.IdempotenciaComandoEntity;

/** 
 * Repositorio para la entidad IdempotenciaComandoEntity.
 * Gestiona los comandos procesados para garantizar idempotencia.
 */
public interface IdempotenciaComandoRepository extends JpaRepository<IdempotenciaComandoEntity, UUID> {
}
