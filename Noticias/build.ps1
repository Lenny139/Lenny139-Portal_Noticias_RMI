# Compila todo el proyecto en la carpeta out/
# Uso:  .\build.ps1

$ErrorActionPreference = "Stop"

$out = "out"
if (-not (Test-Path $out)) {
    New-Item -ItemType Directory -Path $out | Out-Null
}

# Recoge todos los .java bajo src/
$fuentes = Get-ChildItem -Recurse -Filter *.java -Path src | ForEach-Object { $_.FullName }

Write-Host "Compilando..." -ForegroundColor Cyan
javac -d $out $fuentes

Write-Host "Compilacion correcta. Clases en .\$out" -ForegroundColor Green
Write-Host ""
Write-Host "Para ejecutar:" -ForegroundColor Yellow
Write-Host "  Servidor:  java -cp out co.edu.upb.noticias.server.ServidorNoticias"
Write-Host "  Cliente:   java -cp out co.edu.upb.noticias.client.ClienteNoticias"
