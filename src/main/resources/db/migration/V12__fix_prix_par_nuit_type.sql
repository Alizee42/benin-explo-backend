-- Migration V12: Correction des types float8 restants non convertis par V6
-- La base Neon cloud a toujours des colonnes double precision malgré V6

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'hebergements'
          AND column_name = 'prix_par_nuit'
          AND data_type = 'double precision'
    ) THEN
        ALTER TABLE public.hebergements
            ALTER COLUMN prix_par_nuit TYPE NUMERIC(10,2) USING prix_par_nuit::NUMERIC(10,2);
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'reservations_hebergement'
          AND column_name = 'prix_total'
          AND data_type = 'double precision'
    ) THEN
        ALTER TABLE public.reservations_hebergement
            ALTER COLUMN prix_total TYPE NUMERIC(10,2) USING prix_total::NUMERIC(10,2);
    END IF;
END$$;
