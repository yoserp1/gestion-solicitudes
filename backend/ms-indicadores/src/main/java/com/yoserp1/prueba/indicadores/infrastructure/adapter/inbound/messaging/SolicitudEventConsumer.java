package com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.messaging;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.repository.SolicitudProjectionWriteRepository;

import lombok.RequiredArgsConstructor;

/**
 * Consumidor de los eventos de ciclo de vida de una solicitud.
 */
@Component
@Profile("!docs")
@RequiredArgsConstructor
public class SolicitudEventConsumer {

	private static final String SOLICITUD_REGISTRADA = "SolicitudRegistrada";
	private static final Map<String, String> ESTADO_POR_TIPO = Map.of(
			SOLICITUD_REGISTRADA, "REGISTRADA",
			"SolicitudTomada", "EN_ATENCION",
			"SolicitudResuelta", "RESUELTA",
			"SolicitudCerrada", "CERRADA");

	private final SolicitudProjectionWriteRepository projectionRepository;
	private final ObjectMapper objectMapper;
	private final Clock clock;

    /**
     * Escucha los eventos del topic de solicitudes y los procesa.
     * 
     * @param payload el contenido del evento en formato JSON
     * @throws Exception si ocurre un error al procesar el evento
     */
	@KafkaListener(topics = "${app.kafka.solicitudes-topic:solicitudes.v1}")
	@Transactional
	public void consumir(String payload) throws Exception {
		JsonNode event = objectMapper.readTree(payload);
		validar(event);

		UUID eventId = UUID.fromString(event.required("eventId").asText());
		String eventType = event.required("type").asText();
		int inserted = projectionRepository.registrarEventoProcesado(eventId, eventType, OffsetDateTime.now(clock));
		if (inserted == 0) {
			return;
		}

		if (SOLICITUD_REGISTRADA.equals(eventType)) {
			registrarSolicitud(event);
		} else {
			actualizarEstado(event);
		}
		registrarTransicion(event);
	}

    /**
     * Valida la estructura y el tipo del evento.
     *
     * @param event el nodo JSON que representa el evento
     * @throws IllegalArgumentException si el evento no es válido
     */
	private void validar(JsonNode event) {
		UUID.fromString(event.required("eventId").asText());
		UUID.fromString(event.required("aggregateId").asText());
		UUID.fromString(event.required("correlationId").asText());
		OffsetDateTime.parse(event.required("occurredAt").asText());
		long version = event.required("version").asLong(-1);
		if (version < 0) {
			throw new IllegalArgumentException("Versión de agregado inválida");
		}
		String eventType = event.required("type").asText();
		String expectedState = ESTADO_POR_TIPO.get(eventType);
		if (expectedState == null) {
			throw new IllegalArgumentException("Tipo de evento no soportado");
		}
		if (!expectedState.equals(event.required("estado").asText())) {
			throw new IllegalArgumentException("El tipo de evento no corresponde con su estado");
		}
	}

    /**
     * Registra una nueva solicitud en la proyección.
     *
     * @param event el nodo JSON que representa el evento
     */
	private void registrarSolicitud(JsonNode event) {
		projectionRepository.registrarSolicitud(
				uuid(event, "aggregateId"), uuid(event, "categoriaId"), event.required("categoriaCodigo").asText(),
				event.required("categoriaNombre").asText(), event.required("estado").asText(), fecha(event), fecha(event),
				event.required("version").asLong());
	}

    /**
     * Actualiza el estado de una solicitud en la proyección.
     *
     * @param event el nodo JSON que representa el evento
     */
	private void actualizarEstado(JsonNode event) {
		long version = event.required("version").asLong();
		projectionRepository.actualizarEstado(uuid(event, "aggregateId"), event.required("estado").asText(), fecha(event),
				version);
	}

    /**
     * Registra una transición de estado de una solicitud en la proyección.
     *
     * @param event el nodo JSON que representa el evento
     */
	private void registrarTransicion(JsonNode event) {
		projectionRepository.registrarTransicion(
				uuid(event, "eventId"), uuid(event, "aggregateId"), uuid(event, "categoriaId"),
				event.required("estado").asText(), fecha(event));
	}

    /**
     * Convierte un campo de tipo UUID del evento a un objeto UUID.
     *
     * @param event el nodo JSON que representa el evento
     * @param field el nombre del campo que contiene el UUID
     * @return el objeto UUID correspondiente
     */
	private static UUID uuid(JsonNode event, String field) {
		return UUID.fromString(event.required(field).asText());
	}

    /**
	 * Convierte el campo "occurredAt" del evento a un objeto OffsetDateTime.
     *
     * @param event el nodo JSON que representa el evento
     * @return el objeto OffsetDateTime correspondiente
     */
	private static OffsetDateTime fecha(JsonNode event) {
		return OffsetDateTime.parse(event.required("occurredAt").asText());
	}
}
