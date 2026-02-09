-- ================================================================
-- DONNÉES DE TEST POUR BENIN EXPLO (Structure réelle de la base)
-- ================================================================

-- Supprimer les données existantes
DELETE FROM circuit_perso_jour_activites WHERE 1=1;
DELETE FROM circuit_personnalise_jours WHERE 1=1;
DELETE FROM circuits_personnalises WHERE 1=1;
DELETE FROM circuit_activites WHERE 1=1;
DELETE FROM circuits WHERE 1=1;
DELETE FROM activites WHERE 1=1;
DELETE FROM villes WHERE 1=1;
DELETE FROM zones WHERE 1=1;

-- ================================================================
-- ZONES
-- ================================================================
INSERT INTO zones (nom, description) VALUES
('Sud', 'Zone côtière avec plages et patrimoine historique'),
('Centre', 'Zone centrale avec culture et artisanat'),
('Nord', 'Zone touristique avec parcs nationaux');

-- ================================================================
-- VILLES  
-- ================================================================
INSERT INTO villes (nom, zone_id) VALUES
-- Villes du Sud (zone_id = 1)
('Cotonou', 1),
('Ouidah', 1),
('Grand-Popo', 1),

-- Villes du Centre (zone_id = 2)
('Abomey', 2),
('Bohicon', 2),
('Dassa-Zoumé', 2),

-- Villes du Nord (zone_id = 3)
('Parakou', 3),
('Natitingou', 3),
('Djougou', 3);

-- ================================================================
-- ACTIVITÉS (structure réelle: id_activite, nom, description, ville_id, zone_id)
-- ================================================================

-- Activités de Cotonou (ville_id=1, zone_id=1)
INSERT INTO activites (nom, description, ville_id, zone_id, duree_interne) VALUES
('Visite du Marché Dantokpa', 'Plus grand marché d''Afrique de l''Ouest', 1, 1, 180),
('Tour de la ville moderne', 'Architecture coloniale et moderne', 1, 1, 120),
('Plage de Fidjrossè', 'Détente sur la plage urbaine', 1, 1, 240),

-- Activités de Ouidah (ville_id=2, zone_id=1)
('Route des Esclaves', 'Parcours historique mémoriel', 2, 1, 180),
('Forêt sacrée', 'Découverte du vaudou traditionnel', 2, 1, 120),
('Musée d''Histoire', 'Patrimoine de l''esclavage', 2, 1, 120),
('Plage de Ouidah', 'Plage historique', 2, 1, 180),

-- Activités de Grand-Popo (ville_id=3, zone_id=1)
('Excursion en pirogue', 'Découverte des lagunes', 3, 1, 240),
('Plage de Mono', 'Plage sauvage et tranquille', 3, 1, 240),

-- Activités d'Abomey (ville_id=4, zone_id=2)
('Palais Royaux', 'Site UNESCO patrimoine mondial', 4, 2, 180),
('Musée Historique', 'Histoire du royaume du Dahomey', 4, 2, 120),
('Marché artisanal', 'Artisanat traditionnel', 4, 2, 120),

-- Activités de Dassa-Zoumé (ville_id=6, zone_id=2)
('Randonnée des 41 collines', 'Trek panoramique', 6, 2, 300),
('Grotte de Dassa', 'Site spirituel', 6, 2, 120),

-- Activités de Parakou (ville_id=7, zone_id=3)
('Marché de Parakou', 'Marché traditionnel du Nord', 7, 3, 120),
('Quartier artisanal', 'Découverte de l''artisanat local', 7, 3, 180),

-- Activités de Natitingou (ville_id=8, zone_id=3)
('Parc de la Pendjari', 'Safari et observation des animaux', 8, 3, 480),
('Tata Somba', 'Architecture traditionnelle', 8, 3, 180),
('Cascade de Tanougou', 'Baignade naturelle', 8, 3, 240),

-- Activités de Djougou (ville_id=9, zone_id=3)
('Marché de Djougou', 'Grand marché du Nord', 9, 3, 120),
('Villages Yom', 'Découverte culturelle', 9, 3, 240);

-- ================================================================
-- CIRCUITS PERSONNALISÉS
-- Note: Utiliser la table circuits_personnalises (avec s)
-- ================================================================

-- Demande EN_ATTENTE: Famille Sud
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours, 
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide,
    message, statut, prix_estime
) VALUES (
    'MENSAH', 'Kofi',
    'kofi.mensah@example.com', '+229 96 12 34 56',
    4, 3,
    true, 'HOTEL',
    true, 'VOITURE',
    true,
    'Nous souhaitons un circuit familial relaxant avec les enfants.',
    'EN_ATTENTE', 200000
);

