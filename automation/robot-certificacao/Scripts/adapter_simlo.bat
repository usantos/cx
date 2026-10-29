@echo off
setlocal
cd /d "%~dp0.."
if not exist ".venv\Scripts\python.exe" (
  echo Crie o ambiente .venv e instale requirements.lock.txt antes de executar.
  exit /b 1
)
".venv\Scripts\python.exe" "Scripts\adapter_rqm.py"
exit /b %ERRORLEVEL%
