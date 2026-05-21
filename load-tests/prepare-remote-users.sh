#!/usr/bin/env bash

set -euo pipefail

COUNT="${1:-3000}"
VM_NAME="${VM_NAME:-airbus-vm}"
VM_USER="${VM_USER:-airbus}"
GCP_ZONE="${GCP_ZONE:-us-central1-a}"
GCP_PROJECT="${GCP_PROJECT:-}"
REMOTE_DIR="${REMOTE_DIR:-/opt/airbus}"
REMOTE_SQL_PATH="/tmp/load-users.sql"
PASSWORD_HASH='$2b$10$ssY7oCZujrzDGe9P9FBKm.QUqlc8DYBwb.F8Jzl6jdfjfX6ESsNMe'

if ! [[ "$COUNT" =~ ^[0-9]+$ ]] || [ "$COUNT" -lt 1 ]; then
  echo "Usage: $0 [num_users]" >&2
  echo "num_users must be a positive integer." >&2
  exit 1
fi

if ! command -v gcloud >/dev/null 2>&1; then
  echo "gcloud is required locally." >&2
  exit 1
fi

if [ -z "$GCP_PROJECT" ]; then
  echo "Set GCP_PROJECT in the environment before running this script." >&2
  exit 1
fi

tmp_sql="$(mktemp)"
cleanup() {
  rm -f "$tmp_sql"
}
trap cleanup EXIT

{
  echo "-- ${COUNT} utilizadores de carga, password: 1234567890"
  echo
  echo "-- limpar dados de carga anteriores"
  echo "DELETE FROM viagem"
  echo "  WHERE utente_id IN (SELECT id FROM utente WHERE email LIKE 'load\\_%@test.pt' ESCAPE '\\');"
  echo "DELETE FROM titulo_zona WHERE titulo_id IN ("
  echo "  SELECT id FROM titulo_transporte WHERE utente_id IN ("
  echo "    SELECT id FROM utente WHERE email LIKE 'load\\_%@test.pt' ESCAPE '\\'"
  echo "  )"
  echo ");"
  echo "DELETE FROM titulo_transporte"
  echo "  WHERE utente_id IN (SELECT id FROM utente WHERE email LIKE 'load\\_%@test.pt' ESCAPE '\\');"
  echo "DELETE FROM utente WHERE email LIKE 'load\\_%@test.pt' ESCAPE '\\';"
  echo
  echo "WITH new_users AS ("
  echo "  INSERT INTO utente (nome, email, password) VALUES"

  for i in $(seq 1 "$COUNT"); do
    printf "  ('Load User %03d', 'load_%03d@test.pt', '%s')" "$i" "$i" "$PASSWORD_HASH"
    if [ "$i" -lt "$COUNT" ]; then
      printf ",\n"
    else
      printf "\n"
    fi
  done

  echo "  RETURNING id"
  echo "),"
  echo "new_titles AS ("
  echo "  INSERT INTO titulo_transporte"
  echo "    (utente_id, data_aquisicao, estado, tipo_titulo, data_inicio, data_fim)"
  echo "  SELECT id, NOW(), 'ATIVO', 'PASSE_ANUAL', '2026-01-01', '2027-12-31'"
  echo "  FROM new_users"
  echo "  RETURNING id"
  echo ")"
  echo "INSERT INTO titulo_zona (titulo_id, zona_id)"
  echo "SELECT t.id, z.zona_id"
  echo "FROM new_titles t"
  echo "CROSS JOIN (VALUES (1), (2), (3)) AS z(zona_id);"
} > "$tmp_sql"

echo "Uploading SQL to ${VM_USER}@${VM_NAME}:${REMOTE_SQL_PATH}..."
gcloud compute ssh \
  --project="$GCP_PROJECT" \
  --zone="$GCP_ZONE" \
  --tunnel-through-iap \
  --quiet \
  "${VM_USER}@${VM_NAME}" \
  --command="cat > '${REMOTE_SQL_PATH}'" < "$tmp_sql"

echo "Importing ${COUNT} load users into postgres on ${VM_NAME}..."
gcloud compute ssh \
  --project="$GCP_PROJECT" \
  --zone="$GCP_ZONE" \
  --tunnel-through-iap \
  --quiet \
  "${VM_USER}@${VM_NAME}" \
  --command="set -euo pipefail
    cd '${REMOTE_DIR}'
    sudo docker compose exec -T postgres psql -U postgres -d airbus -v ON_ERROR_STOP=1 -f /dev/stdin < '${REMOTE_SQL_PATH}'
    rm -f '${REMOTE_SQL_PATH}'
    sudo docker compose exec -T postgres psql -U postgres -d airbus -tA -c \"SELECT COUNT(*) FROM utente WHERE email LIKE 'load\\\\_%@test.pt' ESCAPE '\\\\';\""

echo "Remote load users ready."
