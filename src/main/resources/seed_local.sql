-- ================================================================
-- SEED LOCAL - Données de test pour Benin Explo
-- Compatible avec le schéma actuel (V1-V20)
-- ================================================================

-- Nettoyage dans l'ordre des dépendances FK
DELETE FROM circuit_perso_jour_activites;
DELETE FROM circuit_personnalise_jours;
DELETE FROM circuits_personnalises;
DELETE FROM circuit_activites;
DELETE FROM reservations;
DELETE FROM medias;
DELETE FROM activites;
DELETE FROM circuits;
DELETE FROM utilisateurs;
DELETE FROM villes;
DELETE FROM zones;

-- ================================================================
-- ZONES
-- ================================================================
INSERT INTO zones (nom, description) VALUES
('Sud',    'Region sud du Benin'),
('Centre', 'Region centre du Benin'),
('Nord',   'Region nord du Benin');

-- ================================================================
-- VILLES
-- ================================================================
INSERT INTO villes (nom, zone_id) VALUES
-- Sud
('Cotonou',       (SELECT id_zone FROM zones WHERE nom='Sud')),
('Porto-Novo',    (SELECT id_zone FROM zones WHERE nom='Sud')),
('Ouidah',        (SELECT id_zone FROM zones WHERE nom='Sud')),
('Grand-Popo',    (SELECT id_zone FROM zones WHERE nom='Sud')),
('Abomey-Calavi', (SELECT id_zone FROM zones WHERE nom='Sud')),
('Allada',        (SELECT id_zone FROM zones WHERE nom='Sud')),
('Lokossa',       (SELECT id_zone FROM zones WHERE nom='Sud')),
-- Centre
('Abomey',        (SELECT id_zone FROM zones WHERE nom='Centre')),
('Bohicon',       (SELECT id_zone FROM zones WHERE nom='Centre')),
('Cove',          (SELECT id_zone FROM zones WHERE nom='Centre')),
('Dassa-Zoume',   (SELECT id_zone FROM zones WHERE nom='Centre')),
('Savalou',       (SELECT id_zone FROM zones WHERE nom='Centre')),
-- Nord
('Parakou',       (SELECT id_zone FROM zones WHERE nom='Nord')),
('Djougou',       (SELECT id_zone FROM zones WHERE nom='Nord')),
('Natitingou',    (SELECT id_zone FROM zones WHERE nom='Nord')),
('Kandi',         (SELECT id_zone FROM zones WHERE nom='Nord')),
('Malanville',    (SELECT id_zone FROM zones WHERE nom='Nord'));

-- ================================================================
-- CATEGORIES ACTIVITES
-- ================================================================
INSERT INTO categories_activites (nom) VALUES
('Culture & Histoire'),
('Nature & Aventure'),
('Plage & Détente'),
('Gastronomie & Marché');

-- ================================================================
-- ACTIVITES
-- ================================================================
INSERT INTO activites (nom, description, duree_interne, ville_id, categorie_id) VALUES
-- Cotonou
('Visite du Marché Dantokpa',   'Plus grand marché d''Afrique de l''Ouest', 180,
    (SELECT id_ville FROM villes WHERE nom='Cotonou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Gastronomie & Marché')),
