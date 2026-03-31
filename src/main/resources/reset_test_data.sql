-- ================================================================
-- RESET COMPLET + DONNÉES DE TEST COHÉRENTES — BENIN EXPLO
-- Généré le 2026-03-26
-- ================================================================
-- Mots de passe :
--   admin@beninexplo.com  → monMotDePasse123
--   marie.agossou@test.com / pierre.hounto@test.com → test1234
-- ================================================================

BEGIN;

-- ----------------------------------------------------------------
-- 1. VIDER TOUTES LES TABLES + RÉINITIALISER LES SÉQUENCES
-- ----------------------------------------------------------------
TRUNCATE TABLE
    circuit_perso_jour_activites,
    circuit_personnalise_jours,
    circuits_personnalises,
    circuit_activites,
    devis_activites,
    devis,
    client_demandes,
    reservations,
    reservations_hebergement,
    tombola_participants,
    actualite,
    activites,
    circuits,
    hebergements,
    medias,
    parametres_site,
    tarifs_circuit_personnalise,
    vehicules,
    utilisateurs,
    villes,
    zones,
    categories_activites
RESTART IDENTITY CASCADE;

-- ----------------------------------------------------------------
-- 2. PARAMÈTRES DU SITE
-- ----------------------------------------------------------------
INSERT INTO parametres_site (email_contact, telephone_contact, adresse_agence) VALUES
('contact@beninexplo.com', '+229 21 30 00 00', 'Avenue Jean-Paul II, Cotonou, Bénin');

-- ----------------------------------------------------------------
-- 3. UTILISATEURS
-- ----------------------------------------------------------------
-- Admin
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, date_creation) VALUES
('Admin', 'BeninExplo', 'admin@beninexplo.com',
 '$2b$10$QXHpVTyOlxLAapnPE0K.Ee2DbBRUY6vekHQVBO382FRw2DPLVckNS',
 'ADMIN', NOW());

-- Utilisateurs normaux
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, date_creation) VALUES
('AGOSSOU', 'Marie', 'marie.agossou@test.com',
 '$2b$10$3dXZ7BDcBrVda8v/IiR1IeI25rHZIE22XCgrrPZnWBKQ4ikgOc9TO',
 'USER', NOW() - INTERVAL '15 days'),
('HOUNTO', 'Pierre', 'pierre.hounto@test.com',
 '$2b$10$3dXZ7BDcBrVda8v/IiR1IeI25rHZIE22XCgrrPZnWBKQ4ikgOc9TO',
 'USER', NOW() - INTERVAL '7 days'),
-- Participant tombola
('DOSSOU', 'Chantal', 'chantal.dossou@test.com',
 '$2b$10$3dXZ7BDcBrVda8v/IiR1IeI25rHZIE22XCgrrPZnWBKQ4ikgOc9TO',
 'PARTICIPANT', NOW() - INTERVAL '3 days');

-- ----------------------------------------------------------------
-- 4. ZONES GÉOGRAPHIQUES
-- ----------------------------------------------------------------
INSERT INTO zones (nom, description) VALUES
('Sud',    'Zone côtière : plages, patrimoine historique, traditions vaudou'),
('Centre', 'Zone culturelle : royaumes, artisanat, collines'),
('Nord',   'Zone naturelle : parcs nationaux, architectures traditionnelles');

