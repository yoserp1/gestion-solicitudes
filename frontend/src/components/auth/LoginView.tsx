import Login from '@mui/icons-material/Login'
import { Box, Button, FormControl, InputLabel, MenuItem, Paper, Select, Stack, TextField, Typography } from '@mui/material'
import { type FormEvent, useState } from 'react'
import type { Role, Session } from '../../interfaces'

interface LoginViewProps {
  onLogin: (session: Session) => void
}

export function LoginView({ onLogin }: LoginViewProps) {
  const [userId, setUserId] = useState('analista-demo')
  const [role, setRole] = useState<Role>('ANALISTA')

  const submit = (event: FormEvent) => {
    event.preventDefault()
    if (userId.trim().length >= 3) onLogin({ userId: userId.trim(), role })
  }

  return (
    <Box component="main" sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center', p: 2, background: 'radial-gradient(circle at 15% 20%, #dbe9df 0, transparent 32%), #f4f6f3' }}>
      <Paper component="form" onSubmit={submit} variant="outlined" sx={{ width: 'min(100%, 430px)', p: { xs: 3, sm: 5 } }}>
        <Stack spacing={3}>
          <Box>
            <Typography variant="overline" color="primary">Gestión de solicitudes</Typography>
            <Typography component="h1" variant="h4" fontWeight={800}>Iniciar sesión</Typography>
            <Typography color="text.secondary">Selecciona una identidad local para acceder según su rol.</Typography>
          </Box>
          <TextField label="Identificador de usuario" value={userId} onChange={(event) => setUserId(event.target.value)} required inputProps={{ minLength: 3, maxLength: 100 }} autoFocus />
          <FormControl>
            <InputLabel id="login-role-label">Rol</InputLabel>
            <Select labelId="login-role-label" label="Rol" value={role} onChange={(event) => setRole(event.target.value as Role)}>
              <MenuItem value="SOLICITANTE">Solicitante</MenuItem>
              <MenuItem value="ANALISTA">Analista</MenuItem>
              <MenuItem value="SUPERVISOR">Supervisor</MenuItem>
            </Select>
          </FormControl>
          <Button type="submit" variant="contained" startIcon={<Login />} disabled={userId.trim().length < 3}>Ingresar</Button>
        </Stack>
      </Paper>
    </Box>
  )
}
