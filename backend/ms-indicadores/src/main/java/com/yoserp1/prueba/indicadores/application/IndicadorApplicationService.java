package com.yoserp1.prueba.indicadores.application;

import static com.yoserp1.prueba.indicadores.application.IndicadorConstants.*;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest.ApiException;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.projection.TransicionIndicadorProjection;
import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.repository.IndicadorReadRepository;

import lombok.RequiredArgsConstructor;

/** 
 * Calcula resúmenes y tendencias a partir de las proyecciones analíticas.
 */
@Service
@Profile("!docs")
@RequiredArgsConstructor
public class IndicadorApplicationService {

	private final IndicadorReadRepository indicadorReadRepository;
	private final Clock clock;

	/** 
	 * Calcula el resumen del período y categoría solicitados.
	 *
	 * @param desde      la fecha de inicio del período
	 * @param hasta      la fecha de fin del período
	 * @param categoriaId el ID de la categoría
	 * @return el resumen calculado
	 */
	@Transactional(readOnly = true)
	public Resumen resumen(LocalDate desde, LocalDate hasta, UUID categoriaId) {
		LocalDate fechaHasta = hasta == null ? LocalDate.now(clock) : hasta;
		LocalDate fechaDesde = desde == null ? fechaHasta.minusDays(DEFAULT_RANGE_DAYS) : desde;
		Rango rango = validarRango(fechaDesde, fechaHasta, ZoneOffset.UTC);
		var solicitudes = indicadorReadRepository.obtenerSolicitudes(rango.desde(), rango.hastaExclusivo(), categoriaId);

		Map<Estado, Long> porEstado = new EnumMap<>(Estado.class);
		for (Estado estado : Estado.values()) {
			porEstado.put(estado, 0L);
		}
		Map<UUID, CategoriaConteo> porCategoria = new LinkedHashMap<>();
		for (var solicitud : solicitudes) {
			Estado estadoSolicitud = Estado.valueOf(solicitud.getEstado());
			porEstado.compute(estadoSolicitud, (estado, cantidad) -> cantidad + 1);
			porCategoria.compute(solicitud.getCategoriaId(), (id, conteo) -> conteo == null
					? new CategoriaConteo(id, solicitud.getCategoriaCodigo(), solicitud.getCategoriaNombre(), 1L)
					: conteo.incrementar());
		}
		return new Resumen(fechaDesde, fechaHasta, solicitudes.size(), porEstado,
				new ArrayList<>(porCategoria.values()), actualizadoHasta());
	}

	/** 
	 * Calcula la tendencia diaria en la zona horaria solicitada.
	 *
	 * @param desde      la fecha de inicio del período
	 * @param hasta      la fecha de fin del período
	 * @param categoriaId el ID de la categoría
	 * @param zonaHoraria la zona horaria para el cálculo de la tendencia
	 * @return la tendencia calculada
	 */
	@Transactional(readOnly = true)
	public Tendencia tendencia(LocalDate desde, LocalDate hasta, UUID categoriaId, String zonaHoraria) {
		ZoneId zona = validarZona(zonaHoraria);
		Rango rango = validarRango(desde, hasta, zona);
		Map<LocalDate, PuntoConteo> puntos = new LinkedHashMap<>();
		desde.datesUntil(hasta.plusDays(1)).forEach(fecha -> puntos.put(fecha, new PuntoConteo(fecha, 0, 0, 0)));

		Set<TransicionDiaria> contabilizadas = new HashSet<>();
		for (TransicionIndicadorProjection transicion : indicadorReadRepository.obtenerTransiciones(
				rango.desde(), rango.hastaExclusivo(), categoriaId)) {
			LocalDate fecha = OffsetDateTime.parse(transicion.getOcurridoEn()).atZoneSameInstant(zona).toLocalDate();
			Estado estado = Estado.valueOf(transicion.getEstado());
			if (contabilizadas.add(new TransicionDiaria(transicion.getSolicitudId(), estado, fecha))) {
				puntos.computeIfPresent(fecha, (dia, punto) -> punto.incrementar(estado));
			}
		}
		return new Tendencia(desde, hasta, zona.getId(), categoriaId, new ArrayList<>(puntos.values()),
				actualizadoHasta());
	}

