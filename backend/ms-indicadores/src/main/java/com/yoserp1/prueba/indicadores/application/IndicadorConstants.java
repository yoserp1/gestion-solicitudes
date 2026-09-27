package com.yoserp1.prueba.indicadores.application;

/** 
 * Centraliza códigos, mensajes y valores funcionales del módulo de indicadores. 
 */
public final class IndicadorConstants {

	public static final int MAX_RANGE_DAYS = 366;
	public static final int DEFAULT_RANGE_DAYS = 30;
	public static final String UTC_ZONE = "UTC";
	public static final String RANGO_FECHAS_INVALIDO = "RANGO_FECHAS_INVALIDO";
	public static final String ZONA_HORARIA_INVALIDA = "ZONA_HORARIA_INVALIDA";
	public static final String PARAMETROS_INVALIDOS = "PARAMETROS_INVALIDOS";
	public static final String ERROR_INTERNO = "ERROR_INTERNO";
	public static final String MENSAJE_RANGO_INVALIDO = "El rango debe ser consistente y no superar 366 días";
	public static final String MENSAJE_ZONA_INVALIDA = "La zona horaria no es válida";
	public static final String MENSAJE_PARAMETROS_INVALIDOS = "Los parámetros informados no son válidos";
	public static final String MENSAJE_ERROR_INTERNO = "Ocurrió un error interno";

	private IndicadorConstants() {
	}
}
