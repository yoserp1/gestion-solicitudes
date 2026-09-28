import type { Session } from '../interfaces'
import { useAppDispatch, useAppSelector } from '../store'
import { sessionEnded, sessionStarted } from '../store/sessionSlice'

export function useSession() {
  const dispatch = useAppDispatch()
  const session = useAppSelector((state) => state.session.current)
  const setSession = (nextSession: Session) => dispatch(sessionStarted(nextSession))
  const clearSession = () => dispatch(sessionEnded())

  return { session, setSession, clearSession }
}
