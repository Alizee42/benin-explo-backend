-- Ajoute une colonne JSON pour stocker les activites associees a un circuit, dans le
-- meme esprit que galerie/inclus/aventures (liste JSON, pas de table de jonction).
-- Corrige un bug trouve en audit : le champ activiteIds existait deja dans le DTO/frontend
-- (envoye a la creation/edition d'un circuit) mais n'etait jamais lu ni ecrit cote backend,
-- rendant la section "Activites associees" toujours vide cote public.

ALTER TABLE circuits ADD COLUMN IF NOT EXISTS activite_ids TEXT;

-- Supprime le champ "tourisme" : jamais implemente (ni saisi en admin, ni lu/ecrit par le
-- backend), colonne morte depuis le schema initial. Trouve en audit.
ALTER TABLE circuits DROP COLUMN IF EXISTS tourisme;
