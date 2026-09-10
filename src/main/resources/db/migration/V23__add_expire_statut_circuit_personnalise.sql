-- Ajoute le statut EXPIRE pour les devis personnalises acceptes mais jamais payes au-dela du
-- delai (14 jours). Distinct de REFUSE : le devis n'a pas ete refuse par l'admin, il a expire
-- faute de paiement du client. Trouve en audit (chantier relance/expiration).

ALTER TABLE circuits_personnalises DROP CONSTRAINT circuits_personnalises_statut_check;

ALTER TABLE circuits_personnalises ADD CONSTRAINT circuits_personnalises_statut_check
    CHECK (statut IN ('EN_ATTENTE','EN_TRAITEMENT','ACCEPTE','REFUSE','TERMINE','EXPIRE'));

-- Date d'envoi du rappel de paiement (7 jours apres acceptation), pour ne relancer qu'une fois.
ALTER TABLE circuits_personnalises ADD COLUMN IF NOT EXISTS date_rappel_paiement_envoye TIMESTAMP;
