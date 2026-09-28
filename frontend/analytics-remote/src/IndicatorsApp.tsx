import {
  Box,
  FormControl,
  InputLabel,
  LinearProgress,
  MenuItem,
  Paper,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import type { IndicatorsAppProps } from './types'

const STATUS_LABELS = {
  REGISTRADA: 'Registradas',
  EN_ATENCION: 'En atención',
  RESUELTA: 'Resueltas',
  CERRADA: 'Cerradas',
} as const

export default function IndicatorsApp({ categories, filters, summary, trend, onFiltersChange }: IndicatorsAppProps) {
  const maxDaily = Math.max(1, ...trend.puntos.map((point) => point.registradas + point.resueltas + point.cerradas))

  return (
    <Box component="section" aria-labelledby="analytics-heading">
      <Typography id="analytics-heading" variant="h5" component="h2" sx={{ mb: 2, fontWeight: 700 }}>
        Resumen analítico
      </Typography>

      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 3 }}>
        <TextField
          label="Desde"
          type="date"
          value={filters.desde}
          onChange={(event) => onFiltersChange({ ...filters, desde: event.target.value })}
          slotProps={{ inputLabel: { shrink: true } }}
          fullWidth
        />
        <TextField
          label="Hasta"
          type="date"
          value={filters.hasta}
          onChange={(event) => onFiltersChange({ ...filters, hasta: event.target.value })}
          slotProps={{ inputLabel: { shrink: true } }}
          fullWidth
        />
        <FormControl fullWidth>
          <InputLabel id="category-filter-label">Categoría</InputLabel>
          <Select
            labelId="category-filter-label"
            label="Categoría"
            value={filters.categoriaId}
            onChange={(event) => onFiltersChange({ ...filters, categoriaId: event.target.value })}
          >
            <MenuItem value="">Todas</MenuItem>
            {categories.map((category) => <MenuItem key={category.id} value={category.id}>{category.nombre}</MenuItem>)}
          </Select>
        </FormControl>
      </Stack>

      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', lg: 'repeat(5, 1fr)' }, gap: 2, mb: 3 }}>
        <Metric label="Total del período" value={summary.total} emphasized />
        {summary.porEstado.map((item) => <Metric key={item.estado} label={STATUS_LABELS[item.estado]} value={item.cantidad} />)}
      </Box>

      {summary.total === 0 ? (
        <Paper variant="outlined" sx={{ p: 4, textAlign: 'center' }} role="status">
          <Typography variant="h6">Sin datos para el período</Typography>
          <Typography color="text.secondary">Ajusta las fechas o la categoría para ampliar la búsqueda.</Typography>
        </Paper>
      ) : (
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', lg: '1fr 1fr' }, gap: 3 }}>
          <AnalyticsSection title="Distribución por categoría">
            <Stack spacing={2}>
              {summary.porCategoria.map((item) => (
                <Box key={item.categoriaId}>
                  <Stack direction="row" justifyContent="space-between">
                    <Typography>{item.nombre}</Typography><Typography fontWeight={700}>{item.cantidad}</Typography>
                  </Stack>
                  <LinearProgress variant="determinate" value={(item.cantidad / summary.total) * 100} aria-label={`${item.nombre}: ${item.cantidad}`} />
                </Box>
              ))}
            </Stack>
          </AnalyticsSection>
          <AnalyticsSection title="Tendencia de los últimos días">
            <Stack spacing={2}>
              {trend.puntos.slice(-14).map((point) => {
                const total = point.registradas + point.resueltas + point.cerradas
                return (
                  <Box key={point.fecha}>
                    <Stack direction="row" justifyContent="space-between">
                      <Typography component="time" dateTime={point.fecha}>{formatShortDate(point.fecha)}</Typography>
                      <Typography fontWeight={700}>{total}</Typography>
                    </Stack>
                    <LinearProgress variant="determinate" value={(total / maxDaily) * 100} aria-label={`${point.fecha}: ${total} solicitudes`} color="secondary" />
                  </Box>
                )
              })}
            </Stack>
          </AnalyticsSection>
        </Box>
      )}
    </Box>
  )
}

function Metric({ label, value, emphasized = false }: { label: string; value: number; emphasized?: boolean }) {
  return (
    <Paper variant="outlined" sx={{ p: 2, bgcolor: emphasized ? 'primary.main' : 'background.paper', color: emphasized ? 'primary.contrastText' : 'text.primary' }}>
      <Typography variant="body2" color={emphasized ? 'inherit' : 'text.secondary'}>{label}</Typography>
      <Typography variant="h4" component="strong" fontWeight={800}>{value}</Typography>
    </Paper>
  )
}

function AnalyticsSection({ title, children }: { title: string; children: React.ReactNode }) {
  return <Box component="section"><Typography variant="h6" component="h3" sx={{ mb: 2 }}>{title}</Typography>{children}</Box>
}

function formatShortDate(value: string) {
  return new Intl.DateTimeFormat('es-CL', { day: '2-digit', month: 'short' }).format(new Date(`${value}T12:00:00`))
}
