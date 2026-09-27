package com.yoserp1.prueba.indicadores.infrastructure.adapter.outbound.persistence.projection;

import java.util.UUID;

/** 
 * Expone los datos necesarios para calcular la tendencia diaria. 
 */
public interface TransicionIndicadorProjection {

	UUID getSolicitudId();

	String getEstado();

	String getOcurridoEn();
}