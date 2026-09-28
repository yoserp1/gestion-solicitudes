import type { ZodType } from 'zod'
import type { Session } from '../interfaces'

export interface ApiResult<ResponseBody> {
  data: ResponseBody
  etag: string | null
  dataAsOf: string | null
}

interface RequestOptions extends RequestInit {
  session: Session
  write?: boolean
  etag?: string | null
}

export async function requestJson<ResponseBody>(path: string, options: RequestOptions, schema: ZodType<ResponseBody>): Promise<ApiResult<ResponseBody>> {
  const requestHeaders = new Headers(options.headers)
  requestHeaders.set('Accept', 'application/json')
  requestHeaders.set('X-Local-User-Id', options.session.userId)
  requestHeaders.set('X-Local-User-Role', options.session.role)
  requestHeaders.set('X-Correlation-ID', crypto.randomUUID())
  if (options.body) requestHeaders.set('Content-Type', 'application/json')
  if (options.write) requestHeaders.set('Idempotency-Key', crypto.randomUUID())
  if (options.etag) requestHeaders.set('If-Match', options.etag)

  const response = await fetch(path, { ...options, headers: requestHeaders })
  if (!response.ok) {
    const problem = (await response.json().catch(() => null)) as { detail?: string } | null
    if (response.status === 403) throw new Error('Autorización insuficiente para esta operación.')
    throw new Error(problem?.detail ?? `La API respondió con estado ${response.status}`)
  }

  return {
    data: schema.parse(await response.json()),
    etag: response.headers.get('ETag'),
    dataAsOf: response.headers.get('X-Data-As-Of'),
  }
}
