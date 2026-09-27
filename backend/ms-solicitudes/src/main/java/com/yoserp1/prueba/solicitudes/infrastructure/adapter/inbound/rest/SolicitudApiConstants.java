package com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest;

/**
 * Centraliza nombres y formatos propios del contrato HTTP de solicitudes
 */
public final class SolicitudApiConstants {

	public static final String CORRELATION_HEADER = "X-Correlation-ID";
	public static final String SOLICITUD_PATH = "/api/v1/solicitudes/%s";
	public static final String OBSERVACION_PATH = "/api/v1/solicitudes/%s/observaciones/%s";
	public static final String PROBLEM_TYPE_PREFIX = "urn:problem:";
	public static final String PROPERTY_CODE = "code";
	public static final String PROPERTY_CORRELATION_ID = "correlationId";
	public static final String PROPERTY_ERRORS = "errors";
	public static final String PROPERTY_FIELD = "field";
	public static final String PROPERTY_MESSAGE = "message";
	public static final String ETAG_FORMAT = "\"%d\"";

	private SolicitudApiConstants() {
	}
}
