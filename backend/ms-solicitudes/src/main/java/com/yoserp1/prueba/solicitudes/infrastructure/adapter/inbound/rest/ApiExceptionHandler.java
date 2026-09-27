package com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest;

import static com.yoserp1.prueba.solicitudes.application.SolicitudConstants.*;
import static com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.SolicitudApiConstants.*;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de excepciones para la API REST.
 * Intercepta excepciones y genera respuestas con detalles de problemas (Problem Details).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ApiException.class)
	ResponseEntity<ProblemDetail> handleApiException(ApiException exception, HttpServletRequest request) {
		ProblemDetail problem = problem(exception.getStatus(), exception.getCode(), exception.getMessage(), request);
		return response(exception.getStatus(), problem);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA, request);
		problem.setProperty(PROPERTY_ERRORS, exception.getBindingResult().getFieldErrors().stream()
				.map(error -> Map.of(PROPERTY_FIELD, error.getField(), PROPERTY_MESSAGE, error.getDefaultMessage()))
				.toList());
		return response(HttpStatus.BAD_REQUEST, problem);
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	ResponseEntity<ProblemDetail> handleOptimisticLocking(ObjectOptimisticLockingFailureException exception,
			HttpServletRequest request) {
		ProblemDetail problem = problem(HttpStatus.PRECONDITION_FAILED, VERSION_NO_COINCIDE,
				MENSAJE_VERSION_NO_COINCIDE, request);
		return response(HttpStatus.PRECONDITION_FAILED, problem);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ProblemDetail> handleUnexpected(Exception exception, HttpServletRequest request) {
		ProblemDetail problem = problem(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNO, MENSAJE_ERROR_INTERNO, request);
		return response(HttpStatus.INTERNAL_SERVER_ERROR, problem);
	}

	private ProblemDetail problem(HttpStatus status, String code, String detail, HttpServletRequest request) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
		problem.setType(URI.create(PROBLEM_TYPE_PREFIX + code.toLowerCase(Locale.ROOT)));
		problem.setTitle(status.getReasonPhrase());
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty(PROPERTY_CODE, code);
		Object correlationId = request.getAttribute(CorrelationIdFilter.ATTRIBUTE);
		if (correlationId != null) {
			problem.setProperty(PROPERTY_CORRELATION_ID, correlationId);
		}
		return problem;
	}

	private ResponseEntity<ProblemDetail> response(HttpStatus status, ProblemDetail problem) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
		return new ResponseEntity<>(problem, headers, status);
	}
}
