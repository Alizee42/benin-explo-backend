-- Les tarifs de transport/guide/chauffeur/pension etaient tous a 0 depuis la creation de la
-- table (insert initial V1 ne fixait que la devise), ce qui affichait "tarif a confirmer" /
-- l'avertissement "tarifs manquants" en permanence dans le formulaire circuit-personnalise.
-- Valeurs de depart realistes pour le Benin (EUR/jour), ajustables ensuite via l'admin.
UPDATE tarifs_circuit_personnalise
SET
    transport_compact_par_jour = 25.00,
    transport_familial_par_jour = 35.00,
    transport_minibus_par_jour = 50.00,
    transport_bus_par_jour = 80.00,
    guide_par_jour = 20.00,
    chauffeur_par_jour = 15.00,
    pension_complete_par_personne_par_jour = 18.00
WHERE id = 1;
