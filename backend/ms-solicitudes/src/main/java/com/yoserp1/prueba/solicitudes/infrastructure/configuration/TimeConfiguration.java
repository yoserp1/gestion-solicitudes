package com.yoserp1.prueba.solicitudes.infrastructure.configuration;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de tiempo para la aplicación.
 * Proporciona un bean de Clock que utiliza el tiempo del sistema en UTC.
 */
@Configuration
public class TimeConfiguration {

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}
}
