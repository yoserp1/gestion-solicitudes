package com.yoserp1.prueba.solicitudes.application.security;

import com.yoserp1.prueba.solicitudes.domain.model.RolActor;

/**
 * Representa el contexto de un actor en el sistema, incluyendo su identificador y rol.
 */
public record ActorContext(String id, RolActor rol) {
}
