export type View = { kind: 'inbox' } | { kind: 'detail'; id: string } | { kind: 'indicators' }

export function viewFromPath(pathname = window.location.pathname): View {
  if (pathname === '/indicadores') return { kind: 'indicators' }
  const id = pathname.match(/^\/solicitudes\/([^/]+)$/)?.[1]
  return id ? { kind: 'detail', id } : { kind: 'inbox' }
}

export function pathFromView(view: View) {
  if (view.kind === 'detail') return `/solicitudes/${view.id}`
  if (view.kind === 'indicators') return '/indicadores'
  return '/'
}