-- Jours pour demande 1 (Sud: Cotonou -> Ouidah -> Grand-Popo)
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) 
SELECT 1, id, 1, 1 FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com'
UNION ALL
SELECT 2, id, 1, 2 FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com'
UNION ALL
SELECT 3, id, 1, 3 FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com';

-- Activités jour 1 (Cotonou)
INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour = 1 
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com')
  AND a.nom IN ('Visite du Marché Dantokpa', 'Plage de Fidjrossè');

-- Activités jour 2 (Ouidah)
INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour = 2
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com')
  AND a.nom IN ('Route des Esclaves', 'Forêt sacrée', 'Plage de Ouidah');

-- Activités jour 3 (Grand-Popo)
INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour = 3
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'kofi.mensah@example.com')
  AND a.nom IN ('Excursion en pirogue', 'Plage de Mono');

-- ================================================================

-- Demande ACCEPTE: Couple Culture Centre
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide,
    message, statut, prix_estime, prix_final
) VALUES (
    'KOUDOU', 'Aïcha',
    'aicha.koudou@example.com', '+229 97 23 45 67',
    2, 2,
    true, 'AUBERGE',
    true, 'TAXI',
    true,
    'Circuit culturel pour notre anniversaire de mariage.',
    'ACCEPTE', 140000, 135000
);

-- Jours pour demande 2 (Centre: Abomey -> Dassa)
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id)
SELECT 1, id, 2, 4 FROM circuits_personnalises WHERE email_client = 'aicha.koudou@example.com'
UNION ALL
SELECT 2, id, 2, 6 FROM circuits_personnalises WHERE email_client = 'aicha.koudou@example.com';

-- Activités jour 1 (Abomey)
INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour = 1
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'aicha.koudou@example.com')
  AND a.nom IN ('Palais Royaux', 'Musée Historique', 'Marché artisanal');

-- Activités jour 2 (Dassa-Zoumé)
INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour = 2
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'aicha.koudou@example.com')
  AND a.nom IN ('Randonnée des 41 collines', 'Grotte de Dassa');

-- ================================================================

-- Demande REFUSE
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, avec_transport, avec_guide,
    message, statut, prix_estime
) VALUES (
    'DUPONT', 'Jean',
    'jean.dupont@example.com', '+33 6 12 34 56 78',
    20, 1,
    false, false, false,
    'Budget maximum 50000 XOF pour 20 personnes',
    'REFUSE', 50000
);

INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id)
SELECT 1, id, 1, 1 FROM circuits_personnalises WHERE email_client = 'jean.dupont@example.com';

INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'jean.dupont@example.com')
  AND a.nom = 'Visite du Marché Dantokpa';

-- ================================================================

-- Demande EN_TRAITEMENT: Safari Nord
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide,
    message, statut, prix_estime
) VALUES (
    'JOHNSON', 'Michael',
    'michael.johnson@example.com', '+1 555 123 4567',
    6, 5,
    true, 'HOTEL',
    true, 'VOITURE',
    true,
    'We want an authentic safari experience with cultural immersion.',
    'EN_TRAITEMENT', 350000
);

-- Jours pour demande 4 (Nord complet)
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id)
SELECT 1, id, 3, 7 FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com'
UNION ALL
SELECT 2, id, 3, 8 FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com'
UNION ALL
SELECT 3, id, 3, 8 FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com'
UNION ALL
SELECT 4, id, 3, 8 FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com'
UNION ALL
SELECT 5, id, 3, 9 FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com';

-- Activités Parakou (jour 1)
INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour = 1
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com')
  AND a.nom IN ('Marché de Parakou', 'Quartier artisanal');

-- Activités Natitingou (jours 2-4)
INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour IN (2, 3)
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com')
  AND a.nom = 'Parc de la Pendjari';

INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour = 4
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com')
  AND a.nom IN ('Tata Somba', 'Cascade de Tanougou');

-- Activités Djougou (jour 5)
INSERT INTO circuit_perso_jour_activites (circuit_personnalise_jour_id, activite_id)
SELECT cpj.id, a.id_activite 
FROM circuit_personnalise_jours cpj, activites a
WHERE cpj.numero_jour = 5
  AND cpj.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE email_client = 'michael.johnson@example.com')
  AND a.nom IN ('Marché de Djougou', 'Villages Yom');

-- ================================================================
-- STATISTIQUES
-- ================================================================
SELECT 'RÉSUMÉ DES DONNÉES INSÉRÉES' as info;
SELECT COUNT(*) as total_zones FROM zones;
SELECT COUNT(*) as total_villes FROM villes;
SELECT COUNT(*) as total_activites FROM activites;
SELECT COUNT(*) as total_demandes FROM circuits_personnalises;
SELECT statut, COUNT(*) as nombre FROM circuits_personnalises GROUP BY statut;
