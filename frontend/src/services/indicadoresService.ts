import type { IndicatorFilters, Session, SummaryIndicators, TrendIndicators } from '../interfaces'
import { requestJson } from './httpClient'

export const indicadoresService = {
  getSummary: (session: Session, filters: IndicatorFilters, signal?: AbortSignal) =>
    requestJson<SummaryIndicators>(`/api/v1/indicadores/resumen?${indicatorParams(filters)}`, { session, signal }),

  getTrend: (session: Session, filters: IndicatorFilters, signal?: AbortSignal) => {
    const params = indicatorParams(filters)
    params.set('zonaHoraria', 'America/Santiago')
    return requestJson<TrendIndicators>(`/api/v1/indicadores/tendencia?${params}`, { session, signal })
  },
}

function indicatorParams(filters: IndicatorFilters) {
  const params = new URLSearchParams({ desde: filters.desde, hasta: filters.hasta })
  if (filters.categoriaId) params.set('categoriaId', filters.categoriaId)
  return params
}
