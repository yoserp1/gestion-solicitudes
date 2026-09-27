# Secuencia - Registro y proyección de una solicitud

```mermaid
sequenceDiagram
  autonumber
  actor Usuario
  participant Solicitudes as API solicitudes
  participant Operacional as SQL Server / solicitudes_db
  participant Publisher as Publicador Outbox
  participant Kafka
  participant Indicadores as Consumidor indicadores
  participant Analitica as SQL Server / indicadores_db

  Usuario->>Solicitudes: POST /api/v1/solicitudes
  Solicitudes->>Operacional: BEGIN
  Solicitudes->>Operacional: INSERT solicitud e historial
  Solicitudes->>Operacional: INSERT outbox_evento
  Solicitudes->>Operacional: COMMIT
  Solicitudes-->>Usuario: 201 Created + ETag

  loop Cada segundo
    Publisher->>Operacional: Buscar eventos no publicados
    Publisher->>Kafka: Publicar con solicitudId como clave
    Kafka-->>Publisher: Confirmación
    Publisher->>Operacional: Marcar publicado_en
  end

  Kafka->>Indicadores: SolicitudRegistrada v1
  Indicadores->>Analitica: BEGIN
  Indicadores->>Analitica: Registrar eventId si no existe
  alt Evento nuevo
    Indicadores->>Analitica: Crear proyección y transición
  else Evento duplicado
    Indicadores->>Indicadores: Ignorar sin efectos adicionales
  end
  Indicadores->>Analitica: COMMIT
```

Si la publicación falla, el registro permanece pendiente y se reintenta. Si el proceso cae después de publicar y antes de marcar el Outbox, Kafka puede entregar un duplicado; la deduplicación persistente evita aplicar dos veces el mismo evento.