('Tour de la ville moderne',     'Architecture coloniale et moderne',           120,
    (SELECT id_ville FROM villes WHERE nom='Cotonou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
('Plage de Fidjrossè',           'Détente sur la plage urbaine',                240,
    (SELECT id_ville FROM villes WHERE nom='Cotonou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Plage & Détente')),
-- Ouidah
('Route des Esclaves',           'Parcours historique mémoriel',                180,
    (SELECT id_ville FROM villes WHERE nom='Ouidah'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
('Forêt sacrée de Ouidah',       'Découverte du vaudou traditionnel',           120,
    (SELECT id_ville FROM villes WHERE nom='Ouidah'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
('Musée d''Histoire de Ouidah',  'Patrimoine de l''esclavage',                  120,
    (SELECT id_ville FROM villes WHERE nom='Ouidah'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
('Plage de Ouidah',              'Plage historique',                            180,
    (SELECT id_ville FROM villes WHERE nom='Ouidah'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Plage & Détente')),
-- Grand-Popo
('Excursion en pirogue',         'Découverte des lagunes',                      240,
    (SELECT id_ville FROM villes WHERE nom='Grand-Popo'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Nature & Aventure')),
('Plage de Mono',                'Plage sauvage et tranquille',                 240,
    (SELECT id_ville FROM villes WHERE nom='Grand-Popo'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Plage & Détente')),
-- Abomey
('Palais Royaux d''Abomey',      'Site UNESCO patrimoine mondial',              180,
    (SELECT id_ville FROM villes WHERE nom='Abomey'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
('Musée Historique d''Abomey',   'Histoire du royaume du Dahomey',              120,
    (SELECT id_ville FROM villes WHERE nom='Abomey'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
('Marché artisanal d''Abomey',   'Artisanat traditionnel',                      120,
    (SELECT id_ville FROM villes WHERE nom='Abomey'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Gastronomie & Marché')),
-- Dassa-Zoume
('Randonnée des 41 collines',    'Trek panoramique',                            300,
    (SELECT id_ville FROM villes WHERE nom='Dassa-Zoume'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Nature & Aventure')),
('Grotte de Dassa',              'Site spirituel',                              120,
    (SELECT id_ville FROM villes WHERE nom='Dassa-Zoume'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
-- Parakou
('Marché de Parakou',            'Marché traditionnel du Nord',                 120,
    (SELECT id_ville FROM villes WHERE nom='Parakou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Gastronomie & Marché')),
('Quartier artisanal de Parakou','Découverte de l''artisanat local',            180,
    (SELECT id_ville FROM villes WHERE nom='Parakou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
-- Natitingou
('Parc de la Pendjari',          'Safari et observation des animaux',           480,
    (SELECT id_ville FROM villes WHERE nom='Natitingou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Nature & Aventure')),
('Tata Somba',                   'Architecture traditionnelle',                 180,
    (SELECT id_ville FROM villes WHERE nom='Natitingou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire')),
('Cascade de Tanougou',          'Baignade naturelle',                          240,
    (SELECT id_ville FROM villes WHERE nom='Natitingou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Nature & Aventure')),
-- Djougou
('Marché de Djougou',            'Grand marché du Nord',                        120,
    (SELECT id_ville FROM villes WHERE nom='Djougou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Gastronomie & Marché')),
('Villages Yom',                 'Découverte culturelle',                       240,
    (SELECT id_ville FROM villes WHERE nom='Djougou'),
    (SELECT id_categorie FROM categories_activites WHERE nom='Culture & Histoire'));

-- ================================================================
-- CIRCUITS PRÉDÉFINIS
-- ================================================================
INSERT INTO circuits (nom, description, duree_indicative, actif, ville_id) VALUES
('Patrimoine du Sud',
 'Découverte de l''histoire et des plages du sud du Bénin. De Cotonou à Ouidah, explorez la route des esclaves et les traditions vaudou.',
 '3 jours', true,
 (SELECT id_ville FROM villes WHERE nom='Cotonou')),

('Royaume du Dahomey',
 'Plongez dans l''histoire royale du Bénin à travers les palais d''Abomey, site UNESCO.',
 '2 jours', true,
 (SELECT id_ville FROM villes WHERE nom='Abomey')),

('Safari Pendjari',
 'Aventure sauvage dans le parc de la Pendjari, l''un des derniers sanctuaires d''Afrique de l''Ouest.',
 '4 jours', true,
 (SELECT id_ville FROM villes WHERE nom='Natitingou')),

('Grand Tour du Bénin',
 'Circuit complet du Sud au Nord : Histoire, Culture, Nature et Safari.',
 '10 jours', true,
 (SELECT id_ville FROM villes WHERE nom='Cotonou'));

-- Activités liées aux circuits
INSERT INTO circuit_activites (circuit_id, activite_id) VALUES
-- Patrimoine du Sud
((SELECT id_circuit FROM circuits WHERE nom='Patrimoine du Sud'), (SELECT id_activite FROM activites WHERE nom='Visite du Marché Dantokpa')),
((SELECT id_circuit FROM circuits WHERE nom='Patrimoine du Sud'), (SELECT id_activite FROM activites WHERE nom='Route des Esclaves')),
((SELECT id_circuit FROM circuits WHERE nom='Patrimoine du Sud'), (SELECT id_activite FROM activites WHERE nom='Forêt sacrée de Ouidah')),
((SELECT id_circuit FROM circuits WHERE nom='Patrimoine du Sud'), (SELECT id_activite FROM activites WHERE nom='Plage de Ouidah')),
-- Royaume du Dahomey
((SELECT id_circuit FROM circuits WHERE nom='Royaume du Dahomey'), (SELECT id_activite FROM activites WHERE nom='Palais Royaux d''Abomey')),
((SELECT id_circuit FROM circuits WHERE nom='Royaume du Dahomey'), (SELECT id_activite FROM activites WHERE nom='Musée Historique d''Abomey')),
((SELECT id_circuit FROM circuits WHERE nom='Royaume du Dahomey'), (SELECT id_activite FROM activites WHERE nom='Marché artisanal d''Abomey')),
-- Safari Pendjari
((SELECT id_circuit FROM circuits WHERE nom='Safari Pendjari'), (SELECT id_activite FROM activites WHERE nom='Parc de la Pendjari')),
((SELECT id_circuit FROM circuits WHERE nom='Safari Pendjari'), (SELECT id_activite FROM activites WHERE nom='Tata Somba')),
((SELECT id_circuit FROM circuits WHERE nom='Safari Pendjari'), (SELECT id_activite FROM activites WHERE nom='Cascade de Tanougou'));

-- ================================================================
-- UTILISATEUR ADMIN DE TEST
-- ================================================================
-- Mot de passe : Admin1234! (bcrypt)
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, actif) VALUES
('Admin', 'Test', 'admin@beninexplo.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVyXEaGDFC',
 'ADMIN', true);

-- ================================================================
-- CIRCUITS PERSONNALISES (demandes de test)
-- ================================================================
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide, avec_chauffeur, pension_complete,
    message_client, statut, prix_estime, date_creation,
    reference_reservation
) VALUES
('MENSAH', 'Kofi', 'kofi.mensah@example.com', '+229 96 12 34 56',
 4, 3, true, 'HOTEL', true, 'VOITURE', true, false, false,
 'Circuit familial relaxant avec les enfants.',
 'EN_ATTENTE', 200000, CURRENT_DATE, 'CP-2026-0001'),

('KOUDOU', 'Aïcha', 'aicha.koudou@example.com', '+229 97 23 45 67',
 2, 2, true, 'AUBERGE', true, 'TAXI', true, false, false,
 'Circuit culturel pour notre anniversaire de mariage.',
 'ACCEPTE', 140000, CURRENT_DATE, 'CP-2026-0002'),

('DUPONT', 'Jean', 'jean.dupont@example.com', '+33 6 12 34 56 78',
 20, 1, false, null, false, null, false, false, false,
 'Budget maximum 50000 XOF pour 20 personnes.',
 'REFUSE', 50000, CURRENT_DATE, 'CP-2026-0003'),

('JOHNSON', 'Michael', 'michael.johnson@example.com', '+1 555 123 4567',
 6, 5, true, 'HOTEL', true, 'VOITURE', true, true, false,
 'We want an authentic safari experience with cultural immersion.',
 'EN_TRAITEMENT', 350000, CURRENT_DATE, 'CP-2026-0004');

-- Jours circuit MENSAH (Sud)
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE reference_reservation='CP-2026-0001'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Cotonou')),
(2, (SELECT id FROM circuits_personnalises WHERE reference_reservation='CP-2026-0001'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Ouidah')),
(3, (SELECT id FROM circuits_personnalises WHERE reference_reservation='CP-2026-0001'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Grand-Popo'));

-- Activités jours MENSAH
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id=(SELECT id FROM circuits_personnalises WHERE reference_reservation='CP-2026-0001')
  AND j.numero_jour=1 AND a.nom IN ('Visite du Marché Dantokpa','Plage de Fidjrossè');

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id=(SELECT id FROM circuits_personnalises WHERE reference_reservation='CP-2026-0001')
  AND j.numero_jour=2 AND a.nom IN ('Route des Esclaves','Forêt sacrée de Ouidah','Plage de Ouidah');

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id=(SELECT id FROM circuits_personnalises WHERE reference_reservation='CP-2026-0001')
  AND j.numero_jour=3 AND a.nom IN ('Excursion en pirogue','Plage de Mono');

-- ================================================================
-- RÉSUMÉ
-- ================================================================
SELECT 'zones'               AS table_name, COUNT(*) AS nb FROM zones
UNION ALL SELECT 'villes',     COUNT(*) FROM villes
UNION ALL SELECT 'activites',  COUNT(*) FROM activites
UNION ALL SELECT 'circuits',   COUNT(*) FROM circuits
UNION ALL SELECT 'demandes',   COUNT(*) FROM circuits_personnalises
UNION ALL SELECT 'utilisateurs', COUNT(*) FROM utilisateurs;
