import { describe, expect, it } from 'vitest'
import { validateCreateRequest } from './validation'

describe('validateCreateRequest', () => {
  it('rechaza una solicitud incompleta en la frontera del formulario', () => {
    expect(validateCreateRequest({ asunto: 'Corto', descripcion: 'breve', categoriaId: '', prioridad: 'MEDIA' })).toEqual(expect.objectContaining({ descripcion: expect.any(String), categoriaId: expect.any(String) }))
  })

  it('acepta una solicitud válida', () => {
    expect(validateCreateRequest({ asunto: 'Acceso bloqueado', descripcion: 'No puedo acceder al portal desde ayer.', categoriaId: '09ffc558-c87e-41d0-a486-a14c9eca26ca', prioridad: 'ALTA' })).toEqual({})
  })
})
