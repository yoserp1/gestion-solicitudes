package com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest;

/**
 * Centraliza nombres y formatos propios del contrato HTTP de indicadores.
 */
public final class IndicadorApiConstants {

	public static final String CORRELATION_HEADER = "X-Correlation-ID";
	public static final String DATA_AS_OF_HEADER = "X-Data-As-Of";
	public static final String PROBLEM_TYPE_PREFIX = "urn:problem:";
	public static final String PROPERTY_CODE = "code";
	public static final String PROPERTY_CORRELATION_ID = "correlationId";
	public static final long CACHE_MAX_AGE_SECONDS = 30;

	private IndicadorApiConstants() {
	}
}
