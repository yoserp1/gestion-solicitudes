package com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.mapper;

import org.springframework.stereotype.Component;

import com.yoserp1.prueba.solicitudes.application.SolicitudDetalle;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.Actor;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.Categoria;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.EstadoSolicitud;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.HistorialEstado;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.Observacion;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.Prioridad;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.Rol;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.SolicitudDetalleResponse;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.SolicitudResumen;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.CategoriaEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.HistorialEstadoEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.ObservacionEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.SolicitudEntity;

/** Convierte modelos de aplicación y persistencia al contrato OpenAPI. */
@Component
public class SolicitudDtoMapper {

	/** Convierte el detalle completo de una solicitud. */
	public SolicitudDetalleResponse toDetalle(SolicitudDetalle detalle) {
		SolicitudEntity entity = detalle.solicitud();
		var response = new SolicitudDetalleResponse(entity.getId(), entity.getCodigo(), entity.getAsunto(),
				toCategoria(entity.getCategoria()),
				enumValue(Prioridad.class, entity.getPrioridad().name()),
				enumValue(EstadoSolicitud.class, entity.getEstado().name()),
				entity.getCreadaEn(), entity.getActualizadaEn(), entity.getDescripcion(),
				new Actor(entity.getSolicitanteId(), Rol.SOLICITANTE),
				detalle.observaciones().stream().map(this::toObservacion).toList(),
				detalle.historial().stream().map(this::toHistorial).toList());
		response.setVersion(entity.getVersion());
		if (entity.getAnalistaId() != null) {
			response.setAnalistaAsignado(new Actor(entity.getAnalistaId(), Rol.ANALISTA));
		}
		return response;
	}

	/** Convierte una entidad al resumen expuesto por la API. */
	public SolicitudResumen toResumen(SolicitudEntity entity) {
		var response = new SolicitudResumen(entity.getId(), entity.getCodigo(), entity.getAsunto(),
				toCategoria(entity.getCategoria()),
				enumValue(Prioridad.class, entity.getPrioridad().name()),
				enumValue(EstadoSolicitud.class, entity.getEstado().name()),
				entity.getCreadaEn(), entity.getActualizadaEn());
		response.setVersion(entity.getVersion());
		if (entity.getAnalistaId() != null) {
			response.setAnalistaAsignado(new Actor(entity.getAnalistaId(), Rol.ANALISTA));
		}
		return response;
	}

	/** Convierte una categoría persistida al contrato de salida. */
	public Categoria toCategoria(CategoriaEntity entity) {
		return new Categoria(entity.getId(), entity.getCodigo(), entity.getNombre(), entity.isActiva());
	}

	/** Convierte una observación persistida al contrato de salida. */
	public Observacion toObservacion(ObservacionEntity entity) {
		return new Observacion(entity.getId(), entity.getContenido(),
				new Actor(entity.getActorId(), enumValue(Rol.class, entity.getActorRol().name())), entity.getCreadaEn());
	}

	/** Convierte una transición persistida al historial expuesto. */
	public HistorialEstado toHistorial(HistorialEstadoEntity entity) {
		var historial = new HistorialEstado(entity.getId(),
				enumValue(EstadoSolicitud.class, entity.getEstadoDestino().name()),
				new Actor(entity.getActorId(), enumValue(Rol.class, entity.getActorRol().name())),
				entity.getMotivo(), entity.getOcurridoEn());
		if (entity.getEstadoOrigen() != null) {
			historial.setEstadoOrigen(enumValue(EstadoSolicitud.class, entity.getEstadoOrigen().name()));
		}
		return historial;
	}

	private static <E extends Enum<E>> E enumValue(Class<E> type, String value) {
		return Enum.valueOf(type, value);
	}
}
