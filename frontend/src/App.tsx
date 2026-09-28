import { Alert, Box, CircularProgress, Container } from '@mui/material'
import { lazy, Suspense, type FormEvent, useEffect, useState } from 'react'
import { LoginView } from './components/auth/LoginView'
import { RequestState } from './components/common/RequestState'
import { AppHeader } from './components/layout/AppHeader'
import { WorkspaceHeading } from './components/layout/WorkspaceHeading'
import { CreateRequestForm } from './components/requests/CreateRequestForm'
import { RequestDetailView } from './components/requests/RequestDetailView'
import { RequestInbox } from './components/requests/RequestInbox'
import { INITIAL_INDICATOR_FILTERS, INITIAL_REQUEST_FILTERS } from './config/defaults'
import { useSession } from './hooks/useSession'
import { useWorkspaceData } from './hooks/useWorkspaceData'
import { solicitudesService } from './services/solicitudesService'
import type { CreateRequestInput, IndicatorFilters, RequestDetail, RequestFilters, Session, Status } from './interfaces'
import { pathFromView, type View, viewFromPath } from './utils/routing'
const IndicatorsApp = lazy(() => import('analytics/IndicatorsApp'))

const EMPTY_DRAFT: CreateRequestInput = {
  asunto: '',
  descripcion: '',
  categoriaId: '',
  prioridad: 'MEDIA',
}

function App() {
  const { session, setSession, clearSession } = useSession()
  if (!session) return <LoginView onLogin={setSession} />

  return <AuthenticatedApp session={session} setSession={setSession} clearSession={clearSession} />
}

interface AuthenticatedAppProps {
  session: Session
  setSession: (session: Session) => void
  clearSession: () => void
}

