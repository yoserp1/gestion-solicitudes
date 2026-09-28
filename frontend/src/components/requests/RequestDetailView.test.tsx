import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { RequestDetailView } from './RequestDetailView'

const detail = {
  id: '9c8d0ec5-6fd4-4396-a9d9-32fb7200fdf1', codigo: 'SOL-2026-001', asunto: 'Acceso al portal', descripcion: 'No puedo acceder al portal desde ayer.',
  categoria: { id: '09ffc558-c87e-41d0-a486-a14c9eca26ca', codigo: 'ACCESO', nombre: 'Acceso', activa: true },
  prioridad: 'ALTA' as const, estado: 'REGISTRADA' as const, solicitante: { id: 'solicitante-demo', rol: 'SOLICITANTE' as const }, analistaAsignado: null,
  creadaEn: '2026-09-27T10:00:00Z', actualizadaEn: '2026-09-27T10:00:00Z', version: 0, observaciones: [],
  historial: [{ id: '84df1d0d-395e-41e3-bd69-94c780388d1a', estadoOrigen: null, estadoDestino: 'REGISTRADA' as const, actor: { id: 'solicitante-demo', rol: 'SOLICITANTE' as const }, motivo: 'Registro', ocurridoEn: '2026-09-27T10:00:00Z' }],
}

const callbacks = { onReasonChange: vi.fn(), onObservationChange: vi.fn(), onBack: vi.fn(), onAssign: vi.fn(), onTransition: vi.fn(), onAddObservation: vi.fn() }

describe('RequestDetailView', () => {
  it('permite a un analista tomar una solicitud registrada', () => {
    render(<RequestDetailView detail={detail} etag={'"0"'} session={{ userId: 'analista-demo', role: 'ANALISTA' }} reason="" observation="" busy={false} {...callbacks} />)
    expect(screen.getByRole('button', { name: /tomar solicitud/i })).toBeEnabled()
    expect(screen.queryByRole('button', { name: /cerrar/i })).not.toBeInTheDocument()
  })

  it('no muestra acciones de atención a un solicitante', () => {
    render(<RequestDetailView detail={detail} etag={'"0"'} session={{ userId: 'solicitante-demo', role: 'SOLICITANTE' }} reason="" observation="" busy={false} {...callbacks} />)
    expect(screen.queryByRole('heading', { name: 'Atención' })).not.toBeInTheDocument()
  })
})
