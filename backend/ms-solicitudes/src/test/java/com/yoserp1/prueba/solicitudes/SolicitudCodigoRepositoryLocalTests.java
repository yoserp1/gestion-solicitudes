package com.yoserp1.prueba.solicitudes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.SolicitudCodigoRepository;

/** Valida la consulta de la secuencia contra la base local cuando está configurada. */
@SpringBootTest
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class SolicitudCodigoRepositoryLocalTests {

	@Autowired
	private SolicitudCodigoRepository repository;

	@Test
	void obtieneSiguienteValorDeLaSecuencia() {
		assertThat(repository.siguienteValor()).isPositive();
	}
}
