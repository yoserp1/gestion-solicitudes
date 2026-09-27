package com.yoserp1.prueba.solicitudes.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuración de Jackson para la serialización y deserialización de JSON.
 */
@Configuration
public class JacksonConfiguration {

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper().findAndRegisterModules();
	}
}
