import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import { z } from 'zod'
import type { Session } from '../interfaces'

const SESSION_KEY = 'solicitudes.local-session'
const sessionSchema = z.object({
  userId: z.string().trim().min(3).max(100),
  role: z.enum(['SOLICITANTE', 'ANALISTA', 'SUPERVISOR']),
})

interface SessionState {
  current: Session | null
}

function restoreSession(): Session | null {
  const value = localStorage.getItem(SESSION_KEY)
  if (!value) return null
  try {
    const result = sessionSchema.safeParse(JSON.parse(value))
    return result.success ? result.data : null
  } catch {
    return null
  }
}

const initialState: SessionState = { current: restoreSession() }

const sessionSlice = createSlice({
  name: 'session',
  initialState,
  reducers: {
    sessionStarted(state, action: PayloadAction<Session>) {
      const session = sessionSchema.parse(action.payload)
      state.current = session
      localStorage.setItem(SESSION_KEY, JSON.stringify(session))
    },
    sessionEnded(state) {
      state.current = null
      localStorage.removeItem(SESSION_KEY)
    },
  },
})

export const { sessionStarted, sessionEnded } = sessionSlice.actions
export const sessionReducer = sessionSlice.reducer
