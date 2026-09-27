# Contratos de eventos

Los eventos se publican como JSON UTF-8 en el tópico `solicitudes.v1`, usando `aggregateId`
como clave de partición para conservar el orden por agregado.

| Evento | Productor | Consumidor | Contrato |
|---|---|---|---|
| `SolicitudRegistrada` | `ms-solicitudes` | `ms-indicadores` | `solicitud-registrada-v1.schema.json` |
| `SolicitudTomada` | `ms-solicitudes` | `ms-indicadores` | `solicitud-tomada-v1.schema.json` |
| `SolicitudResuelta` | `ms-solicitudes` | `ms-indicadores` | `solicitud-resuelta-v1.schema.json` |
| `SolicitudCerrada` | `ms-solicitudes` | `ms-indicadores` | `solicitud-cerrada-v1.schema.json` |

Todos los eventos incluyen el envelope `eventId`, `occurredAt`, `aggregateId`, `type`,
`version` y `correlationId`. `version` corresponde a la versión del agregado y permite
ignorar actualizaciones atrasadas en la proyección.

La entrega es al menos una vez. `ms-indicadores` registra cada `eventId` en
`evento_procesado` dentro de la misma transacción que modifica la proyección. Los campos
`descripcion` y `asunto` no se publican porque no son necesarios para los indicadores.

Los cambios compatibles agregan campos opcionales. Un cambio incompatible requiere una nueva
versión del archivo de contrato.