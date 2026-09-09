#!/bin/bash
# Sauvegarde quotidienne de la base benin_explo (Postgres tournant dans Docker Compose).
# Usage prevu : cron sur le VPS, pas en local.
#
# 1. pg_dump compresse -> /opt/backups/benin_explo/
# 2. Upload vers Backblaze B2 (remote rclone "b2backup", bucket beniexplo-backups)
# 3. Purge des sauvegardes locales ET distantes de plus de RETENTION_DAYS jours
#
# Pre-requis sur le VPS : rclone configure (remote "b2backup"), docker compose
# avec un service "postgres", le fichier /opt/projects/.env avec POSTGRES_USER.

set -euo pipefail

COMPOSE_DIR="/opt/projects"
BACKUP_DIR="/opt/backups/benin_explo"
DB_NAME="benin_explo"
RETENTION_DAYS=7
RCLONE_REMOTE="b2backup:beniexplo-backups"
DATE=$(date +%Y%m%d-%H%M%S)
DUMP_FILE="${BACKUP_DIR}/benin_explo_${DATE}.sql.gz"

mkdir -p "$BACKUP_DIR"

POSTGRES_USER=$(grep '^POSTGRES_USER=' "${COMPOSE_DIR}/.env" | cut -d= -f2)

cd "$COMPOSE_DIR"
docker compose exec -T postgres pg_dump -U "$POSTGRES_USER" "$DB_NAME" | gzip > "$DUMP_FILE"

echo "Sauvegarde locale creee : $DUMP_FILE ($(du -h "$DUMP_FILE" | cut -f1))"

rclone copy "$DUMP_FILE" "$RCLONE_REMOTE" --quiet
echo "Upload vers Backblaze B2 termine."

# Purge locale : garder seulement les RETENTION_DAYS derniers jours
find "$BACKUP_DIR" -name "benin_explo_*.sql.gz" -mtime "+${RETENTION_DAYS}" -delete

# Purge distante : meme retention sur B2
rclone delete "$RCLONE_REMOTE" --min-age "${RETENTION_DAYS}d" --quiet

echo "Purge terminee (retention ${RETENTION_DAYS} jours). Sauvegarde OK."
