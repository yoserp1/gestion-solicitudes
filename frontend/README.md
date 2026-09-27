# Frontend de solicitudes

Aplicación React 19 + TypeScript + Vite para consultar la bandeja y el detalle de solicitudes.

La sesión local se conserva en `localStorage`; una ruta `/solicitudes/{id}` se puede recargar y
vuelve a consultar la API. Las respuestas de detalle conservan el `ETag` vigente.

```powershell
npm install
npm run dev
```

Vite publica en `http://localhost:5173`; redirige solicitudes a `http://localhost:8081`
e indicadores a `http://localhost:8082`.

## Estructura

```text
src/
├── components/   # Vistas y controles presentacionales por feature
├── config/       # Valores iniciales de filtros
├── hooks/        # Sesión y coordinación de consultas
├── interfaces/   # Contratos TypeScript por dominio
├── services/     # Cliente HTTP y acceso a cada backend
└── utils/        # Formato de fechas y navegación
```

`App.tsx` compone estas piezas y conserva únicamente la coordinación de filtros,
navegación y comandos de usuario. El acceso HTTP reutilizable vive en `services/httpClient.ts`.
