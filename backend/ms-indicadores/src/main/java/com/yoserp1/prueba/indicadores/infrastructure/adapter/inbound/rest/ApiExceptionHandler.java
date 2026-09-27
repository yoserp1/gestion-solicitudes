package com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest;

import static com.yoserp1.prueba.indicadores.application.IndicadorConstants.*;
import static com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.IndicadorApiConstants.*;

import java.net.URI;
import java.util.Locale;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de excepciones para la API que convierte las excepciones en respuestas HTTP con detalles de problemas.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ApiException.class)
	ResponseEntity<ProblemDetail> handleApiException(ApiException exception, HttpServletRequest request) {
		return response(exception.getStatus(), problem(exception.getStatus(), exception.getCode(),
				exception.getMessage(), request));
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, HandlerMethodValidationException.class,
			MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class })
	ResponseEntity<ProblemDetail> handleValidation(Exception exception, HttpServletRequest request) {
		return response(HttpStatus.BAD_REQUEST,
				problem(HttpStatus.BAD_REQUEST, PARAMETROS_INVALIDOS, MENSAJE_PARAMETROS_INVALIDOS, request));
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ProblemDetail> handleUnexpected(Exception exception, HttpServletRequest request) {
		return response(HttpStatus.INTERNAL_SERVER_ERROR,
				problem(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNO, MENSAJE_ERROR_INTERNO, request));
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