import ArrowBack from '@mui/icons-material/ArrowBack'
import AssignmentInd from '@mui/icons-material/AssignmentInd'
import CheckCircle from '@mui/icons-material/CheckCircle'
import History from '@mui/icons-material/History'
import Message from '@mui/icons-material/Message'
import Replay from '@mui/icons-material/Replay'
import VerifiedUser from '@mui/icons-material/VerifiedUser'
import { Box, Button, Chip, Divider, List, ListItem, ListItemText, Paper, Stack, TextField, Typography } from '@mui/material'
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
    <Box component="section">
      <Button onClick={props.onBack} startIcon={<ArrowBack />} sx={{ mb: 2 }}>Volver a la bandeja</Button>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ xs: 'flex-start', sm: 'center' }} spacing={1} sx={{ mb: 3 }}>
        <Box><Typography variant="overline" color="primary">{detail.codigo}</Typography><Typography variant="h4" component="h2" fontWeight={750}>{detail.asunto}</Typography></Box>
        <Chip icon={<VerifiedUser />} label={`ETag ${etag ?? `"${detail.version}"`}`} variant="outlined" />
      </Stack>
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', lg: 'minmax(0, 2fr) minmax(280px, 1fr)' }, gap: 3 }}>
        <Stack spacing={3}>
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
        </Stack>
        <RequestTimeline detail={detail} />
      </Box>
    </Box>
  )
}

function RequestDescription({ detail }: { detail: RequestDetail }) {
  return (
    <Paper component="article" variant="outlined" sx={{ p: { xs: 2, md: 3 } }}>
      <Typography variant="h6" component="h3" gutterBottom>Descripción</Typography>
      <Typography sx={{ whiteSpace: 'pre-wrap', mb: 3 }}>{detail.descripcion}</Typography>
      <Box component="dl" sx={{ m: 0, display: 'grid', gridTemplateColumns: { xs: '1fr 1fr', md: 'repeat(3, 1fr)' }, gap: 2 }}>
        <Metadata label="Estado" value={detail.estado.replace('_', ' ')} />
        <Metadata label="Prioridad" value={detail.prioridad} />
        <Metadata label="Categoría" value={detail.categoria.nombre} />
        <Metadata label="Solicitante" value={detail.solicitante.id} />
        <Metadata label="Analista" value={detail.analistaAsignado?.id ?? 'Sin asignar'} />
        <Metadata label="Actualización" value={formatDateTime(detail.actualizadaEn)} />
      </Box>
    </Paper>
  )
}

function Metadata({ label, value }: { label: string; value: string }) {
  return <Box><Typography component="dt" variant="caption" color="text.secondary">{label}</Typography><Typography component="dd" sx={{ m: 0, fontWeight: 600 }}>{value}</Typography></Box>
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
    <Paper component="section" variant="outlined" sx={{ p: { xs: 2, md: 3 } }}>
      <Typography variant="h6" component="h3" gutterBottom>Atención</Typography>
      <TextField fullWidth label="Motivo de la acción" value={props.reason} onChange={(event) => props.onReasonChange(event.target.value)} inputProps={{ minLength: 3, maxLength: 500 }} sx={{ mb: 2 }} />
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
        {props.canAssign && <Button variant="contained" disabled={props.busy} onClick={props.onAssign} startIcon={<AssignmentInd />}>Tomar solicitud</Button>}
        {props.canResolve && <Button variant="contained" disabled={props.busy} onClick={() => props.onTransition('RESUELTA')} startIcon={<CheckCircle />}>Resolver</Button>}
        {props.canSupervise && (
          <>
            <Button variant="outlined" disabled={props.busy} onClick={() => props.onTransition('EN_ATENCION')} startIcon={<Replay />}>Devolver</Button>
            <Button variant="contained" disabled={props.busy} onClick={() => props.onTransition('CERRADA')} startIcon={<VerifiedUser />}>Cerrar</Button>
          </>
        )}
      </Stack>
    </Paper>
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
    <Paper component="section" variant="outlined" sx={{ p: { xs: 2, md: 3 } }}>
      <Typography variant="h6" component="h3" gutterBottom>Observaciones <Chip size="small" label={props.detail.observaciones.length} /></Typography>
      {props.canObserve && (
        <Stack alignItems="flex-end" spacing={1} sx={{ mb: 2 }}>
          <TextField fullWidth multiline minRows={3} label="Agregar una observación" value={props.observation} onChange={(event) => props.onObservationChange(event.target.value)} inputProps={{ minLength: 3, maxLength: 2000 }} />
          <Button variant="outlined" disabled={props.busy || props.observation.trim().length < 3} onClick={props.onAdd} startIcon={<Message />}>Agregar</Button>
        </Stack>
      )}
      {props.detail.observaciones.length === 0
        ? <Typography color="text.secondary">Sin observaciones.</Typography>
        : <List disablePadding>{[...props.detail.observaciones].reverse().map((item, index) => <Box key={item.id}>{index > 0 && <Divider />}<ListItem disableGutters><ListItemText primary={item.contenido} secondary={`${item.actor.id} · ${formatDateTime(item.creadaEn)}`} /></ListItem></Box>)}</List>}
    </Paper>
  )
}

function RequestTimeline({ detail }: { detail: RequestDetail }) {
  return (
    <Paper component="aside" variant="outlined" sx={{ p: { xs: 2, md: 3 }, alignSelf: 'start' }}>
      <Typography variant="h6" component="h3" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}><History /> Línea de tiempo</Typography>
      <Box component="ol" sx={{ listStyle: 'none', p: 0, m: 0 }}>
        {[...detail.historial].reverse().map((entry) => (
          <Box component="li" key={entry.id} sx={{ borderLeft: 2, borderColor: 'primary.light', pl: 2, py: 1.5 }}>
            <Typography fontWeight={700}>{entry.estadoDestino.replace('_', ' ')}</Typography>
            <Typography variant="body2">{entry.motivo}</Typography>
            <Typography variant="caption" color="text.secondary">{entry.actor.id} · {formatDateTime(entry.ocurridoEn)}</Typography>
          </Box>
        ))}
      </Box>
    </Paper>
  )
}
