import type { Role } from './auth'

export type Priority = 'BAJA' | 'MEDIA' | 'ALTA'
export type Status = 'REGISTRADA' | 'EN_ATENCION' | 'RESUELTA' | 'CERRADA'

export interface Category {
  id: string
  codigo: string
  nombre: string
  activa: boolean
}

export interface Actor {
  id: string
  rol: Role
}

export interface RequestSummary {
  id: string
  codigo: string
  asunto: string
  categoria: Category
  prioridad: Priority
  estado: Status
  analistaAsignado?: Actor | null
  creadaEn: string
  actualizadaEn: string
  version: number
}

export interface HistoryEntry {
  id: string
  estadoOrigen?: Status | null
  estadoDestino: Status
  actor: Actor
  motivo: string
  ocurridoEn: string
}

export interface Observation {
  id: string
  contenido: string
  actor: Actor
  creadaEn: string
}

export interface RequestDetail extends RequestSummary {
  descripcion: string
  solicitante: Actor
  historial: HistoryEntry[]
  observaciones: Observation[]
}

export interface RequestPage {
  content: RequestSummary[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface RequestFilters {
  estado: Status | ''
  categoriaId: string
  prioridad: Priority | ''
  page: number
  size: number
}

export interface CreateRequestInput {
  asunto: string
  descripcion: string
  categoriaId: string
  prioridad: Priority
}
