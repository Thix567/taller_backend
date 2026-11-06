param(
    [int]$Port = 8080,
    [switch]$NoBrowser,
    [string]$Jar
)

$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot

# 1) Localizar el jar: carpeta portable o build de Maven
if (-not $Jar) {
    $candidatos = @(
        (Join-Path $raiz "taller-mecanico-demo.jar"),
        (Join-Path $raiz "target\mecanica-backend-1.0.0.jar")
    )
    $Jar = $candidatos | Where-Object { Test-Path $_ } | Select-Object -First 1
}
if (-not $Jar -or -not (Test-Path $Jar)) {
    throw "No se encontro el jar de la demo. Ejecute scripts\preparar-demo.ps1 o compile con Maven."
}

# 2) Localizar Java: runtime incluido, JAVA_HOME o PATH
$java = Join-Path $raiz "runtime\bin\java.exe"
if (-not (Test-Path $java) -and $env:JAVA_HOME) {
    $candidato = Join-Path $env:JAVA_HOME "bin\java.exe"
    if (Test-Path $candidato) { $java = $candidato }
}
if (-not (Test-Path $java)) {
    $enPath = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($enPath) { $java = $enPath.Source }
}
if (-not (Test-Path $java)) {
    throw "No se encontro Java. Instale JDK/JRE 21 o use la carpeta portable generada con scripts\preparar-demo.ps1."
}

if (Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue) {
    throw "El puerto $Port ya esta en uso. Cierre la aplicacion que lo ocupa o use -Port 8081."
}

$url = "http://localhost:$Port"
Write-Host ""
Write-Host "  Taller Mecanico - modo demostracion" -ForegroundColor Cyan
Write-Host "  Base de datos H2 en memoria (no requiere MySQL ni Node)" -ForegroundColor Gray
Write-Host "  Abriendo: $url" -ForegroundColor Yellow
Write-Host "  Usuario: admin   Clave: admin123" -ForegroundColor Yellow
Write-Host "  Detenga con Ctrl+C en esta ventana." -ForegroundColor Gray
Write-Host ""

$job = $null
if (-not $NoBrowser) {
    $job = Start-Job -ScriptBlock {
        param($url, $Port)
        for ($i = 0; $i -lt 60; $i++) {
            try {
                $cliente = New-Object System.Net.Sockets.TcpClient
                $cliente.Connect("127.0.0.1", $Port)
                $cliente.Close()
                Start-Process $url
                break
            } catch {
                Start-Sleep -Seconds 2
            }
        }
    } -ArgumentList $url, $Port
}

try {
    & $java -jar $Jar "--spring.profiles.active=demo" "--server.port=$Port"
} finally {
    if ($job) {
        Stop-Job $job -ErrorAction SilentlyContinue
        Remove-Job $job -Force -ErrorAction SilentlyContinue
    }
}
