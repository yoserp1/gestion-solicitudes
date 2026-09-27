import {
  ArrowLeft,
  CalendarClock,
  CheckCircle2,
  MessageSquarePlus,
  RotateCcw,
  ShieldCheck,
  UserPlus,
} from 'lucide-react'
import type { RequestDetail, Session, Status } from '../../interfaces'
import { formatDateTime } from '../../utils/dateTime'

interface RequestDetailViewProps {
  detail: RequestDetail
  etag: string | null
  session: Session
  reason: string
  observation: string
  busy: boolean
  onReasonChange: (value: string) => void
  onObservationChange: (value: string) => void
  onBack: () => void
  onAssign: () => void
  onTransition: (status: Status) => void
  onAddObservation: () => void
}

export function RequestDetailView(props: RequestDetailViewProps) {
  const { detail, etag, session, reason, observation, busy } = props
  const canAssign = session.role === 'ANALISTA' && detail.estado === 'REGISTRADA'
  const canResolve = session.role === 'ANALISTA' && detail.estado === 'EN_ATENCION'
  const canSupervise = session.role === 'SUPERVISOR' && detail.estado === 'RESUELTA'
  const canObserve = session.role === 'ANALISTA' && detail.estado === 'EN_ATENCION'

  return (
    <section className="detail-layout">
      <button className="back-button" type="button" onClick={props.onBack}>
        <ArrowLeft size={17} /> Volver a la bandeja
      </button>
      <div className="detail-header">
        <div><span className="code">{detail.codigo}</span><h2>{detail.asunto}</h2></div>
        <span className="version"><ShieldCheck size={17} /> ETag {etag ?? `"${detail.version}"`}</span>
      </div>
      <div className="detail-grid">
        <div className="detail-main">
          <RequestDescription detail={detail} />
          {(canAssign || canResolve || canSupervise) && (
            <RequestActions
              busy={busy}
              canAssign={canAssign}
              canResolve={canResolve}
              canSupervise={canSupervise}
              reason={reason}
              onReasonChange={props.onReasonChange}
              onAssign={props.onAssign}
              onTransition={props.onTransition}
            />
          )}
          <RequestObservations
            detail={detail}
            canObserve={canObserve}
            observation={observation}
            busy={busy}
            onObservationChange={props.onObservationChange}
            onAdd={props.onAddObservation}
          />
        </div>
        <RequestTimeline detail={detail} />
      </div>
    </section>
  )
}

function RequestDescription({ detail }: { detail: RequestDetail }) {
  return (
    <article className="description-panel">
      <h3>Descripción</h3>
      <p>{detail.descripcion}</p>
      <dl className="metadata">
        <div><dt>Estado</dt><dd>{detail.estado.replace('_', ' ')}</dd></div>
        <div><dt>Prioridad</dt><dd>{detail.prioridad}</dd></div>
        <div><dt>Categoría</dt><dd>{detail.categoria.nombre}</dd></div>
        <div><dt>Solicitante</dt><dd>{detail.solicitante.id}</dd></div>
        <div><dt>Analista</dt><dd>{detail.analistaAsignado?.id ?? 'Sin asignar'}</dd></div>
        <div><dt>Actualización</dt><dd>{formatDateTime(detail.actualizadaEn)}</dd></div>
      </dl>
    </article>
  )
}

interface RequestActionsProps {
  busy: boolean
  canAssign: boolean
  canResolve: boolean
  canSupervise: boolean
  reason: string
  onReasonChange: (value: string) => void
  onAssign: () => void
  onTransition: (status: Status) => void
}

function RequestActions(props: RequestActionsProps) {
  return (
    <section className="action-panel">
      <h3>Atención</h3>
      <label>
        Motivo
        <input minLength={3} maxLength={500} value={props.reason} onChange={(event) => props.onReasonChange(event.target.value)} placeholder="Motivo de la acción" />
      </label>
      <div className="action-buttons">
        {props.canAssign && <button className="primary-button" disabled={props.busy} type="button" onClick={props.onAssign}><UserPlus size={17} /> Tomar solicitud</button>}
        {props.canResolve && <button className="primary-button" disabled={props.busy} type="button" onClick={() => props.onTransition('RESUELTA')}><CheckCircle2 size={17} /> Resolver</button>}
        {props.canSupervise && (
          <>
            <button className="secondary-button" disabled={props.busy} type="button" onClick={() => props.onTransition('EN_ATENCION')}><RotateCcw size={17} /> Devolver</button>
            <button className="primary-button" disabled={props.busy} type="button" onClick={() => props.onTransition('CERRADA')}><ShieldCheck size={17} /> Cerrar</button>
          </>
        )}
      </div>
    </section>
  )
}

interface RequestObservationsProps {
  detail: RequestDetail
  canObserve: boolean
  observation: string
  busy: boolean
  onObservationChange: (value: string) => void
  onAdd: () => void
}

function RequestObservations(props: RequestObservationsProps) {
  return (
    <section className="observations">
      <h3>Observaciones <span>{props.detail.observaciones.length}</span></h3>
      {props.canObserve && (
        <div className="observation-entry">
          <textarea rows={3} minLength={3} maxLength={2000} value={props.observation} onChange={(event) => props.onObservationChange(event.target.value)} placeholder="Agregar una observación" />
          <button className="secondary-button" type="button" disabled={props.busy || props.observation.trim().length < 3} onClick={props.onAdd}><MessageSquarePlus size={17} /> Agregar</button>
        </div>
      )}
      {props.detail.observaciones.length === 0
        ? <p className="muted">Sin observaciones.</p>
        : <ul>{[...props.detail.observaciones].reverse().map((item) => <li key={item.id}><p>{item.contenido}</p><small>{item.actor.id} · {formatDateTime(item.creadaEn)}</small></li>)}</ul>}
    </section>
  )
}

function RequestTimeline({ detail }: { detail: RequestDetail }) {
  return (
    <aside className="timeline-panel">
      <h3><CalendarClock size={18} /> Línea de tiempo</h3>
      <ol className="timeline">
        {[...detail.historial].reverse().map((entry) => (
          <li key={entry.id}>
            <i className={`status-dot status-${entry.estadoDestino.toLowerCase()}`} />
            <div><strong>{entry.estadoDestino.replace('_', ' ')}</strong><span>{entry.motivo}</span><small>{entry.actor.id} · {formatDateTime(entry.ocurridoEn)}</small></div>
          </li>
        ))}
      </ol>
    </aside>
  )
}
