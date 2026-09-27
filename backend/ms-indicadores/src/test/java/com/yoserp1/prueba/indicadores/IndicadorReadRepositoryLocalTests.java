package com.yoserp1.prueba.indicadores;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.repository.IndicadorReadRepository;

/** Valida las consultas analíticas contra la base local cuando está configurada. */
@SpringBootTest
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class IndicadorReadRepositoryLocalTests {

	@Autowired
	private IndicadorReadRepository repository;

	@Test
	void ejecutaConsultasNativasConProyecciones() {
		OffsetDateTime desde = OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime hasta = desde.plusMonths(1);

		assertThat(repository.obtenerSolicitudes(desde, hasta, null)).isNotNull();
		assertThat(repository.obtenerTransiciones(desde, hasta, null)).isNotNull();
		repository.obtenerActualizadoHasta();
	}
}
