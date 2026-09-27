# ADR-001 - Integración asíncrona mediante Outbox y proyección CQRS

- Estado: Aceptado
- Fecha: 2026-09-27
- Alcance: Solución
- Responsable: Responsable de la solución

## Contexto

La escritura operacional y los indicadores tienen cargas y modelos diferentes. La creación o transición de una solicitud debe conservarse aunque el servicio analítico esté temporalmente indisponible, sin usar una transacción distribuida entre base de datos y broker.

## Decisión

`ms-solicitudes` persiste el cambio de negocio y un evento en una tabla Outbox dentro de la misma transacción. Un publicador entrega los eventos pendientes a `solicitudes.v1`. `ms-indicadores` mantiene una proyección de lectura independiente y deduplica por `eventId`.

La entrega es al menos una vez. Los eventos `SolicitudRegistrada`, `SolicitudTomada`,
`SolicitudResuelta` y `SolicitudCerrada` usan contratos JSON Schema versionados y
`aggregateId` como clave de partición.

## Alternativas consideradas

| Alternativa | Motivo de descarte |
|---|---|
| Consulta síncrona desde indicadores | Acopla disponibilidad y latencia de ambos servicios. |
| Base de datos compartida | Rompe la propiedad de datos y acopla el modelo analítico al operacional. |
| Publicación directa durante la transacción | Puede confirmar la base y fallar al publicar, o publicar antes de un rollback. |

## Consecuencias

Positivas: desacoplamiento temporal, recuperación tras fallas, modelo de lectura optimizado y trazabilidad del evento.

Negativas: consistencia eventual, operación adicional de Kafka, necesidad de idempotencia y posibilidad de eventos pendientes o mensajes no procesables.

## Riesgos y controles

- Eventos duplicados: deduplicación transaccional mediante `evento_procesado`.
- Pérdida de orden global: orden solo por `solicitudId`; los consumidores no deben asumir orden entre solicitudes.
- Evento inválido: el consumidor rechaza versiones y tipos desconocidos. Una DLQ durable queda como mejora antes de producción.
- Crecimiento de Outbox: requiere política de retención y monitoreo fuera del alcance local.

## Criterios de revisión

Revisar si cambia el volumen, se incorporan nuevos consumidores, se requiere consistencia inmediata o se adopta una plataforma de eventos administrada.
