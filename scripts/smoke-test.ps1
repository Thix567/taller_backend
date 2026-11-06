param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Usuario = "admin",
    [string]$Clave = "admin123"
)

$ErrorActionPreference = "Stop"
$script:fallos = 0

function Paso([string]$texto) {
    Write-Host ""
    Write-Host "== $texto" -ForegroundColor Cyan
}

function Ok([string]$texto) {
    Write-Host "   OK  $texto" -ForegroundColor Green
}

function Fallo([string]$texto) {
    $script:fallos++
    Write-Host "   FALLO  $texto" -ForegroundColor Red
}

function Leer-Error($excepcion) {
    try {
        $stream = $excepcion.Response.GetResponseStream()
        if ($stream) {
            $reader = New-Object System.IO.StreamReader($stream)
            return $reader.ReadToEnd()
        }
    } catch { }
    return $excepcion.Message
}

Paso "Health publico"
try {
    $salud = Invoke-RestMethod "$BaseUrl/api/health" -TimeoutSec 10
    if ($salud.status -eq "UP") { Ok "servicio UP" } else { Fallo "estado inesperado: $($salud.status)" }
} catch { Fallo "no responde: $($_.Exception.Message)" }

Paso "Seguridad: endpoint protegido sin token"
try {
    Invoke-RestMethod "$BaseUrl/api/clientes" -TimeoutSec 10 | Out-Null
    Fallo "debio responder 401"
} catch {
    if ([int]$_.Exception.Response.StatusCode -eq 401) { Ok "401 sin token" } else { Fallo "codigo $([int]$_.Exception.Response.StatusCode)" }
}

Paso "Login JWT"
try {
    $login = Invoke-RestMethod "$BaseUrl/api/auth/login" -Method Post -ContentType "application/json" `
        -Body (@{ username = $Usuario; password = $Clave } | ConvertTo-Json) -TimeoutSec 15
    if ($login.token) { Ok "token recibido (expira en $($login.expiraEn) s)" } else { Fallo "sin token" }
} catch { Fallo (Leer-Error $_.Exception) }

$headers = @{ Authorization = "Bearer $($login.token)" }

Paso "Usuario autenticado"
try {
    $me = Invoke-RestMethod "$BaseUrl/api/auth/me" -Headers $headers -TimeoutSec 10
    if ($me.username -eq $Usuario) { Ok "usuario $($me.username) / rol $($me.rol)" } else { Fallo "usuario inesperado" }
} catch { Fallo (Leer-Error $_.Exception) }

Paso "Catalogos y dashboard"
foreach ($ruta in @("/api/clientes", "/api/vehiculos", "/api/mecanicos", "/api/insumos", "/api/paquetes", "/api/citas", "/api/informes", "/api/configuracion")) {
    try {
        $r = Invoke-RestMethod "$BaseUrl$ruta" -Headers $headers -TimeoutSec 15
        $cantidad = if ($r -is [array]) { $r.Count } else { 1 }
        Ok "$ruta -> $cantidad registro(s)"
    } catch { Fallo "$ruta -> $(Leer-Error $_.Exception)" }
}
try {
    $dash = Invoke-RestMethod "$BaseUrl/api/reportes/dashboard" -Headers $headers -TimeoutSec 15
    Ok "dashboard -> $($dash.totalOrdenes) orden(es), ingresos $($dash.ingresosMes)"
} catch { Fallo "dashboard -> $(Leer-Error $_.Exception)" }

Paso "CRUD temporal de cliente"
$sello = [DateTimeOffset]::Now.ToUnixTimeMilliseconds()
try {
    $cliente = Invoke-RestMethod "$BaseUrl/api/clientes" -Method Post -Headers $headers -ContentType "application/json" `
        -Body (@{ dniRuc = "TMP$sello"; nombreCompleto = "Cliente Smoke Test"; telefonoWhatsapp = "51900000000"; correo = "smoke$sello@test.com"; direccion = "Lima" } | ConvertTo-Json) -TimeoutSec 15
    Ok "crear cliente id=$($cliente.id)"

    $editado = Invoke-RestMethod "$BaseUrl/api/clientes/$($cliente.id)" -Method Put -Headers $headers -ContentType "application/json" `
        -Body (@{ dniRuc = "TMP$sello"; nombreCompleto = "Cliente Smoke Editado"; telefonoWhatsapp = "51900000001"; correo = "smoke$sello@test.com"; direccion = "Arequipa" } | ConvertTo-Json) -TimeoutSec 15
    if ($editado.nombreCompleto -eq "Cliente Smoke Editado") { Ok "editar cliente" } else { Fallo "editar cliente" }

    Invoke-RestMethod "$BaseUrl/api/clientes/$($cliente.id)" -Method Delete -Headers $headers -TimeoutSec 15 | Out-Null
    Ok "eliminar cliente"
} catch { Fallo (Leer-Error $_.Exception) }

Paso "Validacion de errores"
try {
    Invoke-RestMethod "$BaseUrl/api/clientes" -Method Post -Headers $headers -ContentType "application/json" `
        -Body '{"nombreCompleto":""}' -TimeoutSec 15 | Out-Null
    Fallo "debio responder 400"
} catch {
    if ([int]$_.Exception.Response.StatusCode -eq 400) { Ok "400 con detalle de campos" } else { Fallo "codigo $([int]$_.Exception.Response.StatusCode)" }
}

Write-Host ""
if ($script:fallos -eq 0) {
    Write-Host "Smoke test completado sin fallos." -ForegroundColor Green
    exit 0
} else {
    Write-Host "Smoke test con $($script:fallos) fallo(s)." -ForegroundColor Red
    exit 1
}
