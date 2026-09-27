package com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest;

import static com.yoserp1.prueba.solicitudes.application.SolicitudConstants.VERSION_NO_COINCIDE;
import static com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.SolicitudApiConstants.PROPERTY_CODE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

class ApiExceptionHandlerTests {

	private final ApiExceptionHandler handler = new ApiExceptionHandler();

	@Test
	void traduceConflictoOptimistaAPrecondicionFallida() {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/solicitudes/id/asignaciones");
		request.setAttribute(CorrelationIdFilter.ATTRIBUTE, "correlation-id");

		var response = handler.handleOptimisticLocking(
				new ObjectOptimisticLockingFailureException("SolicitudEntity", UUID.randomUUID()), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getProperties()).containsEntry(PROPERTY_CODE, VERSION_NO_COINCIDE);
	}
}