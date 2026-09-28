# Frontend de solicitudes

Frontend React 19 y TypeScript compuesto por un shell y un microfrontend remoto mediante Module
Federation sobre Rspack. La interfaz usa MUI 7 con Emotion, Redux Toolkit conserva la sesión y Zod
valida sesión, formularios y respuestas HTTP.

## Aplicaciones

- **Shell** (`http://localhost:3000`): login local, bandeja, creación, detalle, acciones por rol y carga del remoto.
- **Analytics remote** (`http://localhost:3001`): resumen analítico, expuesto como `analytics/IndicatorsApp` y ejecutable standalone.
- **Storybook** (`http://localhost:6006`): estados representativos de `RequestState` y `RequestTable`.

React, React DOM, Emotion y MUI se comparten como singletons entre host y remoto. En la imagen Docker,
el shell se sirve en `/` y el remoto standalone en `/analytics-remote/`.

## Desarrollo

Con ambos backends disponibles en `8081` y `8082`:

```powershell
npm install
npm run dev
```

Comandos de calidad:

```powershell
npm test
npm run lint
npm run build
npm run storybook
npm run build-storybook
```

## Estructura

```text
analytics-remote/  # remoto Rspack, contrato expuesto y arranque standalone
.storybook/        # catálogo de componentes reutilizables
src/
├── components/    # vistas y controles presentacionales por feature
├── config/        # valores iniciales de filtros
├── hooks/         # coordinación de consultas y acceso a Redux
├── interfaces/    # contratos TypeScript por dominio
├── schemas/       # schemas Zod para fronteras externas
├── services/      # cliente HTTP y acceso a cada backend
├── store/         # Redux Toolkit y sesión persistida
└── utils/         # formato, navegación y validación
```

La sesión local se conserva en `localStorage`; una ruta `/solicitudes/{id}` puede recargarse y vuelve
a consultar la API. Las respuestas de detalle conservan el `ETag` vigente.
