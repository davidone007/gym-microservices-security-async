#!/usr/bin/env bash
set -euo pipefail

# Script para levantar Keycloak (Docker) e iniciar los microservicios localmente (Maven)
# Uso: ./run-services.sh

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

echo "✅ Asegurando permisos de ejecución en los wrappers de Maven (mvnw)..."
# Hacer ejecutable el mvnw raíz y los mvnw dentro de los microservicios
[[ -f "${ROOT_DIR}/mvnw" ]] && chmod +x "${ROOT_DIR}/mvnw"

MICROSERVICIOS=(microservicio-clases microservicio-entrenadores microservicio-equipos microservicio-miembros microservicio-pagos)
for s in "${MICROSERVICIOS[@]}"; do
  [[ -f "${ROOT_DIR}/${s}/mvnw" ]] && chmod +x "${ROOT_DIR}/${s}/mvnw"
done

# Configuración de Keycloak
KEYCLOAK_CONTAINER_NAME="keycloak-gimnasio"
KEYCLOAK_IMAGE="quay.io/keycloak/keycloak:26.5.4"
KEYCLOAK_PORT=8180
KEYCLOAK_VOLUME_NAME="keycloak_gym_data"

echo "🔎 Verificando Docker..."
if ! command -v docker >/dev/null 2>&1; then
  echo "❌ Docker no está instalado o no está en PATH. Instala Docker para usar Keycloak en contenedor." >&2
  exit 1
fi

echo "🛑 Eliminando contenedor previo si existe..."
if docker ps -a --format '{{.Names}}' | grep -q "^${KEYCLOAK_CONTAINER_NAME}$"; then
  docker rm -f "${KEYCLOAK_CONTAINER_NAME}" >/dev/null 2>&1 || true
fi

echo "🚀 Iniciando Keycloak (imagen: ${KEYCLOAK_IMAGE})..."
docker run -d \
  --name "${KEYCLOAK_CONTAINER_NAME}" \
  -p "${KEYCLOAK_PORT}:8080" \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  -v "${KEYCLOAK_VOLUME_NAME}:/opt/keycloak/data" \
  -v "${ROOT_DIR}/keycloak:/opt/keycloak/data/import" \
  "${KEYCLOAK_IMAGE}" start-dev --import-realm

RABBITMQ_CONTAINER_NAME="rabbitmq"
echo "🛑 Eliminando contenedor previo de RabbitMQ si existe..."
if docker ps -a --format '{{.Names}}' | grep -q "^${RABBITMQ_CONTAINER_NAME}$"; then
  docker rm -f "${RABBITMQ_CONTAINER_NAME}" >/dev/null 2>&1 || true
fi

echo "🚀 Iniciando RabbitMQ..."
docker run -d \
  --name "${RABBITMQ_CONTAINER_NAME}" \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3.13-management

# Esperar que Keycloak esté disponible
if ! command -v curl >/dev/null 2>&1; then
  echo "❌ curl no está instalado. Necesario para verificar disponibilidad de Keycloak." >&2
  exit 1
fi

echo "⏳ Esperando a que Keycloak responda en http://localhost:${KEYCLOAK_PORT}/ ..."
MAX_WAIT=120
WAITED=0
until curl -sSf "http://localhost:${KEYCLOAK_PORT}/" >/dev/null 2>&1; do
  sleep 2
  WAITED=$((WAITED+2))
  if [ "$WAITED" -ge "$MAX_WAIT" ]; then
    echo "❌ Timeout esperando Keycloak. Revisa los logs: docker logs ${KEYCLOAK_CONTAINER_NAME}" >&2
    exit 1
  fi
done

echo "✅ Keycloak disponible. Iniciando microservicios..."

mkdir -p logs
# Guardaremos los pids de los microservicios para poder detenerlos con stop-services.sh
PIDFILE="${ROOT_DIR}/run-services.pids"
: >"${PIDFILE}"

run_maven() {
  local name="$1"
  local dir="$2"
  local logfile="${ROOT_DIR}/logs/${name}.log"
  mkdir -p "$(dirname "${logfile}")"
  echo "-> Iniciando ${name} (logs: ${logfile})..."
  if [ -x "${dir}/mvnw" ]; then
    (cd "${dir}" && nohup ./mvnw spring-boot:run -DskipTests >"${logfile}" 2>&1) &
  else
    (cd "${dir}" && nohup mvn spring-boot:run -DskipTests >"${logfile}" 2>&1) &
  fi
  local pid=$!
  echo "${pid}" >>"${PIDFILE}"
  echo "   ${name} iniciado (pid ${pid})."
}

# Lista de aplicaciones a levantar
APPS=( "gimnasio:." \
       "microservicio-clases:microservicio-clases" \
       "microservicio-entrenadores:microservicio-entrenadores" \
       "microservicio-equipos:microservicio-equipos" \
      "microservicio-miembros:microservicio-miembros" \
      "microservicio-pagos:microservicio-pagos" )

for app in "${APPS[@]}"; do
  IFS=":" read -r name dir <<< "$app"
  run_maven "$name" "$dir"
done

echo "✅ Todas las aplicaciones han sido solicitadas."
echo "📝 Revisa logs en: ./logs/*.log"
echo "📝 Para ver logs de Keycloak: docker logs -f ${KEYCLOAK_CONTAINER_NAME}"
echo "🎉 Listo."