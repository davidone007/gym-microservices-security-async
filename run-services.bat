@echo off
setlocal enabledelayedexpansion

:: Script para levantar Keycloak, RabbitMQ, Kafka (Docker) e iniciar los microservicios localmente (Maven) en Windows

echo ===========================================
echo  Iniciando Infraestructura y Servicios
echo ===========================================
echo.

:: Configuración
set KEYCLOAK_CONTAINER_NAME=keycloak-gimnasio
set KEYCLOAK_IMAGE=quay.io/keycloak/keycloak:26.5.4
set KEYCLOAK_PORT=8180
set KEYCLOAK_VOLUME_NAME=keycloak_gym_data
set RABBITMQ_CONTAINER_NAME=rabbitmq
set KAFKA_CONTAINER_NAME=kafka-gimnasio

echo [1/4] Verificando Docker...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
  echo ERROR: Docker no esta instalado o no esta en PATH.
  exit /b 1
)

echo.
echo [2/4] Preparando contenedores de Infraestructura...

:: KEYCLOAK
echo 🛑 Eliminando contenedor previo de Keycloak...
docker rm -f %KEYCLOAK_CONTAINER_NAME% >nul 2>&1

echo 🚀 Iniciando Keycloak...
docker run -d ^
  --name %KEYCLOAK_CONTAINER_NAME% ^
  -p %KEYCLOAK_PORT%:8080 ^
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin ^
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin ^
  -v %KEYCLOAK_VOLUME_NAME%:/opt/keycloak/data ^
  -v %~dp0keycloak:/opt/keycloak/data/import ^
  %KEYCLOAK_IMAGE% start-dev --import-realm

:: RABBITMQ
echo 🛑 Eliminando contenedor previo de RabbitMQ...
docker rm -f %RABBITMQ_CONTAINER_NAME% >nul 2>&1

echo 🚀 Iniciando RabbitMQ...
docker run -d ^
  --name %RABBITMQ_CONTAINER_NAME% ^
  -p 5672:5672 -p 15672:15672 ^
  rabbitmq:3.13-management

:: KAFKA
echo 🛑 Eliminando contenedor previo de Kafka...
docker rm -f %KAFKA_CONTAINER_NAME% >nul 2>&1

echo 🚀 Iniciando Kafka...
docker run -d ^
  --name %KAFKA_CONTAINER_NAME% ^
  -p 9092:9092 ^
  apache/kafka:3.7.0

echo.
echo [3/4] Esperando a que Keycloak inicie...
:wait_keycloak
curl -sSf http://localhost:%KEYCLOAK_PORT%/ >nul 2>&1
if %errorlevel% neq 0 (
  echo ⏳ Esperando Keycloak...
  timeout /t 3 /nobreak >nul
  goto wait_keycloak
)
echo ✅ Keycloak esta listo!

echo.
echo [4/4] Iniciando microservicios...
if not exist "logs" mkdir logs

:: Función o macro para iniciar servicios (en Batch simulado encadenando comandos)
echo -> Iniciando gimnasio en puerto 8080
start "gimnasio" cmd /c "mvnw spring-boot:run -DskipTests > logs\gimnasio.log 2>&1"

echo -> Iniciando microservicio-clases en puerto 8082
start "clases" cmd /c "cd microservicio-clases && call mvnw.cmd spring-boot:run -DskipTests > ..\logs\microservicio-clases.log 2>&1"

echo -> Iniciando microservicio-entrenadores en puerto 8083
start "entrenadores" cmd /c "cd microservicio-entrenadores && call mvnw.cmd spring-boot:run -DskipTests > ..\logs\microservicio-entrenadores.log 2>&1"

echo -> Iniciando microservicio-equipos en puerto 8084
start "equipos" cmd /c "cd microservicio-equipos && call mvnw.cmd spring-boot:run -DskipTests > ..\logs\microservicio-equipos.log 2>&1"

echo -> Iniciando microservicio-miembros en puerto 8081
start "miembros" cmd /c "cd microservicio-miembros && call mvnw.cmd spring-boot:run -DskipTests > ..\logs\microservicio-miembros.log 2>&1"

echo -> Iniciando microservicio-pagos en puerto 8085
start "pagos" cmd /c "cd microservicio-pagos && call mvnw.cmd spring-boot:run -DskipTests > ..\logs\microservicio-pagos.log 2>&1"

echo.
echo ✅ Todos los servicios han sido solicitados (chequea la carpeta 'logs').
echo ===========================================
echo  Finalizado.
echo ===========================================
endlocal
