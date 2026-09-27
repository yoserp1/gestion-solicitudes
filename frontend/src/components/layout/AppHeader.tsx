import { BarChart3, CircleUserRound, FileText, Inbox } from 'lucide-react'
import type { Role, Session } from '../../interfaces'
import type { View } from '../../utils/routing'

interface AppHeaderProps {
  session: Session
  view: View
  onNavigate: (view: View) => void
  onSessionChange: (session: Session) => void
}

export function AppHeader({ session, view, onNavigate, onSessionChange }: AppHeaderProps) {
  return (
    <header className="topbar">
      <button className="brand" type="button" onClick={() => onNavigate({ kind: 'inbox' })} aria-label="Ir a solicitudes">
        <span className="brand-mark"><FileText size={19} /></span>
        <span>TRÁMITE</span>
      </button>

      <nav className="primary-nav" aria-label="Navegación principal">
        <button className={view.kind !== 'indicators' ? 'active' : ''} type="button" onClick={() => onNavigate({ kind: 'inbox' })}>
          <Inbox size={17} /> Solicitudes
        </button>
        <button className={view.kind === 'indicators' ? 'active' : ''} type="button" onClick={() => onNavigate({ kind: 'indicators' })}>
          <BarChart3 size={17} /> Indicadores
        </button>
      </nav>

      <div className="identity" aria-label="Identidad local">
        <CircleUserRound size={18} aria-hidden="true" />
        <label>
          <span className="sr-only">Identificador de usuario</span>
          <input
            value={session.userId}
            onChange={(event) => onSessionChange({ ...session, userId: event.target.value })}
            maxLength={100}
          />
        </label>
        <label>
          <span className="sr-only">Rol</span>
          <select
            value={session.role}
            onChange={(event) => onSessionChange({ ...session, role: event.target.value as Role })}
          >
            <option>SOLICITANTE</option>
            <option>ANALISTA</option>
            <option>SUPERVISOR</option>
          </select>
        </label>
      </div>
    </header>
  )
}