-- ----------------------------------------------------------------
-- 5. VILLES
-- ----------------------------------------------------------------
INSERT INTO villes (nom, zone_id) VALUES
-- Sud
('Cotonou',    (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Ouidah',     (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Grand-Popo', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Porto-Novo', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
-- Centre
('Abomey',       (SELECT id_zone FROM zones WHERE nom = 'Centre')),
('Bohicon',      (SELECT id_zone FROM zones WHERE nom = 'Centre')),
('Dassa-Zoumé',  (SELECT id_zone FROM zones WHERE nom = 'Centre')),
-- Nord
('Parakou',     (SELECT id_zone FROM zones WHERE nom = 'Nord')),
('Natitingou',  (SELECT id_zone FROM zones WHERE nom = 'Nord')),
('Djougou',     (SELECT id_zone FROM zones WHERE nom = 'Nord'));

-- ----------------------------------------------------------------
-- 6. MÉDIAS (images de démonstration)
-- ----------------------------------------------------------------
INSERT INTO medias (url, type, description) VALUES
('/uploads/placeholder-cotonou.jpg',    'IMAGE', 'Vue de Cotonou'),
('/uploads/placeholder-ouidah.jpg',     'IMAGE', 'Route des Esclaves, Ouidah'),
('/uploads/placeholder-abomey.jpg',     'IMAGE', 'Palais Royaux d''Abomey'),
('/uploads/placeholder-pendjari.jpg',   'IMAGE', 'Parc de la Pendjari'),
('/uploads/placeholder-grandpopo.jpg',  'IMAGE', 'Plage de Grand-Popo'),
('/uploads/placeholder-hotel1.jpg',     'IMAGE', 'Hôtel Azalaï Cotonou'),
('/uploads/placeholder-hotel2.jpg',     'IMAGE', 'Auberge de la Côte'),
('/uploads/placeholder-hotel3.jpg',     'IMAGE', 'Lodge Pendjari'),
('/uploads/placeholder-news1.jpg',      'IMAGE', 'Actualité 1'),
('/uploads/placeholder-news2.jpg',      'IMAGE', 'Actualité 2');

-- ----------------------------------------------------------------
-- 7. HÉBERGEMENTS
-- ----------------------------------------------------------------
INSERT INTO hebergements (nom, type, localisation, quartier, description, prix_par_nuit, image_urls) VALUES
('Hôtel Azalaï',
 'Hôtel',
 'Cotonou',
 'Zone des Ambassades',
 'Hôtel 4 étoiles au cœur de Cotonou. Piscine, restaurant gastronomique, centre de conférence. Idéal pour les voyageurs d''affaires et les touristes exigeants.',
 85000,
 '["/uploads/placeholder-hotel1.jpg"]'),

('Auberge de la Côte',
 'Auberge',
 'Ouidah',
 'Quartier Brésil',
 'Auberge charmante à 500 m de la plage, ambiance conviviale. Petit déjeuner inclus, location de vélos disponible.',
 25000,
 '["/uploads/placeholder-hotel2.jpg"]'),

('Villa Lagune',
 'Villa',
 'Grand-Popo',
 'Bord de mer',
 'Villa privée avec accès direct à la plage et vue sur le lagon. Capacité 8 personnes, cuisine équipée, terrasse panoramique.',
 65000,
 '["/uploads/placeholder-grandpopo.jpg"]'),

('Lodge Pendjari',
 'Lodge',
 'Natitingou',
 'Périphérie — porte du parc',
 'Lodge écotouristique aux portes du parc de la Pendjari. Bungalows en bois, observation de la faune au lever du soleil.',
 45000,
 '["/uploads/placeholder-hotel3.jpg"]'),

('Résidence Royale',
 'Résidence',
 'Abomey',
 'Centre historique',
 'Résidence de charme dans une maison coloniale rénovée à deux pas des Palais Royaux. Déco authentique, jardin intérieur.',
 32000,
 '["/uploads/placeholder-abomey.jpg"]');

-- ----------------------------------------------------------------
-- 8. ACTIVITÉS
-- ----------------------------------------------------------------
-- Cotonou
INSERT INTO activites (nom, description, duree_interne, poids, difficulte, ville_id, image_principale_id) VALUES
('Visite du Marché Dantokpa',
 'Le plus grand marché à ciel ouvert d''Afrique de l''Ouest. Immersion totale dans l''économie locale : tissus, épices, artisanat et produits vaudou.',
 180, 3, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Cotonou'), NULL),

('Tour architectural de Cotonou',
 'Découverte de l''architecture coloniale et contemporaine : cathédrale, buildings modernes et quartiers historiques.',
 120, 2, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Cotonou'), NULL),

('Plage de Fidjrossè',
 'Plage urbaine animée, idéale pour se détendre après les visites. Vendeurs de poisson grillé et ambiance locale garantie.',
 240, 1, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Cotonou'), NULL),

-- Ouidah
('Route des Esclaves',
 'Parcours historique de 4 km de la place des enchères à la Porte du Non-Retour. Lieu de mémoire incontournable.',
 180, 4, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Ouidah'), NULL),

('Forêt Sacrée de Kpasse',
 'Forêt vaudou avec statues géantes de divinités. Guide indispensable pour comprendre les rites et symboles.',
 120, 3, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Ouidah'), NULL),

('Musée d''Histoire de Ouidah',
 'Musée installé dans l''ancien fort portugais. Collections sur la traite négrière, les royaumes du Dahomey et le vaudou.',
 90, 3, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Ouidah'), NULL),

('Plage de Ouidah',
 'Plage historique au pied de la Porte du Non-Retour. Atmosphère contemplative et couchers de soleil exceptionnels.',
 180, 1, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Ouidah'), NULL),

-- Grand-Popo
('Excursion en pirogue sur le Mono',
 'Balade en pirogue à travers les mangroves et les villages lacustres du delta du Mono. Observation des oiseaux.',
 240, 3, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Grand-Popo'), NULL),

('Plage sauvage de Grand-Popo',
 'L''une des plus belles plages du Bénin, préservée du tourisme de masse. Tortues marines en saison.',
 300, 1, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Grand-Popo'), NULL),

-- Porto-Novo
('Visite de Porto-Novo',
 'Capitale officielle du Bénin : musée Honmé, grande mosquée brésilienne, marché central et architecture lusophone.',
 180, 3, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Porto-Novo'), NULL),

-- Abomey
('Palais Royaux d''Abomey',
 'Site classé au patrimoine mondial de l''UNESCO. Visite des palais des rois du Dahomey et des bas-reliefs historiques.',
 180, 5, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Abomey'), NULL),

('Musée Historique d''Abomey',
 'Collections de trônes royaux, armes, tissus et objets rituels des douze rois du Dahomey.',
 120, 4, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Abomey'), NULL),

('Marché artisanal d''Abomey',
 'Tapisseries de Abomey, calebasses sculptées, bijoux et bronzes. Le meilleur endroit pour ramener un souvenir authentique.',
 120, 2, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Abomey'), NULL),

-- Dassa-Zoumé
('Randonnée des 41 Collines',
 'Trek au sommet des 41 collines avec vue panoramique sur toute la région centrale. Chemin spirituel pour les pèlerins catholiques.',
 300, 5, 'MOYEN',
 (SELECT id_ville FROM villes WHERE nom = 'Dassa-Zoumé'), NULL),

('Grotte Notre-Dame de Dassa',
 'Grotte naturelle abritant un sanctuaire marial. Lieu de pèlerinage majeur en Afrique de l''Ouest, cadre naturel remarquable.',
 120, 2, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Dassa-Zoumé'), NULL),

-- Parakou
('Marché central de Parakou',
 'Grand marché commercial du Nord-Bénin : produits locaux, coton, bétail et artisanat Bariba.',
 120, 2, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Parakou'), NULL),

('Quartier artisanal de Parakou',
 'Atelier de potiers, forgerons et tisserands. Rencontre avec les artisans et démonstrations de savoir-faire ancestral.',
 150, 3, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Parakou'), NULL),

-- Natitingou
('Parc National de la Pendjari',
 'L''un des derniers grands parcs d''Afrique de l''Ouest. Éléphants, lions, buffles, hippopotames. Safari en 4x4 avec guide.',
 480, 5, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Natitingou'), NULL),

('Villages Tata Somba',
 'Découverte de l''architecture troglodyte Betammaribé, classée à l''UNESCO. Les "châteaux" de boue à deux étages sont uniques au monde.',
 180, 4, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Natitingou'), NULL),

('Cascade de Tanougou',
 'Cascade naturelle de 10 m dans un cadre forestier luxuriant. Baignade possible, pique-nique au bord de l''eau.',
 240, 3, 'MOYEN',
 (SELECT id_ville FROM villes WHERE nom = 'Natitingou'), NULL),

-- Djougou
('Villages Yom de Djougou',
 'Immersion dans la culture Yom : danses traditionnelles, cérémonies d''initiation (selon saison) et gastronomie locale.',
 240, 3, 'FACILE',
 (SELECT id_ville FROM villes WHERE nom = 'Djougou'), NULL);

-- ----------------------------------------------------------------
-- 9. CIRCUITS PRÉDÉFINIS
-- ----------------------------------------------------------------
INSERT INTO circuits (
    nom, description, resume, duree_indicative, prix_indicatif,
    formule_proposee, actif, featured_home, a_la_une,
    ville_id, image_principale_id,
    programme, points_forts, inclus, non_inclus, galerie
) VALUES
(
    'Héritage du Littoral',
    'Partez à la découverte du Sud du Bénin, berceau de l''histoire africaine. De Cotonou à Grand-Popo en passant par Ouidah, ce circuit mêle histoire de la traite négrière, spiritualité vaudou et détente sur des plages sauvages.',
    'Histoire, vaudou et plages du Sud — 3 jours entre Cotonou, Ouidah et Grand-Popo.',
    '3 jours / 2 nuits', 185000,
    'Tout compris (transport, guide, hébergement)',
    true, true, true,
    (SELECT id_ville FROM villes WHERE nom = 'Cotonou'),
    (SELECT id_media FROM medias WHERE description = 'Vue de Cotonou'),
    '[{"jour":1,"titre":"Cotonou — Marché et découverte urbaine","description":"Accueil à l''aéroport, installation à l''hôtel. Visite du marché Dantokpa, tour architectural de la ville et soirée sur la plage de Fidjrossè."},{"jour":2,"titre":"Ouidah — Route des Esclaves","description":"Départ pour Ouidah. Visite du musée d''histoire, parcours de la Route des Esclaves jusqu''à la Porte du Non-Retour, découverte de la Forêt Sacrée de Kpasse."},{"jour":3,"titre":"Grand-Popo et retour","description":"Matinée en pirogue sur le delta du Mono. Farniente sur la plage sauvage. Retour à Cotonou en fin d''après-midi."}]',
    '["Porte du Non-Retour à Ouidah","Balade en pirogue dans le delta du Mono","Immersion dans la spiritualité vaudou","Plages sauvages préservées"]',
    '["Transport climatisé","Guide francophone","2 nuits en hébergement de charme","Petit déjeuner inclus"]',
    '["Vols internationaux","Déjeuners et dîners","Dépenses personnelles"]',
    '["/uploads/placeholder-cotonou.jpg","/uploads/placeholder-ouidah.jpg","/uploads/placeholder-grandpopo.jpg"]'
),
(
    'Royaumes du Dahomey',
    'Plongez dans l''histoire royale du Bénin. Les palais d''Abomey, classés au patrimoine mondial de l''UNESCO, témoignent de la grandeur des douze rois du Dahomey. Le circuit s''achève sur les collines spirituelles de Dassa-Zoumé.',
    'Culture et histoire royale — 2 jours entre Abomey et Dassa-Zoumé.',
    '2 jours / 1 nuit', 120000,
    'Transport et guide inclus',
    true, false, false,
    (SELECT id_ville FROM villes WHERE nom = 'Abomey'),
    (SELECT id_media FROM medias WHERE description = 'Palais Royaux d''Abomey'),
    '[{"jour":1,"titre":"Abomey — Cœur du Dahomey","description":"Visite des Palais Royaux et du Musée Historique. Après-midi au marché artisanal pour découvrir les tapisseries et bronzes. Nuit à la Résidence Royale."},{"jour":2,"titre":"Dassa-Zoumé — Les collines sacrées","description":"Randonnée matinale parmi les 41 collines avec vue panoramique. Visite de la grotte Notre-Dame, lieu de pèlerinage africain majeur. Retour."}]',
    '["Palais Royaux classés UNESCO","Rencontre avec les artisans d''Abomey","Panorama depuis les 41 collines de Dassa","Ambiance spirituelle unique"]',
    '["Transport","Guide spécialisé en histoire","1 nuit en hébergement","Entrées musées"]',
    '["Repas","Dépenses personnelles","Souvenirs"]',
    '["/uploads/placeholder-abomey.jpg"]'
),
(
    'Safari Pendjari',
    'Aventure sauvage dans le parc national de la Pendjari, l''un des écosystèmes les mieux préservés d''Afrique de l''Ouest. Éléphants, lions, buffles et hippos dans leur habitat naturel. Découverte des villages Tata Somba, architecture troglodyte unique au monde.',
    'Safari et nature — 4 jours dans le Nord sauvage du Bénin.',
    '4 jours / 3 nuits', 295000,
    'Formule lodge — tout inclus',
    true, true, false,
    (SELECT id_ville FROM villes WHERE nom = 'Natitingou'),
    (SELECT id_media FROM medias WHERE description = 'Parc de la Pendjari'),
    '[{"jour":1,"titre":"Parakou — Porte du Nord","description":"Trajet vers Parakou. Visite du marché central et du quartier artisanal. Nuit sur place."},{"jour":2,"titre":"Natitingou — Villages Tata Somba","description":"Découverte de l''architecture Betammaribé classée à l''UNESCO. Visite de la cascade de Tanougou. Installation au Lodge Pendjari."},{"jour":3,"titre":"Safari dans la Pendjari","description":"Safari en 4x4 toute la journée avec guide naturaliste. Observation des éléphants, buffles, lions et antilopes à l''aube et en soirée."},{"jour":4,"titre":"Matinée safari et retour","description":"Dernier safari matinal. Déjeuner au lodge. Retour vers le Sud."}]',
    '["Safari en 4x4 dans la Pendjari","Observation des Big 5 béninois","Villages Tata Somba — UNESCO","Cascade de Tanougou","Nuits en lodge écotouristique"]',
    '["Transport 4x4","Guide naturaliste","3 nuits en lodge","Pension complète","Droits d''entrée parc"]',
    '["Vols","Pourboires","Alcool","Dépenses personnelles"]',
    '["/uploads/placeholder-pendjari.jpg"]'
),
(
    'Grand Tour du Bénin',
    'Le circuit intégral du Bénin, du littoral atlantique aux savanes du Nord. Dix jours pour tout vivre : histoire de la traite, spiritualité vaudou, royaumes du Dahomey, randonnées dans les collines, architecture Tata Somba et safari dans la Pendjari.',
    'L''intégrale du Bénin en 10 jours — du Sud au Nord.',
    '10 jours / 9 nuits', 680000,
    'Circuit premium tout compris',
    true, true, true,
    (SELECT id_ville FROM villes WHERE nom = 'Cotonou'),
    (SELECT id_media FROM medias WHERE description = 'Vue de Cotonou'),
    '[{"jour":1,"titre":"Arrivée à Cotonou","description":"Accueil et installation. Soirée de bienvenue."},{"jour":2,"titre":"Cotonou — Marché et plage","description":"Dantokpa, architecture coloniale, plage de Fidjrossè."},{"jour":3,"titre":"Ouidah — Route des Esclaves","description":"Parcours mémoriel, Forêt Sacrée, Musée."},{"jour":4,"titre":"Grand-Popo — Lagon et plage","description":"Pirogue, plage sauvage, détente."},{"jour":5,"titre":"Porto-Novo — Capitale","description":"Musée Honmé, mosquée brésilienne, marché."},{"jour":6,"titre":"Abomey — Palais Royaux","description":"UNESCO, musée, artisans."},{"jour":7,"titre":"Dassa-Zoumé — Les collines","description":"Randonnée, grotte, pèlerinage."},{"jour":8,"titre":"Route vers le Nord — Parakou","description":"Marché, artisans Bariba."},{"jour":9,"titre":"Natitingou — Tata Somba","description":"Villages, cascade, installation lodge."},{"jour":10,"titre":"Safari Pendjari et retour","description":"Safari aube, déjeuner, retour."}]',
    '["L''intégrale des sites UNESCO du Bénin","Safari dans la Pendjari","Route des Esclaves et Porte du Non-Retour","Architecture Tata Somba","Villages lacustres et pirogue"]',
    '["Transport climatisé ou 4x4 selon terrain","Guide expert 10 jours","9 nuits en hébergements sélectionnés","Petits déjeuners","Droits d''entrée tous sites"]',
    '["Vols internationaux","Déjeuners et dîners (sauf mention)","Dépenses personnelles","Assurance voyage"]',
    '["/uploads/placeholder-cotonou.jpg","/uploads/placeholder-ouidah.jpg","/uploads/placeholder-abomey.jpg","/uploads/placeholder-pendjari.jpg","/uploads/placeholder-grandpopo.jpg"]'
);

-- ----------------------------------------------------------------
-- 10. ACTIVITÉS LIÉES AUX CIRCUITS (programme détaillé)
-- ----------------------------------------------------------------
-- Circuit 1 : Héritage du Littoral
INSERT INTO circuit_activites (circuit_id, activite_id, jour_indicatif, ordre) VALUES
((SELECT id_circuit FROM circuits WHERE nom = 'Héritage du Littoral'),
 (SELECT id_activite FROM activites WHERE nom = 'Visite du Marché Dantokpa'), 1, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Héritage du Littoral'),
 (SELECT id_activite FROM activites WHERE nom = 'Plage de Fidjrossè'), 1, 2),
((SELECT id_circuit FROM circuits WHERE nom = 'Héritage du Littoral'),
 (SELECT id_activite FROM activites WHERE nom = 'Route des Esclaves'), 2, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Héritage du Littoral'),
 (SELECT id_activite FROM activites WHERE nom = 'Forêt Sacrée de Kpasse'), 2, 2),
((SELECT id_circuit FROM circuits WHERE nom = 'Héritage du Littoral'),
 (SELECT id_activite FROM activites WHERE nom = 'Plage de Ouidah'), 2, 3),
((SELECT id_circuit FROM circuits WHERE nom = 'Héritage du Littoral'),
 (SELECT id_activite FROM activites WHERE nom = 'Excursion en pirogue sur le Mono'), 3, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Héritage du Littoral'),
 (SELECT id_activite FROM activites WHERE nom = 'Plage sauvage de Grand-Popo'), 3, 2);

-- Circuit 2 : Royaumes du Dahomey
INSERT INTO circuit_activites (circuit_id, activite_id, jour_indicatif, ordre) VALUES
((SELECT id_circuit FROM circuits WHERE nom = 'Royaumes du Dahomey'),
 (SELECT id_activite FROM activites WHERE nom = 'Palais Royaux d''Abomey'), 1, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Royaumes du Dahomey'),
 (SELECT id_activite FROM activites WHERE nom = 'Musée Historique d''Abomey'), 1, 2),
((SELECT id_circuit FROM circuits WHERE nom = 'Royaumes du Dahomey'),
 (SELECT id_activite FROM activites WHERE nom = 'Marché artisanal d''Abomey'), 1, 3),
((SELECT id_circuit FROM circuits WHERE nom = 'Royaumes du Dahomey'),
 (SELECT id_activite FROM activites WHERE nom = 'Randonnée des 41 Collines'), 2, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Royaumes du Dahomey'),
 (SELECT id_activite FROM activites WHERE nom = 'Grotte Notre-Dame de Dassa'), 2, 2);

-- Circuit 3 : Safari Pendjari
INSERT INTO circuit_activites (circuit_id, activite_id, jour_indicatif, ordre) VALUES
((SELECT id_circuit FROM circuits WHERE nom = 'Safari Pendjari'),
 (SELECT id_activite FROM activites WHERE nom = 'Marché central de Parakou'), 1, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Safari Pendjari'),
 (SELECT id_activite FROM activites WHERE nom = 'Villages Tata Somba'), 2, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Safari Pendjari'),
 (SELECT id_activite FROM activites WHERE nom = 'Cascade de Tanougou'), 2, 2),
((SELECT id_circuit FROM circuits WHERE nom = 'Safari Pendjari'),
 (SELECT id_activite FROM activites WHERE nom = 'Parc National de la Pendjari'), 3, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Safari Pendjari'),
 (SELECT id_activite FROM activites WHERE nom = 'Parc National de la Pendjari'), 4, 1);

-- Circuit 4 : Grand Tour
INSERT INTO circuit_activites (circuit_id, activite_id, jour_indicatif, ordre) VALUES
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Visite du Marché Dantokpa'), 2, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Plage de Fidjrossè'), 2, 2),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Route des Esclaves'), 3, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Forêt Sacrée de Kpasse'), 3, 2),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Excursion en pirogue sur le Mono'), 4, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Visite de Porto-Novo'), 5, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Palais Royaux d''Abomey'), 6, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Randonnée des 41 Collines'), 7, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Marché central de Parakou'), 8, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Villages Tata Somba'), 9, 1),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Cascade de Tanougou'), 9, 2),
((SELECT id_circuit FROM circuits WHERE nom = 'Grand Tour du Bénin'),
 (SELECT id_activite FROM activites WHERE nom = 'Parc National de la Pendjari'), 10, 1);

-- ----------------------------------------------------------------
-- 11. TARIFS CIRCUIT PERSONNALISÉ (en XOF)
-- ----------------------------------------------------------------
INSERT INTO tarifs_circuit_personnalise (
    devise,
    transport_compact_par_jour,
    transport_familial_par_jour,
    transport_minibus_par_jour,
    transport_bus_par_jour,
    guide_par_jour,
    chauffeur_par_jour,
    pension_complete_par_personne_par_jour
) VALUES (
    'XOF',
    25000,   -- Citadine / compact
    35000,   -- Familiale / SUV
    60000,   -- Minibus
    90000,   -- Bus
    15000,   -- Guide
    12000,   -- Chauffeur
    20000    -- Pension complète / pers / jour
);

-- ----------------------------------------------------------------
-- 12. CIRCUITS PERSONNALISÉS (exemples — tous les statuts)
-- ----------------------------------------------------------------

-- EN_ATTENTE : Famille en vacances
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide, avec_chauffeur, pension_complete,
    message_client,
    statut, date_creation, date_voyage_souhaitee,
    devise_prix_estime,
    prix_activites_estime, prix_hebergement_estime, prix_transport_estime,
    prix_guide_estime, prix_chauffeur_estime, prix_pension_complete_estime,
    prix_estime
) VALUES (
    'MENSAH', 'Kofi',
    'kofi.mensah@example.com', '+229 96 12 34 56',
    4, 3,
    true, 'HOTEL',
    true, 'VOITURE',
    true, false, false,
    'Nous souhaitons un circuit familial incluant des activités adaptées aux enfants (8 et 11 ans). Priorité aux plages et à l''histoire.',
    'EN_ATTENTE', NOW() - INTERVAL '2 days', CURRENT_DATE + INTERVAL '30 days',
    'XOF',
    60000, 76000, 75000, 45000, 0, 0,
    256000
);

-- Jours : Cotonou → Ouidah → Grand-Popo
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Cotonou')),
(2, (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Ouidah')),
(3, (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Grand-Popo'));

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi')
  AND j.numero_jour = 1 AND a.nom IN ('Visite du Marché Dantokpa','Plage de Fidjrossè');

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi')
  AND j.numero_jour = 2 AND a.nom IN ('Route des Esclaves','Forêt Sacrée de Kpasse','Plage de Ouidah');

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi')
  AND j.numero_jour = 3 AND a.nom IN ('Excursion en pirogue sur le Mono','Plage sauvage de Grand-Popo');

-- EN_TRAITEMENT : Safari famille américaine
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide, avec_chauffeur, pension_complete,
    message_client,
    statut, date_creation, date_voyage_souhaitee,
    commentaire_admin,
    devise_prix_estime,
    prix_activites_estime, prix_hebergement_estime, prix_transport_estime,
    prix_guide_estime, prix_chauffeur_estime, prix_pension_complete_estime,
    prix_estime,
    hebergement_id
) VALUES (
    'JOHNSON', 'Michael',
    'michael.johnson@example.com', '+1 555 123 4567',
    6, 5,
    true, 'LODGE',
    true, '4X4',
    true, true, false,
    'We want an authentic safari experience combined with cultural immersion. We are a family of 4 adults and 2 teenagers.',
    'EN_TRAITEMENT', NOW() - INTERVAL '5 days', CURRENT_DATE + INTERVAL '45 days',
    'Devis préparé. En attente confirmation disponibilité Lodge Pendjari pour 3 chambres.',
    'XOF',
    120000, 135000, 180000, 75000, 60000, 0,
    570000,
    (SELECT id_hebergement FROM hebergements WHERE nom = 'Lodge Pendjari')
);

INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'),
    (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Parakou')),
(2, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'),
    (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Natitingou')),
(3, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'),
    (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Natitingou')),
(4, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'),
    (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Natitingou')),
(5, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'),
    (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Djougou'));

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael')
  AND j.numero_jour = 1 AND a.nom IN ('Marché central de Parakou','Quartier artisanal de Parakou');

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael')
  AND j.numero_jour IN (2,3) AND a.nom IN ('Villages Tata Somba','Cascade de Tanougou');

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael')
  AND j.numero_jour = 4 AND a.nom = 'Parc National de la Pendjari';

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael')
  AND j.numero_jour = 5 AND a.nom = 'Villages Yom de Djougou';

-- ACCEPTE : Couple anniversaire — Centre culturel
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide, avec_chauffeur, pension_complete,
    message_client,
    statut, date_creation, date_voyage_souhaitee, date_traitement,
    commentaire_admin,
    devise_prix_estime,
    prix_activites_estime, prix_hebergement_estime, prix_transport_estime,
    prix_guide_estime, prix_chauffeur_estime, prix_pension_complete_estime,
    prix_estime, prix_final,
    hebergement_id
) VALUES (
    'KOUDOU', 'Aïcha',
    'aicha.koudou@example.com', '+229 97 23 45 67',
    2, 2,
    true, 'RÉSIDENCE',
    true, 'TAXI',
    true, false, false,
    'Circuit culturel romantique pour notre 5e anniversaire de mariage. Nous adorons l''histoire.',
    'ACCEPTE', NOW() - INTERVAL '10 days', CURRENT_DATE + INTERVAL '20 days', CURRENT_DATE - INTERVAL '7 days',
    'Devis accepté. Résidence Royale réservée pour 2 nuits. Guide confirmé.',
    'XOF',
    45000, 64000, 50000, 30000, 0, 0,
    189000, 185000,
    (SELECT id_hebergement FROM hebergements WHERE nom = 'Résidence Royale')
);

INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='KOUDOU' AND prenom_client='Aïcha'),
    (SELECT id_zone FROM zones WHERE nom='Centre'), (SELECT id_ville FROM villes WHERE nom='Abomey')),
(2, (SELECT id FROM circuits_personnalises WHERE nom_client='KOUDOU' AND prenom_client='Aïcha'),
    (SELECT id_zone FROM zones WHERE nom='Centre'), (SELECT id_ville FROM villes WHERE nom='Dassa-Zoumé'));

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='KOUDOU' AND prenom_client='Aïcha')
  AND j.numero_jour = 1 AND a.nom IN ('Palais Royaux d''Abomey','Musée Historique d''Abomey','Marché artisanal d''Abomey');

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id)
SELECT j.id, a.id_activite FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='KOUDOU' AND prenom_client='Aïcha')
  AND j.numero_jour = 2 AND a.nom IN ('Randonnée des 41 Collines','Grotte Notre-Dame de Dassa');

-- REFUSE : Demande irréaliste (budget insuffisant)
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, avec_transport, avec_guide, avec_chauffeur, pension_complete,
    message_client,
    statut, date_creation, date_traitement,
    motif_refus,
    devise_prix_estime,
    prix_activites_estime, prix_hebergement_estime, prix_transport_estime,
    prix_guide_estime, prix_chauffeur_estime, prix_pension_complete_estime,
    prix_estime
) VALUES (
    'DUPONT', 'Jean',
    'jean.dupont@example.com', '+33 6 12 34 56 78',
    15, 1,
    false, false, false, false, false,
    'Budget maximum 30 000 XOF pour tout le groupe, tout compris.',
    'REFUSE', NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days',
    'Le budget demandé (30 000 XOF pour 15 personnes) est inférieur au coût minimum d''un circuit d''une journée. Nous vous invitons à consulter nos tarifs ou à recontacter l''agence pour un devis adapté.',
    'XOF',
    0, 0, 0, 0, 0, 0,
    30000
);

INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='DUPONT' AND prenom_client='Jean'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Cotonou'));

-- TERMINE : Circuit déjà effectué
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide, avec_chauffeur, pension_complete,
    message_client,
    statut, date_creation, date_voyage_souhaitee, date_traitement,
    commentaire_admin,
    devise_prix_estime,
    prix_activites_estime, prix_hebergement_estime, prix_transport_estime,
    prix_guide_estime, prix_chauffeur_estime, prix_pension_complete_estime,
    prix_estime, prix_final
) VALUES (
    'ASSOGBA', 'Rodrigue',
    'rodrigue.assogba@example.com', '+229 95 67 89 01',
    3, 4,
    true, 'HOTEL',
    true, 'VOITURE',
    true, false, true,
    'Circuit complet du Sud au Centre pour moi et mes deux amis. Nous voulons vivre le Bénin authentique.',
    'TERMINE', NOW() - INTERVAL '60 days', NOW() - INTERVAL '30 days', NOW() - INTERVAL '55 days',
    'Circuit terminé avec succès. Feedback très positif du groupe. Guide Narcisse très apprécié.',
    'XOF',
    80000, 100000, 100000, 60000, 0, 240000,
    580000, 560000
);

INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='ASSOGBA' AND prenom_client='Rodrigue'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Cotonou')),
(2, (SELECT id FROM circuits_personnalises WHERE nom_client='ASSOGBA' AND prenom_client='Rodrigue'),
    (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Ouidah')),
(3, (SELECT id FROM circuits_personnalises WHERE nom_client='ASSOGBA' AND prenom_client='Rodrigue'),
    (SELECT id_zone FROM zones WHERE nom='Centre'), (SELECT id_ville FROM villes WHERE nom='Abomey')),
(4, (SELECT id FROM circuits_personnalises WHERE nom_client='ASSOGBA' AND prenom_client='Rodrigue'),
    (SELECT id_zone FROM zones WHERE nom='Centre'), (SELECT id_ville FROM villes WHERE nom='Dassa-Zoumé'));

-- ----------------------------------------------------------------
-- 13. RÉSERVATIONS HÉBERGEMENT
-- ----------------------------------------------------------------
INSERT INTO reservations_hebergement (
    nom_client, prenom_client, email_client, telephone_client,
    date_arrivee, date_depart, nombre_nuits, nombre_personnes,
    prix_total, statut, commentaires, date_creation,
    id_hebergement
) VALUES
('GNIMAGNON', 'Sewanou',
 'sewanou.gnimagnon@example.com', '+229 97 11 22 33',
 CURRENT_DATE + INTERVAL '10 days', CURRENT_DATE + INTERVAL '13 days',
 3, 2,
 170000, 'CONFIRME', 'Demande chambre vue sur piscine si disponible.', CURRENT_DATE - INTERVAL '3 days',
 (SELECT id_hebergement FROM hebergements WHERE nom = 'Hôtel Azalaï')),

('ADANLENOU', 'Félix',
 'felix.adanlenou@example.com', '+229 96 44 55 66',
 CURRENT_DATE + INTERVAL '5 days', CURRENT_DATE + INTERVAL '7 days',
 2, 2,
 50000, 'CONFIRME', NULL, CURRENT_DATE - INTERVAL '1 day',
 (SELECT id_hebergement FROM hebergements WHERE nom = 'Auberge de la Côte')),

('BADA', 'Christelle',
 'christelle.bada@example.com', '+229 95 78 90 12',
 CURRENT_DATE + INTERVAL '20 days', CURRENT_DATE + INTERVAL '24 days',
 4, 4,
 260000, 'EN_ATTENTE', 'Groupe de 4 adultes. Besoin d''un lit bébé supplémentaire.', CURRENT_DATE,
 (SELECT id_hebergement FROM hebergements WHERE nom = 'Villa Lagune'));

-- ----------------------------------------------------------------
-- 14. RÉSERVATIONS CIRCUITS
-- ----------------------------------------------------------------
INSERT INTO reservations (nom, prenom, email, telephone, date_reservation, circuit_id) VALUES
('AGOSSOU', 'Marie',
 'marie.agossou@test.com', '+229 97 12 34 56',
 CURRENT_DATE + INTERVAL '15 days',
 (SELECT id_circuit FROM circuits WHERE nom = 'Héritage du Littoral')),

('HOUNTO', 'Pierre',
 'pierre.hounto@test.com', '+229 96 98 76 54',
 CURRENT_DATE + INTERVAL '25 days',
 (SELECT id_circuit FROM circuits WHERE nom = 'Safari Pendjari'));

-- ----------------------------------------------------------------
-- 15. ACTUALITÉS
-- ----------------------------------------------------------------
INSERT INTO actualite (
    titre, resume, contenu,
    date_publication, publiee, a_la_une,
    image_url,
    auteur_id
) VALUES
(
    'Bénin : meilleure destination culturelle 2025 en Afrique de l''Ouest',
    'Le Bénin vient d''être distingué par le magazine Voyageurs du Monde comme la meilleure destination culturelle d''Afrique de l''Ouest pour 2025.',
    'Le Bénin confirme sa place sur la carte du tourisme mondial. Reconnu pour la richesse de son patrimoine historique — des royaumes du Dahomey aux traditions vaudou — le pays attire chaque année de plus en plus de voyageurs en quête d''authenticité. Nos circuits vous permettent de découvrir ce joyau de l''Afrique de l''Ouest dans les meilleures conditions, avec des guides passionnés et des hébergements soigneusement sélectionnés.',
    NOW() - INTERVAL '5 days', true, true,
    '/uploads/placeholder-abomey.jpg',
    (SELECT id FROM utilisateurs WHERE email = 'admin@beninexplo.com')
),
(
    'Ouverture de la nouvelle saison : Pendjari 2025-2026',
    'Le parc national de la Pendjari ouvre officiellement sa saison touristique avec des chiffres records de biodiversité.',
    'Les équipes de gestion du parc de la Pendjari annoncent une saison 2025-2026 exceptionnelle. Les derniers recensements font état d''une population d''éléphants en hausse de 12%, et de la réapparition de lions dans des zones non observées depuis plus de dix ans. BeninExplo vous propose ses safaris exclusifs en 4x4 avec des guides naturalistes certifiés. Places limitées — réservez maintenant pour vivre cette expérience unique.',
    NOW() - INTERVAL '12 days', true, false,
    '/uploads/placeholder-pendjari.jpg',
    (SELECT id FROM utilisateurs WHERE email = 'admin@beninexplo.com')
),
(
    'Nouvelle offre : séjours hébergement à la Villa Lagune',
    'Découvrez notre nouvelle offre de séjour à la Villa Lagune de Grand-Popo — une villa privée avec accès direct à la plage.',
    'BeninExplo élargit son offre d''hébergement avec la Villa Lagune à Grand-Popo. Cette villa exclusive de 8 personnes offre un accès direct à la plage, une terrasse panoramique sur le lagon du Mono et une cuisine entièrement équipée. Idéale pour des séjours en famille ou entre amis, elle est disponible à partir de 65 000 XOF la nuit. Combinable avec nos circuits du littoral pour une expérience complète du Sud du Bénin.',
    NOW() - INTERVAL '3 days', true, false,
    '/uploads/placeholder-grandpopo.jpg',
    (SELECT id FROM utilisateurs WHERE email = 'admin@beninexplo.com')
);

-- ----------------------------------------------------------------
-- 16. VÉHICULES
-- ----------------------------------------------------------------
INSERT INTO vehicules (marque, modele, matricule, annee, disponible) VALUES
('Toyota',    'Land Cruiser 4x4', 'BJ-1234-AA', 2022, true),
('Toyota',    'HiAce (Minibus)',  'BJ-5678-AB', 2021, true),
('Renault',   'Duster',          'BJ-9012-AC', 2023, true),
('Mercedes',  'Sprinter',        'BJ-3456-AD', 2020, false);

-- ----------------------------------------------------------------
-- 17. TOMBOLA
-- ----------------------------------------------------------------
INSERT INTO tombola_participants (email, nom, prenom, date_inscription, utilisateur_id) VALUES
('chantal.dossou@test.com', 'DOSSOU', 'Chantal',
 NOW() - INTERVAL '3 days',
 (SELECT id FROM utilisateurs WHERE email = 'chantal.dossou@test.com')),
('marie.agossou@test.com', 'AGOSSOU', 'Marie',
 NOW() - INTERVAL '10 days',
 (SELECT id FROM utilisateurs WHERE email = 'marie.agossou@test.com'));

-- ----------------------------------------------------------------
-- RÉSUMÉ
-- ----------------------------------------------------------------
SELECT 'zones'               AS table_name, COUNT(*) AS lignes FROM zones
UNION ALL SELECT 'villes',             COUNT(*) FROM villes
UNION ALL SELECT 'activites',          COUNT(*) FROM activites
UNION ALL SELECT 'circuits',           COUNT(*) FROM circuits
UNION ALL SELECT 'hebergements',       COUNT(*) FROM hebergements
UNION ALL SELECT 'circuits_perso',     COUNT(*) FROM circuits_personnalises
UNION ALL SELECT 'reservations_hebergement', COUNT(*) FROM reservations_hebergement
UNION ALL SELECT 'reservations',       COUNT(*) FROM reservations
UNION ALL SELECT 'actualites',         COUNT(*) FROM actualite
UNION ALL SELECT 'utilisateurs',       COUNT(*) FROM utilisateurs
UNION ALL SELECT 'vehicules',          COUNT(*) FROM vehicules
UNION ALL SELECT 'tombola',            COUNT(*) FROM tombola_participants
ORDER BY table_name;

COMMIT;
