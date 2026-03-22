-- Migration V6: Conversion des colonnes de prix de DOUBLE PRECISION vers NUMERIC(10,2)
-- pour éviter les erreurs d'arrondi sur les calculs financiers

ALTER TABLE public.hebergements
    ALTER COLUMN prix_par_nuit TYPE NUMERIC(10,2) USING prix_par_nuit::NUMERIC(10,2);

ALTER TABLE public.reservations_hebergement
    ALTER COLUMN prix_total TYPE NUMERIC(10,2) USING prix_total::NUMERIC(10,2);
