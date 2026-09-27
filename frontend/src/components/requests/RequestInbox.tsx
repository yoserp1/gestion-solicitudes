import { ChevronLeft, ChevronRight, Inbox } from 'lucide-react'
import type { Category, Priority, RequestFilters, RequestPage, Status } from '../../interfaces'
import { RequestTable } from './RequestTable'

interface RequestInboxProps {
  categories: Category[]
  filters: RequestFilters
  page: RequestPage
  role: string
  onFiltersChange: (filters: Partial<RequestFilters>) => void
  onOpenDetail: (id: string) => void
}

export function RequestInbox({ categories, filters, page, role, onFiltersChange, onOpenDetail }: RequestInboxProps) {
  return (
    <section className="inbox" aria-label="Bandeja de solicitudes">
      <div className="filters">
        <label>
          Estado
          <select value={filters.estado} onChange={(event) => onFiltersChange({ estado: event.target.value as Status | '' })}>
            <option value="">Todos</option><option>REGISTRADA</option><option>EN_ATENCION</option><option>RESUELTA</option><option>CERRADA</option>
          </select>
        </label>
        <label>
          Categoría
          <select value={filters.categoriaId} onChange={(event) => onFiltersChange({ categoriaId: event.target.value })}>
            <option value="">Todas</option>
            {categories.map((category) => <option key={category.id} value={category.id}>{category.nombre}</option>)}
          </select>
        </label>
        <label>
          Prioridad
          <select value={filters.prioridad} onChange={(event) => onFiltersChange({ prioridad: event.target.value as Priority | '' })}>
            <option value="">Todas</option><option>BAJA</option><option>MEDIA</option><option>ALTA</option>
          </select>
        </label>
      </div>
      <div className="inbox-summary">
        <span><Inbox size={17} /> {page.totalElements} solicitudes</span>
        <span>Sesión local: {role}</span>
      </div>
      {page.content.length === 0
        ? <div className="empty-state">No hay solicitudes para los filtros seleccionados.</div>
        : <RequestTable page={page} onOpenDetail={onOpenDetail} />}
      <div className="pagination">
        <button type="button" disabled={page.page === 0} onClick={() => onFiltersChange({ page: page.page - 1 })}>
          <ChevronLeft size={17} /> Anterior
        </button>
        <span>Página {page.totalPages === 0 ? 0 : page.page + 1} de {page.totalPages}</span>
        <button type="button" disabled={page.page + 1 >= page.totalPages} onClick={() => onFiltersChange({ page: page.page + 1 })}>
          Siguiente <ChevronRight size={17} />
        </button>
      </div>
    </section>
  )
}
