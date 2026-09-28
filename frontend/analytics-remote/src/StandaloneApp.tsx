import { Alert, Box, CircularProgress, Typography } from '@mui/material'
import { useEffect, useState } from 'react'
import IndicatorsApp from './IndicatorsApp'
import { categoryListSchema, summarySchema, trendSchema } from './schemas'
import type { Category, IndicatorFilters, SummaryIndicators, TrendIndicators } from './types'

const today = new Date()
const monthAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
const initialFilters: IndicatorFilters = {
  desde: monthAgo.toISOString().slice(0, 10),
  hasta: today.toISOString().slice(0, 10),
  categoriaId: '',
}

export function StandaloneApp() {
  const [filters, setFilters] = useState(initialFilters)
  const [categories, setCategories] = useState<Category[]>([])
  const [summary, setSummary] = useState<SummaryIndicators | null>(null)
  const [trend, setTrend] = useState<TrendIndicators | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const controller = new AbortController()
    const headers = { 'X-Local-User-Id': 'supervisor-demo', 'X-Local-User-Role': 'SUPERVISOR' }
    const params = new URLSearchParams({ desde: filters.desde, hasta: filters.hasta, zonaHoraria: 'America/Santiago' })
    if (filters.categoriaId) params.set('categoriaId', filters.categoriaId)

    Promise.all([
      fetch('/api/v1/categorias', { headers, signal: controller.signal }).then(readJson),
      fetch(`/api/v1/indicadores/resumen?${params}`, { headers, signal: controller.signal }).then(readJson),
      fetch(`/api/v1/indicadores/tendencia?${params}`, { headers, signal: controller.signal }).then(readJson),
    ])
      .then(([categoriesBody, summaryBody, trendBody]) => {
        setCategories(categoryListSchema.parse(categoriesBody))
        setSummary(summarySchema.parse(summaryBody))
        setTrend(trendSchema.parse(trendBody))
        setError(null)
      })
      .catch((reason: unknown) => {
        if (!controller.signal.aborted) setError(reason instanceof Error ? reason.message : 'No fue posible cargar los indicadores')
      })

    return () => controller.abort()
  }, [filters])

  return (
    <Box component="main" sx={{ maxWidth: 1180, mx: 'auto', p: { xs: 2, md: 4 } }}>
      <Typography variant="overline" color="primary">Microfrontend remoto · modo standalone</Typography>
      {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
      {!error && (!summary || !trend) && <Box sx={{ display: 'grid', placeItems: 'center', minHeight: 280 }}><CircularProgress aria-label="Cargando indicadores" /></Box>}
      {summary && trend && <IndicatorsApp categories={categories} filters={filters} summary={summary} trend={trend} onFiltersChange={setFilters} />}
    </Box>
  )
}

async function readJson(response: Response): Promise<unknown> {
  if (!response.ok) throw new Error(`La API respondió con estado ${response.status}`)
  return response.json() as Promise<unknown>
}