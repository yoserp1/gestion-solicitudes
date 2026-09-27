param(
    [string]$SolicitudesUrl = "http://localhost:8081",
    [string]$IndicadoresUrl = "http://localhost:8082"
)

$ErrorActionPreference = "Stop"

function New-RequestHeaders {
    param(
        [string]$UserId,
        [string]$Role,
        [switch]$Write,
        [string]$ETag
    )

    $headers = @{
        Accept               = "application/json"
        "X-Correlation-ID"  = [guid]::NewGuid().ToString()
        "X-Local-User-Id"   = $UserId
        "X-Local-User-Role" = $Role
    }
    if ($Write) {
        $headers["Idempotency-Key"] = [guid]::NewGuid().ToString()
    }
    if ($ETag) {
        $headers["If-Match"] = $ETag
    }
    return $headers
}

function Add-Result {
    param([string]$Endpoint, $Response, [int]$ExpectedStatus)

    if ([int]$Response.StatusCode -ne $ExpectedStatus) {
        throw "$Endpoint devolvio $($Response.StatusCode); se esperaba $ExpectedStatus"
    }
    $script:results.Add([pscustomobject]@{
        Endpoint = $Endpoint
        Status   = [int]$Response.StatusCode
    })
}

$results = [System.Collections.Generic.List[object]]::new()
$categoryId = "10000000-0000-0000-0000-000000000001"

$baselineResponse = Invoke-WebRequest -Uri "$IndicadoresUrl/api/v1/indicadores/resumen" -Headers (New-RequestHeaders "analista-prueba-001" "ANALISTA")
$baselineTotal = [int](($baselineResponse.Content | ConvertFrom-Json).total)

$response = Invoke-WebRequest -Uri "$SolicitudesUrl/api/v1/categorias" -Headers (New-RequestHeaders "solicitante-prueba-001" "SOLICITANTE")
Add-Result "GET categorias" $response 200

$body = @{
    asunto      = "Solicitud prueba 9564"
    descripcion = "Descripcion sintetica para solicitud 7127"
    categoriaId = $categoryId
    prioridad   = "ALTA"
} | ConvertTo-Json
$response = Invoke-WebRequest -Method Post -Uri "$SolicitudesUrl/api/v1/solicitudes" -Headers (New-RequestHeaders "solicitante-prueba-001" "SOLICITANTE" -Write) -ContentType "application/json" -Body $body
Add-Result "POST solicitudes" $response 201
$solicitud = $response.Content | ConvertFrom-Json
$solicitudId = $solicitud.id
$etag = $response.Headers.ETag

$response = Invoke-WebRequest -Uri "$SolicitudesUrl/api/v1/solicitudes?estado=REGISTRADA&categoriaId=$categoryId&prioridad=ALTA&page=0&size=20&sort=creadaEn,desc" -Headers (New-RequestHeaders "solicitante-prueba-001" "SOLICITANTE")
Add-Result "GET solicitudes" $response 200

$response = Invoke-WebRequest -Uri "$SolicitudesUrl/api/v1/solicitudes/$solicitudId" -Headers (New-RequestHeaders "solicitante-prueba-001" "SOLICITANTE")
Add-Result "GET solicitud detalle" $response 200

$body = @{ motivo = "Inicio atencion 5358" } | ConvertTo-Json
$response = Invoke-WebRequest -Method Post -Uri "$SolicitudesUrl/api/v1/solicitudes/$solicitudId/asignaciones" -Headers (New-RequestHeaders "analista-prueba-001" "ANALISTA" -Write -ETag $etag) -ContentType "application/json" -Body $body
Add-Result "POST asignacion" $response 200
$etag = $response.Headers.ETag

$body = @{ contenido = "Observacion sintetica 3871" } | ConvertTo-Json
$response = Invoke-WebRequest -Method Post -Uri "$SolicitudesUrl/api/v1/solicitudes/$solicitudId/observaciones" -Headers (New-RequestHeaders "analista-prueba-001" "ANALISTA" -Write -ETag $etag) -ContentType "application/json" -Body $body
Add-Result "POST observacion" $response 201
$etag = $response.Headers.ETag

$body = @{ estadoDestino = "RESUELTA"; motivo = "Resolucion satisfactoria 7543" } | ConvertTo-Json
$response = Invoke-WebRequest -Method Post -Uri "$SolicitudesUrl/api/v1/solicitudes/$solicitudId/transiciones" -Headers (New-RequestHeaders "analista-prueba-001" "ANALISTA" -Write -ETag $etag) -ContentType "application/json" -Body $body
Add-Result "POST resolver" $response 200
$etag = $response.Headers.ETag

$body = @{ estadoDestino = "CERRADA"; motivo = "Cierre supervisor 1007" } | ConvertTo-Json
$response = Invoke-WebRequest -Method Post -Uri "$SolicitudesUrl/api/v1/solicitudes/$solicitudId/transiciones" -Headers (New-RequestHeaders "supervisor-prueba-001" "SUPERVISOR" -Write -ETag $etag) -ContentType "application/json" -Body $body
Add-Result "POST cerrar" $response 200

$expectedTotal = $baselineTotal + 1
$deadline = [DateTime]::UtcNow.AddSeconds(30)
do {
    $response = Invoke-WebRequest -Uri "$IndicadoresUrl/api/v1/indicadores/resumen" -Headers (New-RequestHeaders "analista-prueba-001" "ANALISTA")
    $resumen = $response.Content | ConvertFrom-Json
    if ([int]$resumen.total -ge $expectedTotal) {
        break
    }
    Start-Sleep -Milliseconds 500
} while ([DateTime]::UtcNow -lt $deadline)
Add-Result "GET indicadores resumen" $response 200

$desde = (Get-Date).AddDays(-30).ToString("yyyy-MM-dd")
$hasta = (Get-Date).ToString("yyyy-MM-dd")
$response = Invoke-WebRequest -Uri "$IndicadoresUrl/api/v1/indicadores/tendencia?desde=$desde&hasta=$hasta&zonaHoraria=America%2FSantiago" -Headers (New-RequestHeaders "analista-prueba-001" "ANALISTA")
Add-Result "GET indicadores tendencia" $response 200
$tendencia = $response.Content | ConvertFrom-Json

$response = Invoke-WebRequest -Uri "$SolicitudesUrl/api/v1/solicitudes/$solicitudId" -Headers (New-RequestHeaders "supervisor-prueba-001" "SUPERVISOR")
$estadoFinal = ($response.Content | ConvertFrom-Json).estado
if ($estadoFinal -ne "CERRADA") {
    throw "La solicitud termino en $estadoFinal; se esperaba CERRADA"
}
if ([int]$resumen.total -lt $expectedTotal) {
    throw "La proyeccion no incorporo la nueva solicitud: total inicial=$baselineTotal, total actual=$($resumen.total)"
}
if ($tendencia.puntos.Count -eq 0) {
    throw "La tendencia no contiene puntos"
}

$results | Format-Table -AutoSize
Write-Output "SolicitudId=$solicitudId EstadoFinal=$estadoFinal"
Write-Output "IndicadoresTotal=$($resumen.total) PuntosTendencia=$($tendencia.puntos.Count)"