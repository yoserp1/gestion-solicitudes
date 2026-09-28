import ChevronLeft from '@mui/icons-material/ChevronLeft'
import ChevronRight from '@mui/icons-material/ChevronRight'
import Inbox from '@mui/icons-material/Inbox'
import { Box, Button, FormControl, InputLabel, MenuItem, Paper, Select, Stack, Typography } from '@mui/material'
import type { Category, Priority, RequestFilters, RequestPage, Status } from '../../interfaces'
import { RequestTable } from './RequestTable'

interface RequestInboxProps {
  categories: Category[]
  filters: RequestFilters
  page: RequestPage
  role: string
  onFiltersChange: (filters: Partial<RequestFilters>) => void
  onOpenDetail: (id: string) => void
}

export function RequestInbox({ categories, filters, page, role, onFiltersChange, onOpenDetail }: RequestInboxProps) {
  return (
    <Box component="section" aria-label="Bandeja de solicitudes">
      <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(3, 1fr)' }, gap: 2 }}>
          <FormControl><InputLabel id="status-filter-label">Estado</InputLabel><Select labelId="status-filter-label" label="Estado" value={filters.estado} onChange={(event) => onFiltersChange({ estado: event.target.value as Status | '' })}><MenuItem value="">Todos</MenuItem><MenuItem value="REGISTRADA">Registrada</MenuItem><MenuItem value="EN_ATENCION">En atención</MenuItem><MenuItem value="RESUELTA">Resuelta</MenuItem><MenuItem value="CERRADA">Cerrada</MenuItem></Select></FormControl>
          <FormControl><InputLabel id="inbox-category-label">Categoría</InputLabel><Select labelId="inbox-category-label" label="Categoría" value={filters.categoriaId} onChange={(event) => onFiltersChange({ categoriaId: event.target.value })}><MenuItem value="">Todas</MenuItem>{categories.map((category) => <MenuItem key={category.id} value={category.id}>{category.nombre}</MenuItem>)}</Select></FormControl>
          <FormControl><InputLabel id="priority-filter-label">Prioridad</InputLabel><Select labelId="priority-filter-label" label="Prioridad" value={filters.prioridad} onChange={(event) => onFiltersChange({ prioridad: event.target.value as Priority | '' })}><MenuItem value="">Todas</MenuItem><MenuItem value="BAJA">Baja</MenuItem><MenuItem value="MEDIA">Media</MenuItem><MenuItem value="ALTA">Alta</MenuItem></Select></FormControl>
        </Box>
      </Paper>
      <Stack direction="row" justifyContent="space-between" sx={{ mb: 1 }}>
        <Typography sx={{ display: 'flex', alignItems: 'center', gap: 1 }}><Inbox fontSize="small" /> {page.totalElements} solicitudes</Typography>
        <Typography color="text.secondary">Rol: {role}</Typography>
      </Stack>
      {page.content.length === 0
        ? <Paper variant="outlined" role="status" sx={{ p: 5, textAlign: 'center' }}><Typography variant="h6">No hay solicitudes</Typography><Typography color="text.secondary">Ajusta los filtros seleccionados para ampliar la búsqueda.</Typography></Paper>
        : <RequestTable page={page} onOpenDetail={onOpenDetail} />}
      <Stack direction="row" alignItems="center" justifyContent="center" spacing={2} sx={{ mt: 2 }}>
        <Button disabled={page.page === 0} onClick={() => onFiltersChange({ page: page.page - 1 })} startIcon={<ChevronLeft />}>Anterior</Button>
        <Typography>Página {page.totalPages === 0 ? 0 : page.page + 1} de {page.totalPages}</Typography>
        <Button disabled={page.page + 1 >= page.totalPages} onClick={() => onFiltersChange({ page: page.page + 1 })} endIcon={<ChevronRight />}>Siguiente</Button>
      </Stack>
    </Box>
  )
}
