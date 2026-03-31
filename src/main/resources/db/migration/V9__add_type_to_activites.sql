ALTER TABLE public.activites
    ADD COLUMN IF NOT EXISTS type character varying(255);

UPDATE public.activites
SET type = 'ACTIVITE'
WHERE type IS NULL;

ALTER TABLE public.activites
    ALTER COLUMN type SET DEFAULT 'ACTIVITE',
    ALTER COLUMN type SET NOT NULL;
