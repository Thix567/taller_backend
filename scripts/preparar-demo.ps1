param(
    [string]$Destino,
    [switch]$SinJre,
    [switch]$Force
)

$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot

if (-not $Destino) {
    $Destino = Join-Path ([Environment]::GetFolderPath("Desktop")) "Taller-Mecanico-Demo"
}

# 1) Compilar el jar (incluye el frontend Angular ya copiado en src/main/resources/static)
$mvn = $env:MAVEN_PATH
if (-not $mvn) {
    $enPath = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($enPath) { $mvn = $enPath.Source }
}
if (-not $mvn -and $env:MAVEN_HOME) {
    $candidato = Join-Path $env:MAVEN_HOME "bin\mvn.cmd"
    if (Test-Path $candidato) { $mvn = $candidato }
}
if (-not $mvn) {
    throw "Maven no encontrado. Instale Maven o defina MAVEN_PATH."
}

Write-Host "Compilando backend (perfil demo)..." -ForegroundColor Cyan
& $mvn -q -DskipTests clean package
if ($LASTEXITCODE -ne 0) { throw "Fallo la compilacion del backend." }

$jarOrigen = Join-Path $raiz "target\mecanica-backend-1.0.0.jar"
if (-not (Test-Path $jarOrigen)) { throw "No se genero el jar esperado." }

# 2) Preparar la carpeta destino
if (Test-Path $Destino) {
    if (-not $Force) {
        throw "Ya existe $Destino. Use -Force para reemplazarla."
    }
    Remove-Item $Destino -Recurse -Force
}
New-Item -ItemType Directory -Path $Destino -Force | Out-Null

Copy-Item $jarOrigen (Join-Path $Destino "taller-mecanico-demo.jar") -Force
Copy-Item (Join-Path $raiz "demo.cmd") $Destino -Force
New-Item -ItemType Directory -Path (Join-Path $Destino "scripts") -Force | Out-Null
Copy-Item (Join-Path $raiz "scripts\demo.ps1") (Join-Path $Destino "scripts\demo.ps1") -Force

# 3) Runtime de Java portable (jlink) para no depender de Java instalado
if (-not $SinJre) {
    $jlink = $null
    if ($env:JAVA_HOME) {
        $candidato = Join-Path $env:JAVA_HOME "bin\jlink.exe"
        if (Test-Path $candidato) { $jlink = $candidato }
    }
    if (-not $jlink) {
        $enPath = Get-Command jlink.exe -ErrorAction SilentlyContinue
        if ($enPath) { $jlink = $enPath.Source }
    }
    if (-not $jlink) {
        throw "No se encontro jlink. Instale un JDK 21+ o use -SinJre."
    }

    Write-Host "Generando runtime de Java portable..." -ForegroundColor Cyan
    & $jlink `
        --add-modules java.se,jdk.unsupported,jdk.crypto.ec,jdk.zipfs,jdk.jfr `
        --strip-debug --no-man-pages --no-header-files --compress=zip-6 `
        --output (Join-Path $Destino "runtime")
    if ($LASTEXITCODE -ne 0) { throw "Fallo jlink al generar el runtime." }
}

# 4) Instrucciones
$texto = @"
TALLER MECANICO - DEMO PORTABLE
================================

REQUISITOS
  Ninguno. Esta carpeta incluye Java y el frontend compilado.
  No necesita MySQL, Node ni internet.

COMO EJECUTAR
  1. Copie esta carpeta completa a la PC (USB, escritorio, etc.).
  2. Haga doble clic en: demo.cmd
  3. Espere unos segundos. Se abrira el navegador en http://localhost:8080
  4. Ingrese con:  usuario admin   clave admin123

SI EL NAVEGADOR NO SE ABRE
  Abra manualmente: http://localhost:8080

NOTAS
  - Los datos son de ejemplo y se reinician cada vez que inicia la demo.
  - Los archivos subidos se guardan en la carpeta uploads-demo.
  - Para detener la aplicacion: cierre la ventana negra o presione Ctrl+C.
  - Si el puerto 8080 esta ocupado: demo.cmd -Port 8081
"@
Set-Content -Path (Join-Path $Destino "LEEME.txt") -Value $texto -Encoding UTF8

$tam = (Get-ChildItem $Destino -Recurse -File | Measure-Object -Sum Length).Sum
Write-Host ""
Write-Host "Demo portable creada en:" -ForegroundColor Green
Write-Host "  $Destino"
Write-Host "  Tamano: $([math]::Round($tam/1MB,1)) MB"
Write-Host ""
Write-Host "Copie esa carpeta a un USB y ejecute demo.cmd en la PC de la universidad." -ForegroundColor Yellow
