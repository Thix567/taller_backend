param(
    [int]$Port = 3308,
    [string]$DataDir,
    [string]$MysqlHome,
    [int]$TimeoutSeconds = 90
)

$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
if (-not $DataDir) { $DataDir = Join-Path $raiz ".mysql-data" }

function Buscar-MysqlHome {
    $candidatos = @(
        "C:\Program Files\MySQL\MySQL Server 8.0",
        "C:\Program Files\MySQL\MySQL Server 8.4",
        "C:\Program Files\MySQL\MySQL Server 9.0"
    )
    foreach ($c in $candidatos) {
        if (Test-Path (Join-Path $c "bin\mysqld.exe")) { return $c }
    }
    $enPath = Get-Command mysqld.exe -ErrorAction SilentlyContinue
    if ($enPath) { return (Split-Path -Parent (Split-Path -Parent $enPath.Source)) }
    throw "No se encontro MySQL Server. Indique la ruta con -MysqlHome 'C:\Program Files\MySQL\MySQL Server 8.0'."
}

if (-not $MysqlHome) { $MysqlHome = Buscar-MysqlHome }
$mysqld = Join-Path $MysqlHome "bin\mysqld.exe"
$mysql = Join-Path $MysqlHome "bin\mysql.exe"

if (-not (Test-Path $mysqld)) { throw "No existe $mysqld" }

$escuchando = Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue
if ($escuchando) {
    Write-Host "MySQL ya esta escuchando en 127.0.0.1:$Port" -ForegroundColor Green
    exit 0
}

if (-not (Test-Path $DataDir)) {
    New-Item -ItemType Directory -Force -Path $DataDir | Out-Null
}

$inicializado = Test-Path (Join-Path $DataDir "mysql")
if (-not $inicializado) {
    Write-Host "Inicializando instancia MySQL de desarrollo en $DataDir ..." -ForegroundColor Cyan
    & $mysqld --initialize-insecure --datadir="$DataDir" --basedir="$MysqlHome" --log-error="$DataDir\init.log"
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo la inicializacion de MySQL. Revise $DataDir\init.log"
    }
}

function Start-Servidor {
    $argumentos = "--datadir=`"$DataDir`" --port=$Port --bind-address=127.0.0.1 --mysqlx=0 --log-error=`"$DataDir\server.log`""
    return Start-Process -FilePath $mysqld -ArgumentList $argumentos -WindowStyle Hidden -PassThru
}

function Esperar-Puerto {
    $limite = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $limite) {
        if (Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue) { return $true }
        Start-Sleep -Milliseconds 800
    }
    return $false
}

Write-Host "Iniciando MySQL en 127.0.0.1:$Port ..." -ForegroundColor Cyan
$proceso = Start-Servidor

if (-not (Esperar-Puerto)) {
    Write-Host "MySQL no arranco. Se reintenta limpiando tablespaces UNDO huerfanos..." -ForegroundColor Yellow
    if ($proceso -and -not $proceso.HasExited) {
        Stop-Process -Id $proceso.Id -Force -ErrorAction SilentlyContinue
    }
    # Detener solo instancias de MySQL que usen este datadir (no el servicio MySQL del sistema).
    Get-CimInstance Win32_Process -Filter "Name = 'mysqld.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -and $_.CommandLine.Contains($DataDir) } |
        ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
    Start-Sleep -Seconds 2
    Remove-Item (Join-Path $DataDir "undo_001"), (Join-Path $DataDir "undo_002"),
        (Join-Path $DataDir "undo_1_trunc.log"), (Join-Path $DataDir "undo_2_trunc.log") `
        -Force -ErrorAction SilentlyContinue
    $proceso = Start-Servidor
    if (-not (Esperar-Puerto)) {
        throw "No se pudo iniciar MySQL. Revise $DataDir\server.log"
    }
}

& $mysql -h 127.0.0.1 -P $Port -u root -e "SELECT VERSION() AS version;"
if ($LASTEXITCODE -ne 0) { throw "MySQL arranco pero no acepta conexiones." }

Write-Host ""
Write-Host "MySQL listo: 127.0.0.1:$Port (usuario root sin clave)" -ForegroundColor Green
Write-Host "Base de datos: bd_mecanica | usuario app: taller_app / taller123" -ForegroundColor Green
Write-Host "Detener con: scripts\detener-mysql.ps1" -ForegroundColor DarkGray
