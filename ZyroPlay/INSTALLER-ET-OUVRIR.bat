@echo off
chcp 65001 >nul
title ZyroPlay - Installation + Cursor

echo Installation puis ouverture dans Cursor...
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0install-sur-windows.ps1"

if exist "%~dp0OUVRIR-DANS-CURSOR.bat" (
    call "%~dp0OUVRIR-DANS-CURSOR.bat"
) else if exist "D:\PROJET DEV\ZYRO PLAY\OUVRIR-DANS-CURSOR.bat" (
    call "D:\PROJET DEV\ZYRO PLAY\OUVRIR-DANS-CURSOR.bat"
)
