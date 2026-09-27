package com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.mapper;

import org.springframework.stereotype.Component;

import com.yoserp1.prueba.indicadores.application.IndicadorConstants;
import com.yoserp1.prueba.indicadores.application.IndicadorApplicationService.Resumen;
import com.yoserp1.prueba.indicadores.application.IndicadorApplicationService.Tendencia;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.ConteoPorCategoria;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.ConteoPorEstado;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.EstadoSolicitud;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.Periodo;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.PuntoTendencia;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.ResumenIndicadoresResponse;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.generated.model.TendenciaDiariaResponse;

/**
 * Mapper que convierte los resultados analíticos internos al contrato OpenAPI de indicadores.
 */
@Component
public class IndicadorDtoMapper {

	/** 
	 * Convierte el resumen calculado al contrato de salida. 
	 * 
	 * @param resumen el resumen calculado internamente
	 * @return el objeto de respuesta conforme al contrato OpenAPI
	 */
	public ResumenIndicadoresResponse toResumen(Resumen resumen) {
		var porEstado = resumen.porEstado().entrySet().stream()
				.map(entry -> new ConteoPorEstado(EstadoSolicitud.valueOf(entry.getKey().name()), entry.getValue()))
				.toList();
		var porCategoria = resumen.porCategoria().stream()
				.map(item -> new ConteoPorCategoria(item.id(), item.codigo(), item.nombre(), item.cantidad()))
				.toList();
		return new ResumenIndicadoresResponse(
				new Periodo(resumen.desde(), resumen.hasta(), IndicadorConstants.UTC_ZONE), resumen.total(), porEstado,
				porCategoria, resumen.actualizadoHasta());
	}

	/**
	 * Convierte la tendencia calculada al contrato de salida.
	 *
	 * @param tendencia la tendencia calculada internamente
	 * @return el objeto de respuesta conforme al contrato OpenAPI
	 */
	public TendenciaDiariaResponse toTendencia(Tendencia tendencia) {
		var puntos = tendencia.puntos().stream()
				.map(item -> new PuntoTendencia(item.fecha(), item.registradas(), item.resueltas(), item.cerradas()))
				.toList();
		var response = new TendenciaDiariaResponse(
				new Periodo(tendencia.desde(), tendencia.hasta(), tendencia.zonaHoraria()), puntos,
				tendencia.actualizadoHasta());
		response.setCategoriaId(tendencia.categoriaId());
		return response;
	}
}
