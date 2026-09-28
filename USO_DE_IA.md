# Uso de inteligencia artificial

## Herramienta utilizada

- GitHub CopilotCLI.

## Actividades apoyadas

- Revisión de completitud de los entregables.
- Propuesta y generación inicial de Dockerfiles y Docker Compose.
- Implementación asistida del publicador Outbox y del consumidor Kafka idempotente.
- Redacción inicial de contratos JSON Schema, diagramas C4, secuencia, ADR y documentación operativa.
- Revisión de coherencia entre configuración, código, contratos y README.

## Verificaciones realizadas

- Construcción de las dos imágenes mediante `docker compose build`.
- Validación de `compose.yaml` mediante `docker compose config`.
- Compilación y ejecución de pruebas Maven en ambos microservicios.
- Validación sintáctica de los documentos JSON y XML draw.io.
- Inspección del código generado antes de incorporarlo como parte de la solución.

## Decisiones de la solución

- Se conservó la separación entre escritura operacional y lectura analítica.
- Se eligió Outbox para evitar publicación no coordinada con la transacción de negocio.
- Se definió entrega al menos una vez y deduplicación persistente por `eventId`.
- Se minimizó el payload de eventos, excluyendo asunto y descripción.
- Se mantuvo autenticación simplificada solo para el perfil local.

## Responsabilidad y revisión

La IA se utilizó como herramienta de apoyo. El responsable de la entrega debe revisar, comprender y
validar el código y la documentación antes de publicar el repositorio o presentarlo al equipo evaluador.
No se incorporaron credenciales productivas ni datos personales reales en los artefactos generados.