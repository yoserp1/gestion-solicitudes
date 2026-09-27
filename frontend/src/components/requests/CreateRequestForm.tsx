import type { FormEvent } from 'react'
import { Send } from 'lucide-react'
import type { Category, CreateRequestInput, Priority } from '../../interfaces'

interface CreateRequestFormProps {
  categories: Category[]
  draft: CreateRequestInput
  busy: boolean
  onDraftChange: (draft: CreateRequestInput) => void
  onCancel: () => void
  onSubmit: (event: FormEvent) => void
}

export function CreateRequestForm({ categories, draft, busy, onDraftChange, onCancel, onSubmit }: CreateRequestFormProps) {
  return (
    <form className="create-form" onSubmit={onSubmit}>
      <div className="section-title">
        <div><span className="eyebrow">NUEVO CASO</span><h2>Registrar solicitud</h2></div>
        <button type="button" className="text-button" onClick={onCancel}>Cancelar</button>
      </div>
      <div className="form-grid">
        <label className="wide-field">
          Asunto
          <input required minLength={5} maxLength={150} value={draft.asunto} onChange={(event) => onDraftChange({ ...draft, asunto: event.target.value })} />
        </label>
        <label>
          Categoría
          <select required value={draft.categoriaId} onChange={(event) => onDraftChange({ ...draft, categoriaId: event.target.value })}>
            {categories.map((category) => <option key={category.id} value={category.id}>{category.nombre}</option>)}
          </select>
        </label>
        <label>
          Prioridad
          <select value={draft.prioridad} onChange={(event) => onDraftChange({ ...draft, prioridad: event.target.value as Priority })}>
            <option>BAJA</option><option>MEDIA</option><option>ALTA</option>
          </select>
        </label>
        <label className="wide-field">
          Descripción
          <textarea required minLength={10} maxLength={2000} rows={4} value={draft.descripcion} onChange={(event) => onDraftChange({ ...draft, descripcion: event.target.value })} />
        </label>
      </div>
      <div className="form-actions">
        <button className="primary-button" disabled={busy} type="submit"><Send size={17} /> Registrar</button>
      </div>
    </form>
  )
}
