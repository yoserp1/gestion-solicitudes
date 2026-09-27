package com.yoserp1.prueba.solicitudes.application;

import java.util.Map;

/** 
 * Centraliza códigos, mensajes y valores funcionales del módulo de solicitudes. 
 */
public final class SolicitudConstants {

	public static final String AUTENTICACION_REQUERIDA = "AUTENTICACION_REQUERIDA";
	public static final String ROL_NO_AUTORIZADO = "ROL_NO_AUTORIZADO";
	public static final String CATEGORIA_INVALIDA = "CATEGORIA_INVALIDA";
	public static final String ORDEN_INVALIDO = "ORDEN_INVALIDO";
	public static final String SOLICITUD_NO_DISPONIBLE = "SOLICITUD_NO_DISPONIBLE";
	public static final String SOLICITUD_NO_ASIGNADA = "SOLICITUD_NO_ASIGNADA";
	public static final String SOLICITUD_TRANSICION_INVALIDA = "SOLICITUD_TRANSICION_INVALIDA";
	public static final String IF_MATCH_REQUERIDO = "IF_MATCH_REQUERIDO";
	public static final String IF_MATCH_INVALIDO = "IF_MATCH_INVALIDO";
	public static final String VERSION_NO_COINCIDE = "VERSION_NO_COINCIDE";
	public static final String SOLICITUD_NO_ENCONTRADA = "SOLICITUD_NO_ENCONTRADA";
	public static final String IDEMPOTENCY_KEY_REUTILIZADA = "IDEMPOTENCY_KEY_REUTILIZADA";
	public static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";
	public static final String ERROR_INTERNO = "ERROR_INTERNO";

	public static final String MENSAJE_AUTENTICACION_REQUERIDA = "Se requiere autenticación";
	public static final String MENSAJE_ROL_NO_AUTORIZADO = "El rol no está autorizado para esta operación";
	public static final String MENSAJE_CATEGORIA_INVALIDA = "La categoría no existe o está inactiva";
	public static final String MENSAJE_ORDEN_INVALIDO = "El criterio de orden no es válido";
	public static final String MENSAJE_SOLICITUD_NO_DISPONIBLE = "La solicitud ya no está disponible para asignación";
	public static final String MENSAJE_SOLICITUD_NO_ASIGNADA = "La solicitud no está asignada al analista autenticado";
	public static final String MENSAJE_TRANSICION_INVALIDA = "La transición de estado no está permitida";
	public static final String MENSAJE_IF_MATCH_REQUERIDO = "Se requiere If-Match";
	public static final String MENSAJE_IF_MATCH_INVALIDO = "If-Match no tiene un formato válido";
	public static final String MENSAJE_VERSION_NO_COINCIDE = "El recurso fue modificado por otra operación";
	public static final String MENSAJE_SOLICITUD_NO_ENCONTRADA = "La solicitud no existe";
	public static final String MENSAJE_IDEMPOTENCIA_REUTILIZADA = "La clave de idempotencia fue utilizada con otro comando";
	public static final String MENSAJE_SOLICITUD_INVALIDA = "La solicitud contiene datos inválidos";
	public static final String MENSAJE_ERROR_INTERNO = "Ocurrió un error interno";
	public static final String MENSAJE_SECUENCIA_SIN_VALOR = "La secuencia de solicitudes no devolvió un valor";
	public static final String MENSAJE_HASH_NO_CALCULADO = "No fue posible calcular el hash del comando";
	public static final String MENSAJE_EVENTO_NO_SERIALIZADO = "No fue posible serializar el evento";

	public static final String OPERACION_REGISTRAR = "REGISTRAR_SOLICITUD";
	public static final String OPERACION_TOMAR = "TOMAR_SOLICITUD";
	public static final String OPERACION_AGREGAR_OBSERVACION = "AGREGAR_OBSERVACION";
	public static final String OPERACION_CAMBIAR_ESTADO = "CAMBIAR_ESTADO";
	public static final String EVENTO_SOLICITUD_REGISTRADA = "SolicitudRegistrada";
	public static final String EVENTO_SOLICITUD_TOMADA = "SolicitudTomada";
	public static final String EVENTO_SOLICITUD_RESUELTA = "SolicitudResuelta";
	public static final String EVENTO_SOLICITUD_CERRADA = "SolicitudCerrada";
	public static final String MOTIVO_SOLICITUD_REGISTRADA = "Solicitud registrada";
	public static final String MOTIVO_INICIO_ATENCION = "Inicio de atención";
	public static final String FORMATO_CODIGO = "SOL-%d-%06d";
	public static final String ALGORITMO_HASH = "SHA-256";
	public static final String IF_MATCH_PATTERN = "\"[0-9]+\"";
	public static final String ORDEN_PREDETERMINADO = "creadaEn,desc";
	public static final Map<String, String> CAMPOS_ORDEN = Map.of(
			"creadaEn", "creadaEn", "actualizadaEn", "actualizadaEn", "prioridad", "prioridad");

	private SolicitudConstants() {
	}
}
