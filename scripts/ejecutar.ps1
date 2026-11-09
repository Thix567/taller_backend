param(
    [switch]$Build,
    [int]$Port = 8080,
    [int]$DbPort = 3308,
    [string]$MavenPath,
    [string]$MysqlHome,
    [string]$RootPassword
)

$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$backend = $raiz
$jar = Join-Path $backend "target\mecanica-backend-1.0.0.jar"

# Cargar variables desde .env si existe (formato KEY=VALUE)
$envFile = Join-Path $raiz ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        $linea = $_.Trim()
        if ($linea -and -not $linea.StartsWith("#") -and $linea.Contains("=")) {
            $partes = $linea.Split("=", 2)
            [Environment]::SetEnvironmentVariable($partes[0].Trim(), $partes[1].Trim(), "Process")
        }
    }
}

if (-not $env:DB_PORT) { $env:DB_PORT = "$DbPort" }
if (-not $RootPassword) { $RootPassword = $env:DB_ROOT_PASSWORD }
if (-not $MavenPath) { $MavenPath = $env:MAVEN_PATH }

# 1) MySQL
& (Join-Path $PSScriptRoot "iniciar-mysql.ps1") -Port $DbPort -MysqlHome $MysqlHome

# 2) Base de datos y usuario de aplicacion (idempotente)
function Buscar-MysqlHome {
    foreach ($c in @(
        "C:\Program Files\MySQL\MySQL Server 8.0",
        "C:\Program Files\MySQL\MySQL Server 8.4",
        "C:\Program Files\MySQL\MySQL Server 9.0"
    )) {
        if (Test-Path (Join-Path $c "bin\mysql.exe")) { return $c }
    }
    $enPath = Get-Command mysql.exe -ErrorAction SilentlyContinue
    if ($enPath) { return (Split-Path -Parent (Split-Path -Parent $enPath.Source)) }
    throw "No se encontro mysql.exe. Instale MySQL Server 8 o pase -MysqlHome."
}

if (-not $MysqlHome) { $MysqlHome = Buscar-MysqlHome }
$mysql = Join-Path $MysqlHome "bin\mysql.exe"
$sql = (Join-Path $raiz "db\crear-bd.sql").Replace("\", "/")
Write-Host "Verificando base de datos bd_mecanica..." -ForegroundColor Cyan
$mysqlArgs = @("-h", "127.0.0.1", "-P", "$DbPort", "-u", "root", "--default-character-set=utf8mb4")
if ($RootPassword) { $mysqlArgs += "-p$RootPassword" }
& $mysql @mysqlArgs -e "source $sql"
if ($LASTEXITCODE -ne 0) { throw "No se pudo crear/verificar la base de datos. Revise DB_ROOT_PASSWORD en .env." }

# 3) JAVA_HOME
if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) {
    $candidatos = @(
        "C:\Program Files\Java\jdk-24",
        "C:\Program Files\Java\jdk-21",
        "C:\Program Files\Java\latest"
    )
    foreach ($c in $candidatos) {
        if (Test-Path (Join-Path $c "bin\java.exe")) { $env:JAVA_HOME = $c; break }
    }
}
if (-not $env:JAVA_HOME) { throw "Defina JAVA_HOME con un JDK 21 o superior." }
$java = Join-Path $env:JAVA_HOME "bin\java.exe"

# 4) Compilar si hace falta
if ($Build -or -not (Test-Path $jar)) {
    if (-not $MavenPath) {
        $mvnCmd = Get-Command mvn.cmd -ErrorAction SilentlyContinue
        if ($mvnCmd) { $MavenPath = $mvnCmd.Source }
    }
    if (-not $MavenPath -and $env:MAVEN_HOME) {
        $candidato = Join-Path $env:MAVEN_HOME "bin\mvn.cmd"
        if (Test-Path $candidato) { $MavenPath = $candidato }
    }
    if (-not $MavenPath) {
        foreach ($c in @(
            "C:\Program Files\Apache\maven\bin\mvn.cmd",
            "C:\apache-maven\bin\mvn.cmd"
        )) {
            if (Test-Path $c) { $MavenPath = $c; break }
        }
    }
    if (-not $MavenPath) {
        throw "Maven no encontrado. Instale Maven, agreguelo al PATH o defina MAVEN_PATH en .env."
    }

    Write-Host "Compilando backend..." -ForegroundColor Cyan
    Push-Location $backend
    try {
        & $MavenPath -DskipTests package
        if ($LASTEXITCODE -ne 0) { throw "Fallo la compilacion." }
    } finally {
        Pop-Location
    }
}

# 5) Ejecutar
Write-Host ""
Write-Host "Backend en http://localhost:$Port" -ForegroundColor Green
Write-Host "Swagger en http://localhost:$Port/swagger-ui.html" -ForegroundColor Green
Write-Host "Credenciales: admin / admin123" -ForegroundColor DarkGray
Write-Host "Ctrl+C para detener." -ForegroundColor DarkGray
Write-Host ""

Push-Location $backend
try {
    & $java -jar $jar "--server.port=$Port"
} finally {
    Pop-Location
}
