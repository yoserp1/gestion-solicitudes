import { useEffect, useState } from 'react'
import type { Session } from '../interfaces'

const SESSION_KEY = 'solicitudes.local-session'
const DEFAULT_SESSION: Session = { userId: 'analista-demo', role: 'ANALISTA' }
const VALID_ROLES = ['SOLICITANTE', 'ANALISTA', 'SUPERVISOR']

function readStoredSession(): Session {
  try {
    const stored = JSON.parse(localStorage.getItem(SESSION_KEY) ?? '') as Session
    if (stored.userId && VALID_ROLES.includes(stored.role)) return stored
  } catch {
    return DEFAULT_SESSION
  }
  return DEFAULT_SESSION
}

export function useSession() {
  const [session, setSession] = useState<Session>(readStoredSession)

  useEffect(() => {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session))
  }, [session])

  return { session, setSession }
}
