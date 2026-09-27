export type Role = 'SOLICITANTE' | 'ANALISTA' | 'SUPERVISOR'

export interface Session {
  userId: string
  role: Role
}
