#!/usr/bin/env bash
set -euo pipefail

# Script para detener los microservicios en Windows (Git Bash)
# Uso: ./stop-services.sh [--keycloak]

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

STOP_KEYCLOAK=false
if [ "${1-}" = "--keycloak" ]; then
  STOP_KEYCLOAK=true
fi

# ─── Puertos de los microservicios ────────────────────────────────────────────
# Ajusta estos puertos según tu configuración
PUERTOS=(8080 8081 8082 8083 8084 8085)

# ─── Función: obtener PID que usa un puerto (Windows via netstat) ─────────────
get_pid_for_port() {
  local port="$1"
  # netstat -ano muestra: Proto  Local      Foreign    State   PID
  # Buscamos líneas LISTENING en el puerto dado y extraemos el PID (última columna)
  netstat -ano 2>/dev/null \
    | grep -E "^[[:space:]]*(TCP|UDP)[[:space:]].*:${port}[[:space:]]" \
    | grep -i "LISTENING" \
    | awk '{print $NF}' \
    | sort -u \
    || true
}

# ─── Matar por puerto ─────────────────────────────────────────────────────────
echo "🔍 Buscando y matando procesos por puerto..."
for port in "${PUERTOS[@]}"; do
  pids=$(get_pid_for_port "$port")

  if [ -z "$pids" ]; then
    echo "   Puerto $port: libre."
    continue
  fi

  for pid in $pids; do
    # Ignorar PID 0 o 4 (Sistema en Windows)
    if [ "$pid" -le 4 ] 2>/dev/null; then
      continue
    fi

    echo "   Puerto $port → matando PID $pid..."
    cmd.exe /c "taskkill /PID $pid /F" >/dev/null 2>&1 \
      && echo "   ✅ PID $pid eliminado." \
      || echo "   ⚠️  No se pudo eliminar PID $pid (quizás ya terminó)."
  done
done

# ─── Segunda pasada: matar procesos java.exe relacionados al proyecto ─────────
echo ""
echo "🔍 Buscando procesos java.exe/mvnw huérfanos del proyecto..."

# WMIC lista procesos con su línea de comando completa
if command -v wmic >/dev/null 2>&1; then
  # Obtener todos los java.exe con su CommandLine y ProcessId
  while IFS= read -r line; do
    # Buscar líneas que contengan patrones del proyecto
    if echo "$line" | grep -qiE "spring-boot:run|co\.analisys|GimnasioApplication|microservicio"; then
      # Extraer el PID de la línea (formato: ...  1234)
      pid=$(echo "$line" | awk '{print $NF}' | tr -d '[:space:]')
      if [[ "$pid" =~ ^[0-9]+$ ]] && [ "$pid" -gt 4 ]; then
        echo "   Proceso huérfano encontrado (PID $pid), matando..."
        cmd.exe /c "taskkill /PID $pid /F" >/dev/null 2>&1 \
          && echo "   ✅ PID $pid eliminado." \
          || echo "   ⚠️  No se pudo eliminar PID $pid."
      fi
    fi
  done < <(wmic process where "name='java.exe'" get ProcessId,CommandLine 2>/dev/null || true)
else
  echo "   WMIC no disponible, omitiendo búsqueda de huérfanos."
fi

# ─── Limpiar pidfile si existe ────────────────────────────────────────────────
PIDFILE="${ROOT_DIR}/run-services.pids"
if [ -f "$PIDFILE" ]; then
  echo ""
  echo "🗑️  Eliminando ${PIDFILE}..."
  rm -f "$PIDFILE"
fi

# ─── Keycloak ─────────────────────────────────────────────────────────────────
if [ "$STOP_KEYCLOAK" = true ]; then
  if command -v docker >/dev/null 2>&1; then
    echo ""
    echo "🛑 Deteniendo contenedor Keycloak (keycloak-gimnasio)..."
    docker rm -f keycloak-gimnasio >/dev/null 2>&1 || true
    echo "   ✅ Keycloak detenido."
    
    echo "🛑 Deteniendo contenedor RabbitMQ (rabbitmq)..."
    docker rm -f rabbitmq >/dev/null 2>&1 || true
    echo "   ✅ RabbitMQ detenido."
  else
    echo "⚠️  Docker no disponible; no se puede detener Keycloak ni RabbitMQ." >&2
  fi
fi

echo ""
echo "✅ Hecho."