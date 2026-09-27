import type { Status } from './solicitudes'

interface Period {
  desde: string
  hasta: string
  zonaHoraria: string
}

export interface SummaryIndicators {
  periodo: Period
  total: number
  porEstado: Array<{ estado: Status; cantidad: number }>
  porCategoria: Array<{ categoriaId: string; codigo: string; nombre: string; cantidad: number }>
  actualizadoHasta: string
}

export interface TrendPoint {
  fecha: string
  registradas: number
  resueltas: number
  cerradas: number
}

export interface TrendIndicators {
  periodo: Period
  categoriaId?: string | null
  puntos: TrendPoint[]
  actualizadoHasta: string
}

export interface IndicatorFilters {
  desde: string
  hasta: string
  categoriaId: string
}
