@echo off
setlocal enabledelayedexpansion

cd /d %~dp0

echo Verificando Docker...
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo Docker no está instalado o no está en PATH.
    exit /b 1
)

REM ---------------------------
REM CONFIGURACIÓN
REM ---------------------------
set KEYCLOAK_CONTAINER_NAME=keycloak-gimnasio
set KEYCLOAK_IMAGE=quay.io/keycloak/keycloak:26.5.4
set KEYCLOAK_PORT=8180
set KEYCLOAK_VOLUME_NAME=keycloak_gym_data

set RABBITMQ_CONTAINER_NAME=rabbitmq
set KAFKA_CONTAINER_NAME=kafka-gimnasio

REM ---------------------------
REM ELIMINAR CONTENEDORES
REM ---------------------------
for %%C in (%KEYCLOAK_CONTAINER_NAME% %RABBITMQ_CONTAINER_NAME% %KAFKA_CONTAINER_NAME%) do (
    docker ps -a --format "{{.Names}}" | findstr /x %%C >nul
    if !errorlevel! == 0 (
        echo Eliminando contenedor %%C...
        docker rm -f %%C >nul 2>nul
    )
)

REM ---------------------------
REM LEVANTAR CONTENEDORES
REM ---------------------------
echo Iniciando Keycloak...
docker run -d ^
  --name %KEYCLOAK_CONTAINER_NAME% ^
  -p %KEYCLOAK_PORT%:8080 ^
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin ^
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin ^
  -v %KEYCLOAK_VOLUME_NAME%:/opt/keycloak/data ^
    -v "%cd%\keycloak:/opt/keycloak/data/import" ^
  %KEYCLOAK_IMAGE% start-dev --import-realm

echo Iniciando RabbitMQ...
docker run -d ^
  --name %RABBITMQ_CONTAINER_NAME% ^
  -p 5672:5672 -p 15672:15672 ^
  rabbitmq:3.13-management

echo Iniciando Kafka...
docker run -d ^
  --name %KAFKA_CONTAINER_NAME% ^
  -p 9092:9092 ^
  apache/kafka:3.7.0

REM ---------------------------
REM ESPERAR KEYCLOAK
REM ---------------------------
echo Esperando a Keycloak...
set WAITED=0

:wait_loop
curl -s http://localhost:%KEYCLOAK_PORT% >nul 2>nul
if %errorlevel% neq 0 (
    timeout /t 2 >nul
    set /a WAITED+=2
    if %WAITED% geq 120 (
        echo Timeout esperando Keycloak
        exit /b 1
    )
    goto wait_loop
)

echo Keycloak listo. Iniciando microservicios...

REM ---------------------------
REM LOGS
REM ---------------------------
if not exist logs mkdir logs

set PIDFILE=run-services.pids
type nul > %PIDFILE%

REM ---------------------------
REM FUNCIÓN SIMULADA
REM ---------------------------
call :run_maven gimnasio .
call :run_maven service-registry service-registry
call :run_maven api-gateway api-gateway
call :run_maven microservicio-clases microservicio-clases
call :run_maven microservicio-entrenadores microservicio-entrenadores
call :run_maven microservicio-equipos microservicio-equipos
call :run_maven microservicio-miembros microservicio-miembros
call :run_maven microservicio-pagos microservicio-pagos

echo Todos los servicios iniciados
echo Logs en ./logs
goto :eof

:run_maven
set NAME=%1
set DIR=%2
set LOGFILE=logs\%NAME%.log

echo Iniciando %NAME%...

if exist "%DIR%\mvnw.cmd" (
    start "" cmd /c "cd /d %DIR% && mvnw.cmd spring-boot:run -DskipTests > ..\%LOGFILE% 2>&1"
) else (
    start "" cmd /c "cd /d %DIR% && mvn spring-boot:run -DskipTests > ..\%LOGFILE% 2>&1"
)

echo %NAME% iniciado
goto :eof