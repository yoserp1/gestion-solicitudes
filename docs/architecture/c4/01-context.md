# C4 Nivel 1 - Contexto del sistema

El sistema permite registrar y gestionar solicitudes, y consultar indicadores derivados de sus eventos.

```mermaid
C4Context
  title Contexto - Gestión de solicitudes e indicadores
  Person(solicitante, "Solicitante", "Registra y consulta sus solicitudes")
  Person(analista, "Analista", "Atiende solicitudes y consulta indicadores")
  Person(supervisor, "Supervisor", "Supervisa transiciones e indicadores")
  System(sistema, "Gestión de solicitudes e indicadores", "Gestiona el ciclo de vida operacional y una proyección analítica eventualmente consistente")
  System_Ext(idp, "Proveedor de identidad", "Emite tokens JWT fuera del perfil local")

  Rel(solicitante, sistema, "Registra y consulta solicitudes", "HTTPS/JSON")
  Rel(analista, sistema, "Atiende solicitudes y consulta indicadores", "HTTPS/JSON")
  Rel(supervisor, sistema, "Supervisa y consulta indicadores", "HTTPS/JSON")
  Rel(sistema, idp, "Valida identidad y roles", "OIDC/JWT")
```

## Notas

- En desarrollo local, las cabeceras `X-Local-User-Id` y `X-Local-User-Role` sustituyen al proveedor de identidad.
- La lectura analítica puede presentar un retraso breve respecto de la escritura operacional.
- La vista editable está en `diagrams/01-context.drawio`.
