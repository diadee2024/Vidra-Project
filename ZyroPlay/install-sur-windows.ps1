# ZyroPlay — Installation automatique sur Windows
# Clic droit > "Exécuter avec PowerShell"  OU  coller dans PowerShell Admin

$ErrorActionPreference = "Stop"
$Destination = "D:\PROJET DEV\ZYRO PLAY"
$RepoUrl = "https://github.com/diadee2024/Vidra-Project.git"
$Branch = "cursor/zyro-play-iptv-950c"
$TempClone = "$env:TEMP\ZyroPlay-Install"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ZYRO PLAY — Installation automatique" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Creer le dossier destination
if (-not (Test-Path "D:\PROJET DEV")) {
    New-Item -ItemType Directory -Path "D:\PROJET DEV" -Force | Out-Null
}
New-Item -ItemType Directory -Path $Destination -Force | Out-Null
Write-Host "[OK] Dossier cree : $Destination" -ForegroundColor Green

# Verifier Git
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "[ERREUR] Git n'est pas installe." -ForegroundColor Red
    Write-Host "Installez Git : https://git-scm.com/download/win" -ForegroundColor Yellow
    Read-Host "Appuyez sur Entree pour quitter"
    exit 1
}

# Cloner ou mettre a jour
if (Test-Path $TempClone) { Remove-Item -Recurse -Force $TempClone }
Write-Host "[...] Telechargement depuis GitHub..." -ForegroundColor Yellow
git clone --depth 1 --branch $Branch $RepoUrl $TempClone 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERREUR] Echec du clone Git. Verifiez votre connexion internet." -ForegroundColor Red
    Read-Host "Appuyez sur Entree pour quitter"
    exit 1
}
Write-Host "[OK] Code telecharge" -ForegroundColor Green

# Copier ZyroPlay vers destination
$Source = Join-Path $TempClone "ZyroPlay"
if (-not (Test-Path $Source)) {
    Write-Host "[ERREUR] Dossier ZyroPlay introuvable dans le depot." -ForegroundColor Red
    exit 1
}

Write-Host "[...] Copie des fichiers vers $Destination ..." -ForegroundColor Yellow
robocopy $Source $Destination /E /XD build .gradle .kotlin app\build /NFL /NDL /NJH /NJS /nc /ns /np | Out-Null
if ($LASTEXITCODE -ge 8) {
  Copy-Item -Path "$Source\*" -Destination $Destination -Recurse -Force
}

# Copier le logo si present dans le dossier parent
$LogoCandidates = @(
    "$Destination\logo.png",
    "$Destination\zyro_logo.png",
    "D:\PROJET DEV\ZYRO PLAY\logo.png"
)
$LogoDest = "$Destination\app\src\main\res\drawable\zyro_logo.png"
foreach ($logo in $LogoCandidates) {
    if ((Test-Path $logo) -and ($logo -ne $LogoDest)) {
        Copy-Item $logo $LogoDest -Force
        Write-Host "[OK] Logo copie vers l'application" -ForegroundColor Green
        break
    }
}

# Nettoyage
Remove-Item -Recurse -Force $TempClone -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  INSTALLATION TERMINEE !" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Projet installe dans :" -ForegroundColor White
Write-Host "  $Destination" -ForegroundColor Cyan
Write-Host ""
Write-Host "Prochaines etapes :" -ForegroundColor White
Write-Host "  1. Ouvrir Android Studio" -ForegroundColor Gray
Write-Host "  2. File > Open > selectionner le dossier ci-dessus" -ForegroundColor Gray
Write-Host "  3. Attendre la sync Gradle" -ForegroundColor Gray
Write-Host "  4. Build > Build APK" -ForegroundColor Gray
Write-Host ""
Write-Host "Ou compiler en ligne de commande :" -ForegroundColor White
Write-Host "  cd `"$Destination`"" -ForegroundColor DarkGray
Write-Host "  .\gradlew assembleRelease" -ForegroundColor DarkGray
Write-Host ""

# Ouvrir le dossier dans l'explorateur
explorer $Destination

Read-Host "Appuyez sur Entree pour fermer"
