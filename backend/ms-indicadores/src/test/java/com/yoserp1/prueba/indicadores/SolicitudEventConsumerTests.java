package com.yoserp1.prueba.indicadores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.messaging.SolicitudEventConsumer;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.repository.SolicitudProjectionWriteRepository;

@ExtendWith(MockitoExtension.class)
class SolicitudEventConsumerTests {

	private static final String EVENT = """
			{
			  "eventId": "20000000-0000-0000-0000-000000000001",
			  "occurredAt": "2026-09-27T12:00:00Z",
			  "aggregateId": "30000000-0000-0000-0000-000000000001",
			  "type": "SolicitudRegistrada",
			  "version": 0,
			  "correlationId": "40000000-0000-0000-0000-000000000001",
			  "estado": "REGISTRADA",
			  "categoriaId": "10000000-0000-0000-0000-000000000002",
			  "categoriaCodigo": "SOPORTE",
			  "categoriaNombre": "Soporte operativo",
			  "actorId": "usuario-demo"
			}
			""";

	@Mock
	private SolicitudProjectionWriteRepository projectionRepository;

	private SolicitudEventConsumer consumer;

	@BeforeEach
	void setUp() {
		consumer = new SolicitudEventConsumer(projectionRepository, new ObjectMapper(),
				Clock.fixed(Instant.parse("2026-09-27T12:01:00Z"), ZoneOffset.UTC));
	}

	@Test
	void proyectaUnEventoNuevo() throws Exception {
		when(projectionRepository.registrarEventoProcesado(any(), anyString(), any())).thenReturn(1);

		consumer.consumir(EVENT);

		verify(projectionRepository).registrarSolicitud(any(), any(), eq("SOPORTE"), eq("Soporte operativo"),
				eq("REGISTRADA"), any(), any(), eq(0L));
		verify(projectionRepository).registrarTransicion(any(), any(), any(), eq("REGISTRADA"), any());
	}

	@Test
	void ignoraUnEventoYaProcesado() throws Exception {
		when(projectionRepository.registrarEventoProcesado(any(), anyString(), any())).thenReturn(0);

		consumer.consumir(EVENT);

		verify(projectionRepository, never()).registrarSolicitud(any(), any(), anyString(), anyString(), anyString(), any(),
				any(), any(Long.class));
		verify(projectionRepository, never()).registrarTransicion(any(), any(), any(), anyString(), any());
	}

	@Test
	void proyectaLosTresEventosDeCambioDeEstado() throws Exception {
		when(projectionRepository.registrarEventoProcesado(any(), anyString(), any())).thenReturn(1);

		consumer.consumir(evento("SolicitudTomada", "EN_ATENCION", 1));
		consumer.consumir(evento("SolicitudResuelta", "RESUELTA", 2));
		consumer.consumir(evento("SolicitudCerrada", "CERRADA", 3));

		verify(projectionRepository).registrarEventoProcesado(any(), eq("SolicitudTomada"), any());
		verify(projectionRepository).registrarEventoProcesado(any(), eq("SolicitudResuelta"), any());
		verify(projectionRepository).registrarEventoProcesado(any(), eq("SolicitudCerrada"), any());
		verify(projectionRepository, times(3)).actualizarEstado(any(), anyString(), any(), any(Long.class));
		verify(projectionRepository, times(3)).registrarTransicion(any(), any(), any(), anyString(), any());
	}

	@Test
	void rechazaElTipoDeEventoLegado() {
		assertThatThrownBy(() -> consumer.consumir(evento("SolicitudEstadoCambiado", "RESUELTA", 2)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Tipo de evento no soportado");
	}

	private static String evento(String type, String estado, long version) {
		return EVENT.replace("SolicitudRegistrada", type)
				.replace("REGISTRADA", estado)
				.replace("\"version\": 0", "\"version\": " + version);
	}
}