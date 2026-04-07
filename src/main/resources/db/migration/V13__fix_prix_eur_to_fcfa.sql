-- Migration V13 : Normalisation des prix en EUR
-- Les données insérées directement en SQL (seed/reset) utilisaient des valeurs FCFA (ex: 185000, 680000).
-- Le formulaire admin (edit-circuit) stocke en EUR via getPrixIndicatifEnEur().
-- Cette migration convertit les valeurs FCFA (> 5000) en EUR en divisant par le taux fixe 655,957.
-- Les valeurs ≤ 5000 sont déjà en EUR et ne sont pas touchées.

-- 1. Circuits : prix_indicatif  (ex: 185000 FCFA → 282 EUR)
UPDATE public.circuits
SET prix_indicatif = ROUND(CAST(prix_indicatif / 655.957 AS NUMERIC), 2)
WHERE prix_indicatif IS NOT NULL
  AND prix_indicatif > 5000;

-- 2. Hébergements : prix_par_nuit  (ex: 85000 FCFA → 130 EUR)
UPDATE public.hebergements
SET prix_par_nuit = ROUND(CAST(prix_par_nuit / 655.957 AS NUMERIC), 2)
WHERE prix_par_nuit IS NOT NULL
  AND prix_par_nuit > 5000;

-- 3. Circuits personnalisés : prix_estime, prix_final et sous-totaux
UPDATE public.circuits_personnalises
SET prix_estime = ROUND(CAST(prix_estime / 655.957 AS NUMERIC), 2)
WHERE prix_estime IS NOT NULL AND prix_estime > 5000;

UPDATE public.circuits_personnalises
SET prix_final = ROUND(CAST(prix_final / 655.957 AS NUMERIC), 2)
WHERE prix_final IS NOT NULL AND prix_final > 5000;

UPDATE public.circuits_personnalises
SET prix_activites_estime      = ROUND(CAST(prix_activites_estime / 655.957      AS NUMERIC), 2),
    prix_hebergement_estime    = ROUND(CAST(prix_hebergement_estime / 655.957    AS NUMERIC), 2),
    prix_transport_estime      = ROUND(CAST(prix_transport_estime / 655.957      AS NUMERIC), 2),
    prix_guide_estime          = ROUND(CAST(prix_guide_estime / 655.957          AS NUMERIC), 2),
    prix_chauffeur_estime      = ROUND(CAST(prix_chauffeur_estime / 655.957      AS NUMERIC), 2),
    prix_pension_complete_estime = ROUND(CAST(prix_pension_complete_estime / 655.957 AS NUMERIC), 2)
WHERE (prix_activites_estime > 5000
    OR prix_hebergement_estime > 5000
    OR prix_transport_estime > 5000
    OR prix_guide_estime > 5000
    OR prix_chauffeur_estime > 5000
    OR prix_pension_complete_estime > 5000);

-- 4. Réservations hébergement : prix_total
UPDATE public.reservations_hebergement
SET prix_total = ROUND(CAST(prix_total / 655.957 AS NUMERIC), 2)
WHERE prix_total IS NOT NULL
  AND prix_total > 5000;

-- 5. Tarifs circuit personnalisé : les montants sont aussi en FCFA
UPDATE public.tarifs_circuit_personnalise
SET transport_compact_par_jour           = ROUND(CAST(transport_compact_par_jour / 655.957           AS NUMERIC), 2),
    transport_familial_par_jour          = ROUND(CAST(transport_familial_par_jour / 655.957          AS NUMERIC), 2),
    transport_minibus_par_jour           = ROUND(CAST(transport_minibus_par_jour / 655.957           AS NUMERIC), 2),
    transport_bus_par_jour               = ROUND(CAST(transport_bus_par_jour / 655.957               AS NUMERIC), 2),
    guide_par_jour                       = ROUND(CAST(guide_par_jour / 655.957                       AS NUMERIC), 2),
    chauffeur_par_jour                   = ROUND(CAST(chauffeur_par_jour / 655.957                   AS NUMERIC), 2),
    pension_complete_par_personne_par_jour = ROUND(CAST(pension_complete_par_personne_par_jour / 655.957 AS NUMERIC), 2),
    devise                               = 'EUR'
WHERE (transport_compact_par_jour > 5000
    OR guide_par_jour > 5000);
