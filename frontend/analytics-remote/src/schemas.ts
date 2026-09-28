import { z } from 'zod'

const uuidSchema = z.string().regex(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)

const periodSchema = z.object({
  desde: z.string(),
  hasta: z.string(),
  zonaHoraria: z.string(),
})

const statusSchema = z.enum(['REGISTRADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA'])

export const categoryListSchema = z.array(z.object({
  id: uuidSchema,
  codigo: z.string(),
  nombre: z.string(),
  activa: z.boolean(),
}))

export const summarySchema = z.object({
  periodo: periodSchema,
  total: z.number().int().nonnegative(),
  porEstado: z.array(z.object({ estado: statusSchema, cantidad: z.number().int().nonnegative() })),
  porCategoria: z.array(z.object({
    categoriaId: uuidSchema,
    codigo: z.string(),
    nombre: z.string(),
    cantidad: z.number().int().nonnegative(),
  })),
  actualizadoHasta: z.string(),
})

export const trendSchema = z.object({
  periodo: periodSchema,
  categoriaId: uuidSchema.nullable().optional(),
  puntos: z.array(z.object({
    fecha: z.string(),
    registradas: z.number().int().nonnegative(),
    resueltas: z.number().int().nonnegative(),
    cerradas: z.number().int().nonnegative(),
  })),
  actualizadoHasta: z.string(),
})
