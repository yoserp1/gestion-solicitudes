package com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada para la API que encapsula el estado HTTP, un código de error y un mensaje.
 */
public class ApiException extends RuntimeException {

	private final HttpStatus status;
	private final String code;

	/**
	 * Crea una nueva instancia de ApiException.
	 *
	 * @param status  el estado HTTP asociado con la excepción
	 * @param code    el código de error específico
	 * @param message el mensaje descriptivo de la excepción
	 */
	public ApiException(HttpStatus status, String code, String message) {
		super(message);
		this.status = status;
		this.code = code;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}
}