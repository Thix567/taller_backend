param(
    [int]$Port = 3308,
    [string]$MysqlHome
)

$ErrorActionPreference = "Stop"

function Buscar-MysqlHome {
    $candidatos = @(
        "C:\Program Files\MySQL\MySQL Server 8.0",
        "C:\Program Files\MySQL\MySQL Server 8.4",
        "C:\Program Files\MySQL\MySQL Server 9.0"
    )
    foreach ($c in $candidatos) {
        if (Test-Path (Join-Path $c "bin\mysqladmin.exe")) { return $c }
    }
    $enPath = Get-Command mysqladmin.exe -ErrorAction SilentlyContinue
    if ($enPath) { return (Split-Path -Parent (Split-Path -Parent $enPath.Source)) }
    throw "No se encontro mysqladmin.exe. Indique -MysqlHome."
}

if (-not $MysqlHome) { $MysqlHome = Buscar-MysqlHome }
$mysqladmin = Join-Path $MysqlHome "bin\mysqladmin.exe"

if (-not (Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue)) {
    Write-Host "MySQL no esta escuchando en el puerto $Port." -ForegroundColor Yellow
    exit 0
}

& $mysqladmin -h 127.0.0.1 -P $Port -u root shutdown
Write-Host "MySQL detenido." -ForegroundColor Green
