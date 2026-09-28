import Add from '@mui/icons-material/Add'
import Refresh from '@mui/icons-material/Refresh'
import { Box, Button, IconButton, Stack, Tooltip, Typography } from '@mui/material'
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
    <Stack component="section" direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ xs: 'stretch', sm: 'center' }} spacing={2} sx={{ mb: 3 }}>
      <Box>
        <Typography variant="overline" color="primary">OPERACIÓN / {copy.eyebrow}</Typography>
        <Typography component="h1" variant="h1">{copy.title}</Typography>
        <Typography color="text.secondary">{copy.subtitle}</Typography>
      </Box>
      <Stack direction="row" spacing={1}>
        {view.kind === 'inbox' && role === 'SOLICITANTE' && (
          <Button variant="contained" startIcon={<Add />} onClick={onCreate}>Nueva solicitud</Button>
        )}
        <Tooltip title="Actualizar información"><IconButton onClick={onReload} aria-label="Actualizar información"><Refresh /></IconButton></Tooltip>
      </Stack>
    </Stack>
  )
}
