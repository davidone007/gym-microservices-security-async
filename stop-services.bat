@echo off
setlocal enabledelayedexpansion

:: Script para detener los microservicios del gym en Windows
:: Uso: stop-services.bat [--keycloak]

set STOP_KEYCLOAK=false
if "%1"=="--keycloak" set STOP_KEYCLOAK=true

echo.
echo ===========================================
echo  Deteniendo microservicios del gym
echo ===========================================
echo.

:: ─── Puertos a liberar ────────────────────────────────────────────────────────
:: Ajusta este listado si tus puertos cambian
set PUERTOS=8080 8081 8082 8083 8084 8085

echo [1/2] Matando procesos por puerto...
echo.

for %%P in (%PUERTOS%) do (
  echo    Buscando proceso en puerto %%P...

  :: netstat -ano: ultima columna es el PID
  :: findstr busca lineas con ese puerto en estado LISTENING
  for /f "tokens=5" %%I in ('netstat -ano ^| findstr ":%%P " ^| findstr "LISTENING"') do (
    set PID=%%I

    :: Ignorar PIDs del sistema (0 y 4)
    if !PID! GTR 4 (
      echo    Puerto %%P ^-^> matando PID !PID!...
      taskkill /PID !PID! /F >nul 2>&1
      if !errorlevel! == 0 (
        echo    OK: PID !PID! eliminado.
      ) else (
        echo    WARN: No se pudo eliminar PID !PID!
      )
    )
  )
)

echo.
echo [2/2] Buscando procesos java.exe huerfanos del proyecto...
echo.

:: Buscar java.exe cuya línea de comando contenga patrones del proyecto
:: wmic devuelve la CommandLine completa de cada proceso java
for /f "tokens=1,*" %%A in ('wmic process where "name='java.exe'" get ProcessId^,CommandLine /format:csv 2^>nul ^| findstr /i "spring-boot:run co.analisys GimnasioApplication microservicio"') do (
  :: La salida CSV de wmic es: Node,CommandLine,ProcessId
  :: Tomamos el ultimo token que es el PID
  for %%Z in (%%A %%B) do set LAST=%%Z
  set PID=!LAST!

  if defined PID (
    echo    Huerfano encontrado ^-^> matando PID !PID!...
    taskkill /PID !PID! /F >nul 2>&1
    if !errorlevel! == 0 (
      echo    OK: PID !PID! eliminado.
    ) else (
      echo    WARN: No se pudo eliminar !PID!
    )
    set PID=
  )
)

:: ─── Keycloak ─────────────────────────────────────────────────────────────────
if "%STOP_KEYCLOAK%"=="true" (
  echo.
  echo Deteniendo contenedor Keycloak...
  docker rm -f keycloak-gimnasio >nul 2>&1
  if !errorlevel! == 0 (
    echo OK: Keycloak detenido.
  ) else (
    echo WARN: No se encontro el contenedor keycloak-gimnasio.
  )
)

:: ─── Limpiar pidfile si existe ────────────────────────────────────────────────
if exist "run-services.pids" (
  del /f "run-services.pids" >nul 2>&1
  echo Pidfile eliminado.
)

echo.
echo ===========================================
echo  Listo.
echo ===========================================
echo.
endlocal
