import ChevronRight from '@mui/icons-material/ChevronRight'
import { Chip, IconButton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip, Typography } from '@mui/material'
import type { RequestPage } from '../../interfaces'
import { formatDateTime } from '../../utils/dateTime'

interface RequestTableProps {
  page: RequestPage
  onOpenDetail: (id: string) => void
}

export function RequestTable({ page, onOpenDetail }: RequestTableProps) {
  return (
    <TableContainer sx={{ bgcolor: 'background.paper', border: 1, borderColor: 'divider', borderRadius: 1 }}>
      <Table aria-label="Solicitudes" sx={{ minWidth: 760 }}>
        <TableHead><TableRow><TableCell>Código / asunto</TableCell><TableCell>Categoría</TableCell><TableCell>Prioridad</TableCell><TableCell>Estado</TableCell><TableCell>Actualización</TableCell><TableCell align="right">Abrir</TableCell></TableRow></TableHead>
        <TableBody>{page.content.map((request) => (
          <TableRow key={request.id} hover>
            <TableCell><Typography fontWeight={700}>{request.codigo}</Typography><Typography variant="body2" color="text.secondary">{request.asunto}</Typography></TableCell>
            <TableCell>{request.categoria.nombre}</TableCell>
            <TableCell><Chip size="small" label={request.prioridad} color={request.prioridad === 'ALTA' ? 'error' : request.prioridad === 'MEDIA' ? 'warning' : 'default'} /></TableCell>
            <TableCell><Chip size="small" variant="outlined" label={request.estado.replace('_', ' ')} color={request.estado === 'CERRADA' ? 'success' : request.estado === 'EN_ATENCION' ? 'primary' : 'default'} /></TableCell>
            <TableCell>{formatDateTime(request.actualizadaEn)}</TableCell>
            <TableCell align="right"><Tooltip title={`Abrir ${request.codigo}`}><IconButton onClick={() => onOpenDetail(request.id)} aria-label={`Abrir solicitud ${request.codigo}`}><ChevronRight /></IconButton></Tooltip></TableCell>
          </TableRow>
        ))}</TableBody>
      </Table>
    </TableContainer>
  )
}
