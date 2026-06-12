# ZyroPlay — Installation Windows (sans Git, extraction selective)
$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.IO.Compression.FileSystem

$Destination = "D:\PROJET DEV\ZYRO PLAY"
$ZipUrl = "https://github.com/diadee2024/Vidra-Project/archive/refs/heads/cursor/zyro-play-iptv-950c.zip"
$ZipFile = "$env:TEMP\ZyroPlay-only.zip"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ZYRO PLAY - Installation automatique" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

New-Item -ItemType Directory -Path "D:\PROJET DEV" -Force | Out-Null
New-Item -ItemType Directory -Path $Destination -Force | Out-Null

Write-Host "[...] Telechargement..." -ForegroundColor Yellow
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest -Uri $ZipUrl -OutFile $ZipFile -UseBasicParsing
Write-Host "[OK] Telecharge" -ForegroundColor Green

Write-Host "[...] Extraction ZyroPlay uniquement..." -ForegroundColor Yellow
$zip = [System.IO.Compression.ZipFile]::OpenRead($ZipFile)
$zyroPrefix = $null
foreach ($e in $zip.Entries) {
    if ($e.FullName -match "^([^/]+)/ZyroPlay/") {
        $zyroPrefix = $Matches[1] + "/ZyroPlay/"
        break
    }
}
if (-not $zyroPrefix) {
    Write-Host "[ERREUR] Dossier ZyroPlay introuvable dans l'archive." -ForegroundColor Red
    $zip.Dispose()
    Read-Host "Entree pour quitter"
    exit 1
}

$count = 0
foreach ($entry in $zip.Entries) {
    if ($entry.FullName.StartsWith($zyroPrefix) -and -not $entry.FullName.EndsWith("/")) {
        $relative = $entry.FullName.Substring($zyroPrefix.Length) -replace "/", "\"
        $outPath = Join-Path $Destination $relative
        $parent = Split-Path $outPath -Parent
        if (-not (Test-Path $parent)) {
            New-Item -ItemType Directory -Path $parent -Force | Out-Null
        }
        [System.IO.Compression.ZipFileExtensions]::ExtractToFile($entry, $outPath, $true)
        $count++
    }
}
$zip.Dispose()
Remove-Item $ZipFile -Force -ErrorAction SilentlyContinue

Write-Host "[OK] $count fichiers copies" -ForegroundColor Green

# Logo optionnel
$LogoDest = "$Destination\app\src\main\res\drawable\zyro_logo.png"
foreach ($logo in @("$Destination\logo.png", "$Destination\zyro_logo.png")) {
    if ((Test-Path $logo) -and ($logo -ne $LogoDest)) {
        New-Item -ItemType Directory -Path (Split-Path $LogoDest) -Force | Out-Null
        Copy-Item $logo $LogoDest -Force
        Write-Host "[OK] Logo integre" -ForegroundColor Green
        break
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  INSTALLATION TERMINEE !" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "  $Destination" -ForegroundColor Cyan
Write-Host ""
Write-Host "Cursor : File > Open Folder > ce dossier" -ForegroundColor White
Write-Host ""

explorer $Destination
Read-Host "Appuyez sur Entree pour fermer"
