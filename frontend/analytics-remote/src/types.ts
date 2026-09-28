export type Status = 'REGISTRADA' | 'EN_ATENCION' | 'RESUELTA' | 'CERRADA'

export interface Category {
  id: string
  codigo: string
  nombre: string
  activa: boolean
}

export interface IndicatorFilters {
  desde: string
  hasta: string
  categoriaId: string
}

export interface SummaryIndicators {
  periodo: { desde: string; hasta: string; zonaHoraria: string }
  total: number
  porEstado: Array<{ estado: Status; cantidad: number }>
  porCategoria: Array<{ categoriaId: string; codigo: string; nombre: string; cantidad: number }>
  actualizadoHasta: string
}

export interface TrendIndicators {
  periodo: { desde: string; hasta: string; zonaHoraria: string }
  categoriaId?: string | null
  puntos: Array<{ fecha: string; registradas: number; resueltas: number; cerradas: number }>
  actualizadoHasta: string
}

export interface IndicatorsAppProps {
  categories: Category[]
  filters: IndicatorFilters
  summary: SummaryIndicators
  trend: TrendIndicators
  onFiltersChange: (filters: IndicatorFilters) => void
}
