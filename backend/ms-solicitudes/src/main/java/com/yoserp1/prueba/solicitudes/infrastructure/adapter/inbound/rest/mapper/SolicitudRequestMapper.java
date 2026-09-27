package com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.mapper;

import org.springframework.stereotype.Component;

import com.yoserp1.prueba.solicitudes.domain.model.EstadoSolicitud;
import com.yoserp1.prueba.solicitudes.domain.model.Prioridad;

/** Convierte valores del contrato OpenAPI a modelos del dominio. */
@Component
public class SolicitudRequestMapper {

	/** Convierte una prioridad recibida al enum del dominio. */
	public Prioridad toPrioridad(Enum<?> prioridad) {
		return prioridad == null ? null : Prioridad.valueOf(prioridad.name());
	}

	/** Convierte un estado recibido al enum del dominio. */
	public EstadoSolicitud toEstado(Enum<?> estado) {
		return estado == null ? null : EstadoSolicitud.valueOf(estado.name());
	}
}
