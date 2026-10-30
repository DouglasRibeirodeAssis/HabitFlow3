@echo off
setlocal
set "DESTINO=%~dp0gradle\wrapper\gradle-wrapper.jar"
set "SHA=2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046"
if not exist "%DESTINO%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "try { Invoke-WebRequest -UseBasicParsing -Uri 'https://services.gradle.org/distributions/gradle-8.11.1-wrapper.jar' -OutFile '%DESTINO%' -ErrorAction Stop } catch { Write-Host $_; exit 1 }"
  if errorlevel 1 (
    echo Nao foi possivel obter o Gradle Wrapper. Verifique sua conexao.
    exit /b 1
  )
)
powershell -NoProfile -ExecutionPolicy Bypass -Command "if ((Get-FileHash -Algorithm SHA256 '%DESTINO%').Hash.ToLower() -ne '%SHA%') { exit 1 }"
if errorlevel 1 (
  del "%DESTINO%"
  echo Verificacao de seguranca falhou. Execute novamente.
  exit /b 1
)
echo Gradle Wrapper pronto.
exit /b 0
