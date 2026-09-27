package com.yoserp1.prueba.indicadores.infrastructure.adapter.inbound.rest;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que asegura que cada solicitud HTTP tenga un identificador de correlación único.
 * Si la solicitud no incluye un encabezado de correlación, se genera uno nuevo.
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
	public static final String HEADER = IndicadorApiConstants.CORRELATION_HEADER;
	public static final String ATTRIBUTE = CorrelationIdFilter.class.getName() + ".id";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String value = request.getHeader(HEADER);
		try {
			value = value == null ? UUID.randomUUID().toString() : UUID.fromString(value).toString();
		} catch (IllegalArgumentException exception) {
			value = UUID.randomUUID().toString();
		}
		request.setAttribute(ATTRIBUTE, value);
		response.setHeader(HEADER, value);
		filterChain.doFilter(request, response);
	}
}