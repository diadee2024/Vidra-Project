@echo off
chcp 65001 >nul
title ZyroPlay - Ouvrir dans Cursor

set "PROJET=D:\PROJET DEV\ZYRO PLAY"

echo.
echo ========================================
echo   Ouverture de ZyroPlay dans Cursor...
echo ========================================
echo.

if not exist "%PROJET%\build.gradle.kts" (
    echo [ERREUR] Projet introuvable dans :
    echo   %PROJET%
    echo.
    echo Lancez d'abord l'installation Git ou le script PowerShell.
    pause
    exit /b 1
)

set "CURSOR_EXE="

if exist "%LOCALAPPDATA%\Programs\cursor\Cursor.exe" set "CURSOR_EXE=%LOCALAPPDATA%\Programs\cursor\Cursor.exe"
if exist "%LOCALAPPDATA%\Programs\Cursor\Cursor.exe" set "CURSOR_EXE=%LOCALAPPDATA%\Programs\Cursor\Cursor.exe"
if exist "%ProgramFiles%\Cursor\Cursor.exe" set "CURSOR_EXE=%ProgramFiles%\Cursor\Cursor.exe"
if exist "%ProgramFiles(x86)%\Cursor\Cursor.exe" set "CURSOR_EXE=%ProgramFiles(x86)%\Cursor\Cursor.exe"

where cursor >nul 2>&1
if %errorlevel%==0 (
    echo [OK] Lancement via commande cursor...
    start "" cursor "%PROJET%"
    goto :done
)

if defined CURSOR_EXE (
    echo [OK] Lancement : %CURSOR_EXE%
    start "" "%CURSOR_EXE%" "%PROJET%"
    goto :done
)

echo [ERREUR] Cursor n'est pas installe ou introuvable.
echo.
echo Telechargez Cursor ici : https://cursor.com/download
echo Puis relancez ce fichier.
echo.
pause
exit /b 1

:done
echo.
echo [OK] Cursor devrait s'ouvrir avec ZyroPlay !
echo Dossier : %PROJET%
echo.
timeout /t 4 >nul
