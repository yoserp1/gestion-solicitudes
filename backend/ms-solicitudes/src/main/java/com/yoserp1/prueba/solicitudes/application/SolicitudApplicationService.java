package com.yoserp1.prueba.solicitudes.application;

import static com.yoserp1.prueba.solicitudes.application.SolicitudConstants.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoserp1.prueba.solicitudes.application.security.ActorContext;
import com.yoserp1.prueba.solicitudes.application.security.ActorProvider;
import com.yoserp1.prueba.solicitudes.domain.model.EstadoSolicitud;
import com.yoserp1.prueba.solicitudes.domain.model.Prioridad;
import com.yoserp1.prueba.solicitudes.domain.model.RolActor;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.ApiException;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.CategoriaEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.HistorialEstadoEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.IdempotenciaComandoEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.ObservacionEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.OutboxEventoEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.SolicitudEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.CategoriaRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.HistorialEstadoRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.IdempotenciaComandoRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.ObservacionRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.OutboxEventoRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.SolicitudCodigoRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.SolicitudRepository;

import lombok.RequiredArgsConstructor;

/** 
 * Orquesta los casos de uso y reglas de negocio de las solicitudes.
 */
@Service
@RequiredArgsConstructor
public class SolicitudApplicationService {
	private final SolicitudRepository solicitudes;
	private final CategoriaRepository categorias;
	private final ObservacionRepository observaciones;
	private final HistorialEstadoRepository historiales;
	private final IdempotenciaComandoRepository idempotencias;
	private final OutboxEventoRepository outbox;
	private final ActorProvider actorProvider;
	private final SolicitudCodigoRepository solicitudCodigoRepository;
	private final ObjectMapper objectMapper;
	private final Clock clock;

