package com.yoserp1.prueba.solicitudes.application;

import java.util.List;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.HistorialEstadoEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.ObservacionEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.SolicitudEntity;

/** 
 * Representa el detalle completo de una solicitud.
 * Incluye la solicitud en sí, sus observaciones y su historial de estados.
 */
public record SolicitudDetalle(SolicitudEntity solicitud, List<ObservacionEntity> observaciones,
		List<HistorialEstadoEntity> historial) {
}