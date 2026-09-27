package com.yoserp1.prueba.solicitudes.application.security;

import static com.yoserp1.prueba.solicitudes.application.SolicitudConstants.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.yoserp1.prueba.solicitudes.domain.model.RolActor;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.inbound.rest.ApiException;

/**
 * Proporciona el contexto del actor actual autenticado en el sistema.
 */
@Component
public class ActorProvider {

	/**
	 * Obtiene el contexto del actor actual autenticado.
	 *
	 * @return El contexto del actor actual.
	 * @throws ApiException Si no hay un actor autenticado o si el rol del actor no es válido.
	 */
	public ActorContext actual() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, AUTENTICACION_REQUERIDA, MENSAJE_AUTENTICACION_REQUERIDA);
		}
		RolActor rol = authentication.getAuthorities().stream()
				.map(authority -> authority.getAuthority())
				.filter(authority -> authority.startsWith("ROLE_"))
				.map(authority -> authority.substring(5))
				.map(this::rolValido)
				.findFirst()
				.orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, ROL_NO_AUTORIZADO,
						MENSAJE_ROL_NO_AUTORIZADO));
		return new ActorContext(authentication.getName(), rol);
	}

	/**
	 * Valida y convierte un valor de rol en un objeto RolActor.
	 *
	 * @param valor El valor del rol a validar.
	 * @return El objeto RolActor correspondiente al valor proporcionado.
	 * @throws ApiException Si el valor del rol no es válido.
	 */
	private RolActor rolValido(String valor) {
		try {
			return RolActor.valueOf(valor);
		} catch (IllegalArgumentException exception) {
			throw new ApiException(HttpStatus.FORBIDDEN, ROL_NO_AUTORIZADO, MENSAJE_ROL_NO_AUTORIZADO);
		}
	}
}
