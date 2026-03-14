#!/usr/bin/env bash
set -euo pipefail

# Script para detener los microservicios iniciados con run-services.sh
# Uso: ./stop-services.sh [--keycloak]

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

STOP_KEYCLOAK=false
if [ "${1-}" = "--keycloak" ]; then
  STOP_KEYCLOAK=true
fi

PIDFILE="${ROOT_DIR}/run-services.pids"

if [ -f "${PIDFILE}" ]; then
  echo "Deteniendo microservicios usando ${PIDFILE}..."
  mapfile -t PIDS < "${PIDFILE}"
  rm -f "${PIDFILE}"
else
  echo "No se encontró ${PIDFILE}; buscando procesos (varias heurísticas)..."
  PIDS=()

  # 1) pgrep por patrones comunes
  PATTERNS=("mvnw spring-boot:run" "mvn spring-boot:run" "org.springframework.boot" "co.analisys")
  for pat in "${PATTERNS[@]}"; do
    while IFS= read -r pid; do
      [[ -n "$pid" ]] || continue
      PIDS+=("$pid")
    done < <(pgrep -f "$pat" || true)
  done

  # 2) buscar procesos que escuchan en los puertos conocidos de los microservicios (NO 8080=Keycloak)
  if command -v lsof >/dev/null 2>&1; then
    for port in 8081 8082 8083 8084; do
      while IFS= read -r pid; do
        [[ -n "$pid" ]] || continue
        # Verificar que NO sea docker (Keycloak) ni PID negativo
        if ! kill -0 "$pid" 2>/dev/null || [ "$pid" -lt 0 ]; then
          continue
        fi
        cmd=$(ps -p "$pid" -o cmd= 2>/dev/null || true)
        # Solo agregar si es proceso Java/Maven, no Docker
        if echo "$cmd" | grep -qE 'java|mvn|mvnw'; then
          PIDS+=("$pid")
        fi
      done < <(lsof -ti tcp:"$port" || true)
    done
  fi

  # 3) fallback: scan de ps para detectar comandos java/spring boot
  if [ ${#PIDS[@]} -eq 0 ]; then
    while IFS= read -r line; do
      pid=$(echo "$line" | awk '{print $1}')
      cmd=$(echo "$line" | cut -d' ' -f2-)
      if echo "$cmd" | grep -qE 'spring-boot:run|org.springframework.boot|co.analisys|GimnasioApplication'; then
        PIDS+=("$pid")
      fi
    done < <(ps -eo pid,cmd | tail -n +2)
  fi

  # unique
  if [ ${#PIDS[@]} -gt 0 ]; then
    mapfile -t PIDS < <(printf "%s\n" "${PIDS[@]}" | sort -u)
  fi
fi

if [ ${#PIDS[@]} -eq 0 ]; then
  echo "No se encontraron procesos de microservicios (spring-boot:run ni heurísticas aplicables)."
else
  echo "Deteniendo PIDs: ${PIDS[*]}"
  kill ${PIDS[*]} || true
  sleep 2
  # force kill remaining
  STILL_ALIVE=()
  for pid in "${PIDS[@]}"; do
    if kill -0 "$pid" 2>/dev/null; then
      STILL_ALIVE+=("$pid")
    fi
  done
  if [ ${#STILL_ALIVE[@]} -gt 0 ]; then
    echo "Forzando kill -9 a: ${STILL_ALIVE[*]}"
    kill -9 ${STILL_ALIVE[*]} || true
  fi
  echo "Microservicios detenidos."
fi

if [ "$STOP_KEYCLOAK" = true ]; then
  if command -v docker >/dev/null 2>&1; then
    echo "Deteniendo contenedor Keycloak (keycloak-gimnasio) si existe..."
    docker rm -f keycloak-gimnasio >/dev/null 2>&1 || true
    echo "Keycloak detenido."
  else
    echo "Docker no disponible; no se puede detener Keycloak." >&2
  fi
fi

echo "Hecho."