function AuthenticatedApp({ session, setSession, clearSession }: AuthenticatedAppProps) {
  const [view, setView] = useState<View>(viewFromPath)
  const [requestFilters, setRequestFilters] = useState<RequestFilters>(INITIAL_REQUEST_FILTERS)
  const [indicatorFilters, setIndicatorFilters] = useState<IndicatorFilters>(INITIAL_INDICATOR_FILTERS)
  const data = useWorkspaceData({ session, view, requestFilters, indicatorFilters })
  const [showCreate, setShowCreate] = useState(false)
  const [draft, setDraft] = useState<CreateRequestInput>(EMPTY_DRAFT)
  const [reason, setReason] = useState('')
  const [observation, setObservation] = useState('')
  const [actionBusy, setActionBusy] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)
  const [notice, setNotice] = useState<string | null>(null)

  useEffect(() => {
    const restoreRoute = () => setView(viewFromPath())
    window.addEventListener('popstate', restoreRoute)
    return () => window.removeEventListener('popstate', restoreRoute)
  }, [])

  const clearFeedback = () => {
    data.setError(null)
    setActionError(null)
    setNotice(null)
  }

  const navigate = (nextView: View) => {
    window.history.pushState({}, '', pathFromView(nextView))
    data.beginLoad()
    clearFeedback()
    setView(nextView)
  }

  const reload = () => {
    clearFeedback()
    data.reload()
  }

  const updateRequestFilters = (patch: Partial<RequestFilters>) => {
    clearFeedback()
    setRequestFilters((current) => ({ ...current, ...patch, page: patch.page ?? 0 }))
  }

  const updateIndicatorFilters = (nextFilters: IndicatorFilters) => {
    clearFeedback()
    setIndicatorFilters(nextFilters)
  }

  const createRequest = async (event: FormEvent) => {
    event.preventDefault()
    setActionBusy(true)
    clearFeedback()
    try {
      const result = await solicitudesService.create(session, draft)
      setShowCreate(false)
      setDraft(EMPTY_DRAFT)
      navigate({ kind: 'detail', id: result.data.id })
      setNotice('Solicitud registrada correctamente.')
    } catch (requestError) {
      setActionError(errorMessage(requestError, 'No fue posible registrar la solicitud'))
    } finally {
      setActionBusy(false)
    }
  }

  const runDetailCommand = async (
    command: () => Promise<{ data: RequestDetail; etag: string | null }>,
    successMessage: string,
  ) => {
    setActionBusy(true)
    clearFeedback()
    try {
      const result = await command()
      data.updateDetail(result.data, result.etag)
      setReason('')
      setNotice(successMessage)
    } catch (requestError) {
      setActionError(errorMessage(requestError, 'No fue posible completar la acción'))
    } finally {
      setActionBusy(false)
    }
  }

  const assignRequest = () => {
    const { detail, etag } = data
    if (!detail || !etag) return
    void runDetailCommand(
      () => solicitudesService.assign(session, detail.id, etag, reason || 'Inicio de atención'),
      'Solicitud asignada.',
    )
  }

  const transitionRequest = (status: Status) => {
    const { detail, etag } = data
    if (!detail || !etag) return
    void runDetailCommand(
      () => solicitudesService.transition(session, detail.id, etag, status, reason || 'Cambio de estado'),
      'Estado actualizado.',
    )
  }

  const addObservation = async () => {
    if (!data.detail || !data.etag || observation.trim().length < 3) return
    setActionBusy(true)
    clearFeedback()
    try {
      await solicitudesService.addObservation(session, data.detail.id, data.etag, observation.trim())
      await data.refreshDetail()
      setObservation('')
      setNotice('Observación agregada.')
    } catch (requestError) {
      setActionError(errorMessage(requestError, 'No fue posible agregar la observación'))
    } finally {
      setActionBusy(false)
    }
  }

  const hasBlockingState = data.loading || Boolean(actionError ?? data.error)

  return (
    <div className="app-shell">
      <AppHeader
        session={session}
        view={view}
        onNavigate={navigate}
        onSessionChange={(nextSession) => {
          clearFeedback()
          setSession(nextSession)
        }}
        onLogout={clearSession}
      />

      <Container component="main" maxWidth="xl" sx={{ py: { xs: 2, md: 4 } }}>
        <WorkspaceHeading
          view={view}
          role={session.role}
          onCreate={() => setShowCreate((value) => !value)}
          onReload={reload}
        />
        <RequestState loading={data.loading} error={actionError ?? data.error} notice={notice} onRetry={reload} />

        {!hasBlockingState && view.kind === 'inbox' && (
          <>
            {showCreate && (
              <CreateRequestForm
                categories={data.categories}
                draft={draft}
                busy={actionBusy}
                onDraftChange={setDraft}
                onCancel={() => setShowCreate(false)}
                onSubmit={createRequest}
              />
            )}
            <RequestInbox
              categories={data.categories}
              filters={requestFilters}
              page={data.page}
              role={session.role}
              onFiltersChange={updateRequestFilters}
              onOpenDetail={(id) => navigate({ kind: 'detail', id })}
            />
          </>
        )}

        {!hasBlockingState && view.kind === 'detail' && data.detail && (
          <RequestDetailView
            detail={data.detail}
            etag={data.etag}
            session={session}
            reason={reason}
            observation={observation}
            busy={actionBusy}
            onReasonChange={setReason}
            onObservationChange={setObservation}
            onBack={() => navigate({ kind: 'inbox' })}
            onAssign={assignRequest}
            onTransition={transitionRequest}
            onAddObservation={() => void addObservation()}
          />
        )}

        {!hasBlockingState && view.kind === 'indicators' && data.summary && data.trend && (
          <Suspense fallback={<Box sx={{ display: 'grid', placeItems: 'center', minHeight: 280 }}><CircularProgress aria-label="Cargando módulo analítico" /></Box>}>
            <IndicatorsApp
              categories={data.categories}
              filters={indicatorFilters}
              summary={data.summary}
              trend={data.trend}
              onFiltersChange={updateIndicatorFilters}
            />
          </Suspense>
        )}
        {!hasBlockingState && view.kind === 'detail' && !data.detail && <Alert severity="warning">No tienes autorización o la solicitud no está disponible.</Alert>}
      </Container>
    </div>
  )
}

function errorMessage(error: unknown, fallback: string) {
  return error instanceof Error ? error.message : fallback
}

export default App
