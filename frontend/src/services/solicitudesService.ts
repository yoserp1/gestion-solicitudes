import type { Category, CreateRequestInput, Observation, RequestDetail, RequestFilters, RequestPage, Session, Status } from '../interfaces'
import { categoryListSchema, observationSchema, requestDetailSchema, requestPageSchema } from '../schemas'
import { requestJson } from './httpClient'

export const solicitudesService = {
  getCategories: (session: Session, signal?: AbortSignal) =>
    requestJson<Category[]>('/api/v1/categorias', { session, signal }, categoryListSchema),

  getPage: (session: Session, filters: RequestFilters, signal?: AbortSignal) => {
    const params = new URLSearchParams({ page: String(filters.page), size: String(filters.size), sort: 'actualizadaEn,desc' })
    if (filters.estado) params.set('estado', filters.estado)
    if (filters.categoriaId) params.set('categoriaId', filters.categoriaId)
    if (filters.prioridad) params.set('prioridad', filters.prioridad)
    return requestJson<RequestPage>(`/api/v1/solicitudes?${params}`, { session, signal }, requestPageSchema)
  },

  getById: (session: Session, id: string, signal?: AbortSignal) =>
    requestJson<RequestDetail>(`/api/v1/solicitudes/${id}`, { session, signal }, requestDetailSchema),

  create: (session: Session, body: CreateRequestInput) =>
    requestJson<RequestDetail>('/api/v1/solicitudes', { session, method: 'POST', write: true, body: JSON.stringify(body) }, requestDetailSchema),

  assign: (session: Session, id: string, etag: string, motivo: string) =>
    requestJson<RequestDetail>(`/api/v1/solicitudes/${id}/asignaciones`, { session, method: 'POST', write: true, etag, body: JSON.stringify({ motivo }) }, requestDetailSchema),

  transition: (session: Session, id: string, etag: string, estadoDestino: Status, motivo: string) =>
    requestJson<RequestDetail>(`/api/v1/solicitudes/${id}/transiciones`, { session, method: 'POST', write: true, etag, body: JSON.stringify({ estadoDestino, motivo }) }, requestDetailSchema),

  addObservation: (session: Session, id: string, etag: string, contenido: string) =>
    requestJson<Observation>(`/api/v1/solicitudes/${id}/observaciones`, { session, method: 'POST', write: true, etag, body: JSON.stringify({ contenido }) }, observationSchema),
}
