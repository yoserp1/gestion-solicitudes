package com.yoserp1.prueba.solicitudes.application;

import static com.yoserp1.prueba.solicitudes.application.SolicitudConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.yoserp1.prueba.solicitudes.application.security.ActorContext;
import com.yoserp1.prueba.solicitudes.application.security.ActorProvider;
import com.yoserp1.prueba.solicitudes.domain.model.EstadoSolicitud;
import com.yoserp1.prueba.solicitudes.domain.model.RolActor;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.ApiException;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.HistorialEstadoRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.IdempotenciaComandoRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.OutboxEventoRepository;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.SolicitudRepository;

@ExtendWith(MockitoExtension.class)
class SolicitudApplicationServiceTests {

	@Mock
	private SolicitudRepository solicitudes;
	@Mock
	private HistorialEstadoRepository historiales;
	@Mock
	private IdempotenciaComandoRepository idempotencias;
	@Mock
	private OutboxEventoRepository outbox;
	@Mock
	private ActorProvider actorProvider;
	@InjectMocks
	private SolicitudApplicationService service;

	@Test
	void rechazaTransicionDeSolicitanteAntesDeConsultarOPersistir() {
		when(actorProvider.actual()).thenReturn(new ActorContext("solicitante-demo", RolActor.SOLICITANTE));

		assertThatThrownBy(() -> service.cambiarEstado(UUID.randomUUID(), UUID.randomUUID(), "\"1\"",
				EstadoSolicitud.CERRADA, "Intento no autorizado", UUID.randomUUID()))
				.isInstanceOfSatisfying(ApiException.class, exception -> {
					assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
					assertThat(exception.getCode()).isEqualTo(ROL_NO_AUTORIZADO);
				});

		verifyNoInteractions(solicitudes, historiales, idempotencias, outbox);
	}

	@Test
	void mapeaCadaEstadoAlTipoDeEventoEsperado() {
		assertThat(SolicitudApplicationService.eventTypeFor(EstadoSolicitud.REGISTRADA))
				.isEqualTo(EVENTO_SOLICITUD_REGISTRADA);
		assertThat(SolicitudApplicationService.eventTypeFor(EstadoSolicitud.EN_ATENCION))
				.isEqualTo(EVENTO_SOLICITUD_TOMADA);
		assertThat(SolicitudApplicationService.eventTypeFor(EstadoSolicitud.RESUELTA))
				.isEqualTo(EVENTO_SOLICITUD_RESUELTA);
		assertThat(SolicitudApplicationService.eventTypeFor(EstadoSolicitud.CERRADA))
				.isEqualTo(EVENTO_SOLICITUD_CERRADA);
	}
}