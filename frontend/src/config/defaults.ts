import type { IndicatorFilters, RequestFilters } from '../interfaces'

export const INITIAL_REQUEST_FILTERS: RequestFilters = {
  estado: '',
  categoriaId: '',
  prioridad: '',
  page: 0,
  size: 10,
}

const TODAY = new Date()
const MONTH_AGO = new Date(TODAY.getTime() - 30 * 24 * 60 * 60 * 1000)

export const INITIAL_INDICATOR_FILTERS: IndicatorFilters = {
  desde: MONTH_AGO.toISOString().slice(0, 10),
  hasta: TODAY.toISOString().slice(0, 10),
  categoriaId: '',
}
