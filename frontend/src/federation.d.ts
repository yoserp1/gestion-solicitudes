declare module 'analytics/IndicatorsApp' {
  import type { ComponentType } from 'react'
  import type { Category, IndicatorFilters, SummaryIndicators, TrendIndicators } from './interfaces'

  interface IndicatorsAppProps {
    categories: Category[]
    filters: IndicatorFilters
    summary: SummaryIndicators
    trend: TrendIndicators
    onFiltersChange: (filters: IndicatorFilters) => void
  }

  const IndicatorsApp: ComponentType<IndicatorsAppProps>
  export default IndicatorsApp
}
