-- Test dataset aligned with the current JPA model.

DELETE FROM circuit_perso_jour_activites;
DELETE FROM circuit_personnalise_jours;
DELETE FROM circuits_personnalises;
DELETE FROM reservations_hebergement;
DELETE FROM reservations;
DELETE FROM devis;
DELETE FROM circuit_activites;
DELETE FROM circuits;
DELETE FROM activites;
DELETE FROM hebergements;
DELETE FROM medias;
DELETE FROM villes;
DELETE FROM zones;
DELETE FROM utilisateurs;

INSERT INTO zones (nom, description) VALUES
('Sud', 'Zone cotiere'),
('Centre', 'Zone culturelle'),
('Nord', 'Zone nature');

INSERT INTO villes (nom, zone_id) VALUES
('Cotonou', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Ouidah', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Abomey', (SELECT id_zone FROM zones WHERE nom = 'Centre')),
('Natitingou', (SELECT id_zone FROM zones WHERE nom = 'Nord'));

INSERT INTO activites (nom, description, ville_id, duree_interne, poids, difficulte) VALUES
(
    'Visite du Marche Dantokpa',
    'Grand marche populaire de Cotonou',
    (SELECT id_ville FROM villes WHERE nom = 'Cotonou'),
    180,
    10,
    'FACILE'
),
(
    'Route des Esclaves',
    'Parcours historique a Ouidah',
    (SELECT id_ville FROM villes WHERE nom = 'Ouidah'),
    150,
    8,
    'FACILE'
),
(
    'Palais Royaux',
    'Patrimoine historique d Abomey',
    (SELECT id_ville FROM villes WHERE nom = 'Abomey'),
    180,
    9,
    'MOYEN'
),
(
    'Cascade de Tanougou',
    'Site naturel dans le Nord',
    (SELECT id_ville FROM villes WHERE nom = 'Natitingou'),
    240,
    7,
    'MOYEN'
);

INSERT INTO circuits (
    nom,
    description,
    resume,
    duree_indicative,
    prix_indicatif,
    formule_proposee,
    actif,
    ville_id,
    img,
    galerie,
    programme,
    points_forts,
    inclus,
    non_inclus,
    tourisme,
    aventures
) VALUES (
    'Circuit decouverte Sud',
    'Circuit de reference pour les tests backend.',
    'Resume de reference pour les tests.',
    '3 jours / 2 nuits',
    185000,
    'Standard',
    TRUE,
    (SELECT id_ville FROM villes WHERE nom = 'Cotonou'),
    'https://example.com/circuit-sud.jpg',
    '[]',
    '[]',
    '[]',
    '[]',
    '[]',
    '[]',
    '[]'
);

INSERT INTO hebergements (
    nom,
    type,
    localisation,
    quartier,
    description,
    prix_par_nuit,
    image_urls
) VALUES (
    'Hotel Test Cotonou',
    'Hotel',
    'Cotonou',
    'Haie Vive',
    'Hebergement de reference pour les tests.',
    72.5,
    '[\"/assets/images/test-hebergement.jpg\"]'
);

INSERT INTO circuits_personnalises (
    nom_client,
    prenom_client,
    email_client,
    telephone_client,
    message_client,
    nombre_jours,
    nombre_personnes,
    date_creation,
    date_voyage_souhaitee,
    avec_hebergement,
    type_hebergement,
    avec_transport,
    type_transport,
    avec_guide,
    avec_chauffeur,
    pension_complete,
    prix_estime,
    prix_final,
    statut
) VALUES (
    'Mensah',
    'Kofi',
    'kofi.mensah@example.com',
    '+22996123456',
    'Demande de circuit personnalise pour les tests.',
    2,
    4,
    CURRENT_DATE,
    DATEADD('DAY', 30, CURRENT_DATE),
    TRUE,
    'confort',
    TRUE,
    'voiture',
    TRUE,
    FALSE,
    FALSE,
    200000,
    NULL,
    'EN_ATTENTE'
);

INSERT INTO circuit_personnalise_jours (
    circuit_personnalise_id,
    numero_jour,
    zone_id,
    ville_id,
    description_jour
) VALUES
(
    (SELECT id FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com'),
    1,
    (SELECT id_zone FROM zones WHERE nom = 'Sud'),
    (SELECT id_ville FROM villes WHERE nom = 'Cotonou'),
    'Jour 1 a Cotonou'
),
(
    (SELECT id FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com'),
    2,
    (SELECT id_zone FROM zones WHERE nom = 'Sud'),
    (SELECT id_ville FROM villes WHERE nom = 'Ouidah'),
    'Jour 2 a Ouidah'
);

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT cpj.id, a.id_activite
FROM circuit_personnalise_jours cpj
JOIN activites a ON a.nom = 'Visite du Marche Dantokpa'
WHERE cpj.numero_jour = 1
  AND cpj.circuit_personnalise_id = (
      SELECT id FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com'
  );

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT cpj.id, a.id_activite
FROM circuit_personnalise_jours cpj
JOIN activites a ON a.nom = 'Route des Esclaves'
WHERE cpj.numero_jour = 2
  AND cpj.circuit_personnalise_id = (
      SELECT id FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com'
  );
