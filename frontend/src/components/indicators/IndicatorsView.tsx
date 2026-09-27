import type { Category, IndicatorFilters, SummaryIndicators, TrendIndicators } from '../../interfaces'
import { formatDateTime, formatShortDate } from '../../utils/dateTime'

interface IndicatorsViewProps {
  categories: Category[]
  filters: IndicatorFilters
  summary: SummaryIndicators
  trend: TrendIndicators
  onFiltersChange: (filters: IndicatorFilters) => void
}

export function IndicatorsView({ categories, filters, summary, trend, onFiltersChange }: IndicatorsViewProps) {
  const maxDaily = Math.max(1, ...trend.puntos.map((point) => point.registradas + point.resueltas + point.cerradas))

  return (
    <section className="indicators-view">
      <div className="filters indicator-filters">
        <label>Desde<input type="date" value={filters.desde} onChange={(event) => onFiltersChange({ ...filters, desde: event.target.value })} /></label>
        <label>Hasta<input type="date" value={filters.hasta} onChange={(event) => onFiltersChange({ ...filters, hasta: event.target.value })} /></label>
        <label>
          Categoría
          <select value={filters.categoriaId} onChange={(event) => onFiltersChange({ ...filters, categoriaId: event.target.value })}>
            <option value="">Todas</option>
            {categories.map((category) => <option key={category.id} value={category.id}>{category.nombre}</option>)}
          </select>
        </label>
      </div>

      <div className="metric-grid">
        <article className="metric-total"><span>Total del período</span><strong>{summary.total}</strong><small>Actualizado {formatDateTime(summary.actualizadoHasta)}</small></article>
        {summary.porEstado.map((item) => <article key={item.estado}><span><i className={`status-dot status-${item.estado.toLowerCase()}`} />{item.estado.replace('_', ' ')}</span><strong>{item.cantidad}</strong></article>)}
      </div>

      <div className="analytics-grid">
        <section>
          <SectionTitle eyebrow="DISTRIBUCIÓN" title="Por categoría" />
          <div className="category-bars">
            {summary.porCategoria.map((item) => (
              <div key={item.categoriaId}>
                <span>{item.nombre}</span>
                <div><i style={{ width: `${summary.total === 0 ? 0 : (item.cantidad / summary.total) * 100}%` }} /></div>
                <strong>{item.cantidad}</strong>
              </div>
            ))}
          </div>
        </section>
        <section>
          <SectionTitle eyebrow="ÚLTIMOS DÍAS" title="Tendencia" />
          <div className="trend-list">
            {trend.puntos.slice(-14).map((point) => {
              const total = point.registradas + point.resueltas + point.cerradas
              return (
                <div key={point.fecha}>
                  <time>{formatShortDate(point.fecha)}</time>
                  <div className="trend-track"><i style={{ width: `${(total / maxDaily) * 100}%` }} /></div>
                  <strong>{total}</strong>
                </div>
              )
            })}
          </div>
        </section>
      </div>
    </section>
  )
}

function SectionTitle({ eyebrow, title }: { eyebrow: string; title: string }) {
  return <div className="section-title"><div><span className="eyebrow">{eyebrow}</span><h2>{title}</h2></div></div>
}
