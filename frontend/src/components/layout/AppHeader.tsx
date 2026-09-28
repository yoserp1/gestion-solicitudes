import Analytics from '@mui/icons-material/Analytics'
import Assignment from '@mui/icons-material/Assignment'
import Logout from '@mui/icons-material/Logout'
import { AppBar, Box, Button, Container, FormControl, InputLabel, MenuItem, Select, Stack, Toolbar, Typography } from '@mui/material'
import type { Role, Session } from '../../interfaces'
import type { View } from '../../utils/routing'

interface AppHeaderProps {
  session: Session
  view: View
  onNavigate: (view: View) => void
  onSessionChange: (session: Session) => void
  onLogout: () => void
}

export function AppHeader({ session, view, onNavigate, onSessionChange, onLogout }: AppHeaderProps) {
  return (
    <AppBar position="sticky" color="inherit" elevation={0} sx={{ borderBottom: 1, borderColor: 'divider' }}>
      <Container maxWidth="xl">
        <Toolbar disableGutters sx={{ minHeight: { xs: 64, md: 72 }, gap: 2, flexWrap: { xs: 'wrap', md: 'nowrap' }, py: { xs: 1, md: 0 } }}>
          <Button color="primary" startIcon={<Assignment />} onClick={() => onNavigate({ kind: 'inbox' })} aria-label="Ir a solicitudes">
            <Typography fontWeight={900}>TRÁMITE</Typography>
          </Button>
          <Stack component="nav" aria-label="Navegación principal" direction="row" sx={{ flexGrow: 1 }}>
            <Button variant={view.kind !== 'indicators' ? 'contained' : 'text'} startIcon={<Assignment />} onClick={() => onNavigate({ kind: 'inbox' })}>Solicitudes</Button>
            <Button variant={view.kind === 'indicators' ? 'contained' : 'text'} startIcon={<Analytics />} onClick={() => onNavigate({ kind: 'indicators' })}>Indicadores</Button>
          </Stack>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }} aria-label="Identidad local">
            <Typography variant="body2" sx={{ display: { xs: 'none', sm: 'block' } }}>{session.userId}</Typography>
            <FormControl size="small" sx={{ minWidth: 132 }}>
              <InputLabel id="session-role-label">Rol</InputLabel>
              <Select labelId="session-role-label" label="Rol" value={session.role} onChange={(event) => onSessionChange({ ...session, role: event.target.value as Role })}>
                <MenuItem value="SOLICITANTE">Solicitante</MenuItem><MenuItem value="ANALISTA">Analista</MenuItem><MenuItem value="SUPERVISOR">Supervisor</MenuItem>
              </Select>
            </FormControl>
            <Button color="inherit" onClick={onLogout} aria-label="Cerrar sesión" title="Cerrar sesión"><Logout /></Button>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  )
}
