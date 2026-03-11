DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'activites'
          AND column_name = 'ville_id'
    ) THEN
        ALTER TABLE activites ADD COLUMN ville_id BIGINT;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'activites'
          AND column_name = 'ville'
    ) THEN
        EXECUTE $sql$
            UPDATE activites a
            SET ville_id = v.id_ville
            FROM villes v
            WHERE lower(trim(a.ville)) = lower(trim(v.nom))
              AND a.ville_id IS NULL
        $sql$;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'activites'
          AND column_name = 'zone_id'
    ) THEN
        EXECUTE $sql$
            UPDATE activites a
            SET zone_id = v.zone_id
            FROM villes v
            WHERE a.ville_id = v.id_ville
              AND (a.zone_id IS NULL OR a.zone_id <> v.zone_id)
        $sql$;
    END IF;
END $$;
