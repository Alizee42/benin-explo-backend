CREATE TABLE IF NOT EXISTS public.tarifs_circuit_personnalise (
    id bigserial PRIMARY KEY,
    devise character varying(10) NOT NULL DEFAULT 'EUR',
    transport_compact_par_jour numeric(12,2) NOT NULL DEFAULT 0,
    transport_familial_par_jour numeric(12,2) NOT NULL DEFAULT 0,
    transport_minibus_par_jour numeric(12,2) NOT NULL DEFAULT 0,
    transport_bus_par_jour numeric(12,2) NOT NULL DEFAULT 0,
    guide_par_jour numeric(12,2) NOT NULL DEFAULT 0,
    chauffeur_par_jour numeric(12,2) NOT NULL DEFAULT 0,
    pension_complete_par_personne_par_jour numeric(12,2) NOT NULL DEFAULT 0
);

INSERT INTO public.tarifs_circuit_personnalise (
    devise,
    transport_compact_par_jour,
    transport_familial_par_jour,
    transport_minibus_par_jour,
    transport_bus_par_jour,
    guide_par_jour,
    chauffeur_par_jour,
    pension_complete_par_personne_par_jour
)
SELECT 'EUR', 0, 0, 0, 0, 0, 0, 0
WHERE NOT EXISTS (
    SELECT 1 FROM public.tarifs_circuit_personnalise
);

ALTER TABLE public.circuits_personnalises
    ADD COLUMN IF NOT EXISTS prix_activites_estime numeric(12,2),
    ADD COLUMN IF NOT EXISTS prix_hebergement_estime numeric(12,2),
    ADD COLUMN IF NOT EXISTS prix_transport_estime numeric(12,2),
    ADD COLUMN IF NOT EXISTS prix_guide_estime numeric(12,2),
    ADD COLUMN IF NOT EXISTS prix_chauffeur_estime numeric(12,2),
    ADD COLUMN IF NOT EXISTS prix_pension_complete_estime numeric(12,2);

UPDATE public.circuits_personnalises
SET prix_activites_estime = COALESCE(prix_activites_estime, prix_estime, 0),
    prix_hebergement_estime = COALESCE(prix_hebergement_estime, 0),
    prix_transport_estime = COALESCE(prix_transport_estime, 0),
    prix_guide_estime = COALESCE(prix_guide_estime, 0),
    prix_chauffeur_estime = COALESCE(prix_chauffeur_estime, 0),
    prix_pension_complete_estime = COALESCE(prix_pension_complete_estime, 0),
    devise_prix_estime = COALESCE(devise_prix_estime, 'EUR');

ALTER TABLE public.circuits_personnalises
    ALTER COLUMN prix_activites_estime SET DEFAULT 0,
    ALTER COLUMN prix_hebergement_estime SET DEFAULT 0,
    ALTER COLUMN prix_transport_estime SET DEFAULT 0,
    ALTER COLUMN prix_guide_estime SET DEFAULT 0,
    ALTER COLUMN prix_chauffeur_estime SET DEFAULT 0,
    ALTER COLUMN prix_pension_complete_estime SET DEFAULT 0;

ALTER TABLE public.circuits_personnalises
    ALTER COLUMN prix_activites_estime SET NOT NULL,
    ALTER COLUMN prix_hebergement_estime SET NOT NULL,
    ALTER COLUMN prix_transport_estime SET NOT NULL,
    ALTER COLUMN prix_guide_estime SET NOT NULL,
    ALTER COLUMN prix_chauffeur_estime SET NOT NULL,
    ALTER COLUMN prix_pension_complete_estime SET NOT NULL;
