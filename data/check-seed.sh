#!/bin/sh

set -eu

DB_HOST="${POSTGRES_HOST:-postgres}"
DB_PORT="${POSTGRES_PORT:-5432}"
DB_USER="${POSTGRES_USER:-postgres}"
DB_NAME="${POSTGRES_DB:-airbus}"

export PGPASSWORD="${POSTGRES_PASSWORD:?POSTGRES_PASSWORD is required}"

psql_cmd() {
  psql \
    -h "$DB_HOST" \
    -p "$DB_PORT" \
    -U "$DB_USER" \
    -d "$DB_NAME" \
    -v ON_ERROR_STOP=1 \
    "$@"
}

echo "Waiting for PostgreSQL..."
until pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" >/dev/null 2>&1; do
  sleep 2
done

echo "Waiting for backend schema..."
until [ "$(psql_cmd -tA -c "SELECT to_regclass('public.zona_geografica') IS NOT NULL;")" = "t" ]; do
  sleep 2
done

echo "Checking whether the UrbanBus seed is already loaded..."
if [ "$(psql_cmd -tA -c "SELECT EXISTS (SELECT 1 FROM zona_geografica WHERE nome = 'Zona A - Centro') AND (SELECT COUNT(*) FROM paragem) >= 500;")" = "t" ]; then
  echo "Seed data already present, skipping."
  exit 0
fi

echo "Seed data missing, loading /seed/seed.sql..."
psql_cmd -f /seed/seed.sql
echo "Seed finished."