	/** 
	 * Registra una nueva solicitud de forma idempotente.
	 * 
	 * @param clave Identificador único de la operación para garantizar idempotencia.
	 * @param asunto Asunto de la solicitud.
	 * @param descripcion Descripción de la solicitud.
	 * @param categoriaId Identificador de la categoría de la solicitud.
	 * @param prioridad Prioridad de la solicitud.
	 * @return Detalle de la solicitud registrada.
	 * @throws ApiException Si la categoría no es válida o no está activa.
	 */
	@Transactional
	public SolicitudDetalle registrar(UUID clave, String asunto, String descripcion, UUID categoriaId, Prioridad prioridad,
			UUID correlationId) {
		ActorContext actor = requireRole(RolActor.SOLICITANTE);
		String hash = hash(Map.of("asunto", asunto, "descripcion", descripcion, "categoriaId", categoriaId.toString(), "prioridad", prioridad.name()));
		Optional<UUID> replay = replay(clave, OPERACION_REGISTRAR, actor, hash);
		if (replay.isPresent()) return detalleVisible(replay.get(), actor);

		CategoriaEntity categoria = categorias.findById(categoriaId)
				.filter(CategoriaEntity::isActiva)
				.orElseThrow(() -> new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, CATEGORIA_INVALIDA,
						MENSAJE_CATEGORIA_INVALIDA));
		OffsetDateTime ahora = OffsetDateTime.now(clock);
		SolicitudEntity solicitud = new SolicitudEntity();
		solicitud.setId(UUID.randomUUID());
		solicitud.setCodigo(siguienteCodigo(ahora));
		solicitud.setAsunto(asunto);
		solicitud.setDescripcion(descripcion);
		solicitud.setCategoria(categoria);
		solicitud.setPrioridad(prioridad);
		solicitud.setEstado(EstadoSolicitud.REGISTRADA);
		solicitud.setSolicitanteId(actor.id());
		solicitud.setCreadaEn(ahora);
		solicitud.setActualizadaEn(ahora);
		solicitudes.saveAndFlush(solicitud);
		registrarHistorial(solicitud, null, EstadoSolicitud.REGISTRADA, actor, MOTIVO_SOLICITUD_REGISTRADA, ahora);
		registrarOutbox(solicitud, actor, ahora, correlationId);
		guardarIdempotencia(clave, OPERACION_REGISTRAR, actor, hash, solicitud.getId(), ahora);
		return detalle(buscar(solicitud.getId()));
	}

	/** 
	 * Lista las solicitudes visibles para el actor autenticado.
	 * 
	 * @param estado Filtro por estado de la solicitud.
	 * @param categoriaId Filtro por categoría de la solicitud.
	 * @param prioridad Filtro por prioridad de la solicitud.
	 * @param page Número de página.
	 * @param size Tamaño de página.
	 * @param sortValue Campo y dirección de ordenamiento.
	 * @return Página de solicitudes visibles para el actor autenticado.
	 */
	@Transactional(readOnly = true)
	public Page<SolicitudEntity> listar(EstadoSolicitud estado, UUID categoriaId, Prioridad prioridad,
			int page, int size, String sortValue) {
		ActorContext actor = actorProvider.actual();
		Specification<SolicitudEntity> spec = Specification.unrestricted();
		if (actor.rol() == RolActor.SOLICITANTE) spec = spec.and((root, query, cb) -> cb.equal(root.get("solicitanteId"), actor.id()));
		if (estado != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
		if (categoriaId != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId));
		if (prioridad != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("prioridad"), prioridad));
		String[] sortParts = Optional.ofNullable(sortValue).orElse(ORDEN_PREDETERMINADO).split(",", -1);
		if (sortParts.length != 2 || !CAMPOS_ORDEN.containsKey(sortParts[0])
				|| !(sortParts[1].equals("asc") || sortParts[1].equals("desc"))) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ORDEN_INVALIDO, MENSAJE_ORDEN_INVALIDO);
		}
		Sort.Direction direction = Sort.Direction.fromString(sortParts[1]);
		return solicitudes.findAll(spec, PageRequest.of(page, size, Sort.by(direction, CAMPOS_ORDEN.get(sortParts[0]))));
	}

	/** 
	 * Obtiene el detalle visible de una solicitud.
	 *
	 * @param id Identificador de la solicitud.
	 * @return Detalle de la solicitud visible para el actor autenticado.
	 * @throws ApiException Si la solicitud no es visible para el actor autenticado.
	 */
	@Transactional(readOnly = true)
	public SolicitudDetalle obtener(UUID id) {
		return detalleVisible(id, actorProvider.actual());
	}

	/** 
	 * Asigna una solicitud registrada al analista autenticado.
	 */
	@Transactional
	public SolicitudDetalle tomar(UUID id, UUID clave, String ifMatch, String motivo, UUID correlationId) {
		ActorContext actor = requireRole(RolActor.ANALISTA);
		String hash = hash(Map.of("solicitudId", id.toString(), "motivo",
				Optional.ofNullable(motivo).orElse(MOTIVO_INICIO_ATENCION)));
		Optional<UUID> replay = replay(clave, OPERACION_TOMAR, actor, hash);
		if (replay.isPresent()) return detalleVisible(replay.get(), actor);
		SolicitudEntity solicitud = buscar(id);
		validarVersion(solicitud, ifMatch);
		if (solicitud.getEstado() != EstadoSolicitud.REGISTRADA) {
			throw new ApiException(HttpStatus.CONFLICT, SOLICITUD_NO_DISPONIBLE, MENSAJE_SOLICITUD_NO_DISPONIBLE);
		}
		solicitud.setAnalistaId(actor.id());
		transicionar(solicitud, EstadoSolicitud.EN_ATENCION, actor,
				Optional.ofNullable(motivo).orElse(MOTIVO_INICIO_ATENCION), correlationId);
		guardarIdempotencia(clave, OPERACION_TOMAR, actor, hash, id, OffsetDateTime.now(clock));
		return detalle(solicitud);
	}

	/** 
	 * Agrega una observación a una solicitud en atención.
	 *
	 * @param id Identificador de la solicitud.
	 * @param clave Identificador único de la operación para garantizar idempotencia.
	 * @param ifMatch Versión de la solicitud para control de concurrencia.
	 * @param contenido Contenido de la observación.
	 * @return Observación agregada a la solicitud.
	 * @throws ApiException Si la solicitud no está en atención o no está asignada al analista autenticado.
	 */
	@Transactional
	public ObservacionEntity agregarObservacion(UUID id, UUID clave, String ifMatch, String contenido) {
		ActorContext actor = requireRole(RolActor.ANALISTA);
		String hash = hash(Map.of("solicitudId", id.toString(), "contenido", contenido));
		Optional<UUID> replay = replay(clave, OPERACION_AGREGAR_OBSERVACION, actor, hash);
		if (replay.isPresent()) return observaciones.findById(replay.get()).orElseThrow(() -> noEncontrada());
		SolicitudEntity solicitud = buscar(id);
		validarVersion(solicitud, ifMatch);
		if (solicitud.getEstado() != EstadoSolicitud.EN_ATENCION || !actor.id().equals(solicitud.getAnalistaId())) {
			throw new ApiException(HttpStatus.CONFLICT, SOLICITUD_NO_ASIGNADA, MENSAJE_SOLICITUD_NO_ASIGNADA);
		}
		OffsetDateTime ahora = OffsetDateTime.now(clock);
		ObservacionEntity observacion = new ObservacionEntity();
		observacion.setId(UUID.randomUUID());
		observacion.setSolicitud(solicitud);
		observacion.setContenido(contenido);
		observacion.setActorId(actor.id());
		observacion.setActorRol(actor.rol());
		observacion.setCreadaEn(ahora);
		observaciones.save(observacion);
		solicitud.setActualizadaEn(ahora);
		solicitudes.saveAndFlush(solicitud);
		guardarIdempotencia(clave, OPERACION_AGREGAR_OBSERVACION, actor, hash, observacion.getId(), ahora);
		return observacion;
	}

	/** 
	 * Cambia el estado de una solicitud cuando la transición es válida.
	 *
	 * @param id Identificador de la solicitud.
	 * @param clave Identificador único de la operación para garantizar idempotencia.
	 * @param ifMatch Versión de la solicitud para control de concurrencia.
	 * @param destino Estado destino de la solicitud.
	 * @param motivo Motivo del cambio de estado.
	 * @return Detalle de la solicitud con el nuevo estado.
	 * @throws ApiException Si la transición de estado no es válida o la solicitud no cumple con los requisitos para el cambio.
	 */
	@Transactional
	public SolicitudDetalle cambiarEstado(UUID id, UUID clave, String ifMatch, EstadoSolicitud destino, String motivo,
			UUID correlationId) {
		ActorContext actor = requireAnyRole(RolActor.ANALISTA, RolActor.SUPERVISOR);
		String hash = hash(Map.of("solicitudId", id.toString(), "destino", destino.name(), "motivo", motivo));
		Optional<UUID> replay = replay(clave, OPERACION_CAMBIAR_ESTADO, actor, hash);
		if (replay.isPresent()) return detalleVisible(replay.get(), actor);
		SolicitudEntity solicitud = buscar(id);
		validarVersion(solicitud, ifMatch);
		boolean analistaResuelve = actor.rol() == RolActor.ANALISTA
				&& solicitud.getEstado() == EstadoSolicitud.EN_ATENCION && destino == EstadoSolicitud.RESUELTA
				&& actor.id().equals(solicitud.getAnalistaId());
		boolean supervisorGestiona = actor.rol() == RolActor.SUPERVISOR && solicitud.getEstado() == EstadoSolicitud.RESUELTA
				&& (destino == EstadoSolicitud.EN_ATENCION || destino == EstadoSolicitud.CERRADA);
		if (!analistaResuelve && !supervisorGestiona) {
			throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, SOLICITUD_TRANSICION_INVALIDA,
					MENSAJE_TRANSICION_INVALIDA);
		}
		transicionar(solicitud, destino, actor, motivo, correlationId);
		guardarIdempotencia(clave, OPERACION_CAMBIAR_ESTADO, actor, hash, id, OffsetDateTime.now(clock));
		return detalle(solicitud);
	}

	/** 
	 * Lista las categorías activas disponibles para registrar solicitudes.
	 *
	 * @return Lista de categorías activas.
	 * @throws ApiException Si ocurre un error al acceder a las categorías.
	 */
	@Transactional(readOnly = true)
	public List<CategoriaEntity> listarCategorias() {
		actorProvider.actual();
		return categorias.findByActivaTrueOrderByNombreAsc();
	}

	/**
	 * Realiza la transición de estado de una solicitud.
	 * 
	 * @param solicitud Solicitud que se va a transicionar.
	 * @param destino Estado destino de la solicitud.
	 * @param actor Actor que realiza la transición.
	 * @param motivo Motivo del cambio de estado.
	 */
	private void transicionar(SolicitudEntity solicitud, EstadoSolicitud destino, ActorContext actor, String motivo,
			UUID correlationId) {
		EstadoSolicitud origen = solicitud.getEstado();
		OffsetDateTime ahora = OffsetDateTime.now(clock);
		solicitud.setEstado(destino);
		solicitud.setActualizadaEn(ahora);
		solicitudes.saveAndFlush(solicitud);
		registrarHistorial(solicitud, origen, destino, actor, motivo, ahora);
		registrarOutbox(solicitud, actor, ahora, correlationId);
	}

	/**
	 * Valida la versión de una solicitud utilizando el encabezado If-Match.
	 * 
	 * @param solicitud Solicitud cuya versión se va a validar.
	 * @param ifMatch Valor del encabezado If-Match.
	 */
	private void validarVersion(SolicitudEntity solicitud, String ifMatch) {
		if (ifMatch == null || ifMatch.isBlank()) {
			throw new ApiException(HttpStatus.PRECONDITION_REQUIRED, IF_MATCH_REQUERIDO, MENSAJE_IF_MATCH_REQUERIDO);
		}
		if (!ifMatch.matches(IF_MATCH_PATTERN)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, IF_MATCH_INVALIDO, MENSAJE_IF_MATCH_INVALIDO);
		}
		long version = Long.parseLong(ifMatch.substring(1, ifMatch.length() - 1));
		if (solicitud.getVersion() == null || solicitud.getVersion() != version) {
			throw new ApiException(HttpStatus.PRECONDITION_FAILED, VERSION_NO_COINCIDE, MENSAJE_VERSION_NO_COINCIDE);
		}
	}

	/**
	 * Obtiene el detalle de una solicitud visible para el actor especificado.
	 * 
	 * @param id ID de la solicitud.
	 * @param actor Actor que solicita el detalle.
	 * @return Detalle de la solicitud visible para el actor.
	 */
	private SolicitudDetalle detalleVisible(UUID id, ActorContext actor) {
		SolicitudEntity solicitud = buscar(id);
		if (actor.rol() == RolActor.SOLICITANTE && !actor.id().equals(solicitud.getSolicitanteId())) throw noEncontrada();
		return detalle(solicitud);
	}

	/**
	 * Obtiene el detalle de una solicitud.
	 * 
	 * @param solicitud Solicitud de la cual se va a obtener el detalle.
	 * @return Detalle de la solicitud.
	 */
	private SolicitudDetalle detalle(SolicitudEntity solicitud) {
		return new SolicitudDetalle(solicitud,
				observaciones.findBySolicitudIdOrderByCreadaEnAsc(solicitud.getId()),
				historiales.findBySolicitudIdOrderByOcurridoEnAsc(solicitud.getId()));
	}

	/**
	 * Busca una solicitud por su ID.
	 * 
	 * @param id ID de la solicitud.
	 * @return Entidad de la solicitud encontrada.
	 */
	private SolicitudEntity buscar(UUID id) {
		return solicitudes.findWithCategoriaById(id).orElseThrow(this::noEncontrada);
	}

	/**
	 * Crea una excepción indicando que la solicitud no fue encontrada.
	 * 
	 * @return Excepción de API indicando que la solicitud no fue encontrada.
	 */
	private ApiException noEncontrada() {
		return new ApiException(HttpStatus.NOT_FOUND, SOLICITUD_NO_ENCONTRADA, MENSAJE_SOLICITUD_NO_ENCONTRADA);
	}

	/**
	 * Requiere que el actor tenga un rol específico.
	 * 
	 * @param role Rol requerido.
	 * @return Contexto del actor si tiene el rol requerido.
	 * @throws ApiException Si el actor no tiene el rol requerido.
	 */
	private ActorContext requireRole(RolActor role) {
		ActorContext actor = actorProvider.actual();
		if (actor.rol() != role) {
			throw new ApiException(HttpStatus.FORBIDDEN, ROL_NO_AUTORIZADO, MENSAJE_ROL_NO_AUTORIZADO);
		}
		return actor;
	}

	private ActorContext requireAnyRole(RolActor... roles) {
		ActorContext actor = actorProvider.actual();
		if (List.of(roles).contains(actor.rol())) {
			return actor;
		}
		throw new ApiException(HttpStatus.FORBIDDEN, ROL_NO_AUTORIZADO, MENSAJE_ROL_NO_AUTORIZADO);
	}

	/**
	 * Maneja la lógica de idempotencia para una operación específica.
	 * 
	 * @param clave Clave de idempotencia.
	 * @param operacion Operación que se está realizando.
	 * @param actor Contexto del actor que realiza la operación.
	 * @param hash Hash de la solicitud.
	 * @return ID del recurso si la operación ya fue realizada previamente.
	 * @throws ApiException Si la clave de idempotencia fue reutilizada de manera incorrecta.
	 */
	private Optional<UUID> replay(UUID clave, String operacion, ActorContext actor, String hash) {
		return idempotencias.findById(clave).map(existing -> {
			if (!existing.getOperacion().equals(operacion) || !existing.getActorId().equals(actor.id()) || !existing.getRequestHash().equals(hash)) {
				throw new ApiException(HttpStatus.CONFLICT, IDEMPOTENCY_KEY_REUTILIZADA,
						MENSAJE_IDEMPOTENCIA_REUTILIZADA);
			}
			return existing.getRecursoId();
		});
	}

	/**
	 * Guarda la información de idempotencia para una operación específica.
	 * 
	 * @param clave Clave de idempotencia.
	 * @param operacion Operación que se está realizando.
	 * @param actor Contexto del actor que realiza la operación.
	 * @param hash Hash de la solicitud.
	 * @param recursoId ID del recurso asociado a la operación.
	 * @param ahora Fecha y hora actual.
	 */
	private void guardarIdempotencia(UUID clave, String operacion, ActorContext actor, String hash, UUID recursoId, OffsetDateTime ahora) {
		IdempotenciaComandoEntity entity = new IdempotenciaComandoEntity();
		entity.setClave(clave);
		entity.setOperacion(operacion);
		entity.setActorId(actor.id());
		entity.setRequestHash(hash);
		entity.setRecursoId(recursoId);
		entity.setCreadoEn(ahora);
		idempotencias.save(entity);
	}

	/**
	 * Registra un historial de cambios de estado para una solicitud.
	 * 
	 * @param solicitud Solicitud cuyo estado ha cambiado.
	 * @param origen Estado de origen.
	 * @param destino Estado de destino.
	 * @param actor Contexto del actor que realiza el cambio.
	 * @param motivo Motivo del cambio de estado.
	 * @param ahora Fecha y hora actual.
	 */
	private void registrarHistorial(SolicitudEntity solicitud, EstadoSolicitud origen, EstadoSolicitud destino,
			ActorContext actor, String motivo, OffsetDateTime ahora) {
		HistorialEstadoEntity historial = new HistorialEstadoEntity();
		historial.setId(UUID.randomUUID());
		historial.setSolicitud(solicitud);
		historial.setEstadoOrigen(origen);
		historial.setEstadoDestino(destino);
		historial.setActorId(actor.id());
		historial.setActorRol(actor.rol());
		historial.setMotivo(motivo);
		historial.setOcurridoEn(ahora);
		historiales.save(historial);
	}

	/**
	 * Registra un evento en la tabla de outbox.
	 * 
	 * @param solicitud Solicitud asociada al evento.
	 * @param actor Contexto del actor que genera el evento.
	 * @param ahora Fecha y hora actual.
	 * @param correlationId Identificador de correlación del comando que originó el evento.
	 */
	private void registrarOutbox(SolicitudEntity solicitud, ActorContext actor, OffsetDateTime ahora,
			UUID correlationId) {
		String eventType = eventTypeFor(solicitud.getEstado());
		OutboxEventoEntity event = new OutboxEventoEntity();
		event.setEventId(UUID.randomUUID());
		event.setAggregateId(solicitud.getId());
		event.setEventType(eventType);
		event.setPayload(json(Map.ofEntries(
				Map.entry("eventId", event.getEventId()), Map.entry("occurredAt", ahora.toString()),
				Map.entry("aggregateId", solicitud.getId()), Map.entry("type", eventType),
				Map.entry("version", solicitud.getVersion()), Map.entry("correlationId", correlationId),
				Map.entry("estado", solicitud.getEstado().name()),
				Map.entry("categoriaId", solicitud.getCategoria().getId()),
				Map.entry("categoriaCodigo", solicitud.getCategoria().getCodigo()),
				Map.entry("categoriaNombre", solicitud.getCategoria().getNombre()), Map.entry("actorId", actor.id()))));
		event.setOcurridoEn(ahora);
		event.setIntentos(0);
		outbox.save(event);
	}

	static String eventTypeFor(EstadoSolicitud estado) {
		return switch (estado) {
			case REGISTRADA -> EVENTO_SOLICITUD_REGISTRADA;
			case EN_ATENCION -> EVENTO_SOLICITUD_TOMADA;
			case RESUELTA -> EVENTO_SOLICITUD_RESUELTA;
			case CERRADA -> EVENTO_SOLICITUD_CERRADA;
		};
	}

	/**
	 * Genera el siguiente código para una solicitud.
	 * 
	 * @param ahora Fecha y hora actual.
	 * @return Siguiente código de solicitud.
	 */
	private String siguienteCodigo(OffsetDateTime ahora) {
		long sequence = solicitudCodigoRepository.siguienteValor();
		return FORMATO_CODIGO.formatted(ahora.getYear(), sequence);
	}

	/**
	 * Calcula el hash de un conjunto de valores.
	 * 
	 * @param values Valores a incluir en el hash.
	 * @return Hash calculado.
	 */
	private String hash(Map<String, ?> values) {
		try {
			byte[] canonical = objectMapper.writeValueAsBytes(new TreeMap<>(values));
			return HexFormat.of().formatHex(MessageDigest.getInstance(ALGORITMO_HASH).digest(canonical));
		} catch (JsonProcessingException | NoSuchAlgorithmException exception) {
			throw new IllegalStateException(MENSAJE_HASH_NO_CALCULADO, exception);
		}
	}
	
	/**
	 * Convierte un conjunto de valores a su representación JSON.
	 * 
	 * @param values Valores a convertir.
	 * @return Representación JSON de los valores.
	 */
	private String json(Map<String, ?> values) {
		try {
			return objectMapper.writeValueAsString(values);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException(MENSAJE_EVENTO_NO_SERIALIZADO, exception);
		}
	}
}
