package com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Excepción personalizada para manejar errores de la API.
 * Contiene un código de error y un estado HTTP asociado.
 */
@Getter
public class ApiException extends RuntimeException {
	private final HttpStatus status;
	private final String code;

	public ApiException(HttpStatus status, String code, String message) {
		super(message);
		this.status = status;
		this.code = code;
	}
}
