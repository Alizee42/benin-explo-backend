-- Suppression des tables devenues code mort (aucune entite JPA, aucun controller,
-- jamais appelees par le frontend) :
-- - devis / devis_activites : premier prototype de demande de circuit (tout premier
--   commit du projet), remplace depuis par le systeme circuits_personnalises (5 etapes).
-- - circuit_activites : table de liaison circuit<->activite jamais raccordee au modele
--   reel (l'entite Circuit ne reference aucune activite liee).
-- - vehicules : CRUD complet jamais appele ; le flux transport actif
--   (circuits_personnalises.type_transport) utilise un champ texte libre, sans relation
--   vers cette table.
--
-- tombolas / participations_tombola sont volontairement conservees : modele plus riche
-- que tombola_participants, prevu comme socle du futur developpement de la tombola.

DROP TABLE IF EXISTS devis_activites;
DROP TABLE IF EXISTS devis;
DROP TABLE IF EXISTS circuit_activites;
DROP TABLE IF EXISTS vehicules;
