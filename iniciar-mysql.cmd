@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\iniciar-mysql.ps1" %*
