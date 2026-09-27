import { ChevronRight } from 'lucide-react'
import type { RequestPage } from '../../interfaces'
import { formatDateTime } from '../../utils/dateTime'

interface RequestTableProps {
  page: RequestPage
  onOpenDetail: (id: string) => void
}

export function RequestTable({ page, onOpenDetail }: RequestTableProps) {
  return (
    <div className="request-table">
      <div className="table-row table-head" aria-hidden="true">
        <span>Código / asunto</span>
        <span>Categoría</span>
        <span>Prioridad</span>
        <span>Estado</span>
        <span>Actualización</span>
        <span />
      </div>
      {page.content.map((request) => (
        <button
          className="table-row request-row"
          type="button"
          key={request.id}
          onClick={() => onOpenDetail(request.id)}
        >
          <span className="request-title">
            <strong>{request.codigo}</strong>
            <small>{request.asunto}</small>
          </span>
          <span>{request.categoria.nombre}</span>
          <span className={`priority priority-${request.prioridad.toLowerCase()}`}>{request.prioridad}</span>
          <span>
            <i className={`status-dot status-${request.estado.toLowerCase()}`} />
            {request.estado.replace('_', ' ')}
          </span>
          <span>{formatDateTime(request.actualizadaEn)}</span>
          <ChevronRight size={18} aria-hidden="true" />
        </button>
      ))}
    </div>
  )
}
