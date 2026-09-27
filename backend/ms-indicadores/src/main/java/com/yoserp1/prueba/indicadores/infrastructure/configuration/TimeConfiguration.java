package com.yoserp1.prueba.indicadores.infrastructure.configuration;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuración de tiempo para la aplicación.
 * Proporciona un bean de Clock que utiliza la zona horaria UTC.
 */
@Configuration
@Profile("!docs")
public class TimeConfiguration {

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}
}