package com.yoserp1.prueba.solicitudes.infrastructure.configuration;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuración de seguridad para el perfil "local". Permite la autenticación basada en encabezados HTTP personalizados.
 */
@Configuration
public class LocalSecurityConfiguration {
	private static final String LOCAL_USER_ID_HEADER = "X-Local-User-Id";
	private static final String LOCAL_USER_ROLE_HEADER = "X-Local-User-Role";
	private static final String ROLE_PREFIX = "ROLE_";

	@Bean
	SecurityFilterChain localSecurityFilterChain(HttpSecurity http,
			@Value("${app.security.local-user-id}") String userId,
			@Value("${app.security.local-role}") String role) throws Exception {
		OncePerRequestFilter localIdentity = new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws ServletException, IOException {
				String requestUserId = valueOrDefault(request.getHeader(LOCAL_USER_ID_HEADER), userId);
				String requestRole = valueOrDefault(request.getHeader(LOCAL_USER_ROLE_HEADER), role);
				var authentication = new UsernamePasswordAuthenticationToken(requestUserId, null,
						List.of(new SimpleGrantedAuthority(ROLE_PREFIX + requestRole)));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				chain.doFilter(request, response);
			}
		};
		return http.csrf(AbstractHttpConfigurer::disable)
				.addFilterBefore(localIdentity, AnonymousAuthenticationFilter.class)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/openapi/**", "/actuator/health").permitAll()
						.anyRequest().authenticated())
				.build();
	}

	private static String valueOrDefault(String value, String defaultValue) {
		return value == null || value.isBlank() ? defaultValue : value;
	}
}
