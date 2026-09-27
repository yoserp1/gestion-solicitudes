import { CheckCircle2 } from 'lucide-react'

interface RequestStateProps {
  loading: boolean
  error: string | null
  notice: string | null
  onRetry: () => void
}

export function RequestState({ loading, error, notice, onRetry }: RequestStateProps) {
  return (
    <>
      {notice && <div className="notice" role="status"><CheckCircle2 size={18} /> {notice}</div>}
      {loading && <div className="state-message" role="status"><span className="loader" /> Consultando información...</div>}
      {!loading && error && (
        <div className="error-state" role="alert">
          <strong>No fue posible completar la operación</strong>
          <span>{error}</span>
          <button type="button" onClick={onRetry}>Reintentar</button>
        </div>
      )}
    </>
  )
}
