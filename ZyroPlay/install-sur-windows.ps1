# ZyroPlay — Installation automatique sur Windows (sans Git requis)
# Clic droit > "Executer avec PowerShell"

$ErrorActionPreference = "Stop"
$Destination = "D:\PROJET DEV\ZYRO PLAY"
$ZipUrl = "https://github.com/diadee2024/Vidra-Project/archive/refs/heads/cursor/zyro-play-iptv-950c.zip"
$TempDir = "$env:TEMP\ZyroPlay-Install"
$ZipFile = "$env:TEMP\ZyroPlay-zip.zip"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ZYRO PLAY - Installation automatique" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Creer le dossier destination
if (-not (Test-Path "D:\PROJET DEV")) {
    New-Item -ItemType Directory -Path "D:\PROJET DEV" -Force | Out-Null
}
New-Item -ItemType Directory -Path $Destination -Force | Out-Null
Write-Host "[OK] Dossier : $Destination" -ForegroundColor Green

# Telecharger le ZIP (pas besoin de Git)
Write-Host "[...] Telechargement depuis GitHub (ZIP)..." -ForegroundColor Yellow
try {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $ZipUrl -OutFile $ZipFile -UseBasicParsing
} catch {
    Write-Host "[ERREUR] Telechargement impossible. Verifiez votre connexion internet." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Read-Host "Appuyez sur Entree pour quitter"
    exit 1
}
Write-Host "[OK] Telechargement termine" -ForegroundColor Green

# Extraire
Write-Host "[...] Extraction des fichiers..." -ForegroundColor Yellow
if (Test-Path $TempDir) { Remove-Item -Recurse -Force $TempDir }
Expand-Archive -Path $ZipFile -DestinationPath $TempDir -Force
Remove-Item $ZipFile -Force -ErrorAction SilentlyContinue

# Trouver le dossier ZyroPlay dans l'archive
$ExtractedRoot = Get-ChildItem -Path $TempDir -Directory | Select-Object -First 1
$Source = Join-Path $ExtractedRoot.FullName "ZyroPlay"

if (-not (Test-Path $Source)) {
    Write-Host "[ERREUR] Dossier ZyroPlay introuvable dans l'archive." -ForegroundColor Red
    Read-Host "Appuyez sur Entree pour quitter"
    exit 1
}

# Copier vers destination
Write-Host "[...] Copie vers $Destination ..." -ForegroundColor Yellow
Get-ChildItem -Path $Source -Force | ForEach-Object {
    $target = Join-Path $Destination $_.Name
    if ($_.PSIsContainer) {
        if (Test-Path $target) { Remove-Item $target -Recurse -Force }
        Copy-Item $_.FullName $target -Recurse -Force
    } else {
        Copy-Item $_.FullName $target -Force
    }
}

# Integrer le logo si present
$LogoDest = "$Destination\app\src\main\res\drawable\zyro_logo.png"
$LogoCandidates = @(
    "$Destination\logo.png",
    "$Destination\zyro_logo.png",
    "D:\PROJET DEV\ZYRO PLAY\logo.png"
)
foreach ($logo in $LogoCandidates) {
    if ((Test-Path $logo) -and ($logo -ne $LogoDest)) {
        New-Item -ItemType Directory -Path (Split-Path $LogoDest) -Force | Out-Null
        Copy-Item $logo $LogoDest -Force
        Write-Host "[OK] Logo integre dans l'application" -ForegroundColor Green
        break
    }
}

# Nettoyage
Remove-Item -Recurse -Force $TempDir -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  INSTALLATION TERMINEE !" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Projet installe dans :" -ForegroundColor White
Write-Host "  $Destination" -ForegroundColor Cyan
Write-Host ""
Write-Host "Ouvrir dans Cursor :" -ForegroundColor White
Write-Host "  1. Lancez Cursor" -ForegroundColor Gray
Write-Host "  2. File > Open Folder" -ForegroundColor Gray
Write-Host "  3. Selectionnez : D:\PROJET DEV\ZYRO PLAY" -ForegroundColor Gray
Write-Host ""
Write-Host "Compiler l'APK :" -ForegroundColor White
Write-Host "  cd `"$Destination`"" -ForegroundColor DarkGray
Write-Host "  .\gradlew assembleRelease" -ForegroundColor DarkGray
Write-Host ""

explorer $Destination
Read-Host "Appuyez sur Entree pour fermer"
