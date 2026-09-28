import type { Meta, StoryObj } from '@storybook/react-webpack5'
import { RequestTable } from './RequestTable'

const page = {
  page: 0,
  size: 10,
  totalElements: 2,
  totalPages: 1,
  content: [
    { id: '9c8d0ec5-6fd4-4396-a9d9-32fb7200fdf1', codigo: 'SOL-2026-001', asunto: 'Acceso al portal', categoria: { id: '09ffc558-c87e-41d0-a486-a14c9eca26ca', codigo: 'ACCESO', nombre: 'Acceso', activa: true }, prioridad: 'ALTA' as const, estado: 'REGISTRADA' as const, creadaEn: '2026-09-27T10:00:00Z', actualizadaEn: '2026-09-27T10:00:00Z', version: 0 },
    { id: '5865849f-2e39-4c36-80ed-4df38115cf87', codigo: 'SOL-2026-002', asunto: 'Actualizar antecedentes', categoria: { id: 'af9b2c69-5efe-4874-b521-1a3b23809bf9', codigo: 'DATOS', nombre: 'Datos personales', activa: true }, prioridad: 'MEDIA' as const, estado: 'EN_ATENCION' as const, creadaEn: '2026-09-26T12:00:00Z', actualizadaEn: '2026-09-27T11:30:00Z', version: 1 },
  ],
}

const meta = { title: 'Requests/RequestTable', component: RequestTable, args: { page, onOpenDetail: () => undefined }, tags: ['autodocs'] } satisfies Meta<typeof RequestTable>
export default meta
type Story = StoryObj<typeof meta>
export const WithRequests: Story = {}
