-- Migration V7: Ajout des index manquants pour les performances

-- Index sur les colonnes statut (filtrées fréquemment)
CREATE INDEX IF NOT EXISTS idx_circuits_personnalises_statut ON public.circuits_personnalises (statut);
CREATE INDEX IF NOT EXISTS idx_reservations_hebergement_statut ON public.reservations_hebergement (statut);

-- Index sur les clés étrangères (PostgreSQL ne les crée pas automatiquement)
CREATE INDEX IF NOT EXISTS idx_reservations_hebergement_id_hebergement ON public.reservations_hebergement (id_hebergement);
CREATE INDEX IF NOT EXISTS idx_devis_circuit_id ON public.devis (circuit_id);
CREATE INDEX IF NOT EXISTS idx_circuit_activites_circuit_id ON public.circuit_activites (circuit_id);
CREATE INDEX IF NOT EXISTS idx_circuit_activites_activite_id ON public.circuit_activites (activite_id);
CREATE INDEX IF NOT EXISTS idx_villes_zone_id ON public.villes (zone_id);
CREATE INDEX IF NOT EXISTS idx_activites_ville_id ON public.activites (ville_id);
CREATE INDEX IF NOT EXISTS idx_medias_circuit_id ON public.medias (circuit_id);
