#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

backend_pid=""
frontend_pid=""
backend_pgid=""
frontend_pgid=""

pgid_for_pid() {
  local pid="$1"
  ps -o pgid= -p "${pid}" | tr -d ' '
}

cleanup() {
  if [[ -n "${backend_pgid}" ]]; then
    kill -TERM "-${backend_pgid}" 2>/dev/null || true
  elif [[ -n "${backend_pid}" ]]; then
    kill -TERM "${backend_pid}" 2>/dev/null || true
  fi

  if [[ -n "${frontend_pgid}" ]]; then
    kill -TERM "-${frontend_pgid}" 2>/dev/null || true
  elif [[ -n "${frontend_pid}" ]]; then
    kill -TERM "${frontend_pid}" 2>/dev/null || true
  fi
}

trap cleanup EXIT INT TERM

(cd "${ROOT_DIR}/backend" && {
  if [[ -x "./gradlew" ]]; then
    echo "Running backend checks (tests + coverage)..."
    ./gradlew check

    echo "Starting backend..."
    ./gradlew bootRun
  else
    if ! command -v gradle >/dev/null 2>&1; then
      echo "Gradle is not installed and ./gradlew is missing. Install Gradle or run 'gradle wrapper' in backend." >&2
      exit 1
    fi
    gradle bootRun
  fi
}) &
backend_pid=$!
backend_pgid="$(pgid_for_pid "${backend_pid}")"

(cd "${ROOT_DIR}/frontend" && npm run dev) &
frontend_pid=$!
frontend_pgid="$(pgid_for_pid "${frontend_pid}")"

wait "${backend_pid}" "${frontend_pid}"
