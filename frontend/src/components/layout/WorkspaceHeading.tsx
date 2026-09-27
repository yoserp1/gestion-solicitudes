import { FilePlus2, RefreshCw } from 'lucide-react'
import type { Role } from '../../interfaces'
import type { View } from '../../utils/routing'

interface WorkspaceHeadingProps {
  view: View
  role: Role
  onCreate: () => void
  onReload: () => void
}

const COPY = {
  inbox: {
    eyebrow: 'SOLICITUDES',
    title: 'Bandeja de trabajo',
    subtitle: 'Crea, filtra y atiende casos desde una sola vista.',
  },
  detail: {
    eyebrow: 'SOLICITUDES',
    title: 'Detalle de solicitud',
    subtitle: 'Trazabilidad, atención y versión vigente.',
  },
  indicators: {
    eyebrow: 'INDICADORES',
    title: 'Indicadores operacionales',
    subtitle: 'Modelo de lectura actualizado por eventos.',
  },
} as const

export function WorkspaceHeading({ view, role, onCreate, onReload }: WorkspaceHeadingProps) {
  const copy = COPY[view.kind]

  return (
    <section className="workspace-heading">
      <div>
        <p className="eyebrow">OPERACIÓN / {copy.eyebrow}</p>
        <h1>{copy.title}</h1>
        <p className="subtitle">{copy.subtitle}</p>
      </div>
      <div className="heading-actions">
        {view.kind === 'inbox' && role === 'SOLICITANTE' && (
          <button className="primary-button" type="button" onClick={onCreate}>
            <FilePlus2 size={18} /> Nueva solicitud
          </button>
        )}
        <button className="icon-button" type="button" onClick={onReload} title="Actualizar">
          <RefreshCw size={19} aria-hidden="true" />
          <span className="sr-only">Actualizar</span>
        </button>
      </div>
    </section>
  )
}