	/** 
	 * Obtiene la fecha y hora hasta la cual se han actualizado los datos.
	 *
	 * @return la fecha y hora de la última actualización
	 */
	private OffsetDateTime actualizadoHasta() {
		String resultado = indicadorReadRepository.obtenerActualizadoHasta();
		return resultado == null ? OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC) : OffsetDateTime.parse(resultado);
	}

	/** 
	 * Valida el rango de fechas proporcionado.
	 *
	 * @param desde la fecha de inicio del período
	 * @param hasta la fecha de fin del período
	 * @param zona  la zona horaria para la validación
	 * @return el rango validado
	 * @throws ApiException si el rango no es válido
	 */
	private Rango validarRango(LocalDate desde, LocalDate hasta, ZoneId zona) {
		if (desde == null || hasta == null || desde.isAfter(hasta)
				|| ChronoUnit.DAYS.between(desde, hasta) + 1 > MAX_RANGE_DAYS) {
			throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, RANGO_FECHAS_INVALIDO, MENSAJE_RANGO_INVALIDO);
		}
		return new Rango(desde.atStartOfDay(zona).toOffsetDateTime(),
				hasta.plusDays(1).atStartOfDay(zona).toOffsetDateTime());
	}

	/** 
	 * Valida la zona horaria proporcionada.
	 *
	 * @param zonaHoraria la zona horaria a validar
	 * @return la zona horaria validada
	 * @throws ApiException si la zona horaria no es válida
	 */
	private ZoneId validarZona(String zonaHoraria) {
		try {
			return ZoneId.of(zonaHoraria == null || zonaHoraria.isBlank() ? UTC_ZONE : zonaHoraria);
		} catch (DateTimeException exception) {
			throw new ApiException(HttpStatus.BAD_REQUEST, ZONA_HORARIA_INVALIDA, MENSAJE_ZONA_INVALIDA);
		}
	}

	/** 
	 * Estados considerados por los indicadores.
	 */
	public enum Estado { REGISTRADA, EN_ATENCION, RESUELTA, CERRADA }

	/** 
	 * Resultado agregado del resumen de solicitudes.
	 */
	public record Resumen(LocalDate desde, LocalDate hasta, long total, Map<Estado, Long> porEstado,
			List<CategoriaConteo> porCategoria, OffsetDateTime actualizadoHasta) {}

	/** 
	 * Conteo de solicitudes asociado a una categoría.
	 */
	public record CategoriaConteo(UUID id, String codigo, String nombre, long cantidad) {
		CategoriaConteo incrementar() {
			return new CategoriaConteo(id, codigo, nombre, cantidad + 1);
		}
	}

	/** 
	 * Resultado de la tendencia diaria.
	 */
	public record Tendencia(LocalDate desde, LocalDate hasta, String zonaHoraria, UUID categoriaId,
			List<PuntoConteo> puntos, OffsetDateTime actualizadoHasta) {}

	/** 
	 * Conteos diarios por estado relevante.
	 */
	public record PuntoConteo(LocalDate fecha, long registradas, long resueltas, long cerradas) {
		PuntoConteo incrementar(Estado estado) {
			return switch (estado) {
				case REGISTRADA -> new PuntoConteo(fecha, registradas + 1, resueltas, cerradas);
				case RESUELTA -> new PuntoConteo(fecha, registradas, resueltas + 1, cerradas);
				case CERRADA -> new PuntoConteo(fecha, registradas, resueltas, cerradas + 1);
				case EN_ATENCION -> this;
			};
		}
	}

	/** 
	 * Representa un rango de fechas con inicio y fin exclusivo.
	 */
	private record Rango(OffsetDateTime desde, OffsetDateTime hastaExclusivo) {}

	/** 
	 * Representa una transición diaria de una solicitud.
	 */
	private record TransicionDiaria(UUID solicitudId, Estado estado, LocalDate fecha) {}
}