import { z } from 'zod'
import { createRequestSchema } from '../schemas/apiSchemas'
import type { CreateRequestInput } from '../interfaces'

export function validateCreateRequest(input: CreateRequestInput): Record<string, string> {
  const result = createRequestSchema.safeParse(input)
  if (result.success) return {}
  const errors = z.flattenError(result.error).fieldErrors
  return Object.fromEntries(Object.entries(errors).map(([field, messages]) => [field, messages?.[0] ?? 'Valor inválido']))
}
