# C4 Nivel 2 - Contenedores

```mermaid
C4Container
  title Contenedores - Gestión de solicitudes e indicadores
  Person(usuario, "Usuario", "Solicitante, analista o supervisor")
  System_Ext(idp, "Proveedor de identidad", "Emite tokens JWT")

  System_Boundary(sistema, "Gestión de solicitudes e indicadores") {
    Container(apiSolicitudes, "API de solicitudes", "Java 21 / Spring Boot", "Gestiona solicitudes, idempotencia, concurrencia y Outbox")
    Container(apiIndicadores, "API de indicadores", "Java 21 / Spring Boot", "Consume eventos y expone lecturas analíticas")
    ContainerQueue(kafka, "Broker de eventos", "Apache Kafka", "Distribuye eventos del tópico solicitudes.v1")
    ContainerDb(dbSolicitudes, "Base operacional", "SQL Server", "Solicitudes, historial, idempotencia y Outbox")
    ContainerDb(dbIndicadores, "Base analítica", "SQL Server", "Proyecciones y eventos procesados")
  }

  Rel(usuario, apiSolicitudes, "Gestiona solicitudes", "HTTP/JSON")
  Rel(usuario, apiIndicadores, "Consulta indicadores", "HTTP/JSON")
  Rel(apiSolicitudes, idp, "Valida JWT", "OIDC")
  Rel(apiIndicadores, idp, "Valida JWT", "OIDC")
  Rel(apiSolicitudes, dbSolicitudes, "Lee y escribe", "JDBC")
  Rel(apiSolicitudes, kafka, "Publica eventos desde Outbox", "Kafka")
  Rel(kafka, apiIndicadores, "Entrega eventos", "Kafka")
  Rel(apiIndicadores, dbIndicadores, "Actualiza y consulta proyecciones", "JDBC")
```

## Decisiones relevantes

- Cada servicio es dueño de su esquema y ejecuta migraciones Flyway al arrancar.
- La clave Kafka es `solicitudId`, por lo que se conserva el orden dentro de cada agregado.
- La entrega es al menos una vez y el consumidor deduplica mediante `evento_procesado`.
- La vista editable está en `diagrams/02-containers.drawio`.
