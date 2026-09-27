package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository;

import static com.yoserp1.prueba.solicitudes.application.SolicitudConstants.MENSAJE_SECUENCIA_SIN_VALOR;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.SolicitudEntity;

/** 
 * Repositorio para la entidad SolicitudEntity.
 * Proporciona códigos correlativos para las solicitudes.
 */
public interface SolicitudCodigoRepository extends Repository<SolicitudEntity, UUID> {

	/** 
	 * Obtiene el siguiente valor de la secuencia administrada por SQL Server.
	 * @return Siguiente valor de la secuencia.
	 */
	@Query(value = "SELECT NEXT VALUE FOR solicitud_codigo_seq", nativeQuery = true)
	Long obtenerSiguienteValor();

	/** 
	 * Obtiene el siguiente valor y valida la respuesta de la base de datos.
	 * @return Siguiente valor de la secuencia.
	 */
	default long siguienteValor() {
		Long valor = obtenerSiguienteValor();
		if (valor == null) {
			throw new IllegalStateException(MENSAJE_SECUENCIA_SIN_VALOR);
		}
		return valor;
	}
}
