-- V15: Link reservations to authenticated user accounts

ALTER TABLE public.reservations_hebergement
    ADD COLUMN IF NOT EXISTS utilisateur_id bigint;

ALTER TABLE public.reservations
    ADD COLUMN IF NOT EXISTS utilisateur_id bigint;

UPDATE public.reservations_hebergement rh
SET utilisateur_id = u.id
FROM public.utilisateurs u
WHERE rh.utilisateur_id IS NULL
  AND rh.email_client IS NOT NULL
  AND lower(trim(rh.email_client)) = lower(trim(u.email));

UPDATE public.reservations r
SET utilisateur_id = u.id
FROM public.utilisateurs u
WHERE r.utilisateur_id IS NULL
  AND r.email IS NOT NULL
  AND lower(trim(r.email)) = lower(trim(u.email));

ALTER TABLE public.reservations_hebergement
    DROP CONSTRAINT IF EXISTS fk_reservations_hebergement_utilisateur;

ALTER TABLE public.reservations_hebergement
    ADD CONSTRAINT fk_reservations_hebergement_utilisateur
    FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateurs(id)
    ON DELETE SET NULL;

ALTER TABLE public.reservations
    DROP CONSTRAINT IF EXISTS fk_reservations_utilisateur;

ALTER TABLE public.reservations
    ADD CONSTRAINT fk_reservations_utilisateur
    FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateurs(id)
    ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_reservations_hebergement_utilisateur_id
    ON public.reservations_hebergement(utilisateur_id);

CREATE INDEX IF NOT EXISTS idx_reservations_utilisateur_id
    ON public.reservations(utilisateur_id);
