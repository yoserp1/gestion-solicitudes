import { z } from 'zod'

const uuidSchema = z.string().regex(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i, 'Identificador inválido')

export const roleSchema = z.enum(['SOLICITANTE', 'ANALISTA', 'SUPERVISOR'])
export const prioritySchema = z.enum(['BAJA', 'MEDIA', 'ALTA'])
export const statusSchema = z.enum(['REGISTRADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA'])

export const categorySchema = z.object({
  id: uuidSchema,
  codigo: z.string(),
  nombre: z.string(),
  activa: z.boolean(),
})

const actorSchema = z.object({ id: z.string(), rol: roleSchema })

const requestSummarySchema = z.object({
  id: uuidSchema,
  codigo: z.string(),
  asunto: z.string(),
  categoria: categorySchema,
  prioridad: prioritySchema,
  estado: statusSchema,
  analistaAsignado: actorSchema.nullish(),
  creadaEn: z.string(),
  actualizadaEn: z.string(),
  version: z.number().int().nonnegative(),
})

export const requestDetailSchema = requestSummarySchema.extend({
  descripcion: z.string(),
  solicitante: actorSchema,
  historial: z.array(z.object({
    id: uuidSchema,
    estadoOrigen: statusSchema.nullish(),
    estadoDestino: statusSchema,
    actor: actorSchema,
    motivo: z.string(),
    ocurridoEn: z.string(),
  })),
  observaciones: z.array(z.object({
    id: uuidSchema,
    contenido: z.string(),
    actor: actorSchema,
    creadaEn: z.string(),
  })),
})

export const requestPageSchema = z.object({
  content: z.array(requestSummarySchema),
  page: z.number().int().nonnegative(),
  size: z.number().int().positive(),
  totalElements: z.number().int().nonnegative(),
  totalPages: z.number().int().nonnegative(),
})

const periodSchema = z.object({ desde: z.string(), hasta: z.string(), zonaHoraria: z.string() })
export const summaryIndicatorsSchema = z.object({
  periodo: periodSchema,
  total: z.number().int().nonnegative(),
  porEstado: z.array(z.object({ estado: statusSchema, cantidad: z.number().int().nonnegative() })),
  porCategoria: z.array(z.object({ categoriaId: uuidSchema, codigo: z.string(), nombre: z.string(), cantidad: z.number().int().nonnegative() })),
  actualizadoHasta: z.string(),
})
export const trendIndicatorsSchema = z.object({
  periodo: periodSchema,
  categoriaId: uuidSchema.nullish(),
  puntos: z.array(z.object({ fecha: z.string(), registradas: z.number().int().nonnegative(), resueltas: z.number().int().nonnegative(), cerradas: z.number().int().nonnegative() })),
  actualizadoHasta: z.string(),
})

export const observationSchema = z.object({
  id: uuidSchema,
  contenido: z.string(),
  actor: actorSchema,
  creadaEn: z.string(),
})

export const createRequestSchema = z.object({
  asunto: z.string().trim().min(5, 'El asunto debe tener al menos 5 caracteres').max(150),
  descripcion: z.string().trim().min(10, 'La descripción debe tener al menos 10 caracteres').max(2000),
  categoriaId: uuidSchema,
  prioridad: prioritySchema,
})

export const categoryListSchema = z.array(categorySchema)
