import { useEffect, useState } from 'react'
import { indicadoresService } from '../services/indicadoresService'
import { solicitudesService } from '../services/solicitudesService'
import type {
  Category,
  IndicatorFilters,
  RequestDetail,
  RequestFilters,
  RequestPage,
  Session,
  SummaryIndicators,
  TrendIndicators,
} from '../interfaces'
import type { View } from '../utils/routing'

const EMPTY_PAGE: RequestPage = { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0 }

interface WorkspaceDataOptions {
  session: Session
  view: View
  requestFilters: RequestFilters
  indicatorFilters: IndicatorFilters
}

export function useWorkspaceData({ session, view, requestFilters, indicatorFilters }: WorkspaceDataOptions) {
  const [categories, setCategories] = useState<Category[]>([])
  const [page, setPage] = useState<RequestPage>(EMPTY_PAGE)
  const [detail, setDetail] = useState<RequestDetail | null>(null)
  const [etag, setEtag] = useState<string | null>(null)
  const [summary, setSummary] = useState<SummaryIndicators | null>(null)
  const [trend, setTrend] = useState<TrendIndicators | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [reloadToken, setReloadToken] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    solicitudesService.getCategories(session, controller.signal)
      .then(({ data }) => setCategories(data))
      .catch((requestError: Error) => {
        if (requestError.name !== 'AbortError') setError(requestError.message)
      })
    return () => controller.abort()
  }, [session])

  useEffect(() => {
    const controller = new AbortController()

    async function loadView() {
      if (view.kind === 'detail') {
        const result = await solicitudesService.getById(session, view.id, controller.signal)
        setDetail(result.data)
        setEtag(result.etag)
        return
      }
      if (view.kind === 'indicators') {
        const [summaryResult, trendResult] = await Promise.all([
          indicadoresService.getSummary(session, indicatorFilters, controller.signal),
          indicadoresService.getTrend(session, indicatorFilters, controller.signal),
        ])
        setSummary(summaryResult.data)
        setTrend(trendResult.data)
        return
      }
      const result = await solicitudesService.getPage(session, requestFilters, controller.signal)
      setPage(result.data)
      setDetail(null)
      setEtag(null)
    }

    loadView()
      .catch((requestError: Error) => {
        if (requestError.name !== 'AbortError') setError(requestError.message)
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false)
      })
    return () => controller.abort()
  }, [indicatorFilters, reloadToken, requestFilters, session, view])

  const beginLoad = () => {
    setLoading(true)
    setError(null)
  }

  const reload = () => {
    beginLoad()
    setReloadToken((value) => value + 1)
  }

  const updateDetail = (nextDetail: RequestDetail, nextEtag: string | null) => {
    setDetail(nextDetail)
    setEtag(nextEtag)
  }

  const refreshDetail = async () => {
    if (view.kind !== 'detail') return
    const result = await solicitudesService.getById(session, view.id)
    updateDetail(result.data, result.etag)
  }

  return {
    categories,
    page,
    detail,
    etag,
    summary,
    trend,
    loading,
    error,
    setError,
    beginLoad,
    reload,
    updateDetail,
    refreshDetail,
  }
}
