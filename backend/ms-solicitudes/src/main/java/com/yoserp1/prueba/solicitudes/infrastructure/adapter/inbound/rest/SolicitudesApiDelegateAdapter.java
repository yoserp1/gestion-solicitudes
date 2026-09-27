package com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import com.yoserp1.prueba.solicitudes.application.SolicitudApplicationService;
import com.yoserp1.prueba.solicitudes.application.SolicitudDetalle;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.api.AtencionApiDelegate;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.api.CatalogoApiDelegate;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.api.ObservacionesApiDelegate;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.api.SolicitudesApiDelegate;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.AgregarObservacionRequest;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.CambiarEstadoSolicitudRequest;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.Categoria;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.EstadoSolicitud;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.Observacion;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.PaginaSolicitudesResponse;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.Prioridad;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.RegistrarSolicitudRequest;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.SolicitudDetalleResponse;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.generated.model.TomarSolicitudRequest;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.mapper.SolicitudDtoMapper;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.mapper.SolicitudRequestMapper;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.ObservacionEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.SolicitudEntity;

import lombok.RequiredArgsConstructor;

/**
 * Implementa los endpoints OpenAPI y delega la lógica al servicio de aplicación.
 */
@Service
@RequiredArgsConstructor
public class SolicitudesApiDelegateAdapter implements SolicitudesApiDelegate, AtencionApiDelegate,
		ObservacionesApiDelegate, CatalogoApiDelegate {

	private final SolicitudApplicationService service;
	private final SolicitudDtoMapper mapper;
	private final SolicitudRequestMapper requestMapper;

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.empty();
	}

	@Override
	public ResponseEntity<SolicitudDetalleResponse> registrarSolicitud(UUID idempotencyKey,
			RegistrarSolicitudRequest request, UUID correlationId) {
		UUID effectiveCorrelationId = correlation(correlationId);
		SolicitudDetalle detalle = service.registrar(idempotencyKey, request.getAsunto(), request.getDescripcion(),
				request.getCategoriaId(), requestMapper.toPrioridad(request.getPrioridad()), effectiveCorrelationId);
		SolicitudDetalleResponse body = mapper.toDetalle(detalle);
		return ResponseEntity.created(URI.create(SolicitudApiConstants.SOLICITUD_PATH.formatted(body.getId())))
				.eTag(etag(body.getVersion())).header(SolicitudApiConstants.CORRELATION_HEADER, effectiveCorrelationId.toString()).body(body);
	}

	@Override
	public ResponseEntity<PaginaSolicitudesResponse> listarSolicitudes(UUID correlationId,
			EstadoSolicitud estado, UUID categoriaId, Prioridad prioridad,
			Integer page, Integer size, String sort) {
		Page<SolicitudEntity> result = service.listar(
				requestMapper.toEstado(estado), categoriaId, requestMapper.toPrioridad(prioridad),
				page == null ? 0 : page, size == null ? 20 : size, sort);
		var body = new PaginaSolicitudesResponse(result.getContent().stream().map(mapper::toResumen).toList(),
				result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
		return ResponseEntity.ok().header(SolicitudApiConstants.CORRELATION_HEADER, correlation(correlationId).toString()).body(body);
	}

	@Override
	public ResponseEntity<SolicitudDetalleResponse> obtenerSolicitud(UUID solicitudId, UUID correlationId) {
		SolicitudDetalleResponse body = mapper.toDetalle(service.obtener(solicitudId));
		return ResponseEntity.ok().eTag(etag(body.getVersion()))
				.header(SolicitudApiConstants.CORRELATION_HEADER, correlation(correlationId).toString()).body(body);
	}

	@Override
	public ResponseEntity<SolicitudDetalleResponse> tomarSolicitud(UUID solicitudId, UUID idempotencyKey,
			String ifMatch, UUID correlationId, TomarSolicitudRequest request) {
		UUID effectiveCorrelationId = correlation(correlationId);
		SolicitudDetalleResponse body = mapper.toDetalle(service.tomar(solicitudId, idempotencyKey, ifMatch,
				request == null ? null : request.getMotivo(), effectiveCorrelationId));
		return ResponseEntity.ok().eTag(etag(body.getVersion()))
				.header(SolicitudApiConstants.CORRELATION_HEADER, effectiveCorrelationId.toString()).body(body);
	}

	@Override
	public ResponseEntity<SolicitudDetalleResponse> cambiarEstadoSolicitud(UUID solicitudId, UUID idempotencyKey,
			String ifMatch, CambiarEstadoSolicitudRequest request, UUID correlationId) {
		UUID effectiveCorrelationId = correlation(correlationId);
		SolicitudDetalleResponse body = mapper.toDetalle(service.cambiarEstado(solicitudId, idempotencyKey, ifMatch,
				requestMapper.toEstado(request.getEstadoDestino()), request.getMotivo(), effectiveCorrelationId));
		return ResponseEntity.ok().eTag(etag(body.getVersion()))
				.header(SolicitudApiConstants.CORRELATION_HEADER, effectiveCorrelationId.toString()).body(body);
	}

	@Override
	public ResponseEntity<Observacion> agregarObservacion(UUID solicitudId, UUID idempotencyKey, String ifMatch,
			AgregarObservacionRequest request, UUID correlationId) {
		ObservacionEntity entity = service.agregarObservacion(solicitudId, idempotencyKey, ifMatch, request.getContenido());
		Long version = service.obtener(solicitudId).solicitud().getVersion();
		return ResponseEntity.created(URI.create(SolicitudApiConstants.OBSERVACION_PATH.formatted(solicitudId, entity.getId())))
				.eTag(etag(version)).header(SolicitudApiConstants.CORRELATION_HEADER, correlation(correlationId).toString())
				.body(mapper.toObservacion(entity));
	}

	@Override
	public ResponseEntity<List<Categoria>> listarCategoriasActivas(UUID correlationId) {
		return ResponseEntity.ok().header(SolicitudApiConstants.CORRELATION_HEADER, correlation(correlationId).toString())
				.body(service.listarCategorias().stream().map(mapper::toCategoria).toList());
	}

	private static String etag(Long version) {
		return SolicitudApiConstants.ETAG_FORMAT.formatted(version);
	}

	private static UUID correlation(UUID correlationId) {
		return correlationId == null ? UUID.randomUUID() : correlationId;
	}
}
