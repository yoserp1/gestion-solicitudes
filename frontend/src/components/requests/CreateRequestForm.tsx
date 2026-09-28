import Send from '@mui/icons-material/Send'
import { Box, Button, FormControl, FormHelperText, InputLabel, MenuItem, Paper, Select, Stack, TextField, Typography } from '@mui/material'
import { type FormEvent, useState } from 'react'
import type { Category, CreateRequestInput, Priority } from '../../interfaces'
import { validateCreateRequest } from '../../utils/validation'

interface CreateRequestFormProps {
  categories: Category[]
  draft: CreateRequestInput
  busy: boolean
  onDraftChange: (draft: CreateRequestInput) => void
  onCancel: () => void
  onSubmit: (event: FormEvent) => void
}

export function CreateRequestForm({ categories, draft, busy, onDraftChange, onCancel, onSubmit }: CreateRequestFormProps) {
  const [attempted, setAttempted] = useState(false)
  const errors = attempted ? validateCreateRequest(draft) : {}

  const submit = (event: FormEvent) => {
    setAttempted(true)
    if (Object.keys(validateCreateRequest(draft)).length > 0) {
      event.preventDefault()
      return
    }
    onSubmit(event)
  }

  return (
    <Paper component="form" onSubmit={submit} variant="outlined" sx={{ p: { xs: 2, md: 3 }, mb: 3 }} noValidate>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 3 }}>
        <Box><Typography variant="overline" color="primary">NUEVO CASO</Typography><Typography variant="h5" component="h2">Registrar solicitud</Typography></Box>
        <Button type="button" color="inherit" onClick={onCancel}>Cancelar</Button>
      </Stack>
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '2fr 1fr 1fr' }, gap: 2 }}>
        <TextField label="Asunto" value={draft.asunto} onChange={(event) => onDraftChange({ ...draft, asunto: event.target.value })} error={Boolean(errors.asunto)} helperText={errors.asunto} inputProps={{ maxLength: 150 }} />
        <FormControl error={Boolean(errors.categoriaId)}>
          <InputLabel id="create-category-label">Categoría</InputLabel>
          <Select labelId="create-category-label" label="Categoría" value={draft.categoriaId} onChange={(event) => onDraftChange({ ...draft, categoriaId: event.target.value })}>
            {categories.map((category) => <MenuItem key={category.id} value={category.id}>{category.nombre}</MenuItem>)}
          </Select>
          {errors.categoriaId && <FormHelperText>{errors.categoriaId}</FormHelperText>}
        </FormControl>
        <FormControl>
          <InputLabel id="create-priority-label">Prioridad</InputLabel>
          <Select labelId="create-priority-label" label="Prioridad" value={draft.prioridad} onChange={(event) => onDraftChange({ ...draft, prioridad: event.target.value as Priority })}>
            <MenuItem value="BAJA">Baja</MenuItem><MenuItem value="MEDIA">Media</MenuItem><MenuItem value="ALTA">Alta</MenuItem>
          </Select>
        </FormControl>
        <TextField sx={{ gridColumn: { md: '1 / -1' } }} label="Descripción" multiline minRows={4} value={draft.descripcion} onChange={(event) => onDraftChange({ ...draft, descripcion: event.target.value })} error={Boolean(errors.descripcion)} helperText={errors.descripcion} inputProps={{ maxLength: 2000 }} />
      </Box>
      <Stack direction="row" justifyContent="flex-end" sx={{ mt: 2 }}><Button variant="contained" disabled={busy} type="submit" startIcon={<Send />}>Registrar</Button></Stack>
    </Paper>
  )
}
