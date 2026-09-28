import { Alert, Box, Button, CircularProgress } from '@mui/material'

interface RequestStateProps {
  loading: boolean
  error: string | null
  notice: string | null
  onRetry: () => void
}

export function RequestState({ loading, error, notice, onRetry }: RequestStateProps) {
  return (
    <Box sx={{ mb: notice || loading || error ? 3 : 0 }}>
      {notice && <Alert severity="success" role="status">{notice}</Alert>}
      {loading && <Box role="status" sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 2, minHeight: 160 }}><CircularProgress size={26} /> Consultando información...</Box>}
      {!loading && error && (
        <Alert severity={error.includes('Autorización insuficiente') ? 'warning' : 'error'} role="alert" action={<Button color="inherit" size="small" onClick={onRetry}>Reintentar</Button>}>
          <strong>{error.includes('Autorización insuficiente') ? 'Autorización insuficiente' : 'No fue posible completar la operación'}</strong><br />{error}
        </Alert>
      )}
    </Box>
  )
}
