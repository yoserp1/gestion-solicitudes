package com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.yoserp1.prueba.indicadores.application.IndicadorApplicationService;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.api.IndicadoresApiDelegate;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.ResumenIndicadoresResponse;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.TendenciaDiariaResponse;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.mapper.IndicadorDtoMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Implementa los endpoints OpenAPI y delega los cálculos al servicio de aplicación.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IndicadoresApiDelegateAdapter implements IndicadoresApiDelegate {

	private final IndicadorApplicationService service;
	private final IndicadorDtoMapper mapper;
	private final HttpServletRequest request;

	/**
	 * Obtiene el resumen de indicadores para un rango de fechas y una categoría específica.
	 *
	 * @param correlationId el identificador de correlación de la solicitud
	 * @param desde        la fecha de inicio del rango
	 * @param hasta        la fecha de fin del rango
	 * @param categoriaId  el identificador de la categoría
	 * @return la respuesta HTTP con el resumen de indicadores
	 */
	@Override
	public ResponseEntity<ResumenIndicadoresResponse> obtenerResumenIndicadores(UUID correlationId,
			LocalDate desde, LocalDate hasta, UUID categoriaId) {
		var resumen = service.resumen(desde, hasta, categoriaId);
		var body = mapper.toResumen(resumen);
		return response(body, correlation(correlationId), resumen.actualizadoHasta().toString());
	}

	/**
	 * Obtiene la tendencia diaria de indicadores para un rango de fechas y una categoría específica.
	 *
	 * @param desde        la fecha de inicio del rango
	 * @param hasta        la fecha de fin del rango
	 * @param correlationId el identificador de correlación de la solicitud
	 * @param categoriaId  el identificador de la categoría
	 * @param zonaHoraria  la zona horaria para los cálculos
	 * @return la respuesta HTTP con la tendencia diaria de indicadores
	 */
	@Override
	public ResponseEntity<TendenciaDiariaResponse> obtenerTendenciaDiaria(LocalDate desde, LocalDate hasta,
			UUID correlationId, UUID categoriaId, String zonaHoraria) {
		var tendencia = service.tendencia(desde, hasta, categoriaId, zonaHoraria);
		var body = mapper.toTendencia(tendencia);
		return response(body, correlation(correlationId), tendencia.actualizadoHasta().toString());
	}

	/**
	 * Construye la respuesta HTTP con los encabezados de correlación y la fecha de actualización.
	 *
	 * @param body            el cuerpo de la respuesta
	 * @param correlationId   el identificador de correlación
	 * @param actualizadoHasta la fecha de la última actualización de los datos
	 * @param <T>             el tipo del cuerpo de la respuesta
	 * @return la respuesta HTTP construida
	 */
	private <T> ResponseEntity<T> response(T body, String correlationId, String actualizadoHasta) {
		return ResponseEntity.ok()
				.header(IndicadorApiConstants.CORRELATION_HEADER, correlationId)
				.header(IndicadorApiConstants.DATA_AS_OF_HEADER, actualizadoHasta)
				.cacheControl(CacheControl.maxAge(
						Duration.ofSeconds(IndicadorApiConstants.CACHE_MAX_AGE_SECONDS)).cachePrivate())
				.body(body);
	}

	/**
	 * Obtiene el identificador de correlación de la solicitud, ya sea desde el encabezado HTTP o generando uno nuevo si no está presente.
	 *
	 * @param correlationId el identificador de correlación proporcionado
	 * @return el identificador de correlación efectivo
	 */
	private String correlation(UUID correlationId) {
		Object filtered = request.getAttribute(CorrelationIdFilter.ATTRIBUTE);
		return filtered == null
				? (correlationId == null ? UUID.randomUUID() : correlationId).toString()
				: filtered.toString();
	}
}
