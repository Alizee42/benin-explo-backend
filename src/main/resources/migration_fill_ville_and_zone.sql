-- Migration: add ville_id to activites and populate from villes, then propagate zone_id
-- IMPORTANT: make a DB backup before running.

BEGIN;

-- Add column ville_id if not exists (Postgres syntax)
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='activites' AND column_name='ville_id'
    ) THEN
        ALTER TABLE activites ADD COLUMN ville_id bigint;
    END IF;
END $$;

-- Fill ville_id by matching activites.ville (text) with villes.nom (case-insensitive)
UPDATE activites a
SET ville_id = v.id_ville
FROM villes v
WHERE lower(trim(a.ville)) = lower(trim(v.nom));

-- Propagate zone_id from villes if available
UPDATE activites a
SET zone_id = v.zone_id
FROM villes v
WHERE a.ville_id = v.id_ville;

COMMIT;

-- After running, verify results:
-- SELECT id_activite, nom, ville, ville_id, zone_id FROM activites WHERE ville_id IS NULL LIMIT 50;
-- SELECT z.nom AS zone, COUNT(a.*) AS nb_activites
-- FROM zones z LEFT JOIN activites a ON a.zone_id = z.id_zone
-- GROUP BY z.nom ORDER BY z.nom;
